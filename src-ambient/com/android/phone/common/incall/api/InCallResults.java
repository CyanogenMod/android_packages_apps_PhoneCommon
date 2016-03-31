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
import com.cyanogen.ambient.incall.extension.StatusCodes;
import com.cyanogen.ambient.incall.results.AccountHandleResult;
import com.cyanogen.ambient.incall.results.AuthenticationStateResult;
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
 * Created by bird on 4/6/16.
 */
public class InCallResults {

    private static final String TAG = "TAG";

    public static List<ComponentName> gotInstalledPlugins(
            InstalledPluginsResult installedPluginsResult) {

        if (installedPluginsResult.components == null) {
            new ArrayList<ComponentName>();
        }

        return installedPluginsResult.components;
    }

    public static void gotStatus(CallMethodInfo cmi, PluginStatusResult pluginStatusResult) {
        cmi.mStatus = pluginStatusResult.status;
    }

    public static void gotMimeType(CallMethodInfo cmi, MimeTypeResult mimeTypeResult, int type) {
        switch(type) {
            case InCallTypedResult.GENERAL_MIME_TYPE:
                cmi.mMimeType = mimeTypeResult.mimeType;
                break;
            case InCallTypedResult.VIDEO_MIME_TYPE:
                cmi.mVideoCallableMimeType = mimeTypeResult.mimeType;
                break;
            case InCallTypedResult.IM_MIME_TYPE:
                cmi.mImMimeType = mimeTypeResult.mimeType;
                break;
        }
    }

    public static void gotIntent(CallMethodInfo cmi, PendingIntentResult pendingIntentResult,
                                 int type) {
        switch(type) {
            case InCallTypedResult.LOGIN_INTENT:
                cmi.mLoginIntent = pendingIntentResult.intent;
                break;
            case InCallTypedResult.CREDIT_INTENT:
                cmi.mManageCreditIntent = pendingIntentResult.intent;
                break;
            case InCallTypedResult.SETTINGS_INTENT:
                cmi.mSettingsIntent = pendingIntentResult.intent;
                break;
        }
    }

    public static void gotAuthenticationState(CallMethodInfo cmi,
                                              AuthenticationStateResult result) {
        cmi.mIsAuthenticated = result.result == StatusCodes.AuthenticationState.LOGGED_IN;
    }

    public static void gotAccountHandle(CallMethodInfo cmi, Context context,
                                        AccountHandleResult result) {
        cmi.mAccountHandle = result.accountHandle;
        if (TextUtils.isEmpty(cmi.mAccountHandle)) {
            cmi.mAccountHandle = CallMethodUtils.lookupAccountHandle(context, cmi.mAccountType);
        }
    }

