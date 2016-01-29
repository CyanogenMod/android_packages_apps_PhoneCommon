package com.android.phone.common.incall;

import android.content.ComponentName;
import android.os.RemoteException;
import android.util.Log;

import com.cyanogen.ambient.incall.extension.GetCreditInfoResult;
import com.cyanogen.ambient.incall.extension.ICallCreditListener;

public class CallCreditListenerImpl extends ICallCreditListener.Stub {
    private static final String TAG = "CallCreditListener";
    private ComponentName mComponentName;

    public static CallCreditListenerImpl getInstance(ComponentName componentName) {
        return new CallCreditListenerImpl(componentName);
    }

    private CallCreditListenerImpl(ComponentName cn) {
        mComponentName = cn;
    }

    @Override
    public void creditInfoUpdated(GetCreditInfoResult gcir)
            throws RemoteException {
        Log.d(TAG, "getting creditInfoUpdated for: " + mComponentName + " gcir: " + gcir);
        CallMethodHelper.updateCreditInfo(mComponentName, gcir);
    }
}