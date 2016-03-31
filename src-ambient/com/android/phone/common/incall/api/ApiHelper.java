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

package com.android.phone.common.incall.api;

import android.content.Context;
import android.util.Log;

import com.android.phone.common.ambient.AmbientDataSubscription;
import com.android.phone.common.incall.DialerDataSubscription;
import com.android.phone.common.incall.CallMethodInfo;
import com.android.phone.common.incall.ContactsDataSubscription;
import com.cyanogen.ambient.incall.InCallApi;
import com.cyanogen.ambient.incall.InCallServices;

/**
 * Helper for getting instance and InCall Api
 */
public class ApiHelper {

    static InCallApi thisApi() {
        return InCallServices.getInstance();
    }

}
