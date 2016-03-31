package com.android.phone.common.incall;

import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.android.phone.common.incall.api.InCallResults;
import com.cyanogen.ambient.incall.extension.IAuthenticationListener;

public class AuthenticationListenerImpl extends IAuthenticationListener.Stub {

    private static final String TAG = AuthenticationListenerImpl.class.getSimpleName();
    private static final boolean DEBUG = false;

    private ComponentName mComponentName;
    private Context mContext;

    public static AuthenticationListenerImpl getInstance(Context context, ComponentName
                                                         componentName) {

        return new AuthenticationListenerImpl(context, componentName);
    }

    private AuthenticationListenerImpl(Context context, ComponentName cn) {
        mContext = context;
        mComponentName = cn;
    }

    @Override
    public void authenticationStateUpdated(int state) throws RemoteException {
        if (DEBUG) {
            Log.d(TAG, "Getting authenticationStateUpdated for: " + mComponentName + " state: "
                    + state);
        }

        InCallResults.updateAuthenticationState(mContext, mComponentName, state);
    }

}