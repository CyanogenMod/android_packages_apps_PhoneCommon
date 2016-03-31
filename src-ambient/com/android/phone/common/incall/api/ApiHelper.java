package com.android.phone.common.incall.api;

import android.content.Context;
import com.android.phone.common.incall.CallMethodHelper;
import com.cyanogen.ambient.incall.InCallApi;

/**
 * Helper for getting instance and InCall Api
 */
public class ApiHelper {

    public static CallMethodHelper getInstance(Context c) {
        return CallMethodHelper.INCALL.get(c);
    }

    static InCallApi thisApi(Context c) {
        return getInstance(c).mAmbientAPI;
    }

}
