/*
 * Copyright (C) 2015 The CyanogenMod Project
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

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import java.lang.ref.WeakReference;

public class StartInCallCallReceiver extends ResultReceiver {
    private static final String TAG = StartInCallCallReceiver.class.getSimpleName();
    private static final boolean DEBUG = false;

    private WeakReference<Receiver> mReceiver;

    public interface InCallCallListener {
        void onResult(int resultCode);
    }

    public StartInCallCallReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (DEBUG) {
            Log.d(TAG, "Result received resultCode: " + resultCode + " resultData: " + resultData);
        }

        if (mReceiver != null) {
            Receiver receiver = mReceiver.get();
            if (receiver != null) {
                receiver.onReceiveResult(resultCode, resultData);
            }
        }
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = new WeakReference<Receiver>(receiver);
    }

}