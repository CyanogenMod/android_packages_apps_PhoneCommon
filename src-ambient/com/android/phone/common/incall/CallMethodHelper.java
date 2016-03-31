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

import com.android.phone.common.ambient.SingletonHolder;
import com.android.phone.common.ambient.TypedPendingResult;
import com.android.phone.common.incall.api.DataListeners;
import com.android.phone.common.incall.api.InCallQueries;
import com.android.phone.common.incall.api.InCallResults;
import com.android.phone.common.nudge.api.NudgeQueries;
import com.cyanogen.ambient.common.api.Result;
import com.cyanogen.ambient.discovery.results.BundleResult;
import com.cyanogen.ambient.discovery.util.NudgeKey;
import com.cyanogen.ambient.incall.InCallApi;
import com.cyanogen.ambient.incall.InCallServices;
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

/**
 *  Call Method Helper - In charge of loading InCall Mod Data
 *
 *  Fragments and Activities can subscribe to changes with subscribe.
 *
 */
public class CallMethodHelper extends AmbientDataSubscription<InCallApi, CallMethodInfo> {

    protected static final String TAG = CallMethodHelper.class.getSimpleName();

    public CallMethodHelper(Context context, InCallApi api) {
        super(context, api);
    }

    public interface InCallCallListener {
        void onResult(int resultCode);
    }

    public static final SingletonHolder<CallMethodHelper, Context> INCALL =
            new SingletonHolder<CallMethodHelper, Context>() {

                @Override
                protected CallMethodHelper create(Context context) {
                    // Let's get started here.
                    return new CallMethodHelper(context, InCallServices.getInstance());
                }

            };


    public static void init(Context c) {
        INCALL.get(c).refresh();
    }

    @Override
    public void onRefreshRequested() {
        InCallQueries.updateCallPlugins(mContext);
    }

    @Override
    protected List<ComponentName> onBootstrapReady(Result r) {
        return InCallResults.gotInstalledPlugins((InstalledPluginsResult)r);
    }

    @Override
    protected void requestedModInfo(ArrayList<TypedPendingResult> queries,
                                    ComponentName componentName) {

        queries.add(InCallQueries.getCallMethodInfo(mContext, componentName));
        queries.add(InCallQueries.getCallMethodStatus(mContext, componentName));
        queries.add(InCallQueries.getCallMethodMimeType(mContext, componentName));
        queries.add(InCallQueries.getCallMethodVideoCallableMimeType(mContext, componentName));
        queries.add(InCallQueries.getCallMethodAuthenticated(mContext, componentName));
        queries.add(InCallQueries.getLoginIntent(mContext, componentName));
        queries.add(InCallQueries.getSettingsIntent(mContext, componentName));
        queries.add(InCallQueries.getCreditInfo(mContext, componentName));
        queries.add(InCallQueries.getHintText(mContext, componentName));
        queries.add(InCallQueries.getManageCreditsIntent(mContext, componentName));

        TypedPendingResult creditQuery = NudgeQueries.getNudgeConfig(mClient, mContext,
                componentName, NudgeKey.INCALL_CREDIT_NUDGE);
        if (creditQuery != null) {
            queries.add(creditQuery);
        }

    }

    @Override
    protected CallMethodInfo getNewModObject(ComponentName componentName) {
        CallMethodInfo cmi = new CallMethodInfo();
        cmi.mComponent = componentName;
        return cmi;
    }

    @Override
    public void onDynamicRefreshRequested(ArrayList<TypedPendingResult> queries,
                                          ComponentName componentName) {

        queries.add(InCallQueries.getCallMethodAuthenticated(mContext, componentName));
        queries.add(InCallQueries.getCreditInfo(mContext, componentName));
    }

    @Override
    public void enableListeners(CallMethodInfo cn) {
        DataListeners.enableCreditListener(mContext, cn);
        DataListeners.enableAuthListener(mContext, cn);
    }

    @Override
    public void disableListeners(CallMethodInfo cn) {
        DataListeners.disableCreditListener(mContext, cn);
        DataListeners.disableAuthListener(mContext, cn);
    }

    @Override
    public void onPostResult(CallMethodInfo cmi, Result r, int type) {
        if (r instanceof InCallProviderInfoResult) {
            InCallResults.gotGeneralInfo(cmi, mContext, (InCallProviderInfoResult)r);
        } else if (r instanceof PluginStatusResult) {
            InCallResults.gotStatus(cmi, (PluginStatusResult)r);
        } else if (r instanceof MimeTypeResult) {
            InCallResults.gotMimeType(cmi, (MimeTypeResult)r, type);
        } else if (r instanceof AuthenticationStateResult) {
            InCallResults.gotAuthenticationState(cmi, (AuthenticationStateResult)r);
        } else if (r instanceof AccountHandleResult) {
            InCallResults.gotAccountHandle(cmi, mContext, (AccountHandleResult)r);
        } else if (r instanceof PendingIntentResult) {
            InCallResults.gotIntent(cmi, (PendingIntentResult)r, type);
        } else if (r instanceof GetCreditInfoResultResult) {
            GetCreditInfoResult gcir = ((GetCreditInfoResultResult)r).result;
            InCallResults.gotCreditData(cmi, gcir);
        } else if (r instanceof BundleResult) {
            InCallResults.gotNudgeData(mContext, cmi, (BundleResult)r);
        } else if (r instanceof HintTextResultResult) {
            InCallResults.gotHintText(cmi, (HintTextResultResult)r);
        }
    }
}
