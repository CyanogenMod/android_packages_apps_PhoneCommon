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

package com.android.phone.common.incall.utils;

import android.content.Context;

import com.android.phone.common.ambient.AmbientDataSubscription;
import com.android.phone.common.incall.CallMethodInfo;
import com.android.phone.common.incall.api.ApiHelper;
import com.cyanogen.ambient.plugin.PluginStatus;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.android.phone.common.ambient.TypedPendingResult.GENERAL_MIME_TYPE;
import static com.android.phone.common.ambient.TypedPendingResult.IM_MIME_TYPE;
import static com.android.phone.common.ambient.TypedPendingResult.VIDEO_MIME_TYPE;


/**
 * Class with logic to get mimes and mime types.
 */
public class MimeTypeUtils extends ApiHelper {

    private static String getMimeTypes(AmbientDataSubscription<CallMethodInfo> instance,
            boolean enabledOnly, int mimeID) {

        String mimeTypes = "";
        List<String> mimeTypesList = new ArrayList<>();
        for (CallMethodInfo cmi : instance.getPluginInfo().values()) {
            String mimeType = null;
            switch(mimeID) {
                case GENERAL_MIME_TYPE:
                    mimeType = cmi.mMimeType;
                    break;
                case IM_MIME_TYPE:
                    mimeType = cmi.mVideoCallableMimeType;
                    break;
                case VIDEO_MIME_TYPE:
                    mimeType = cmi.mImMimeType;
            }

            if (!enabledOnly) {
                mimeTypesList.add(mimeType);
                continue;
            }

            if (cmi.mStatus == PluginStatus.ENABLED) {
                mimeTypesList.add(mimeType);
            }
        }

        if (!mimeTypesList.isEmpty()) {
            mimeTypes = Joiner.on(",").skipNulls().join(mimeTypesList);
        }
        return mimeTypes;
    }


    /**
     * A few items need a list of mime types in a comma delimited list. Since we are already
     * querying all the plugins. We can easily build this list ahead of time.
     *
     * Items that require this should subscribe and grab this updated list when needed.
     * @return string of all (not limited to enabled) mime types
     */
    public static String getAllMimeTypes(AmbientDataSubscription<CallMethodInfo> instance) {
        return getMimeTypes(instance, false, GENERAL_MIME_TYPE);
    }

    /**
     * A few items need a list of mime types in a comma delimited list. Since we are already
     * querying all the plugins. We can easily build this list ahead of time.
     *
     * Items that require this should subscribe and grab this updated list when needed.
     * @return string of enabled mime types
     */
    public static String getAllEnabledMimeTypes(AmbientDataSubscription<CallMethodInfo> instance) {
        return getMimeTypes(instance, true, GENERAL_MIME_TYPE);
    }

    /**
     * A few items need a list of video callable mime types in a comma delimited list.
     * Since we are already querying all the plugins. We can easily build this list ahead of time.
     *
     * Items that require this should subscribe and grab this updated list when needed.
     * @return string of enabled video callable mime types
     */
    public static String getAllEnabledVideoCallableMimeTypes(
            AmbientDataSubscription<CallMethodInfo> instance) {
        return getMimeTypes(instance, true, VIDEO_MIME_TYPE);
    }

    public static String getAllEnabledImMimeTypes(
            AmbientDataSubscription<CallMethodInfo> instance) {
        return getMimeTypes(instance, true, IM_MIME_TYPE);
    }

    public static Set<String> getAllEnabledVideoImMimeSet(
            AmbientDataSubscription<CallMethodInfo> instance) {
        String[] videoMimes = getAllEnabledVideoCallableMimeTypes(instance).split(",");
        String[] imMimes = getAllEnabledImMimeTypes(instance).split(",");
        HashSet<String> mimeSet = new HashSet<>();

        if (videoMimes != null) {
            mimeSet.addAll(Arrays.asList(videoMimes));
        }
        if (imMimes != null) {
            mimeSet.addAll(Arrays.asList(imMimes));
        }
        return mimeSet;
    }

    public static Set<String> getAllEnabledVoiceMimeSet(
            AmbientDataSubscription<CallMethodInfo> instance) {
        String[] mimes = getAllEnabledMimeTypes(instance).split(",");
        HashSet<String> mimeSet = new HashSet<>();
        if (mimes != null) {
            mimeSet.addAll(Arrays.asList(mimes));
        }
        return mimeSet;
    }


}
