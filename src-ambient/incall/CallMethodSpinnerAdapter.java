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

package com.android.phone.common.incall;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.android.phone.common.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cyanogen.ambient.incall.util.InCallHelper.NO_COLOR;

public class CallMethodSpinnerAdapter extends ArrayAdapter<CallMethodInfo>
        implements SpinnerAdapter {
    private static final String TAG = CallMethodSpinnerAdapter.class.getSimpleName();

    private final Context mContext;
    private Map<String, Integer> mComponentMap;

    public CallMethodSpinnerAdapter(Context context, List<CallMethodInfo> objects) {
        super(context, 0, objects);
        mContext = context.getApplicationContext();
        processCallMethods(objects);
    }

    /**
     * Remove all elements from the list.
     */
    @Override
    public void clear() {
        super.clear();
        if (mComponentMap != null) {
            mComponentMap.clear();
        }
    }

    /**
     * Adds the specified call method info at the end of the array.
     *
     * @param object The call method info entry to add at the end of the array.
     */
    public void add(CallMethodInfo object) {
        super.add(object);
        processCallMethod(object);
    }

    /**
     * Adds the specified list of call method infos at the end of the array.
     *
     * @param objects The list of call method info entries to add at the end of the array.
     */
    public void addAll(List<CallMethodInfo> objects) {
        super.addAll(objects);
        processCallMethods(objects);
    }

    /**
     * Adds the specified list of call method infos at the end of the array.
     *
     * @param objects The list of call method info entries to add at the end of the array.
     */
    public void addAll(HashMap<ComponentName, CallMethodInfo> objects) {
        super.addAll(objects.values());
        processCallMethods(objects.values());
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose
     *                    view we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible
     *                    to convert this view to display the correct data, this method can create a
     *                    new view. Heterogeneous lists can specify their number of view types, so
     *                    that this View is always of the right type
     *                    (see {@link #getViewTypeCount()} and {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CallMethodInfo callMethodInfo = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.call_method_spinner_item, parent, false);
        }

        setIcon(convertView, callMethodInfo);

        return convertView;
    }

    /**
     * <p>Get a {@link View} that displays in the drop down popup
     * the data at the specified position in the data set.</p>
     *
     * @param position      index of the item whose view we want.
     * @param convertView   the old view to reuse, if possible. Note: You should
     *        check that this view is non-null and of an appropriate type before
     *        using. If it is not possible to convert this view to display the
     *        correct data, this method can create a new view.
     * @param parent the parent that this view will eventually be attached to
     * @return a {@link View} corresponding to the data at the
     *         specified position.
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        CallMethodInfo callMethodInfo = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.call_method_spinner_dropdown_item, parent, false);
        }

        setIcon(convertView, callMethodInfo);

        TextView text=(TextView) convertView.findViewById(R.id.call_method_spinner_item_text);
        text.setText(callMethodInfo.mName);

        return convertView;
    }

    private void setIcon(View convertView, CallMethodInfo callMethodInfo) {
        ImageView icon = (ImageView) convertView.findViewById(R.id.call_method_spinner_item_image);
        if (callMethodInfo.mBadgeIcon != null) {
            icon.setImageDrawable(callMethodInfo.mBadgeIcon);
            icon.getDrawable().setTintList(null);
            icon.setBackground(null);
        } else {
            int drawableId = getIconForSlot(callMethodInfo.mSlotId);

            Drawable forground = mContext.getDrawable(drawableId);
            Drawable background = mContext.getDrawable(R.drawable.ic_sim_backing);

            if (callMethodInfo.mColor != NO_COLOR) {
                forground.setTint(callMethodInfo.mColor);
            } else {
                forground.setTint(mContext.getResources().getColor(R.color.sim_icon_color));
            }

            Drawable[] layers = {background, forground};
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            icon.setImageDrawable(layerDrawable);
        }
    }

    public static int getIconForSlot(int slotId) {
        switch (slotId) {
            case -1:
                // SubscriptionManager.INVALID_SIM_SLOT_INDEX
                return R.drawable.ic_nosim;
            case 1:
                return R.drawable.ic_sim_2;
            case 2:
                return R.drawable.ic_sim_3;
            case 3:
                return R.drawable.ic_sim_4;
            default:
                return R.drawable.ic_sim_1;
        }
    }

    /**
     * Map call method component names to spinner positions.
     * @param callMethodInfo new call method
     */
    private void processCallMethod(CallMethodInfo callMethodInfo) {
        if (mComponentMap == null) {
            mComponentMap = new HashMap<String, Integer>();
        }
        if (callMethodInfo != null) {
            String key = getCallMethodKey(callMethodInfo);
            mComponentMap.put(key, mComponentMap.size());
        }
    }

    /**
     * Map call method component names to spinner positions.
     * @param callMethodInfoList list of current call methods
     */
    private void processCallMethods(Collection<CallMethodInfo> callMethodInfoList) {
        if (mComponentMap == null) {
            mComponentMap = new HashMap<String, Integer>();
        }
        if (callMethodInfoList != null) {
            for (CallMethodInfo info : callMethodInfoList) {
                processCallMethod(info);
            }
        }
    }

    /**
     * Returns the position of the specified component within the spinner
     * @param key String key for call method lookup
     * @return Position of specified component, 0 if not found.
     */
    public int getPosition(String key) {
        int position = 0;
        if (!TextUtils.isEmpty(key)) {
            position = mComponentMap.containsKey(key) ?
                    mComponentMap.get(key) : 0;
        }
        return position;
    }

    /**
     * Returns an ID for the specified CallMethodInfo
     * @param info CallMethodInfo to create id for
     * @return String key for specified CallMethodInfo.
     */
    public static String getCallMethodKey(CallMethodInfo info) {
        if (info == null || info.mComponent == null) {
            return null;
        }
        return info.mComponent.flattenToString() + String.valueOf(info.mSubId);
    }
}
