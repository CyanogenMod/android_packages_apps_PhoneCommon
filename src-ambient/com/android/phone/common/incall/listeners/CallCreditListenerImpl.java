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