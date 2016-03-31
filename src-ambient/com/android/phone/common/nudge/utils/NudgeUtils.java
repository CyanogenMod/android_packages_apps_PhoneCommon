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
