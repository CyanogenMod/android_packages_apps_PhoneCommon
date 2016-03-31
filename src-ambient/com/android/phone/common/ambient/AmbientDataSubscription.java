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
import com.cyanogen.ambient.common.api.Result;
import com.cyanogen.ambient.common.api.ResultCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    // Bootstrap is the initial callback to get your installed mods.
    // this is the only item that executes ASAP and has no componentname tied to it
    public ResultCallback BOOTSTRAP = new ResultCallback<Result>() {

        @Override
        public void onResult(Result result) {
            List<ComponentName> installedPlugins = onBootstrapReady(result);

            for (ComponentName cn : installedPlugins) {
                ArrayList<TypedPendingResult>  apiCallbacks = new ArrayList<>();
                getModInfo().put(cn, getNewModObject(cn));
                requestedModInfo(apiCallbacks, cn, mContext);
                executeAll(apiCallbacks, cn);
            }
        }

    };

    public interface ModChanged<M> {
        void onChanged(HashMap<ComponentName, M> modInfos);
    }

    protected abstract void onPostResult(M mod, ComponentName componentName, Result result,
                                         int type);

    protected abstract List<ComponentName> onBootstrapReady(Result result);

    protected abstract void requestedModInfo(ArrayList<TypedPendingResult> queries,
                                             ComponentName componentName, Context context);

    protected abstract void onRefreshRequested();

    protected abstract void onDynamicRefreshRequested(Context context,
            ArrayList<TypedPendingResult> apiCallbacks, ComponentName cn);

    protected abstract void enableListeners(Context context, M mod);
    protected abstract void disableListeners(Context context, M mod);

    protected abstract M getNewModObject(ComponentName componentName);

    private void enableModListeners(ComponentName cn) {
        if (!mEnabledListeners.containsKey(cn) || !mEnabledListeners.get(cn)) {
            M mod = verifyModExistance(cn);
            if (mod != null) {
                enableListeners(mContext, mod);
                mEnabledListeners.put(cn, true);
            }

        }
    }

    private void disableModListeners(ComponentName cn) {
        if (mEnabledListeners.containsKey(cn) || mEnabledListeners.get(cn)) {
            M mod = verifyModExistance(cn);
            disableListeners(mContext, mod);
            mEnabledListeners.put(cn, false);
        }
    }

    /**
     * Broadcasts mCallMethodInfos to all registered clients on the Main thread.
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
     * callMethodInfo data is already collected and the initial broadcast has been sent.
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
     * Unsubscribes the client. All clients should unsubscribe when they are removed.
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
     *
     */
    public void refreshDynamicItems() {
        for (ComponentName cn : mModInfo.keySet()) {
            ArrayList<TypedPendingResult> apiCallbacks = new ArrayList<>();
            onDynamicRefreshRequested(mContext, apiCallbacks, cn);
            executeAll(apiCallbacks, cn);
        }
    }

    public AmbientDataSubscription(Context context, T api) {
        mContext = context;
        mClient = AmbientConnection.CLIENT.get(context);
        mAmbientAPI = api;
        mMainHandler = new Handler(context.getMainLooper());
        mModInfo = new HashMap<>();
    }

    private void executeAll(final ArrayList<TypedPendingResult> apiCallbacks,
                           final ComponentName componentName) {
        for (final TypedPendingResult pendingResult : apiCallbacks) {
            pendingResult.mPendingResult.setResultCallback(new ResultCallback() {
                @Override
                public void onResult(Result result) {
                    M mod = verifyModExistance(componentName);
                    onPostResult(mod, componentName, result, pendingResult.mType);
                    apiCallbacks.remove(pendingResult);
                    if (verifyModExistance(componentName) != null) {
                        maybeBroadcastToSubscribers(apiCallbacks);
                    } else {
                        // Our mod no longer exists for some reason, clear other callbacks
                        apiCallbacks.clear();
                    }

                }
            });
        }
        enableModListeners(componentName);
    }

    public void refresh() {
        onRefreshRequested();
    }

    public static boolean infoReady() {
        return dataHasBeenBroadcastPreviously;
    }

    private void maybeBroadcastToSubscribers(ArrayList<TypedPendingResult> apiCallbacks) {
        maybeBroadcastToSubscribers(apiCallbacks, false);
    }

    /**
     * Broadcast to subscribers once we know we've gathered all our data. Do not do this until we
     * have everything we need for sure.
     *
     * This method is called after every callback from AmbientCore. We will keep track of all of
     * the callbacks, once we have accounted for all callbacks from all plugins, we can go ahead
     * and update subscribers.
     *
     * @param apiCallbacks hashmap of our callbacks and pendingresults
     * @param critical marks this call as critical to shortcircut our general broadcast and
     *                 broadcast right away.
     */
    private void maybeBroadcastToSubscribers(
            ArrayList<TypedPendingResult> apiCallbacks, boolean critical) {

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
     * @return R if valid, otherwise null
     */
    public M getModIfExists(ComponentName cn) {
        if (getModInfo().containsKey(cn)) {
            return getModInfo().get(cn);
        } else {
            return null;
        }
    }

    private M verifyModExistance(ComponentName componentName) {
        M mod = getModIfExists(componentName);
        if (mod == null) {
            // TODO: cancel new calls
            return null;
        }
        return mod;
    }

    public HashMap<ComponentName, M> getModInfo() {
        return mModInfo;
    }
}
