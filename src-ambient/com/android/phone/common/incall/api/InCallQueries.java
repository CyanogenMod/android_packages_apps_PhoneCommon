package com.android.phone.common.incall.api;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
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
    public static void updateCallPlugins(Context context) {
        thisApi(context).getInstalledPlugins(getInstance(context).mClient).setResultCallback(
                getInstance(context).BOOTSTRAP);
    }

    /**
     * Get the plugin info
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodInfo(Context context,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi(context).getProviderInfo(getInstance(context).mClient,
                componentName), InCallTypedResult.NONE);
    }

    /**
     * Get our mod enabled status
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodStatus(Context context,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi(context).getPluginStatus(getInstance(context).mClient,
                componentName), InCallTypedResult.NONE);
    }

    /**
     * Get the mod mimetype
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodMimeType(Context context,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi(context).getCallableMimeType(
                getInstance(context).mClient, componentName), InCallTypedResult.GENERAL_MIME_TYPE);
    }

    /**
     * Get the plugin video mimetype
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodVideoCallableMimeType(Context context,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi(context).getVideoCallableMimeType(
                getInstance(context).mClient, componentName), InCallTypedResult.VIDEO_MIME_TYPE);
    }

    /**
     * Get the plugin im mime type
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodImMimeType(Context context,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi(context).getImMimeType(getInstance(context).mClient,
                componentName), InCallTypedResult.IM_MIME_TYPE);
    }

    /**
     * Get the Authentication state of the callmethod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodAuthenticated(Context context,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi(context).getAuthenticationState(
                getInstance(context).mClient, componentName), InCallTypedResult.NONE);
    }

    /**
     * Get the credits intent of the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getManageCreditsIntent(Context context,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi(context).getManageCreditsIntent(
                getInstance(context).mClient, componentName), InCallTypedResult.CREDIT_INTENT);
    }

    /**
     * Get the login intent of the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getLoginIntent(Context context, ComponentName componentName) {
        return new InCallTypedResult(thisApi(context).getLoginIntent(
                getInstance(context).mClient, componentName), InCallTypedResult.LOGIN_INTENT);
    }

    /**
     * Get the settings intent for the callmethod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getSettingsIntent(Context context,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi(context).getSettingsIntent(
                getInstance(context).mClient, componentName), InCallTypedResult.SETTINGS_INTENT);
    }

    /**
     * Get the credit info of the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCreditInfo(Context context, ComponentName componentName) {
        return new InCallTypedResult(thisApi(context).getCreditInfo(
                getInstance(context).mClient, componentName), InCallTypedResult.NONE);
    }

    /**
     * Get the account handle of the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getCallMethodAccountHandle(Context context,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi(context).getAccountHandle(getInstance(context).mClient,
                componentName), InCallTypedResult.NONE);
    }

    /**
     * Get the hint texts for the callmethod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getHintText(Context context, ComponentName componentName) {
        return new InCallTypedResult(thisApi(context).getHintText(getInstance(context).mClient,
                componentName), InCallTypedResult.NONE);
    }

    /**
     * Get the default search intent for the mod
     * @param componentName of mod
     *
     * @return InCallTypedResult to identify which field to modify in our CallMethodInfo object
     */
    public static InCallTypedResult getDefaultDirectorySearchIntent(Context context,
            ComponentName componentName) {
        return new InCallTypedResult(thisApi(context).getDirectorySearchIntent(
                getInstance(context).mClient, componentName, null),
                InCallTypedResult.DEFAULT_DIRECTORY_SEARCH_INTENT);
    }

    /**
     * Send analytics to our mod
     * @param context context
     * @param componentName Componentname of the mod to send to
     * @param event the event to send.
     */
    public static void shipAnalyticsToPlugin(Context context,
            ComponentName componentName, Event event) {
        if (componentName == null) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "componentName: " + componentName.toShortString()
                    + ":Event: " + event.toString());
        }
        thisApi(context).sendAnalyticsEventToPlugin(getInstance(context).mClient,
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
