package com.android.phone.common.incall.utils;

import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import com.android.phone.common.incall.CallMethodHelper;
import com.android.phone.common.incall.CallMethodInfo;
import com.cyanogen.ambient.plugin.PluginStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper for filtering call method objects in various ways.
 */
public class CallMethodFilters {

    /**
     * This is helpful for items that don't want to subscribe to updates or for things that
     * need a quick CMI and have a component name.
     * @param cn Component name wanted.
     * @return specific call method when given a component name.
     */
    public static CallMethodInfo getCallMethod(ComponentName cn, Context c) {
        return CallMethodHelper.INCALL.get(c).getModIfExists(cn);
    }

    /**
     * Helper method for subscribed clients to remove any item that is not enabled from the hashmap
     * @param input HashMap returned from a broadcast
     * @param output HashMap with only enabled items
     */
    public static void removeDisabled(HashMap<ComponentName, CallMethodInfo> input,
                                      HashMap<ComponentName, CallMethodInfo> output) {
        for (Map.Entry<ComponentName, CallMethodInfo> entry : input.entrySet()) {
            ComponentName key = entry.getKey();
            CallMethodInfo value = entry.getValue();

            if (value.mStatus == PluginStatus.ENABLED) {
                output.put(key, value);
            }
        }
    }

    public static HashMap<ComponentName, CallMethodInfo> getAllEnabledCallMethods(Context c) {
        HashMap<ComponentName, CallMethodInfo> cmi = new HashMap<>();
        removeDisabled(CallMethodHelper.INCALL.get(c).getModInfo(), cmi);
        return cmi;
    }

    public static HashMap<ComponentName, CallMethodInfo> getAllEnabledAndHiddenCallMethods
            (Context c) {
        HashMap<ComponentName, CallMethodInfo> cmi = new HashMap<>();
        for (Map.Entry<ComponentName, CallMethodInfo> entry : CallMethodHelper.INCALL.get(c)
                .getModInfo().entrySet()) {
            ComponentName key = entry.getKey();
            CallMethodInfo value = entry.getValue();

            if (value.mStatus == PluginStatus.ENABLED || value.mStatus == PluginStatus.HIDDEN) {
                cmi.put(key, value);
            }
        }
        return cmi;
    }


    public static CallMethodInfo getMethodForMimeType(String mimeType, boolean enableOnly,
                                                      Context c) {
        CallMethodInfo targetEntry = null;
        for (CallMethodInfo entry : CallMethodHelper.INCALL.get(c).getModInfo().values()) {
            // TODO: find out why mimetype may be null
            if (!TextUtils.isEmpty(entry.mMimeType)) {
                if (enableOnly && entry.mStatus != PluginStatus.ENABLED) {
                    continue;
                }
                if (entry.mMimeType.equals(mimeType)) {
                    targetEntry = entry;
                    break;
                }
            }
        }
        return targetEntry;
    }

}
