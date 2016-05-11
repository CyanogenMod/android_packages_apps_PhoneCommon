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

package com.android.phone.common.widget;

import android.annotation.Nullable;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.android.phone.common.R;

/**
 * Makes sure that the two children textviews share the width fairly.
 * If both text views fit within half the width, share 50/50.
 * If neither text view fits within half the width, share 50/50.
 * If one textview fits within half the width, give it enough space to show full string and give
 * remaining space to other string.
 */
public class ResizingILDContainerLayout extends LinearLayout {
    private View mIldCountry;
    private View mIldRate;

    public ResizingILDContainerLayout(Context context) {
        super(context);
    }

    public ResizingILDContainerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizingILDContainerLayout(Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ResizingILDContainerLayout(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIldCountry = findViewById(R.id.ild_country);
        mIldRate = findViewById(R.id.ild_rate);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mIldCountry.measure(0, 0);
        mIldRate.measure(0, 0);

        final LinearLayout.LayoutParams countryLayoutParams =
                (LinearLayout.LayoutParams) mIldCountry.getLayoutParams();
        final LinearLayout.LayoutParams rateLayoutParams =
                (LinearLayout.LayoutParams) mIldRate.getLayoutParams();
        final int width = View.MeasureSpec.getSize(widthMeasureSpec) -
                countryLayoutParams.leftMargin - countryLayoutParams.rightMargin -
                rateLayoutParams.leftMargin - rateLayoutParams.rightMargin;
        final int halfWidth = width / 2;
        int countryWidth = mIldCountry.getMeasuredWidth();
        int rateWidth = mIldRate.getMeasuredWidth();

        if (halfWidth <= countryWidth && halfWidth > rateWidth) {
            countryWidth = width - rateWidth;
        } else if (halfWidth > countryWidth && halfWidth <= rateWidth) {
            rateWidth = width - countryWidth;
        } else {
            // space equally
            countryWidth = rateWidth = halfWidth;
        }


        countryLayoutParams.width = countryWidth;
        rateLayoutParams.width = rateWidth;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}