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

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.provider.ContactsContract;
import android.telephony.SubscriptionManager;
import android.util.Log;
import com.android.phone.common.ambient.AmbientConnection;
import com.android.phone.common.R;
import com.android.phone.common.util.StartInCallCallReceiver;
import com.cyanogen.ambient.incall.InCallServices;
import com.cyanogen.ambient.incall.extension.CreditBalance;
import com.cyanogen.ambient.incall.extension.CreditInfo;
import com.cyanogen.ambient.incall.extension.StartCallRequest;
import com.cyanogen.ambient.incall.extension.SubscriptionInfo;
import com.google.common.base.Objects;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public class CallMethodInfo {

    public String mId;
    public UserHandle mUserHandle;
    public ComponentName mComponent;
    public String mName;
    public String mSummary;
    public int mSlotId;
    public int mSubId;
    public int mColor;
    public int mStatus;
    public boolean mIsAuthenticated;
    public String mMimeType;
    public String mVideoCallableMimeType;
    public String mImMimeType;
    public String mSubscriptionButtonText;
    public String mCreditButtonText;
    public String mT9HintDescription;
    public PendingIntent mSettingsIntent;
    /* Plugin's simple brand icon (24dp x 24dp)
       Expected format: Vector Drawable (.xml)
       2 colors allowed. */
    public Drawable mBrandIcon;
    /* Plugin's single color simple brand icon (24dp x 24dp)
       Same as to pluginBrandIcon, but only a single color.
       Please keep image single color, it will be tinted in the client application.
       Expected format: Vector Drawable (.xml)
       1 color allowed. */
    public Drawable mSingleColorBrandIcon;
    /* Plugin's attribution badge (17dp x 17dp)
       Similar to pluginBrandIcon.
       To make badge pop against (on top of) images, please add a white outline.
       Expected format: Vector Drawable (.xml)
       2 colors allowed. */
    public Drawable mBadgeIcon;
    /* Plugin's full brand icon (any width (max 360dp) x 60dp height)
       May contain full brand name or icon image.
       Please keep image single color, it will be tinted in the client application.
       Expected format: Vector Drawable (.xml)
       1 color allowed. */
    public Drawable mLoginIcon;
    /* Plugin's first action icon (24dp x 24dp)
       This icon is used in conjunction with pluginActionOneTitle.
       Expected format: Vector Drawable (.xml)
       2 colors allowed. */
    public Drawable mActionOneIcon;
    /* Plugin's second action icon (24dp x 24dp)
       This icon is used in conjunction with pluginActionTwoTitle.
       Expected format: Vector Drawable (.xml)
       2 colors allowed. */
    public Drawable mActionTwoIcon;
    public Resources pluginResources;
    public String mActionOneText;
    public String mActionTwoText;
    public boolean mIsInCallProvider;
    public PendingIntent mManageCreditIntent;
    public CreditInfo mProviderCreditInfo;
    public float mCreditWarn = 0.0f;
    public PendingIntent mLoginIntent;
    public PendingIntent mDefaultDirectorySearchIntent; // empty contact Uri
    public PendingIntent mDirectorySearchIntent;
    public PendingIntent mInviteIntent;
    public String mAccountType;
    private int mCurrencyAmount;
    public String mAccountHandle;
    public int mBrandIconId; // resource ID
    public int mLoginIconId;

    public ComponentName mNudgeComponent;
    public String mLoginSubtitle;
    // Contact login nudge
    public boolean mLoginNudgeEnable;
    public String mLoginNudgeTitle;
    public String mLoginNudgeSubtitle;
    public String mLoginNudgeActionText;
    // Contact install nudge
    public boolean mInstallNudgeEnable;
    public String mInstallNudgeTitle;
    public String mInstallNudgeSubtitle;
    public String mInstallNudgeActionText;
    public String mDependentPackage;
    /* Plugin's IM action icon (24dp x 24dp)
       Expected format: Vector Drawable (.xml)
       1 color allowed. */
    public Drawable mImIcon;
    /* Plugin's video call action icon (24dp x 24dp)
       Expected format: Vector Drawable (.xml)
       1 color allowed. */
    public Drawable mVideoIcon;
    /* Plugin's voice call action icon (24dp x 24dp)
       Expected format: Vector Drawable (.xml)
       1 color allowed. */
    public Drawable mVoiceIcon;
    private static CallMethodInfo sEmergencyCallMethod;

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mComponent, mName, mSummary, mSlotId, mSubId, mColor,
                mMimeType, mVideoCallableMimeType, mSubscriptionButtonText, mCreditButtonText,
                mT9HintDescription, mSettingsIntent, mBrandIcon, mSingleColorBrandIcon, mBadgeIcon,
                mLoginIcon, mActionOneIcon, mActionTwoIcon, pluginResources, mActionOneText,
                mActionTwoText, mIsInCallProvider, mLoginIntent, mDefaultDirectorySearchIntent,
                mDirectorySearchIntent, mInviteIntent, mAccountType, mAccountHandle, mBrandIconId,
                mLoginIconId, mNudgeComponent, mLoginSubtitle, mLoginNudgeEnable,
                mLoginNudgeTitle, mLoginNudgeSubtitle, mLoginNudgeActionText,
                mInstallNudgeEnable, mInstallNudgeTitle, mInstallNudgeSubtitle,
                mInstallNudgeActionText, mDependentPackage, mImIcon, mVideoIcon, mVoiceIcon,
                mIsAuthenticated);
    }

    public static final String TAG = "CallMethodInfo";

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof CallMethodInfo) {
            final CallMethodInfo info = (CallMethodInfo) object;
            return Objects.equal(this.mId, info.mId)
                    && Objects.equal(this.mComponent, info.mComponent)
                    && Objects.equal(this.mName, info.mName)
                    && Objects.equal(this.mSummary, info.mSummary)
                    && Objects.equal(this.mSlotId, info.mSlotId)
                    && Objects.equal(this.mSubId, info.mSubId)
                    && Objects.equal(this.mColor, info.mColor)
                    && Objects.equal(this.mMimeType, info.mMimeType)
                    && Objects.equal(this.mVideoCallableMimeType, info.mVideoCallableMimeType)
                    && Objects.equal(this.mSubscriptionButtonText, info.mSubscriptionButtonText)
                    && Objects.equal(this.mCreditButtonText, info.mCreditButtonText)
                    && Objects.equal(this.mT9HintDescription, info.mT9HintDescription)
                    && Objects.equal(this.mSettingsIntent, info.mSettingsIntent)
                    && Objects.equal(this.mBrandIcon, info.mBrandIcon)
                    && Objects.equal(this.mSingleColorBrandIcon, info.mSingleColorBrandIcon)
                    && Objects.equal(this.mBadgeIcon, info.mBadgeIcon)
                    && Objects.equal(this.mLoginIcon, info.mLoginIcon)
                    && Objects.equal(this.mActionOneIcon, info.mActionOneIcon)
                    && Objects.equal(this.mActionTwoIcon, info.mActionTwoIcon)
                    && Objects.equal(this.pluginResources, info.pluginResources)
                    && Objects.equal(this.mActionOneText, info.mActionOneText)
                    && Objects.equal(this.mActionTwoText, info.mActionTwoText)
                    && Objects.equal(this.mIsInCallProvider, info.mIsInCallProvider)
                    && Objects.equal(this.mLoginIntent, info.mLoginIntent)
                    && Objects.equal(this.mDefaultDirectorySearchIntent,
                        info.mDefaultDirectorySearchIntent)
                    && Objects.equal(this.mDirectorySearchIntent, info.mDirectorySearchIntent)
                    && Objects.equal(this.mInviteIntent, info.mInviteIntent)
                    && Objects.equal(this.mAccountType, info.mAccountType)
                    && Objects.equal(this.mAccountHandle, info.mAccountHandle)
                    && Objects.equal(this.mBrandIconId, info.mBrandIconId)
                    && Objects.equal(this.mLoginIconId, info.mLoginIconId)
                    && Objects.equal(this.mNudgeComponent, info.mNudgeComponent)
                    && Objects.equal(this.mLoginSubtitle, info.mLoginSubtitle)
                    && Objects.equal(this.mLoginNudgeEnable, info.mLoginNudgeEnable)
                    && Objects.equal(this.mLoginNudgeTitle, info.mLoginNudgeTitle)
                    && Objects.equal(this.mLoginNudgeSubtitle, info.mLoginNudgeSubtitle)
                    && Objects.equal(this.mLoginNudgeActionText, info.mLoginNudgeActionText)
                    && Objects.equal(this.mInstallNudgeEnable, info.mInstallNudgeEnable)
                    && Objects.equal(this.mInstallNudgeTitle, info.mInstallNudgeTitle)
                    && Objects.equal(this.mInstallNudgeSubtitle, info.mInstallNudgeSubtitle)
                    && Objects.equal(this.mInstallNudgeActionText, info.mInstallNudgeActionText)
                    && Objects.equal(this.mDependentPackage, info.mDependentPackage)
                    && Objects.equal(this.mImIcon, info.mImIcon)
                    && Objects.equal(this.mVideoIcon, info.mVideoIcon)
                    && Objects.equal(this.mVoiceIcon, info.mVoiceIcon);
        }
        return false;
    }

    /**
     * return empty mock call method that represents emergency call only mode
     */
    public static CallMethodInfo getEmergencyCallMethod(Context context) {
        Context ctx = context.getApplicationContext();
        if (sEmergencyCallMethod == null) {
            sEmergencyCallMethod = new CallMethodInfo();
            sEmergencyCallMethod.mName =
                    ctx.getString(R.string.call_method_spinner_item_emergency_call);
            sEmergencyCallMethod.mColor =
                    ctx.getResources().getColor(R.color.emergency_call_icon_color);
            sEmergencyCallMethod.mSubId = SubscriptionManager.INVALID_SUBSCRIPTION_ID;
            sEmergencyCallMethod.mSlotId = SubscriptionManager.INVALID_SIM_SLOT_INDEX;
            sEmergencyCallMethod.mIsInCallProvider = false;
        }

        return sEmergencyCallMethod;
    }

    public void placeCall(String origin, String number, Context c) {
        placeCall(origin, number, c, false);
    }

    public void placeCall(String origin, String number, Context c, boolean isVideoCall) {
        placeCall(origin, number, c, isVideoCall, false, null);
    }

    public void placeCall(String origin, String number, Context c, boolean isVideoCall, boolean
            forcePSTN) {
        placeCall(origin, number, c, isVideoCall, forcePSTN, null);
    }

    public void placeCall(String origin, String number, Context c, boolean isVideoCall,
                          boolean forcePSTN, String numberMimeType) {
        StartInCallCallReceiver svcrr = CallMethodHelper.getVoIPResultReceiver(this, origin);
        StartCallRequest request = new StartCallRequest(number, origin, 0, svcrr);

        if (isVideoCall) {
            InCallServices.getInstance().startVideoCall(
                    AmbientConnection.CLIENT.get(c), this.mComponent, request);
        } else {
            if (forcePSTN || (numberMimeType != null && numberMimeType.equals(ContactsContract
                    .CommonDataKinds.Phone.CONTENT_ITEM_TYPE))) {
                InCallServices.getInstance().startOutCall(
                        AmbientConnection.CLIENT.get(c), this.mComponent, request);
            } else {
                InCallServices.getInstance().startVoiceCall(
                        AmbientConnection.CLIENT.get(c), this.mComponent, request);
            }
        }
    }

    public String getCreditsDescriptionText(Resources r) {
        String ret = null;
        CreditInfo ci =  this.mProviderCreditInfo;

        List<SubscriptionInfo> subscriptionInfos = ci.subscriptions;

        if (subscriptionInfos != null && !subscriptionInfos.isEmpty()) {
            int size = subscriptionInfos.size();
            return r.getQuantityString(R.plurals.number_of_incall_subscriptions, size, size);
        } else {
            CreditBalance balance = ci.balance;
            if (balance != null) {
                mCurrencyAmount = (int) balance.balance;
                try {
                    if (balance.currencyCode != null) {
                        Currency currencyCode = Currency.getInstance(balance.currencyCode);
                        BigDecimal availableCredit = BigDecimal.valueOf(mCurrencyAmount,
                                currencyCode.getDefaultFractionDigits());


                         return currencyCode.getSymbol(r.getConfiguration().locale)
                                + availableCredit.toString();
                    } else {
                        throw new IllegalArgumentException();
                    }
                } catch (IllegalArgumentException e) {
                    Log.w(TAG, "Unable to retrieve currency code for plugin: " +
                            this.mComponent);

                    return null;
                }
            } else {
                Log.e(TAG, "Plugin has credit component but the balance and subscriptions are" +
                        " null. This should never happen. Not showing credit banner due to " +
                        "failures.");

                return null;
            }
        }
    }

    public boolean usesSubscriptions() {
        List<SubscriptionInfo> subscriptionInfos = this.mProviderCreditInfo.subscriptions;
        return subscriptionInfos != null && !subscriptionInfos.isEmpty();
    }

    public int getCurrencyAmount() {
        return mCurrencyAmount;
    }
}
