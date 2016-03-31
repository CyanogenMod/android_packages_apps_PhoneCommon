package com.android.phone.common.incall.listeners;

import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.android.phone.common.incall.DialerDataSubscription;
import com.android.phone.common.incall.api.InCallResults;
import com.cyanogen.ambient.incall.extension.GetCreditInfoResult;
import com.cyanogen.ambient.incall.extension.ICallCreditListener;

public class CallCreditListenerImpl extends ICallCreditListener.Stub {

    private static final String TAG = CallCreditListenerImpl.class.getSimpleName();
    private static final boolean DEBUG = false;

    private Context mContext;
    private ComponentName mComponentName;

    public static CallCreditListenerImpl getInstance(Context context, ComponentName componentName) {
        return new CallCreditListenerImpl(context, componentName);
    }

    private CallCreditListenerImpl(Context context, ComponentName cn) {
        mContext = context;
        mComponentName = cn;
    }

    @Override
    public void creditInfoUpdated(GetCreditInfoResult gcir) throws RemoteException {
        if (DEBUG) {
            Log.d(TAG, "got creditInfoUpdated for: " + mComponentName + " gcir: " + gcir);
        }
        InCallResults.updateCreditInfo(DialerDataSubscription.get(mContext), mComponentName, gcir);
    }
}