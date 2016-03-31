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

import com.android.phone.common.ambient.SingletonHolder;
import com.android.phone.common.ambient.TypedPendingResult;
import com.android.phone.common.incall.api.InCallListeners;
import com.android.phone.common.incall.api.InCallQueries;
import com.android.phone.common.nudge.api.NudgeQueries;
import com.cyanogen.ambient.discovery.util.NudgeKey;
import com.cyanogen.ambient.incall.extension.InCallContactInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ContactsDataSubscription extends DialerDataSubscription {

    public ContactsDataSubscription(Context context) {
        super(context);
    }

    private static final SingletonHolder<ContactsDataSubscription, Context> sInstance =
            new SingletonHolder<ContactsDataSubscription, Context>() {

                @Override
                protected ContactsDataSubscription create(Context context) {
                    // Let's get started here.
                    return new ContactsDataSubscription(context);
                }
            };


    public static ContactsDataSubscription get(Context context) {
        return sInstance.get(context);
    }

    public static boolean isCreated() {
        return sInstance.isCreated();
    }

    public static void init(Context context) {
        ContactsDataSubscription.get(context).refresh();
    }

    @Override
    public void onDynamicRefreshRequested(ArrayList<TypedPendingResult> queries,
                                          ComponentName componentName) {
        queries.add(InCallQueries.getCallMethodAuthenticated(mClient, componentName));
        queries.add(InCallQueries.getCallMethodAccountHandle(mClient, componentName));
    }

    @Override
    protected void enableListeners(CallMethodInfo mod) {
        InCallListeners.enableAuthListener(this, mod);
    }

    @Override
    protected void disableListeners(CallMethodInfo mod) {
        InCallListeners.disableAuthListener(this, mod);
    }

    @Override
    protected void requestedModInfo(ArrayList<TypedPendingResult> queries,
            ComponentName componentName) {

        queries.add(InCallQueries.getCallMethodInfo(mClient, componentName));
        queries.add(InCallQueries.getCallMethodStatus(mClient, componentName));
        queries.add(InCallQueries.getCallMethodMimeType(mClient, componentName));
        queries.add(InCallQueries.getCallMethodVideoCallableMimeType(mClient, componentName));
        queries.add(InCallQueries.getCallMethodAuthenticated(mClient, componentName));
        queries.add(InCallQueries.getLoginIntent(mClient, componentName));
        queries.add(InCallQueries.getDefaultDirectorySearchIntent(mClient, componentName));
        queries.add(InCallQueries.getCallMethodImMimeType(mClient, componentName));

        TypedPendingResult fragLogin = NudgeQueries.getNudgeConfig(mClient, mContext, componentName,
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

    public Set<String> getAllPluginComponentNames() {
        Set<String> names = new HashSet<String>();
        HashMap<ComponentName, CallMethodInfo> plugins = this.getPluginInfo();
        for (ComponentName cn : plugins.keySet()) {
            names.add(cn.flattenToString());
        }
        return names;
    }
}
