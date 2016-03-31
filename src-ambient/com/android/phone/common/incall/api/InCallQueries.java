package com.android.phone.common.incall.api;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.android.phone.common.ambient.AmbientDataSubscription;
import com.android.phone.common.incall.CallMethodInfo;
import com.cyanogen.ambient.analytics.Event;
import com.cyanogen.ambient.common.api.AmbientApiClient;
import com.cyanogen.ambient.common.api.Result;
import com.cyanogen.ambient.common.api.ResultCallback;

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
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodInfo(AmbientApiClient client,
            ComponentName componentName) {

        return new InCallTypedResult(thisApi().getProviderInfo(client, componentName),
                InCallTypedResult.GENERAL_DATA);
    }

    /**
     * Get our mod enabled status
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodStatus(AmbientApiClient client,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getPluginStatus(client, componentName),
                InCallTypedResult.STATUS);
    }

    /**
     * Get the mod mimetype
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodMimeType(AmbientApiClient client,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getCallableMimeType(client, componentName),
                InCallTypedResult.GENERAL_MIME_TYPE);
    }

    /**
     * Get the plugin video mimetype
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodVideoCallableMimeType(AmbientApiClient client,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getVideoCallableMimeType(client, componentName),
                InCallTypedResult.VIDEO_MIME_TYPE);
    }

    /**
     * Get the plugin im mime type
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodImMimeType(
            AmbientApiClient client,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getImMimeType(client,
                componentName), InCallTypedResult.IM_MIME_TYPE);
    }

    /**
     * Get the Authentication state of the callmethod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodAuthenticated(AmbientApiClient client,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getAuthenticationState(client, componentName),
                InCallTypedResult.AUTHENTICATION);
    }

    /**
     * Get the credits intent of the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getManageCreditsIntent(AmbientApiClient client,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getManageCreditsIntent(client, componentName),
                InCallTypedResult.CREDIT_INTENT);
    }

    /**
     * Get the login intent of the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getLoginIntent(AmbientApiClient client,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getLoginIntent(client, componentName),
                InCallTypedResult.LOGIN_INTENT);
    }

    /**
     * Get the settings intent for the callmethod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getSettingsIntent(AmbientApiClient client,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getSettingsIntent(client, componentName),
                InCallTypedResult.SETTINGS_INTENT);
    }

    /**
     * Get the credit info of the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCreditInfo(AmbientApiClient client,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getCreditInfo(client, componentName),
                InCallTypedResult.CREDIT_INFO);
    }

    /**
     * Get the account handle of the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodAccountHandle(AmbientApiClient client,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getAccountHandle(client, componentName),
                InCallTypedResult.ACCOUNT_HANDLE);
    }

    /**
     * Get the hint texts for the callmethod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getHintText(AmbientApiClient client,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getHintText(client, componentName),
                InCallTypedResult.HINT_TEXT);
    }

    /**
     * Get the default search intent for the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getDefaultDirectorySearchIntent(AmbientApiClient client,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getDirectorySearchIntent(client, componentName,
                null), InCallTypedResult.DEFAULT_DIRECTORY_SEARCH_INTENT);
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
}
