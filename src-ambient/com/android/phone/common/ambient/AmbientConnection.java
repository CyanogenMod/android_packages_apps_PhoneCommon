package com.android.phone.common.ambient;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.cyanogen.ambient.analytics.AnalyticsServices;
import com.cyanogen.ambient.callerinfo.CallerInfoServices;
import com.cyanogen.ambient.common.ConnectionResult;
import com.cyanogen.ambient.common.api.AmbientApiClient;
import com.cyanogen.ambient.deeplink.DeepLinkServices;
import com.cyanogen.ambient.discovery.DiscoveryManagerServices;
import com.cyanogen.ambient.discovery.NudgeServices;
import com.cyanogen.ambient.incall.InCallServices;

/**
 * Holds Ambient Client for all PIM apps
 */
public class AmbientConnection {

    private static boolean checkPermissionsGranted(Context context, String permission) {
        int status = context.checkCallingOrSelfPermission(permission);
        return status == PackageManager.PERMISSION_GRANTED;
    }

    private static final String INCALL_PERMISSION
            = "com.cyanogen.ambient.permission.BIND_INCALL_SERVICE";

    private static final String PUBLISH_DISCOVERY_NUDGE
            = "com.cyanogen.ambient.permission.PUBLISH_DISCOVERY_NUDGE";

    private static final String DEEPLINK_SERVICE_PERMISSION
            = "com.cyanogen.ambient.permission.BIND_DEEPLINK_SERVICE";

    private static final String DEEPLINK_DATABASE_PERMISSION
            = "com.cyanogen.ambient.permission.READ_DEEPLINK_DATABASE";

    public static final SingletonHolder<AmbientApiClient, Context> CLIENT =
            new SingletonHolder<AmbientApiClient, Context>() {
                private static final String TAG = "PhoneCommon.AmbientSingletonHolder";

                @Override
                protected AmbientApiClient create(Context context) {
                    AmbientApiClient.Builder builder = new AmbientApiClient.Builder(context);
                    builder.addApi(AnalyticsServices.API);
                    builder.addApi(CallerInfoServices.API);

                    if (checkPermissionsGranted(context, INCALL_PERMISSION)) {
                        builder.addApi(InCallServices.API);
                    }

                    if (checkPermissionsGranted(context, PUBLISH_DISCOVERY_NUDGE)) {
                        builder.addApi(NudgeServices.API);
                        builder.addApi(DiscoveryManagerServices.API);
                    }

                    if (checkPermissionsGranted(context, DEEPLINK_SERVICE_PERMISSION) &&
                            checkPermissionsGranted(context, DEEPLINK_DATABASE_PERMISSION)) {
                        builder.addApi(DeepLinkServices.API);
                    }

                    AmbientApiClient client = builder.build();

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
