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

/**
 * @file
 * @brief VoLTE related utilities for Dialer
 */

package com.android.phone.common.util;

import android.content.Context;
import com.android.ims.ImsManager;

/**
 * Collection of utilities that glue the Dialer and Telephony
 * layers together.
 */
public class VolteUtils {
    private static final String TAG = VolteUtils.class.getSimpleName();

    public static boolean isVolteAvailable(Context context, int subId) {
        return ImsManager.isVolteEnabledByPlatform(context) &&
                ImsManager.isVolteProvisionedOnDevice(context, subId);
    }

    public static boolean isVolteInUse(Context context, int subId) {
        return isVolteAvailable(context, subId) &&
                ImsManager.isEnhanced4gLteModeSettingEnabledByUser(context);
    }
}
