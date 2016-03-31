package com.android.phone.common.incall.api;

import android.content.Context;
import android.util.Log;

import com.android.phone.common.ambient.AmbientDataSubscription;
import com.android.phone.common.incall.DialerDataSubscription;
import com.android.phone.common.incall.CallMethodInfo;
import com.android.phone.common.incall.ContactsDataSubscription;
import com.cyanogen.ambient.incall.InCallApi;
import com.cyanogen.ambient.incall.InCallServices;

/**
 * Helper for getting instance and InCall Api
 */
public class ApiHelper {

    public static AmbientDataSubscription<CallMethodInfo> getInstance(Context c) {
        if (DialerDataSubscription.isCreated()) {
            return DialerDataSubscription.get(c);
        } else {
            return ContactsDataSubscription.get(c);
        }
    }

    static InCallApi thisApi() {
        return InCallServices.getInstance();
    }

}
