/*
 * Copyright (C) 2016 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.phone.common.ambient;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cyanogen.ambient.common.api.AmbientApiClient;
import com.cyanogen.ambient.common.api.PendingResult;
import com.cyanogen.ambient.common.api.Result;
import com.cyanogen.ambient.common.api.ResultCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Helper method for holding and broadcasting updated plugin data
 *
 */
public abstract class AmbientDataSubscription<M> {

    private static final String TAG = AmbientDataSubscription.class.getSimpleName();
    private static final boolean DEBUG = false;

    // Our ambient client
    public AmbientApiClient mClient;
    public Context mContext;

    // The handler that processes broadcasts
    private Handler mMainHandler;

    // Holder for plugin object
    private HashMap<ComponentName, M> mPluginInfo;

    private boolean mDataHasBeenBroadcastPreviously = false;

    // A list of our registered clients, who all register with the CallMethodReceiver
    private final HashMap<String, PluginChanged> mRegisteredClients = new HashMap<>();

    // A map of our components and if they have have enabled IInterface listeners. This is to keep
    // track of if listeners should be added or removed.
    private final HashMap<ComponentName, Boolean> mEnabledListeners = new HashMap<>();

    // Wait up to 60 seconds for data to return. Mimicks what PluginBinderManager does.
    private static final long TIMEOUT_MILLISECONDS = 60000L;

    // Bootstrap is the initial callback to get your installed plugins.
    // this is the only item that executes ASAP and has no componentname tied to it
    public final ResultCallback BOOTSTRAP = new ResultCallback<Result>() {

        @Override
        public void onResult(Result result) {
            List<ComponentName> installedPlugins = getPluginComponents(result);
            if (installedPlugins.isEmpty()) {
                // We want to tell our subscribers that we have no plugins to worry about
                broadcast();
                mDataHasBeenBroadcastPreviously = true;
            } else {
                for (ComponentName cn : installedPlugins) {
                    getPluginInfo().put(cn, getNewModObject(cn));
                    getPluginStatus(cn);
                }
            }
        }
    };

    public void getDisabledModInfo(ComponentName cn) {
        ArrayList<TypedPendingResult> apiCallbacks = new ArrayList<>();
        disabledModInfo(apiCallbacks, cn);
        executeAll(apiCallbacks, cn);
    }

    public void getEnabledModInfo(ComponentName cn) {
        ArrayList<TypedPendingResult> apiCallbacks = new ArrayList<>();
        enabledModInfo(apiCallbacks, cn);
        executeAll(apiCallbacks, cn);
    }

    public AmbientDataSubscription(Context context) {
        mContext = context;
        mClient = AmbientConnection.CLIENT.get(context);
        mMainHandler = new Handler(context.getMainLooper());
        mPluginInfo = new HashMap<>();
    }

    public interface PluginChanged<M> {
        void onChanged(HashMap<ComponentName, M> pluginInfo);
    }

    /**
     * OnPostResult is always called on the main thread (like onPostExecute in an asynctask)
     *
     * @param plugin The object that represents the plugin
     * @param result the result of the resultCallback
     * @param type the type defined by the TypedPendingResult that was sent to the apiexecutor
     */
    protected abstract void onPostResult(M plugin, Result result, int type);

    /**
     * Callback that gets the initial list of componentnames that are valid plugin for us to query
     *
     * @param result result of the initial call
     * @return list of componentnames
     */
    protected abstract List<ComponentName> getPluginComponents(Result result);

    /**
     * Gets the required queries for all our plugins
     *
     * @param componentName ComponentName of the plugin we will be querying
     */
    protected abstract void getPluginStatus(ComponentName componentName);

    /**
     * Gets the enabled queries for all our plugins
     *
     * @param queries ArrayList for us to add TypedPendingResults to
     * @param componentName ComponentName of the plugin we will be querying
     */
    protected abstract void enabledModInfo(ArrayList<TypedPendingResult> queries,
                                             ComponentName componentName);


    /**
     * Gets the disabled queries for all our plugins
     *
     * @param queries ArrayList for us to add TypedPendingResults to
     * @param componentName ComponentName of the plugin we will be querying
     */
    protected abstract void disabledModInfo(ArrayList<TypedPendingResult> queries,
            ComponentName componentName);

    /**
     * Action that takes place when a refresh is requested.
     */
    protected abstract void onRefreshRequested();

    /**
     * Called when dynamic items need to be refreshed. Dynamic items being things that may change
     * over time in a plugin.
     *
     * @param apiCallbacks callbacks to add to the queue
     * @param componentName of plugin that needs to be updated
     */
    protected abstract void onDynamicRefreshRequested(ArrayList<TypedPendingResult> apiCallbacks,
                                                      ComponentName componentName);

    /**
     * Methods to enable and disable listeners on a plugin
     *
     * @param plugin object that needs to be listened to.
     */
    protected abstract void enableListeners(M plugin);
    protected abstract void disableListeners(M plugin);

    /**
     * Gets a new plugin object
     *
     * @param componentName of the plugin
     * @return M object
     */
    protected abstract M getNewModObject(ComponentName componentName);

    private void enablePluginListeners(ComponentName cn) {
        if (!mEnabledListeners.containsKey(cn) || !mEnabledListeners.get(cn)) {
            M plugin = getPluginIfExists(cn);
            if (plugin != null) {
                enableListeners(plugin);
                mEnabledListeners.put(cn, true);
            }

        }
    }

