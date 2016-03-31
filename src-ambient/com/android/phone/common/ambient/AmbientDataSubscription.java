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
 * Helper method for holding and broadcasting updated mod data
 *
 */
public abstract class AmbientDataSubscription<T, M> {

    private static final String TAG = AmbientDataSubscription.class.getSimpleName();
    private static final boolean DEBUG = false;

    // Our ambient client
    public AmbientApiClient mClient;
    public Context mContext;

    // The ambient api we're calling
    public T mAmbientAPI;

    // The handler that processes broadcasts
    private Handler mMainHandler;

    // Holder for mod object
    private HashMap<ComponentName, M> mModInfo;

    private static boolean dataHasBeenBroadcastPreviously = false;

    // A list of our registered clients, who all register with the CallMethodReceiver
    private static HashMap<String, ModChanged> mRegisteredClients = new HashMap<>();

    // A map of if our components have enabled IInterface listeners for keeping track of if they
    // should be added or removed.
    private static HashMap<ComponentName, Boolean> mEnabledListeners = new HashMap<>();

    // Wait up to two seconds for data to return.
    private static final long TIMEOUT_MILLISECONDS = 2000L;

    // Bootstrap is the initial callback to get your installed mods.
    // this is the only item that executes ASAP and has no componentname tied to it
    public ResultCallback BOOTSTRAP = new ResultCallback<Result>() {

        @Override
        public void onResult(Result result) {
            List<ComponentName> installedPlugins = getPluginComponents(result);
            for (ComponentName cn : installedPlugins) {
                ArrayList<TypedPendingResult>  apiCallbacks = new ArrayList<>();
                getModInfo().put(cn, getNewModObject(cn));
                requestedModInfo(apiCallbacks, cn);
                executeAll(apiCallbacks, cn);
            }
        }

    };

    public interface ModChanged<M> {
        void onChanged(HashMap<ComponentName, M> modInfos);
    }

    /**
     * OnPostResult is always called on the main thread (like onPostExecute in an asynctask)
     * @param mod The object that represents the mod
     * @param result the result of the resultCallback
     * @param type the type defined by the TypedPendingResult that was sent to the apiexecutor
     */
    protected abstract void onPostResult(M mod, Result result, int type);

    /**
     * Callback that gets the initial list of componentnames that are valid mods for us to query
     * @param result result of the initial call
     * @return list of componentnames
     */
    protected abstract List<ComponentName> getPluginComponents(Result result);

    /**
     * Gets the required queries for all our mods
     * @param queries ArrayList for us to add TypedPendingResults to
     * @param componentName ComponentName of the mod we will be querying
     */
    protected abstract void requestedModInfo(ArrayList<TypedPendingResult> queries,
                                             ComponentName componentName);


    /**
     * Action that takes place when a refresh is requested.
     */
    protected abstract void onRefreshRequested();

    /**
     * Called when dynamic items need to be refreshed
     * @param apiCallbacks callbacks to add to the queue
     * @param componentName of mod that needs to be updated
     */
    protected abstract void onDynamicRefreshRequested(ArrayList<TypedPendingResult> apiCallbacks,
                                                      ComponentName componentName);

    /**
     * Methods to enable and disable plugins
     * @param mod object that needs to be listened to.
     */
    protected abstract void enableListeners(M mod);
    protected abstract void disableListeners(M mod);

    /**
     * Gets a new mod object
     * @param componentName of the mod
     * @return M object
     */
    protected abstract M getNewModObject(ComponentName componentName);

    public AmbientDataSubscription(Context context, T api) {
        mContext = context;
        mClient = AmbientConnection.CLIENT.get(context);
        mAmbientAPI = api;
        mMainHandler = new Handler(context.getMainLooper());
        mModInfo = new HashMap<>();
    }

    private void enableModListeners(ComponentName cn) {
        if (!mEnabledListeners.containsKey(cn) || !mEnabledListeners.get(cn)) {
            M mod = getModIfExists(cn);
            if (mod != null) {
                enableListeners(mod);
                mEnabledListeners.put(cn, true);
            }

        }
    }

