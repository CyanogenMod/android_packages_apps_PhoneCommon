package com.android.phone.common.incall.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.phone.common.incall.CallMethodHelper;
import com.android.phone.common.incall.CallMethodInfo;
import com.android.phone.common.incall.utils.CallMethodUtils;
import com.cyanogen.ambient.discovery.results.BundleResult;
import com.cyanogen.ambient.discovery.util.NudgeKey;
import com.cyanogen.ambient.incall.extension.CreditBalance;
import com.cyanogen.ambient.incall.extension.CreditInfo;
import com.cyanogen.ambient.incall.extension.GetCreditInfoResult;
import com.cyanogen.ambient.incall.extension.HintTextResult;
import com.cyanogen.ambient.incall.extension.StatusCodes;
import com.cyanogen.ambient.incall.results.AccountHandleResult;
import com.cyanogen.ambient.incall.results.AuthenticationStateResult;
import com.cyanogen.ambient.incall.results.HintTextResultResult;
import com.cyanogen.ambient.incall.results.InCallProviderInfoResult;
import com.cyanogen.ambient.incall.results.InstalledPluginsResult;
import com.cyanogen.ambient.incall.results.MimeTypeResult;
import com.cyanogen.ambient.incall.results.PendingIntentResult;
import com.cyanogen.ambient.incall.results.PluginStatusResult;
import com.cyanogen.ambient.incall.util.InCallProviderInfo;

import java.util.ArrayList;
import java.util.List;

import static com.cyanogen.ambient.incall.util.InCallHelper.NO_COLOR;

/**
 * Class that handles writing results to our CallMethodInfo object
 */
public class InCallResults {

    private static final String TAG = InCallResults.class.getSimpleName();

    public static List<ComponentName> gotInstalledPlugins(InstalledPluginsResult result) {

        if (result.components == null) {
            new ArrayList<ComponentName>();
        }

        return result.components;
    }

    public static void gotStatus(CallMethodInfo callMethodInfo, PluginStatusResult result) {
        callMethodInfo.mStatus = result.status;
    }

    public static void gotMimeType(CallMethodInfo callMethodInfo, MimeTypeResult result, int type) {
        switch(type) {
            case InCallTypedResult.GENERAL_MIME_TYPE:
                callMethodInfo.mMimeType = result.mimeType;
                break;
            case InCallTypedResult.VIDEO_MIME_TYPE:
                callMethodInfo.mVideoCallableMimeType = result.mimeType;
                break;
            case InCallTypedResult.IM_MIME_TYPE:
                callMethodInfo.mImMimeType = result.mimeType;
                break;
        }
    }

    public static void gotIntent(CallMethodInfo callMethodInfo, PendingIntentResult result,
            int type) {
        switch(type) {
            case InCallTypedResult.LOGIN_INTENT:
                callMethodInfo.mLoginIntent = result.intent;
                break;
            case InCallTypedResult.CREDIT_INTENT:
                callMethodInfo.mManageCreditIntent = result.intent;
                break;
            case InCallTypedResult.SETTINGS_INTENT:
                callMethodInfo.mSettingsIntent = result.intent;
                break;
        }
    }

    public static void gotAuthenticationState(CallMethodInfo callMethodInfo,
                                              AuthenticationStateResult result) {
        callMethodInfo.mIsAuthenticated
                = result.result == StatusCodes.AuthenticationState.LOGGED_IN;
    }

    public static void gotAccountHandle(CallMethodInfo callMethodInfo, Context context,
                                        AccountHandleResult result) {
        callMethodInfo.mAccountHandle = result.accountHandle;
        if (TextUtils.isEmpty(callMethodInfo.mAccountHandle)) {
            callMethodInfo.mAccountHandle
                    = CallMethodUtils.lookupAccountHandle(context, callMethodInfo.mAccountType);
        }
    }