    private void disablePluginListeners(ComponentName cn) {
        if (mEnabledListeners.containsKey(cn) && mEnabledListeners.get(cn)) {
            M plugin = getPluginIfExists(cn);
            disableListeners(plugin);
            mEnabledListeners.put(cn, false);
        }
    }

    /**
     * Broadcasts mModInfo to all registered clients on the Main thread.
     */
    public void broadcast() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (DEBUG) Log.d(TAG, "broadcast");
                for (PluginChanged client : mRegisteredClients.values()) {
                    client.onChanged(mPluginInfo);
                }
            }
        });
    }

    /***
     * Registers the client, on register returns boolean if plugin info is already collected
     * and the initial broadcast has been sent.
     *
     * @param id  unique string for the client
     * @param cmr client receiver
     * @return boolean isempty
     */
    public boolean subscribe(String id, PluginChanged cmr) {
        mRegisteredClients.put(id, cmr);
        if (DEBUG) Log.v("TAG", "subscribed: " + id);
        return mDataHasBeenBroadcastPreviously;
    }

    /**
     * Unsubscribes the client. All clients must unsubscribe when the client ends.
     *
     * @param id of the client to remove
     */
    public void unsubscribe(String id) {
        mRegisteredClients.remove(id);
        if (mRegisteredClients.isEmpty()) {
            for (ComponentName cn : mPluginInfo.keySet()) {
                disablePluginListeners(cn);
            }
        }
    }

    /**
     * Refreshes certain items that can constantly change.
     */
    public void refreshDynamicItems() {
        for (ComponentName cn : mPluginInfo.keySet()) {
            ArrayList<TypedPendingResult> apiCallbacks = new ArrayList<>();
            onDynamicRefreshRequested(apiCallbacks, cn);
            executeAll(apiCallbacks, cn);
        }
    }

    private void executeAll(final ArrayList<TypedPendingResult> apiCallbacks,
            final ComponentName componentName) {

        final ArrayList<PendingResult> pendingResults = new ArrayList<>();

        for (final TypedPendingResult pendingResult : apiCallbacks) {
            pendingResult.setResultCallback(new ResultCallback() {
                @Override
                public void onResult(Result result) {
                    pendingResults.remove(pendingResult.mPendingResult);
                    if (result == null) {
                        // Our plugin failed to make this call, log out what and why
                        Log.e(TAG, "Result from: " + componentName.getPackageName() + " failed");
                    } else {
                        if (result.getStatus().isSuccess()) {
                            M plugin = getPluginIfExists(componentName);
                            if (plugin != null) {
                                onPostResult(plugin, result, pendingResult.mType);
                            }

                            // check to see if our onPostResult removed the plugin.
                            if (!getPluginInfo().containsKey(componentName)) {
                                // Our plugin no longer exists for some reason, clear other callbacks
                                // and broadcast changes.
                                for (PendingResult pr : pendingResults) {
                                    pr.cancel();
                                }
                                pendingResults.clear();
                            }
                        } else if (result.getStatus().isCanceled()) {
                            // Our plugin was found invalid or no longer exists.
                            Log.e(TAG, "Queries to: " + componentName.getPackageName()
                                    + " are cancelled");
                        } else {
                            Log.e(TAG, "Query to: " + componentName.getPackageName() + " "
                                    + result.getClass().getSimpleName() + " failed. Code: " +
                                    result.getStatus().getStatusMessage());
                        }
                    }
                    maybeBroadcastToSubscribers(pendingResults);

                }
            }, TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);

            // Add items that we've already added to our queue for so we can cancel them if
            // need be. Set resultcallback will return instantly but onResult will not.
            // So we can use this as our "counter" to determine if we should broadcast
            pendingResults.add(pendingResult.mPendingResult);
        }
        enablePluginListeners(componentName);
    }

    public void refresh() {
        onRefreshRequested();
    }

    public boolean infoReady() {
        return mDataHasBeenBroadcastPreviously;
    }

    private void maybeBroadcastToSubscribers(ArrayList<PendingResult> apiCallbacks) {
        maybeBroadcastToSubscribers(apiCallbacks, false);
    }

    /**
     * Broadcast to subscribers once we know we've gathered all our data. Do not do this until we
     * have everything we need for sure.
     *
     * This method is called after every callback from ModCore. We will keep track of all of
     * the callbacks, once we have accounted for all callbacks from all plugins, we can go ahead
     * and update subscribers.
     *
     * @param apiCallbacks hashmap of our callbacks and pendingresults
     * @param critical marks this call as critical to shortcircut our general broadcast and
     *                 broadcast right away.
     */
    private void maybeBroadcastToSubscribers(ArrayList<PendingResult> apiCallbacks,
            boolean critical) {

        if (apiCallbacks.isEmpty() || critical)  {
            // we are on the last item or we are a critical item. broadcast updated hashmap
            broadcast();
            // We don't want to tell providers that our main data has already been broadcast
            // if it's just a critical broadcast to prevent extra work from our subscribers
            // once our long running calls are broadcast then this will be true.
            if (!critical) {
                mDataHasBeenBroadcastPreviously = true;
            }
        }
    }

    /**
     * In order to speed up the process we make calls for providers that may be invalid
     * To prevent this, make sure every resultcallback uses this before filling in the hashmap.
     * @param cn componentname
     * @return M if valid, otherwise null
     */
    public M getPluginIfExists(ComponentName cn) {
        return getPluginInfo().get(cn);
    }

    public HashMap<ComponentName, M> getPluginInfo() {
        return mPluginInfo;
    }
}
