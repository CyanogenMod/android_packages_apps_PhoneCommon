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

package com.android.phone.common.incall.api;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.phone.common.ambient.AmbientDataSubscription;
import com.android.phone.common.ambient.TypedPendingResult;
import com.android.phone.common.incall.CallMethodInfo;
import com.cyanogen.ambient.analytics.Event;
import com.cyanogen.ambient.common.api.AmbientApiClient;
import com.cyanogen.ambient.common.api.Result;
import com.cyanogen.ambient.common.api.ResultCallback;
import com.cyanogen.ambient.incall.extension.InCallContactInfo;

/**
 * Queries for incall plugins
 */
public class InCallQueries extends ApiHelper {

    private static final String TAG = InCallQueries.class.getSimpleName();
    private static final boolean DEBUG = false;

    /**
     * Prepare to query and fire off ModCore calls in all directions
     */
    public static void updateCallPlugins(AmbientDataSubscription<CallMethodInfo> instance) {
        thisApi().getInstalledPlugins(instance.mClient).setResultCallback(instance.BOOTSTRAP);
    }

    /**
     * Get the plugin info
     * @param componentName of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getCallMethodInfo(AmbientApiClient client,
            ComponentName componentName) {

        return new TypedPendingResult(thisApi().getProviderInfo(client, componentName),
                TypedPendingResult.GENERAL_DATA);
    }

    /**
     * Get our mod enabled status
     * @param componentName of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getCallMethodStatus(AmbientApiClient client,
            ComponentName componentName) {
        return new TypedPendingResult(thisApi().getPluginStatus(client, componentName),
                TypedPendingResult.STATUS);
    }

    /**
     * Get the mod mimetype
     * @param componentName of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getCallMethodMimeType(AmbientApiClient client,
            ComponentName componentName) {
        return new TypedPendingResult(thisApi().getCallableMimeType(client, componentName),
                TypedPendingResult.GENERAL_MIME_TYPE);
    }

    /**
     * Get the plugin video mimetype
     * @param componentName of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getCallMethodVideoCallableMimeType(AmbientApiClient client,
            ComponentName componentName) {
        return new TypedPendingResult(thisApi().getVideoCallableMimeType(client, componentName),
                TypedPendingResult.VIDEO_MIME_TYPE);
    }

    /**
     * Get the plugin im mime type
     * @param componentName of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getCallMethodImMimeType(AmbientApiClient client,
            ComponentName componentName) {
        return new TypedPendingResult(thisApi().getImMimeType(client, componentName),
                TypedPendingResult.IM_MIME_TYPE);
    }

    /**
     * Get the Authentication state of the callmethod
     * @param componentName of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getCallMethodAuthenticated(AmbientApiClient client,
            ComponentName componentName) {
        return new TypedPendingResult(thisApi().getAuthenticationState(client, componentName),
                TypedPendingResult.AUTHENTICATION);
    }

    /**
     * Get the credits intent of the mod
     * @param componentName of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getManageCreditsIntent(AmbientApiClient client,
            ComponentName componentName) {
        return new TypedPendingResult(thisApi().getManageCreditsIntent(client, componentName),
                TypedPendingResult.CREDIT_INTENT);
    }

    /**
     * Get the login intent of the mod
     * @param componentName of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getLoginIntent(AmbientApiClient client,
            ComponentName componentName) {
        return new TypedPendingResult(thisApi().getLoginIntent(client, componentName),
                TypedPendingResult.LOGIN_INTENT);
    }

    /**
     * Get the settings intent for the callmethod
     * @param componentName of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getSettingsIntent(AmbientApiClient client,
            ComponentName componentName) {
        return new TypedPendingResult(thisApi().getSettingsIntent(client, componentName),
                TypedPendingResult.SETTINGS_INTENT);
    }

    /**
     * Get the credit info of the mod
     * @param componentName of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getCreditInfo(AmbientApiClient client,
            ComponentName componentName) {
        return new TypedPendingResult(thisApi().getCreditInfo(client, componentName),
                TypedPendingResult.CREDIT_INFO);
    }

    /**
     * Get the account handle of the mod
     * @param componentName of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getCallMethodAccountHandle(AmbientApiClient client,
            ComponentName componentName) {
        return new TypedPendingResult(thisApi().getAccountHandle(client, componentName),
                TypedPendingResult.ACCOUNT_HANDLE);
    }

    /**
     * Get the hint texts for the callmethod
     * @param componentName of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getHintText(AmbientApiClient client,
            ComponentName componentName) {
        return new TypedPendingResult(thisApi().getHintText(client, componentName),
                TypedPendingResult.HINT_TEXT);
    }

    /**
     * Get the default search intent for the mod
     * @param componentName of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getDefaultDirectorySearchIntent(AmbientApiClient client,
            ComponentName componentName) {
        return new TypedPendingResult(thisApi().getDirectorySearchIntent(client, componentName,
                Uri.parse("")), TypedPendingResult.DEFAULT_DIRECTORY_SEARCH_INTENT);
    }

    /**
     * Send analytics to our mod
     * @param client ambient client
     * @param componentName Componentname of the mod to send to
     * @param event the event to send.
     */
    public static void shipAnalyticsToPlugin(AmbientApiClient client, ComponentName componentName,
            Event event) {
        if (componentName == null) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "componentName: " + componentName.toShortString()
                    + ":Event: " + event.toString());
        }
        thisApi().sendAnalyticsEventToPlugin(client, componentName, event)
                .setResultCallback(new ResultCallback<Result>() {
                    @Override
                    public void onResult(Result result) {
                        if (DEBUG) {
                            Log.v(TAG, "Event sent with result: " + result.getStatus()
                                    .getStatusMessage());
                        }
                    }
                });
    }

    public static TypedPendingResult getDirectorySearchIntent(AmbientApiClient client,
            ComponentName componentName, InCallContactInfo contactInfo) {
        return new TypedPendingResult(thisApi().getDirectorySearchIntent(client, componentName,
                contactInfo.mLookupUri), TypedPendingResult.DIRECTORY_SEARCH_INTENT);
    }

    public static TypedPendingResult getInviteIntent(AmbientApiClient client,
            ComponentName componentName, InCallContactInfo contactInfo) {
        return new TypedPendingResult(thisApi().getInviteIntent(client, componentName,
                contactInfo), TypedPendingResult.INVITE_INTENT);
    }
}
