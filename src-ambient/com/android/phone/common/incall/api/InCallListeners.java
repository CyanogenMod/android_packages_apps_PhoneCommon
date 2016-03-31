/*
 * Copyright (C) 2016 The CyanogenMod Project
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

package com.android.phone.common.incall.api;

import android.content.Context;

import com.android.phone.common.ambient.AmbientDataSubscription;
import com.android.phone.common.incall.listeners.AuthenticationListenerImpl;
import com.android.phone.common.incall.listeners.CallCreditListenerImpl;
import com.android.phone.common.incall.CallMethodInfo;

/**
 * Data Listeners that incall implements.
 */
public class InCallListeners extends ApiHelper {

    /**
     * Enables credit listener on a Mod.
     * @param instance us
     * @param callMethodInfo CallMethod to add listener to
     */
    public static void enableCreditListener(AmbientDataSubscription<CallMethodInfo> instance,
            CallMethodInfo callMethodInfo) {
        CallCreditListenerImpl listener = CallCreditListenerImpl.getInstance(instance.mContext,
                callMethodInfo.mComponent);
        thisApi().addCreditListener(instance.mClient, callMethodInfo.mComponent,
                listener);
        callMethodInfo.setCreditListener(listener);
    }

    /**
     * Disables credit listener on a Mod.
     * @param instance us
     * @param callMethodInfo CallMethod to remove listener from
     */
    public static void disableCreditListener(AmbientDataSubscription<CallMethodInfo> instance,
            CallMethodInfo callMethodInfo) {
        CallCreditListenerImpl listener = callMethodInfo.getCreditListener();
        thisApi().removeCreditListener(instance.mClient, callMethodInfo.mComponent,
                listener);
        callMethodInfo.setCreditListener(null);
    }

    /**
     * Enable Auth Listener on a Mod
     * @param instance us
     * @param callMethodInfo CallMethod to add listener to
     */
    public static void enableAuthListener(AmbientDataSubscription<CallMethodInfo> instance,
            CallMethodInfo callMethodInfo) {
        AuthenticationListenerImpl listener = AuthenticationListenerImpl.getInstance(
                instance.mContext, callMethodInfo.mComponent);
        thisApi().addAuthenticationListener(instance.mClient, callMethodInfo.mComponent,
                listener);
        callMethodInfo.setAuthListener(listener);
    }

    /**
     * Disable Auth Listener on a Mod
     * @param instance us
     * @param callMethodInfo CallMethod to remove listener from
     */
    public static void disableAuthListener(AmbientDataSubscription<CallMethodInfo> instance,
            CallMethodInfo callMethodInfo) {
        AuthenticationListenerImpl listener = callMethodInfo.getAuthListener();
        thisApi().removeAuthenticationListener(instance.mClient,
                callMethodInfo.mComponent, listener);
        callMethodInfo.setAuthListener(null);
    }
}
