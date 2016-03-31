package com.android.phone.common.incall.api;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.android.phone.common.ambient.AmbientDataSubscription;
import com.android.phone.common.incall.CallMethodInfo;
import com.cyanogen.ambient.analytics.Event;
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
        thisApi().getInstalledPlugins(instance.mClient).setResultCallback(
                instance.BOOTSTRAP);
    }

    /**
     * Get the plugin info
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodInfo(
            AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName) {

        return new InCallTypedResult(thisApi().getProviderInfo(instance.mClient,
                componentName), InCallTypedResult.GENERAL_DATA);
    }

    /**
     * Get our mod enabled status
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodStatus(
            AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getPluginStatus(instance.mClient,
                componentName), InCallTypedResult.STATUS);
    }

    /**
     * Get the mod mimetype
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodMimeType(
            AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getCallableMimeType(
                instance.mClient, componentName), InCallTypedResult.GENERAL_MIME_TYPE);
    }

    /**
     * Get the plugin video mimetype
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodVideoCallableMimeType(
            AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getVideoCallableMimeType(
                instance.mClient, componentName), InCallTypedResult.VIDEO_MIME_TYPE);
    }

    /**
     * Get the plugin im mime type
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodImMimeType(
            AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getImMimeType(instance.mClient,
                componentName), InCallTypedResult.IM_MIME_TYPE);
    }

    /**
     * Get the Authentication state of the callmethod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodAuthenticated(
            AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getAuthenticationState(
                instance.mClient, componentName), InCallTypedResult.AUTHENTICATION);
    }

    /**
     * Get the credits intent of the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getManageCreditsIntent(
            AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getManageCreditsIntent(
                instance.mClient, componentName), InCallTypedResult.CREDIT_INTENT);
    }

    /**
     * Get the login intent of the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getLoginIntent(AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getLoginIntent(
                instance.mClient, componentName), InCallTypedResult.LOGIN_INTENT);
    }

    /**
     * Get the settings intent for the callmethod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getSettingsIntent(
            AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getSettingsIntent(
                instance.mClient, componentName), InCallTypedResult.SETTINGS_INTENT);
    }

    /**
     * Get the credit info of the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCreditInfo(AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getCreditInfo(
                instance.mClient, componentName), InCallTypedResult.CREDIT_INFO);
    }

    /**
     * Get the account handle of the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodAccountHandle(
            AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getAccountHandle(instance.mClient,
                componentName), InCallTypedResult.ACCOUNT_HANDLE);
    }

    /**
     * Get the hint texts for the callmethod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getHintText(AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getHintText(instance.mClient,
                componentName), InCallTypedResult.HINT_TEXT);
    }

    /**
     * Get the default search intent for the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getDefaultDirectorySearchIntent(
            AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi().getDirectorySearchIntent(
                instance.mClient, componentName, null),
                InCallTypedResult.DEFAULT_DIRECTORY_SEARCH_INTENT);
    }

    /**
     * Send analytics to our mod
     * @param instance of the data
     * @param componentName Componentname of the mod to send to
     * @param event the event to send.
     */
    public static void shipAnalyticsToPlugin(AmbientDataSubscription<CallMethodInfo> instance,
            ComponentName componentName, Event event) {
        if (componentName == null) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "componentName: " + componentName.toShortString()
                    + ":Event: " + event.toString());
        }
        thisApi().sendAnalyticsEventToPlugin(instance.mClient,
                componentName, event).setResultCallback(new ResultCallback<Result>() {
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
