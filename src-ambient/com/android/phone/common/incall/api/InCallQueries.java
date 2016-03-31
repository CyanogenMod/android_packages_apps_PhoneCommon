package com.android.phone.common.incall.api;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import com.android.phone.common.ambient.TypedPendingResult;
import com.android.phone.common.incall.CallMethodHelper;
import com.android.phone.common.nudge.NudgeUtils;
import com.android.phone.common.nudge.api.NudgeQueries;
import com.cyanogen.ambient.analytics.Event;
import com.cyanogen.ambient.common.api.Result;
import com.cyanogen.ambient.common.api.ResultCallback;
import com.cyanogen.ambient.incall.InCallApi;

/**
 * Created by bird on 4/6/16.
 */
public class InCallQueries {

    private static final String TAG = InCallQueries.class.getSimpleName();
    private static final boolean DEBUG = true;

    private static CallMethodHelper getInstance(Context c) {
        return CallMethodHelper.INCALL.get(c);
    }

    public static InCallApi thisApi(Context c) {
        return getInstance(c).mAmbientAPI;
    }

    /**
     * Prepare to query and fire off ModCore calls in all directions
     */
    public static void updateCallPlugins(Context c) {
        thisApi(c).getInstalledPlugins(getInstance(c).mClient).setResultCallback(
                getInstance(c).BOOTSTRAP);
    }

    /**
     * Get the plugin info
     * @param cn
     */
    public static InCallTypedResult getCallMethodInfo(Context c, ComponentName cn) {
        return new InCallTypedResult(thisApi(c).getProviderInfo(getInstance(c).mClient, cn),
                InCallTypedResult.NONE);
    }

    /**
     * Get our plugin enabled status
     * @param cn
     */
    public static InCallTypedResult getCallMethodStatus(Context c, ComponentName cn) {
        return new InCallTypedResult(thisApi(c).getPluginStatus(getInstance(c).mClient, cn),
                InCallTypedResult.NONE);
    }

    /**
     * Get the call method mime type
     * @param cn
     */
    public static InCallTypedResult getCallMethodMimeType(Context c, ComponentName cn) {
        return new InCallTypedResult(thisApi(c).getCallableMimeType(getInstance(c).mClient, cn),
                InCallTypedResult.GENERAL_MIME_TYPE);
    }

    /**
     * Get the call method mime type
     * @param cn
     */
    public static InCallTypedResult getCallMethodVideoCallableMimeType(Context c, ComponentName cn) {
        return new InCallTypedResult(thisApi(c).getVideoCallableMimeType(getInstance(c).mClient, cn),
                InCallTypedResult.VIDEO_MIME_TYPE);
    }

    /**
     * Get the IM mime type
     * @param cn
     */
    public static InCallTypedResult getCallMethodImMimeType(Context c, ComponentName cn) {
        return new InCallTypedResult(thisApi(c).getImMimeType(getInstance(c).mClient, cn),
                InCallTypedResult.IM_MIME_TYPE);
    }

    /**
     * Get the Authentication state of the callmethod
     * @param cn
     */
    public static InCallTypedResult getCallMethodAuthenticated(Context c, ComponentName cn) {
        return new InCallTypedResult(thisApi(c).getAuthenticationState(getInstance(c).mClient, cn),
                InCallTypedResult.NONE);
    }

    public static InCallTypedResult getManageCreditsIntent(Context c, ComponentName cn) {
        return new InCallTypedResult(thisApi(c).getManageCreditsIntent(getInstance(c).mClient, cn),
                InCallTypedResult.CREDIT_INTENT);
    }

    public static InCallTypedResult getLoginIntent(Context c, ComponentName cn) {
        return new InCallTypedResult(thisApi(c).getLoginIntent(getInstance(c).mClient, cn),
                InCallTypedResult.LOGIN_INTENT);
    }

    public static InCallTypedResult getCreditInfo(Context c, ComponentName cn) {
        return new InCallTypedResult(thisApi(c).getCreditInfo(getInstance(c).mClient, cn),
                InCallTypedResult.NONE);
    }

    /**
     * Get the logged in account handle of the callmethod
     * @param cn
     */
    public static InCallTypedResult getCallMethodAccountHandle(Context c, ComponentName cn) {
        return new InCallTypedResult(thisApi(c).getAccountHandle(getInstance(c).mClient, cn),
                InCallTypedResult.NONE);
    }

    /**
     * Get the settings intent for the callmethod
     * @param cn
     */
    public static InCallTypedResult getSettingsIntent(Context c, ComponentName cn) {
        return new InCallTypedResult(thisApi(c).getAccountHandle(getInstance(c).mClient, cn),
                InCallTypedResult.NONE);
    }

    public static InCallTypedResult getDefaultDirectorySearchIntent(Context c, ComponentName cn) {
        return new InCallTypedResult(thisApi(c).getDirectorySearchIntent(
                getInstance(c).mClient, cn, null),
                InCallTypedResult.DEFAULT_DIRECTORY_SEARCH_INTENT);
    }

    public static TypedPendingResult getNudgeConfig(Context c, ComponentName cn, String key) {

        ComponentName nudgeComponent = NudgeUtils.getNudgeComponent(c, cn);
        if (nudgeComponent != null) {
            return NudgeQueries.getNudgeForKey(getInstance(c).mClient, cn, key);
        }
        return null;
    }

    public static void shipAnalyticsToPlugin(Context c, final ComponentName cn, Event e) {
        if (cn == null) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "componentName: " + cn.toShortString());
            Log.d(TAG, "Event: " + e.toString());
        }
        thisApi(c).sendAnalyticsEventToPlugin(getInstance(c).mClient, cn, e)
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
