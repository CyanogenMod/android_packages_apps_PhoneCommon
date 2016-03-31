/*
 * Copyright (C)  2016 The CyanogenMod Project
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

import com.android.phone.common.ambient.AmbientDataSubscription;
import com.android.phone.common.ambient.SingletonHolder;
import com.android.phone.common.ambient.TypedPendingResult;
import com.android.phone.common.incall.api.InCallListeners;
import com.android.phone.common.incall.api.InCallQueries;
import com.android.phone.common.incall.api.InCallResults;
import com.android.phone.common.nudge.api.NudgeQueries;
import com.cyanogen.ambient.common.api.Result;
import com.cyanogen.ambient.discovery.results.BundleResult;
import com.cyanogen.ambient.discovery.util.NudgeKey;
import com.cyanogen.ambient.incall.InCallApi;
import com.cyanogen.ambient.incall.InCallServices;
import com.cyanogen.ambient.incall.extension.GetCreditInfoResult;
import com.cyanogen.ambient.incall.extension.InCallContactInfo;
import com.cyanogen.ambient.incall.results.AccountHandleResult;
import com.cyanogen.ambient.incall.results.AuthenticationStateResult;
import com.cyanogen.ambient.incall.results.GetCreditInfoResultResult;
import com.cyanogen.ambient.incall.results.HintTextResultResult;
import com.cyanogen.ambient.incall.results.InCallProviderInfoResult;
import com.cyanogen.ambient.incall.results.InstalledPluginsResult;
import com.cyanogen.ambient.incall.results.MimeTypeResult;
import com.cyanogen.ambient.incall.results.PendingIntentResult;
import com.cyanogen.ambient.incall.results.PluginStatusResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cyanogen.ambient.incall.util.InCallHelper.NO_COLOR;

public class InCallPluginHelper extends CallMethodHelper {

    public InCallPluginHelper(Context context, InCallApi api) {
        super(context, api);
    }

    public static final SingletonHolder<InCallPluginHelper, Context> INCALL =
            new SingletonHolder<InCallPluginHelper, Context>() {

                @Override
                protected InCallPluginHelper create(Context context) {
                    // Let's get started here.
                    return new InCallPluginHelper(context, InCallServices.getInstance());
                }
            };

    public static void init(Context context) {
        InCallPluginHelper.INCALL.get(context).refresh();
    }

    @Override
    public void onDynamicRefreshRequested(ArrayList<TypedPendingResult> queries,
                                          ComponentName componentName) {
        queries.add(InCallQueries.getCallMethodAuthenticated(mContext, componentName));
        queries.add(InCallQueries.getCallMethodAccountHandle(mContext, componentName));
    }

    @Override
    protected void enableListeners(CallMethodInfo mod) {
        InCallListeners.enableAuthListener(mContext, mod);
    }

    @Override
    protected void disableListeners(CallMethodInfo mod) {
        InCallListeners.disableAuthListener(mContext, mod);
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
        queries.add(InCallQueries.getDefaultDirectorySearchIntent(mContext, componentName));
        queries.add(InCallQueries.getCallMethodImMimeType(mContext, componentName));

        TypedPendingResult fragLogin = NudgeQueries.getNudgeConfig(mClient, mContext,
                componentName,
                NudgeKey.INCALL_CONTACT_FRAGMENT_LOGIN);
        if (fragLogin != null) {
            queries.add(fragLogin);
        }
        TypedPendingResult cardLogin = NudgeQueries.getNudgeConfig(mClient, mContext, componentName,
                NudgeKey.INCALL_CONTACT_CARD_LOGIN);
        if (cardLogin != null) {
            queries.add(cardLogin);
        }
        TypedPendingResult cardDownload = NudgeQueries.getNudgeConfig(mClient, mContext,
                componentName, NudgeKey.INCALL_CONTACT_CARD_DOWNLOAD);
        if (cardDownload != null) {
            queries.add(cardDownload);
        }
    }

    public static void refreshPendingIntents(InCallContactInfo contactInfo) {
        // TODO: implement
    }

    public static Set<String> getAllPluginComponentNames(Context context) {
        Set<String> names = new HashSet<String>();
        HashMap<ComponentName, CallMethodInfo> plugins = INCALL.get(context).getModInfo();
        for (ComponentName cn : plugins.keySet()) {
            names.add(cn.flattenToString());
        }
        return names;
    }
}
