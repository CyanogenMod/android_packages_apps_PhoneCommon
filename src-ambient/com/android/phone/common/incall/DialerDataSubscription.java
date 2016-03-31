/*
 * Copyright (C) 2015 The CyanogenMod Project
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

package com.android.phone.common.incall;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.android.phone.common.ambient.SingletonHolder;
import com.android.phone.common.ambient.TypedPendingResult;
import com.android.phone.common.incall.api.InCallListeners;
import com.android.phone.common.incall.api.InCallQueries;
import com.android.phone.common.incall.api.InCallResults;
import com.android.phone.common.incall.api.InCallTypedResult;
import com.android.phone.common.nudge.api.NudgeQueries;
import com.cyanogen.ambient.common.api.Result;
import com.cyanogen.ambient.discovery.results.BundleResult;
import com.cyanogen.ambient.discovery.util.NudgeKey;
import com.cyanogen.ambient.incall.extension.GetCreditInfoResult;
import com.cyanogen.ambient.incall.results.AccountHandleResult;
import com.cyanogen.ambient.incall.results.AuthenticationStateResult;
import com.cyanogen.ambient.incall.results.GetCreditInfoResultResult;
import com.cyanogen.ambient.incall.results.HintTextResultResult;
import com.cyanogen.ambient.incall.results.InCallProviderInfoResult;
import com.cyanogen.ambient.incall.results.InstalledPluginsResult;
import com.cyanogen.ambient.incall.results.MimeTypeResult;
import com.cyanogen.ambient.incall.results.PendingIntentResult;
import com.cyanogen.ambient.incall.results.PluginStatusResult;
import com.android.phone.common.ambient.AmbientDataSubscription;

import java.util.ArrayList;
import java.util.List;

import static com.cyanogen.ambient.incall.util.InCallHelper.NO_COLOR;

/**
 *  Call Method Helper - In charge of loading InCall Mod Data
 *
 *  Fragments and Activities can subscribe to changes with subscribe.
 */
public class DialerDataSubscription extends AmbientDataSubscription<CallMethodInfo> {

    protected static final String TAG = DialerDataSubscription.class.getSimpleName();

    public DialerDataSubscription(Context context) {
        super(context);
    }

    public static final SingletonHolder<DialerDataSubscription, Context> sInstance =
            new SingletonHolder<DialerDataSubscription, Context>() {

                @Override
                protected DialerDataSubscription create(Context context) {
                    // Let's get started here.
                    return new DialerDataSubscription(context);
                }

            };

    public static DialerDataSubscription get(Context context) {
        return sInstance.get(context);
    }

    public static boolean isCreated() {
        return sInstance.isCreated();
    }

    public static void init(Context context) {
        sInstance.get(context).refresh();
    }

    @Override
    protected void onRefreshRequested() {
        InCallQueries.updateCallPlugins(get(mContext));
    }

    @Override
    protected List<ComponentName> getPluginComponents(Result result) {
        return InCallResults.gotInstalledPlugins((InstalledPluginsResult)result);
    }

    @Override
    protected void requestedModInfo(ArrayList<TypedPendingResult> queries,
            ComponentName componentName) {

        queries.add(InCallQueries.getCallMethodInfo(get(mContext), componentName));
        queries.add(InCallQueries.getCallMethodStatus(get(mContext), componentName));
        queries.add(InCallQueries.getCallMethodMimeType(get(mContext), componentName));
        queries.add(InCallQueries.getCallMethodVideoCallableMimeType(get(mContext), componentName));
        queries.add(InCallQueries.getCallMethodAuthenticated(get(mContext), componentName));
        queries.add(InCallQueries.getLoginIntent(get(mContext), componentName));
        queries.add(InCallQueries.getSettingsIntent(get(mContext), componentName));
        queries.add(InCallQueries.getCreditInfo(get(mContext), componentName));
        queries.add(InCallQueries.getHintText(get(mContext), componentName));
        queries.add(InCallQueries.getManageCreditsIntent(get(mContext), componentName));

        TypedPendingResult creditQuery = NudgeQueries.getNudgeConfig(mClient, mContext,
                componentName, NudgeKey.INCALL_CREDIT_NUDGE);
        if (creditQuery != null) {
            queries.add(creditQuery);
        }
    }

    @Override
    protected CallMethodInfo getNewModObject(ComponentName componentName) {
        CallMethodInfo callMethodInfo = new CallMethodInfo();
        callMethodInfo.mComponent = componentName;
        callMethodInfo.mSlotId = -1;
        callMethodInfo.mSubId = -1;
        callMethodInfo.mColor = NO_COLOR;
        callMethodInfo.mIsInCallProvider = true;
        return callMethodInfo;
    }

    @Override
    protected void onDynamicRefreshRequested(ArrayList<TypedPendingResult> queries,
            ComponentName componentName) {

        queries.add(InCallQueries.getCallMethodAuthenticated(get(mContext), componentName));
        queries.add(InCallQueries.getCreditInfo(get(mContext), componentName));
    }

    @Override
    protected void enableListeners(CallMethodInfo cn) {
        InCallListeners.enableCreditListener(get(mContext), cn);
        InCallListeners.enableAuthListener(get(mContext), cn);
    }

    @Override
    protected void disableListeners(CallMethodInfo cn) {
        InCallListeners.disableCreditListener(get(mContext), cn);
        InCallListeners.disableAuthListener(get(mContext), cn);
    }

    @Override
    protected void onPostResult(CallMethodInfo cmi, Result r, int type) {
        switch (type) {
            case InCallTypedResult.GENERAL_MIME_TYPE:
            case InCallTypedResult.IM_MIME_TYPE:
            case InCallTypedResult.VIDEO_MIME_TYPE:
                InCallResults.gotMimeType(cmi, (MimeTypeResult)r, type);
                break;
            case InCallTypedResult.SETTINGS_INTENT:
            case InCallTypedResult.CREDIT_INTENT:
            case InCallTypedResult.LOGIN_INTENT:
            case InCallTypedResult.DEFAULT_DIRECTORY_SEARCH_INTENT:
                InCallResults.gotIntent(cmi, (PendingIntentResult)r, type);
                break;
            case InCallTypedResult.GENERAL_DATA:
                InCallResults.gotGeneralInfo(cmi, get(mContext), (InCallProviderInfoResult)r);
                break;
            case InCallTypedResult.STATUS:
                InCallResults.gotStatus(cmi, (PluginStatusResult)r);
                break;
            case InCallTypedResult.AUTHENTICATION:
                InCallResults.gotAuthenticationState(cmi, (AuthenticationStateResult)r);
                break;
            case InCallTypedResult.CREDIT_INFO:
                GetCreditInfoResult gcir = ((GetCreditInfoResultResult)r).result;
                InCallResults.gotCreditData(cmi, gcir);
                break;
            case InCallTypedResult.ACCOUNT_HANDLE:
                InCallResults.gotAccountHandle(cmi, mContext, (AccountHandleResult)r);
                break;
            case InCallTypedResult.HINT_TEXT:
                InCallResults.gotHintText(cmi, (HintTextResultResult)r);
                break;
            default:
                Log.e(TAG, "Unhandled result type!");
                break;
        }
    }
}