    private void disableModListeners(ComponentName cn) {
        if (mEnabledListeners.containsKey(cn) || mEnabledListeners.get(cn)) {
            M mod = getModIfExists(cn);
            disableListeners(mod);
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
                for (ModChanged client : mRegisteredClients.values()) {
                    client.onChanged(mModInfo);
                }
            }
        });
    }

    /***
     * Registers the client, on register returns boolean if
     * Mod info is already collected and the initial broadcast has been sent.
     *
     * @param id  unique string for the client
     * @param cmr client receiver
     * @return boolean isempty
     */
    public static boolean subscribe(String id, ModChanged cmr) {
        mRegisteredClients.put(id, cmr);
        if (DEBUG) Log.v("TAG", "subscribed: " + id);
        return dataHasBeenBroadcastPreviously;
    }

    /**
     * Unsubscribes the client. All clients must unsubscribe when the client ends.
     *
     * @param id of the client to remove
     */
    public void unsubscribe(String id) {
        mRegisteredClients.remove(id);
        if (mRegisteredClients.isEmpty()) {
            for (ComponentName cn : mModInfo.keySet()) {
                disableModListeners(cn);
            }
        }
    }

    /**
     * Refreshes certain items that can constantly change.
     */
    public void refreshDynamicItems() {
        for (ComponentName cn : mModInfo.keySet()) {
            ArrayList<TypedPendingResult> apiCallbacks = new ArrayList<>();
            onDynamicRefreshRequested(apiCallbacks, cn);
            executeAll(apiCallbacks, cn);
        }
    }

    private void executeAll(final ArrayList<TypedPendingResult> apiCallbacks,
                           final ComponentName componentName) {

        final ArrayList<PendingResult> pendingResults = new ArrayList<>();

        for (final TypedPendingResult pendingResult : apiCallbacks) {
            pendingResult.mPendingResult.setResultCallback(new ResultCallback() {
                @Override
                public void onResult(Result result) {
                    if (result == null) {
                        // Our mod failed to make this call, log out what and why
                        Log.v(TAG, "Result from: " + componentName.getPackageName() + " failed");
                        pendingResults.remove(pendingResult.mPendingResult);
                        maybeBroadcastToSubscribers(pendingResults);
                        return;
                    }
                    if (result.getStatus().isSuccess()) {
                        M mod = getModIfExists(componentName);
                        onPostResult(mod, result, pendingResult.mType);

                        // check to see if our onPostResult removed the mod.
                        if (getModIfExists(componentName) == null) {
                            // Our mod no longer exists for some reason, clear other callbacks
                            // and broadcast changes.
                            for (PendingResult pr : pendingResults) {
                                pr.cancel();
                            }
                            pendingResults.clear();
                            maybeBroadcastToSubscribers(pendingResults);
                            return;
                        }
                    } else if (result.getStatus().isCanceled()) {
                        // Our mod was found invalid or no longer exists.
                        Log.e(TAG, "Queries to: " + componentName.getPackageName()
                                + " are cancelled");
                    } else {
                        Log.e(TAG, "Query to: " + componentName.getPackageName() + " "
                                + result.getClass().getSimpleName() + " failed");
                    }
                    pendingResults.remove(pendingResult.mPendingResult);
                    maybeBroadcastToSubscribers(pendingResults);

                }
            }, TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);

            // Add items that we've already added to our queue for so we can cancel them if
            // need be. Set resultcallback will return instantly but onResult will not.
            // So we can use this as our "counter" to determine if we should broadcast
            pendingResults.add(pendingResult.mPendingResult);
        }
        enableModListeners(componentName);
    }

    public void refresh() {
        onRefreshRequested();
    }

    public static boolean infoReady() {
        return dataHasBeenBroadcastPreviously;
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
    private void maybeBroadcastToSubscribers(
            ArrayList<PendingResult> apiCallbacks, boolean critical) {

        if (apiCallbacks.isEmpty() || critical)  {
            // we are on the last item or we are a critical item. broadcast updated hashmap
            broadcast();
            // We don't want to tell providers that our main data has already been broadcast
            // if it's just a critical broadcast to prevent extra work from our subscribers
            // once our long running calls are broadcast then this will be true.
            if (!critical) {
                dataHasBeenBroadcastPreviously = true;
            }
        }
    }

    /**
     * In order to speed up the process we make calls for providers that may be invalid
     * To prevent this, make sure every resultcallback uses this before filling in the hashmap.
     * @param cn componentname
     * @return M if valid, otherwise null
     */
    public M getModIfExists(ComponentName cn) {
        if (getModInfo().containsKey(cn)) {
            return getModInfo().get(cn);
        } else {
            return null;
        }
    }

    public HashMap<ComponentName, M> getModInfo() {
        return mModInfo;
    }
}
