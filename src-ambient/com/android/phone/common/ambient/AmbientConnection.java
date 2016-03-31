package com.android.phone.common.ambient;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.cyanogen.ambient.analytics.AnalyticsServices;
import com.cyanogen.ambient.callerinfo.CallerInfoServices;
import com.cyanogen.ambient.common.ConnectionResult;
import com.cyanogen.ambient.common.api.AmbientApiClient;
import com.cyanogen.ambient.discovery.DiscoveryManagerServices;
import com.cyanogen.ambient.discovery.NudgeServices;
import com.cyanogen.ambient.incall.InCallServices;

/**
 * Holds Ambient Client for all PIM apps
 */
public class AmbientConnection {

    public static final SingletonHolder<AmbientApiClient, Context> CLIENT =
            new SingletonHolder<AmbientApiClient, Context>() {
                private static final String TAG = "PhoneCommon.AmbientSingletonHolder";

                @Override
                protected AmbientApiClient create(Context context) {
                    AmbientApiClient client = new AmbientApiClient.Builder(context)
                            .addApi(AnalyticsServices.API)
                            .addApi(InCallServices.API)
                            .addApi(CallerInfoServices.API)
                            .addApi(NudgeServices.API)
                            .addApi(DiscoveryManagerServices.API)
                            .build();

                    client.registerConnectionFailedListener(
                            new AmbientApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(ConnectionResult result) {
                                    Log.w(TAG, "Connection failed: " + result);
                                }
                            });
                    client.registerDisconnectionListener(
                            new AmbientApiClient.OnDisconnectionListener() {
                                @Override
                                public void onDisconnection() {
                                    Log.d(TAG, "Connection disconnected");
                                }
                            });
                    client.registerConnectionCallbacks(
                            new AmbientApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle connectionHint) {
                                    Log.d(TAG, "Connection established");
                                }

                                @Override
                                public void onConnectionSuspended(int cause) {
                                    Log.d(TAG, "Connection suspended");
                                }
                            });
                    client.connect();
                    return client;
                }
            };

}
