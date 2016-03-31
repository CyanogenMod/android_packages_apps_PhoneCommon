package com.android.phone.common.incall.api;

import android.content.Context;
import com.android.phone.common.incall.listeners.AuthenticationListenerImpl;
import com.android.phone.common.incall.listeners.CallCreditListenerImpl;
import com.android.phone.common.incall.CallMethodInfo;

/**
 * Data Listeners that incall implements.
 */
public class InCallListeners extends ApiHelper {

    /**
     * Enables credit listener on a Mod.
     * @param context context
     * @param callMethodInfo CallMethod to add listener to
     */
    public static void enableCreditListener(Context context, CallMethodInfo callMethodInfo) {
        CallCreditListenerImpl listener = CallCreditListenerImpl.getInstance(context,
                callMethodInfo.mComponent);
        thisApi().addCreditListener(getInstance(context).mClient, callMethodInfo.mComponent,
                listener);
        callMethodInfo.setCreditListener(listener);
    }

    /**
     * Disables credit listener on a Mod.
     * @param context context
     * @param callMethodInfo CallMethod to remove listener from
     */
    public static void disableCreditListener(Context context, CallMethodInfo callMethodInfo) {
        CallCreditListenerImpl listener = callMethodInfo.getCreditListener();
        thisApi().removeCreditListener(getInstance(context).mClient, callMethodInfo.mComponent,
                listener);
        callMethodInfo.setCreditListener(null);
    }

    /**
     * Enable Auth Listener on a Mod
     * @param context context
     * @param callMethodInfo CallMethod to add listener to
     */
    public static void enableAuthListener(Context context, CallMethodInfo callMethodInfo) {
        AuthenticationListenerImpl listener = AuthenticationListenerImpl.getInstance(context,
                callMethodInfo.mComponent);
        thisApi().addAuthenticationListener(getInstance(context).mClient, callMethodInfo.mComponent,
                listener);
        callMethodInfo.setAuthListener(listener);
    }

    /**
     * Disable Auth Listener on a Mod
     * @param context context
     * @param callMethodInfo CallMethod to remove listener from
     */
    public static void disableAuthListener(Context context, CallMethodInfo callMethodInfo) {
        AuthenticationListenerImpl listener = callMethodInfo.getAuthListener();
        thisApi().removeAuthenticationListener(getInstance(context).mClient,
                callMethodInfo.mComponent, listener);
        callMethodInfo.setAuthListener(null);
    }
}
