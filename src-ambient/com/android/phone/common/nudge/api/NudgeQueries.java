package com.android.phone.common.nudge.api;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.android.phone.common.ambient.TypedPendingResult;
import com.android.phone.common.nudge.utils.NudgeUtils;
import com.cyanogen.ambient.common.api.AmbientApiClient;
import com.cyanogen.ambient.discovery.NudgeServices;

/**
 * Queries for Nudges
 */
public class NudgeQueries extends ApiHelper {

    private static TypedPendingResult getNudgeForKey(AmbientApiClient client,
            ComponentName componentName, String key) {
        int type = NudgeTypedResult.getNudgeTypeForKey(key);
        return new TypedPendingResult(thisApi().getConfigurationForKey(
                client, componentName, key), type);
    }

    /**
     * Get a nudge configuration for the mod
     * @param cn of mod
     *
     * @return TypedPendingResult to identify which field to modify in our CallMethodInfo object
     */
    public static TypedPendingResult getNudgeConfig(AmbientApiClient client,
            Context c, ComponentName cn, String key) {

        ComponentName nudgeComponent = NudgeUtils.getNudgeComponent(c, cn);
        if (nudgeComponent != null) {
            return NudgeQueries.getNudgeForKey(client, nudgeComponent, key);
        }
        return null;
    }
}