    public static void gotGeneralInfo(CallMethodInfo callMethodInfo, Context context,
                                      InCallProviderInfoResult result) {

        // Component was added on "getNewModObject"
        ComponentName componentName = callMethodInfo.mComponent;
        InCallProviderInfo icpi = result.inCallProviderInfo;
        if (icpi == null) {
            CallMethodHelper.INCALL.get(context).getModInfo().remove(componentName);
            return;
        }

        PackageManager packageManager = context.getPackageManager();
        Resources pluginResources;
        try {
            pluginResources
                    = packageManager.getResourcesForApplication(componentName.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Plugin isn't installed: " + componentName.flattenToShortString());
            CallMethodHelper.INCALL.get(context).getModInfo().remove(componentName);
            return;
        }

        try {
            callMethodInfo.mSingleColorBrandIcon
                    = pluginResources.getDrawable(icpi.getSingleColorBrandIcon());
            callMethodInfo.mActionOneIcon = pluginResources.getDrawable(icpi.getActionOneIcon());
            callMethodInfo.mActionTwoIcon = pluginResources.getDrawable(icpi.getActionTwoIcon());
            callMethodInfo.mBrandIcon = pluginResources.getDrawable(icpi.getBrandIcon());
            callMethodInfo.mLoginIcon = pluginResources.getDrawable(icpi.getLoginIcon());
            callMethodInfo.mVoiceIcon = pluginResources.getDrawable(icpi.getVoiceMimeIcon());
            callMethodInfo.mVideoIcon = pluginResources.getDrawable(icpi.getVideoMimeIcon());
            callMethodInfo.mImIcon = pluginResources.getDrawable(icpi.getImMimeIcon());
            callMethodInfo.mBadgeIcon = pluginResources.getDrawable(icpi.getBadgeIcon());
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Resource Not found: " + componentName.flattenToShortString());
            CallMethodHelper.INCALL.get(context).getModInfo().remove(componentName);
            return;
        }

        callMethodInfo.mSlotId = -1;
        callMethodInfo.mSubId = -1;
        callMethodInfo.mColor = NO_COLOR;
        callMethodInfo.mSubscriptionButtonText = icpi.getSubscriptionButtonText();
        callMethodInfo.mCreditButtonText = icpi.getCreditsButtonText();
        // If present, use the deprecated attribute defined hint text.
        // These values may be overwritten by getHintText.
        callMethodInfo.mT9HintDescriptionNoCreditOrSub = icpi.getT9HintDescription();
        callMethodInfo.mT9HintDescriptionHasCreditOrSub = icpi.getT9HintDescription();

        callMethodInfo.mActionOneText = icpi.getActionOneTitle();
        callMethodInfo.mActionTwoText = icpi.getActionTwoTitle();
        callMethodInfo.mIsInCallProvider = true;
        callMethodInfo.mDependentPackage = icpi.getDependentPackage();
        callMethodInfo.mName = icpi.getTitle();
        callMethodInfo.mSummary = icpi.getSummary();
        callMethodInfo.mAccountType = icpi.getAccountType();
        callMethodInfo.mAccountHandle = icpi.getAccountHandle();
        if (TextUtils.isEmpty(callMethodInfo.mAccountHandle)) {
            callMethodInfo.mAccountHandle
                    = CallMethodUtils.lookupAccountHandle(context, callMethodInfo.mAccountType);
        }
        callMethodInfo.mBrandIconId = icpi.getBrandIcon();
        callMethodInfo.mLoginIconId = icpi.getLoginIcon();
        callMethodInfo.mAccountType = icpi.getAccountType();
    }

    public static void gotCreditData(CallMethodInfo callMethodInfo, GetCreditInfoResult result) {
        if (result.creditInfo == null) {
            // Build zero credit dummy if no result found.
            callMethodInfo.mProviderCreditInfo = new CreditInfo(new CreditBalance(0, null), null);
        } else {
            callMethodInfo.mProviderCreditInfo = result.creditInfo;
        }
    }

    public static void gotHintText(CallMethodInfo callMethodInfo,
            HintTextResultResult resultResult) {
        HintTextResult result = resultResult.result;
        if (result != null) {
            String hintText = result.getNoCreditOrSubscriptionHint();
            if (!TextUtils.isEmpty(hintText)) {
                callMethodInfo.mT9HintDescriptionNoCreditOrSub = hintText;
            }
            hintText = result.getHasCreditOrSubscriptionHint();
            if (!TextUtils.isEmpty(hintText)) {
                callMethodInfo.mT9HintDescriptionHasCreditOrSub = hintText;
            }
        }
    }

    public static void updateCreditInfo(Context context, ComponentName name,
            GetCreditInfoResult result) {
        CallMethodInfo callMethodInfo = CallMethodHelper.INCALL.get(context).getModIfExists(name);
        if (callMethodInfo != null) {
            gotCreditData(callMethodInfo, result);
            CallMethodHelper.INCALL.get(context).getModInfo().put(name, callMethodInfo);

            // Since a CallMethodInfo object was updated here, we should let the subscribers know
            CallMethodHelper.INCALL.get(context).broadcast();
        }
    }

    public static void updateAuthenticationState(Context context, ComponentName name, int state) {
        CallMethodInfo callMethodInfo = CallMethodHelper.INCALL.get(context).getModIfExists(name);
        if (callMethodInfo != null) {
            callMethodInfo.mIsAuthenticated = state == StatusCodes.AuthenticationState.LOGGED_IN;
            CallMethodHelper.INCALL.get(context).getModInfo().put(name, callMethodInfo);

            // Since a CallMethodInfo object was updated here, we should let the subscribers know
            CallMethodHelper.INCALL.get(context).broadcast();
        }
    }

    public static void gotNudgeData(Context context, CallMethodInfo callMethodInfo,
            BundleResult result) {
        if (result.bundle == null) {
            return;
        }
        Bundle bundle = result.bundle;
        for (String key : bundle.keySet()) {
            switch (key) {
                case NudgeKey.INCALL_PARAM_CREDIT_WARN:
                    gotCreditWarn(callMethodInfo, bundle);
                    break;
                case NudgeKey.INCALL_CONTACT_FRAGMENT_LOGIN:
                    gotLoginSubtitle(callMethodInfo, bundle);
                    break;
                case NudgeKey.INCALL_CONTACT_CARD_LOGIN:
                    gotContactCardLogin(callMethodInfo, key, bundle, context);
                    break;
                case NudgeKey.INCALL_CONTACT_CARD_DOWNLOAD:
                    gotContactCardDownload(callMethodInfo, key, bundle, context);
                    break;
            }
        }
    }

    private static void gotLoginSubtitle(CallMethodInfo callMethodInfo, Bundle bundle) {
        callMethodInfo.mLoginSubtitle = bundle.getString(NudgeKey.NUDGE_PARAM_SUBTITLE);
    }

    private static void gotContactCardLogin(CallMethodInfo callMethodInfo, String key,
            Bundle bundle, Context c) {
        callMethodInfo.mLoginNudgeEnable = bundle.getBoolean(NudgeKey.NUDGE_PARAM_ENABLED, true)
                && PreferenceManager.getDefaultSharedPreferences(c).getBoolean(
                callMethodInfo.mComponent.getClassName() + "." + key, true);
        callMethodInfo.mLoginNudgeTitle = bundle.getString(NudgeKey.NUDGE_PARAM_TITLE);
        callMethodInfo.mLoginNudgeSubtitle = bundle.getString(NudgeKey.NUDGE_PARAM_SUBTITLE);
        callMethodInfo.mLoginNudgeActionText = bundle.getString(NudgeKey.NUDGE_PARAM_ACTION_TEXT);
    }

    private static void gotCreditWarn(CallMethodInfo callMethodInfo, Bundle bundle) {
        Object creditWarn = bundle.get(NudgeKey.INCALL_PARAM_CREDIT_WARN);
        if (creditWarn.getClass().equals(Integer.class)) {
            callMethodInfo.mCreditWarn = (Integer) creditWarn;
        } else if (creditWarn.getClass().equals(Float.class)) {
            callMethodInfo.mCreditWarn = (Float) creditWarn;
        } else {
            Log.e(TAG, "Invalid value for Credit Warn limit: " + creditWarn);
        }
    }

    private static void gotContactCardDownload(CallMethodInfo callMethodInfo, String key,
            Bundle bundle, Context context) {
        callMethodInfo.mInstallNudgeEnable = bundle.getBoolean(NudgeKey.NUDGE_PARAM_ENABLED, true)
                && PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                callMethodInfo.mComponent.getClassName() + "." + key, true);
        callMethodInfo.mInstallNudgeTitle = bundle.getString(NudgeKey.NUDGE_PARAM_TITLE);
        callMethodInfo.mInstallNudgeSubtitle = bundle.getString(NudgeKey.NUDGE_PARAM_SUBTITLE);
        callMethodInfo.mInstallNudgeActionText = bundle.getString(NudgeKey.NUDGE_PARAM_ACTION_TEXT);
    }
}
