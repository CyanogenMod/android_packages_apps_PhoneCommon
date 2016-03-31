package com.android.phone.common.incall.utils;

import android.content.Context;
import com.android.phone.common.incall.CallMethodHelper;
import com.android.phone.common.incall.CallMethodInfo;
import com.android.phone.common.incall.api.ApiHelper;
import com.cyanogen.ambient.plugin.PluginStatus;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.android.phone.common.incall.api.InCallTypedResult.GENERAL_MIME_TYPE;
import static com.android.phone.common.incall.api.InCallTypedResult.IM_MIME_TYPE;
import static com.android.phone.common.incall.api.InCallTypedResult.VIDEO_MIME_TYPE;

/**
 * Class with logic to get mimes and mime types.
 */
public class MimeTypeUtils extends ApiHelper {

    private static String getMimeTypes(Context c, boolean enabledOnly, int mimeID) {
        String mimeTypes = "";
        List<String> mimeTypesList = new ArrayList<>();
        for (CallMethodInfo cmi : getInstance(c).getModInfo().values()) {
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
    public static String getAllMimeTypes(Context c) {
        return getMimeTypes(c, false, GENERAL_MIME_TYPE);
    }

    /**
     * A few items need a list of mime types in a comma delimited list. Since we are already
     * querying all the plugins. We can easily build this list ahead of time.
     *
     * Items that require this should subscribe and grab this updated list when needed.
     * @return string of enabled mime types
     */
    public static String getAllEnabledMimeTypes(Context c) {
        return getMimeTypes(c, true, GENERAL_MIME_TYPE);
    }

    /**
     * A few items need a list of video callable mime types in a comma delimited list.
     * Since we are already querying all the plugins. We can easily build this list ahead of time.
     *
     * Items that require this should subscribe and grab this updated list when needed.
     * @return string of enabled video callable mime types
     */
    public static String getAllEnabledVideoCallableMimeTypes(Context c) {
        return getMimeTypes(c, true, VIDEO_MIME_TYPE);
    }

    public static String getAllEnabledImMimeTypes(Context c) {
        return getMimeTypes(c, true, IM_MIME_TYPE);
    }

    public static Set<String> getAllEnabledVideoImMimeSet(Context c) {
        String[] videoMimes = getAllEnabledVideoCallableMimeTypes(c).split(",");
        String[] imMimes = getAllEnabledImMimeTypes(c).split(",");
        HashSet<String> mimeSet = new HashSet<>();

        if (videoMimes != null) {
            mimeSet.addAll(Arrays.asList(videoMimes));
        }
        if (imMimes != null) {
            mimeSet.addAll(Arrays.asList(imMimes));
        }
        return mimeSet;
    }

    public static Set<String> getAllEnabledVoiceMimeSet(Context c) {
        String[] mimes = getAllEnabledMimeTypes(c).split(",");
        HashSet<String> mimeSet = new HashSet<>();
        if (mimes != null) {
            mimeSet.addAll(Arrays.asList(mimes));
        }
        return mimeSet;
    }


}
