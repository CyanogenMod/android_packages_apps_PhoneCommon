package com.android.phone.common.incall;

import android.content.ComponentName;
import android.os.RemoteException;
import android.util.Log;

import com.cyanogen.ambient.incall.extension.IAuthenticationListener;

public class AuthenticationListenerImpl extends IAuthenticationListener.Stub {
    private static final String TAG = "AuthenticationListener";
    private ComponentName mComponentName;

    public static AuthenticationListenerImpl getInstance(ComponentName componentName) {
        return new AuthenticationListenerImpl(componentName);
    }

    private AuthenticationListenerImpl(ComponentName cn) {
        mComponentName = cn;
    }

    @Override
    public void authenticationStateUpdated(int state) throws RemoteException {
        Log.d(TAG, "Getting authenticationStateUpdated for: " + mComponentName + " state: " +
                state);
        CallMethodHelper.updateAuthenticationState(mComponentName, state);
    }
}