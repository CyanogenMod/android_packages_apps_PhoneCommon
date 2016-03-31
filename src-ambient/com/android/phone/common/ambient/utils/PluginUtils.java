package com.android.phone.common.ambient.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;

/**
 * Utilites for Plugins
 */
public class PluginUtils {

    private static final String TAG = PluginUtils.class.getSimpleName();

    public static Resources getPluginResources(Context context, ComponentName componentName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getResourcesForApplication(componentName.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Plugin isn't installed: " + componentName.flattenToShortString());
            return null;
        }
    }

}
