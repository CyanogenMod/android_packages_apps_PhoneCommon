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

package com.android.phone.common.nudge.api;

import com.android.phone.common.ambient.TypedPendingResult;
import com.cyanogen.ambient.common.api.PendingResult;
import com.cyanogen.ambient.discovery.util.NudgeKey;

import android.util.Log;

/**
 * InCall Pending result types.
 */
public class NudgeTypedResult extends TypedPendingResult {

    private static final String TAG = NudgeTypedResult.class.getSimpleName();

    public NudgeTypedResult(PendingResult pendingResult, int type) {
        super(pendingResult, type);
    }

    public static int getNudgeTypeForKey(String key) {
        switch (key) {
            case NudgeKey.INCALL_CREDIT_NUDGE:
                return INCALL_CREDIT_NUDGE;
            case NudgeKey.INCALL_CONTACT_CARD_LOGIN:
                return INCALL_CONTACT_CARD_LOGIN;
            case NudgeKey.INCALL_CONTACT_CARD_DOWNLOAD:
                return INCALL_CONTACT_CARD_DOWNLOAD;
            case NudgeKey.INCALL_CONTACT_FRAGMENT_LOGIN:
                return INCALL_CONTACT_FRAGMENT_LOGIN;
            default:
                Log.e(TAG, "No nudge type!");
                return NONE;
        }
    }
}
