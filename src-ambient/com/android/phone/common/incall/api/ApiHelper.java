package com.android.phone.common.incall.api;

import android.content.Context;

import com.android.phone.common.ambient.AmbientDataSubscription;
import com.android.phone.common.incall.CallMethodHelper;
import com.android.phone.common.incall.CallMethodInfo;
import com.android.phone.common.incall.InCallPluginHelper;
import com.cyanogen.ambient.incall.InCallApi;

/**
 * Helper for getting instance and InCall Api
 */
public class ApiHelper {

    public static AmbientDataSubscription<InCallApi, CallMethodInfo> getInstance(Context c) {
        if (CallMethodHelper.INCALL.isCreated()) {
            return CallMethodHelper.INCALL.get(c);
        } else {
            return InCallPluginHelper.INCALL.get(c);
        }
    }

    static InCallApi thisApi(Context c) {
        return getInstance(c).mAmbientAPI;
    }

}
