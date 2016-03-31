package com.android.phone.common.nudge.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Utilities class to help with nudge events.
 */
public class NudgeUtils {

    public static final String NUDGE_PROVIDER_INTENT = "cyanogen.service.NUDGE_PROVIDER";

    public static ComponentName getNudgeComponent(Context context, final ComponentName cn) {
        // find a nudge component if it exists for this package
        Intent nudgeIntent = new Intent(NUDGE_PROVIDER_INTENT);
        nudgeIntent.setPackage(cn.getPackageName());
        List<ResolveInfo> resolved = context.getPackageManager()
                .queryIntentServices(nudgeIntent, 0);
        if (resolved != null && !resolved.isEmpty()) {
            ResolveInfo result = resolved.get(0);
            return new ComponentName(result.serviceInfo.applicationInfo.packageName,
                    result.serviceInfo.name);
        }
        // if a nudge component doesn't exist, just finish here
        return null;
    }
}