    public static void gotGeneralInfo(CallMethodInfo cmi, ComponentName cn, Context context,
                                      InCallProviderInfoResult inCallProviderInfoResult) {

        InCallProviderInfo icpi = inCallProviderInfoResult.inCallProviderInfo;
        if (icpi == null) {
            CallMethodHelper.INCALL.get(context).getModInfo().remove(cn);
            return;
        }

        PackageManager packageManager = context.getPackageManager();
        Resources pluginResources;
        try {
            pluginResources = packageManager.getResourcesForApplication(cn.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Plugin isn't installed: " + cn);
            CallMethodHelper.INCALL.get(context).getModInfo().remove(cn);
            return;
        }

        try {
            cmi.mSingleColorBrandIcon = pluginResources.getDrawable(icpi.getSingleColorBrandIcon());
            cmi.mActionOneIcon = pluginResources.getDrawable(icpi.getActionOneIcon());
            cmi.mActionTwoIcon = pluginResources.getDrawable(icpi.getActionTwoIcon());
            cmi.mBrandIcon = pluginResources.getDrawable(icpi.getBrandIcon());
            cmi.mLoginIcon = pluginResources.getDrawable(icpi.getLoginIcon());
            cmi.mVoiceIcon = pluginResources.getDrawable(icpi.getVoiceMimeIcon());
            cmi.mVideoIcon = pluginResources.getDrawable(icpi.getVideoMimeIcon());
            cmi.mImIcon = pluginResources.getDrawable(icpi.getImMimeIcon());
            cmi.mBadgeIcon = pluginResources.getDrawable(icpi.getBadgeIcon());

        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Resource Not found: " + cn);
            CallMethodHelper.INCALL.get(context).getModInfo().remove(cn);
            return;
        }

        cmi.mSlotId = -1;
        cmi.mSubId = -1;
        cmi.mColor = NO_COLOR;
        cmi.mSubscriptionButtonText = icpi.getSubscriptionButtonText();
        cmi.mCreditButtonText = icpi.getCreditsButtonText();
        //cmi.mT9HintDescription = icpi.getT9HintDescription();
        cmi.mActionOneText = icpi.getActionOneTitle();
        cmi.mActionTwoText = icpi.getActionTwoTitle();
        cmi.mIsInCallProvider = true;

        cmi.mComponent = cn;
        cmi.mNudgeComponent = icpi.getNudgeComponent() == null ? null :
                ComponentName.unflattenFromString(icpi.getNudgeComponent());
        cmi.mDependentPackage = icpi.getDependentPackage();
        cmi.mName = icpi.getTitle();
        cmi.mSummary = icpi.getSummary();
        cmi.mAccountType = icpi.getAccountType();
        cmi.mAccountHandle = icpi.getAccountHandle();
        if (TextUtils.isEmpty(cmi.mAccountHandle)) {
            cmi.mAccountHandle = CallMethodUtils.lookupAccountHandle(context, cmi.mAccountType);
        }
        cmi.mBrandIconId = icpi.getBrandIcon();
        cmi.mLoginIconId = icpi.getLoginIcon();
        cmi.mAccountType = icpi.getAccountType();
    }

    public static void gotCreditData(CallMethodInfo cmi, GetCreditInfoResult gcir) {
        if (gcir == null || gcir.creditInfo == null) {
            // Build zero credit dummy if no result found.
            cmi.mProviderCreditInfo = new CreditInfo(new CreditBalance(0, null), null);
        } else {
            cmi.mProviderCreditInfo = gcir.creditInfo;
        }
    }

    public static void updateCreditInfo(Context c, ComponentName name, GetCreditInfoResult gcir) {
        CallMethodInfo cmi = CallMethodHelper.INCALL.get(c).getModIfExists(name);
        if (cmi != null) {
            gotCreditData(cmi, gcir);
            CallMethodHelper.INCALL.get(c).getModInfo().put(name, cmi);

            // Since a CallMethodInfo object was updated here, we should let the subscribers know
            CallMethodHelper.INCALL.get(c).broadcast();
        }
    }

    public static void updateAuthenticationState(Context c, ComponentName name, int state) {
        CallMethodInfo cmi = CallMethodHelper.INCALL.get(c).getModIfExists(name);
        if (cmi != null) {
            cmi.mIsAuthenticated = state == StatusCodes.AuthenticationState.LOGGED_IN;
            CallMethodHelper.INCALL.get(c).getModInfo().put(name, cmi);

            // Since a CallMethodInfo object was updated here, we should let the subscribers know
            CallMethodHelper.INCALL.get(c).broadcast();
        }
    }

    public static void gotNudgeData(Context c, CallMethodInfo cmi, BundleResult bundleResult) {
        if (bundleResult == null || bundleResult.bundle == null) {
            return;
        }
        Bundle b = bundleResult.bundle;
        for (String key : b.keySet()) {
            switch (key) {
                case NudgeKey.INCALL_PARAM_CREDIT_WARN:
                    Object creditWarn = b.get(NudgeKey.INCALL_PARAM_CREDIT_WARN);
                    if (creditWarn.getClass().equals(Integer.class)) {
                        cmi.mCreditWarn = (Integer) creditWarn;
                    } else if (creditWarn.getClass().equals(Float.class)) {
                        cmi.mCreditWarn = (Float) creditWarn;
                    } else {
                        Log.e(TAG, "Invalid value for Credit Warn limit: " + creditWarn);
                    }
                    break;
                case NudgeKey.INCALL_CONTACT_FRAGMENT_LOGIN:
                    cmi.mLoginSubtitle = b.getString(NudgeKey.NUDGE_PARAM_SUBTITLE, "");
                    break;
                case NudgeKey.INCALL_CONTACT_CARD_LOGIN:
                    cmi.mLoginNudgeEnable = b.getBoolean(NudgeKey.NUDGE_PARAM_ENABLED, true)
                            && PreferenceManager.getDefaultSharedPreferences(c).getBoolean(
                            cmi.mComponent.getClassName() + "." + key, true);
                    cmi.mLoginNudgeTitle = b.getString(NudgeKey.NUDGE_PARAM_TITLE);
                    cmi.mLoginNudgeSubtitle = b.getString(NudgeKey.NUDGE_PARAM_SUBTITLE);
                    cmi.mLoginNudgeActionText = b.getString(NudgeKey.NUDGE_PARAM_ACTION_TEXT);
                    break;
                case NudgeKey.INCALL_CONTACT_CARD_DOWNLOAD:
                    cmi.mInstallNudgeEnable = b.getBoolean(NudgeKey.NUDGE_PARAM_ENABLED, true)
                            && PreferenceManager.getDefaultSharedPreferences(c).getBoolean(
                            cmi.mComponent.getClassName() + "." + key, true);
                    cmi.mInstallNudgeTitle = b.getString(NudgeKey.NUDGE_PARAM_TITLE);
                    cmi.mInstallNudgeSubtitle = b.getString(NudgeKey.NUDGE_PARAM_SUBTITLE);
                    cmi.mInstallNudgeActionText = b.getString(NudgeKey.NUDGE_PARAM_ACTION_TEXT);
                    break;
            }
        }
    }
}
