package com.android.phone.common.nudge.api;

import android.content.ComponentName;
import com.android.phone.common.ambient.TypedPendingResult;
import com.cyanogen.ambient.common.api.AmbientApiClient;
import com.cyanogen.ambient.discovery.NudgeServices;

/**
 * Created by bird on 4/6/16.
 */
public class NudgeQueries {

    public static TypedPendingResult getNudgeForKey(AmbientApiClient client,
                                                    ComponentName componentName, String key) {
        return new TypedPendingResult(NudgeServices.NudgeApi.getConfigurationForKey(
                client, componentName, key), TypedPendingResult.NONE);
    }
}
