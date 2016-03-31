package com.android.phone.common.incall.api;

import android.content.Context;
import com.android.phone.common.incall.AuthenticationListenerImpl;
import com.android.phone.common.incall.CallCreditListenerImpl;
import com.android.phone.common.incall.CallMethodHelper;
import com.android.phone.common.incall.CallMethodInfo;
import com.cyanogen.ambient.incall.InCallApi;

/**
 * Created by bird on 4/7/16.
 */
public class DataListeners {

    private static CallMethodHelper getInstance(Context c) {
        return CallMethodHelper.INCALL.get(c);
    }

    private static InCallApi thisApi(Context c) {
        return getInstance(c).mAmbientAPI;
    }

    public static void enableCreditListener(Context c, CallMethodInfo callMethodInfo) {
        CallCreditListenerImpl listener = CallCreditListenerImpl.getInstance(c, callMethodInfo.mComponent);
        thisApi(c).addCreditListener(getInstance(c).mClient, callMethodInfo.mComponent, listener);
        callMethodInfo.setCreditListener(listener);
    }

    public static void disableCreditListener(Context c, CallMethodInfo callMethodInfo) {
        CallCreditListenerImpl listener = callMethodInfo.getCreditListener();
        thisApi(c).removeCreditListener(getInstance(c).mClient, callMethodInfo.mComponent, listener);
        callMethodInfo.setCreditListener(null);
    }

    public static void enableAuthListener(Context c, CallMethodInfo callMethodInfo) {
        AuthenticationListenerImpl listener = AuthenticationListenerImpl.getInstance(c, callMethodInfo.mComponent);
        thisApi(c).addAuthenticationListener(getInstance(c).mClient, callMethodInfo.mComponent, listener);
        callMethodInfo.setAuthListener(listener);
    }

    public static void disableAuthListener(Context c, CallMethodInfo callMethodInfo) {
        AuthenticationListenerImpl listener = callMethodInfo.getAuthListener();
        thisApi(c).removeAuthenticationListener(getInstance(c).mClient, callMethodInfo.mComponent, listener);
        callMethodInfo.setAuthListener(null);
    }
}
