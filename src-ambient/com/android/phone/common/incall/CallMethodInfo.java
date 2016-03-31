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
import android.telecom.PhoneAccountHandle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.phone.common.ambient.AmbientConnection;
import com.android.phone.common.R;
import com.android.phone.common.incall.listeners.AuthenticationListenerImpl;
import com.android.phone.common.incall.listeners.CallCreditListenerImpl;
import com.android.phone.common.incall.utils.CallMethodUtils;
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
    public boolean mIsInCallProvider;
    public boolean mIsAuthenticated;
    public String mMimeType;
    public String mVideoCallableMimeType;
    public String mImMimeType;
    public String mSubscriptionButtonText;
    public String mCreditButtonText;
    public String mT9HintDescriptionNoCreditOrSub;
    public String mT9HintDescriptionHasCreditOrSub;
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
    /* Plugin's video call action icon (24dp x 24dp)
       Expected format: Vector Drawable (.xml)
       1 color allowed. */
    public Drawable mVideoIcon;
    /* Plugin's IM action icon (24dp x 24dp)
       Expected format: Vector Drawable (.xml)
       1 color allowed. */
    public Drawable mImIcon;
    /* Plugin's voice call action icon (24dp x 24dp)
       Expected format: Vector Drawable (.xml)
       1 color allowed. */
    public Drawable mVoiceIcon;
    public CreditInfo mProviderCreditInfo;
    public float mCreditWarn = 0.0f;
    private int mCurrencyAmount;
    private CallCreditListenerImpl mCreditListener;
    public PendingIntent mManageCreditIntent;
    public PendingIntent mLoginIntent;
    public PendingIntent mDefaultDirectorySearchIntent; // empty contact Uri
    public PendingIntent mDirectorySearchIntent;
    public PendingIntent mInviteIntent;
    public PendingIntent mSettingsIntent;
    private static CallMethodInfo sEmergencyCallMethod;
    private AuthenticationListenerImpl mAuthListener;
    public String mAccountType;
    public String mDependentPackage;
    public String mLoginSubtitle;
    public String mAccountHandle;
    public int mLoginIconId; // resource ID
    public int mBrandIconId; // resource ID
    // Contact install nudge
    public boolean mInstallNudgeEnable;
    public String mInstallNudgeSubtitle;
    public String mInstallNudgeActionText;
    // Contact login nudge
    public boolean mLoginNudgeEnable;
    public String mLoginNudgeSubtitle;
    public String mLoginNudgeActionText;

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mComponent, mName, mSlotId, mSubId);
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
                    && Objects.equal(this.mSlotId, info.mSlotId)
                    && Objects.equal(this.mSubId, info.mSubId);
        }
        return false;
    }

    /**
     * return empty mock call method that represents emergency call only mode
     */
    public static CallMethodInfo getEmergencyCallMethod(Context ctx) {
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

    public static PhoneAccountHandle getPhoneAccountHandleFromCallMethodInfo(Context ctx,
            CallMethodInfo callMethodInfo, String number) {
        CallMethodInfo emergencyCallMethod = getEmergencyCallMethod(ctx);
        // If no sim is selected, or emergency callmethod selected, or number is
        // an emergency number, phone account handle should be null, and will use the
        // default account.
        // Else, create PhoneAccountHandle from selected callmethod components and
        // initial call using that account.
        PhoneAccountHandle handle = null;
        if (callMethodInfo != null && !callMethodInfo.mIsInCallProvider &&
                !callMethodInfo.equals(emergencyCallMethod) &&
                (number == null || !PhoneNumberUtils.isEmergencyNumber(number))) {
            handle = new PhoneAccountHandle(callMethodInfo.mComponent,
                    callMethodInfo.mId,
                    callMethodInfo.mUserHandle);
        }
        return handle;
    }

    public void placeCall(String origin, String number, Context c) {
        placeCall(origin, number, c, false);
    }

    public void placeCall(String origin, String number, Context c, boolean isVideoCall) {
        placeCall(origin, number, c, isVideoCall, false, null, null);
    }

    public void placeCall(String origin, String number, Context c, boolean isVideoCall, boolean
            forcePSTN) {
        placeCall(origin, number, c, isVideoCall, forcePSTN, null, null);
    }

    public void placeCall(String origin, String number, Context c, boolean isVideoCall, boolean
            forcePSTN, StartInCallCallReceiver.InCallCallListener listener) {
        placeCall(origin, number, c, isVideoCall, forcePSTN, null, listener);
    }

    public void placeCall(String origin, String number, Context c, boolean isVideoCall,
                          boolean forcePSTN, String numberMimeType) {
        placeCall(origin, number, c, isVideoCall, forcePSTN, numberMimeType, null);
    }

    public void placeCall(String origin, String number, Context c, boolean isVideoCall,
            boolean forcePSTN, String numberMimeType,
            StartInCallCallReceiver.InCallCallListener listener) {

        StartInCallCallReceiver svcrr
                = CallMethodUtils.getVoIPResultReceiver(c, this, origin, listener);
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
        CreditInfo ci =  this.mProviderCreditInfo;

        List<SubscriptionInfo> subscriptionInfos = ci.subscriptions;

        if (showSubscriptions()) {
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

    public boolean hasAvailableCredit() {
        boolean hasAvailableCredit = false;
        CreditInfo ci =  this.mProviderCreditInfo;
        if (ci != null) {
            CreditBalance balance = ci.balance;
            if (balance != null) {
                mCurrencyAmount = (int) balance.balance;
                hasAvailableCredit = mCurrencyAmount > 0;
            }
        }
        return hasAvailableCredit;
    }

    public boolean hasSubscription() {
        boolean hasSubscription = false;
        CreditInfo ci =  this.mProviderCreditInfo;
        if (ci != null) {
            List<SubscriptionInfo> subscriptionInfos = ci.subscriptions;
            hasSubscription = subscriptionInfos != null && !subscriptionInfos.isEmpty();
        }
        return hasSubscription;
    }

    public boolean showSubscriptions() {
        // If the user has > 0 credits, we don't want to show the subscription.
        boolean hasCredits = hasAvailableCredit();
        boolean hasSubscriptions = hasSubscription();

        return !hasCredits && hasSubscriptions;
    }

    public int getCurrencyAmount() {
        return mCurrencyAmount;
    }

    /**
     * Returns hint text based on credit and subscription availability.
     * If either is null or empty, use the non-null/non-empty string.
     * Null is returned if both are null or empty.
     * @return hintText String hint text for the current situation
     */
    public String getHintText() {
        String hintText = null;
        boolean hasAvailableCredit = hasAvailableCredit();
        boolean usesSubscriptions = hasSubscription();
        if (!hasAvailableCredit && !usesSubscriptions) {
            // No credits and no subscription
            if (!TextUtils.isEmpty(mT9HintDescriptionNoCreditOrSub)) {
                // Use no credits or subscription hint
                hintText = mT9HintDescriptionNoCreditOrSub;
            } else if (!TextUtils.isEmpty(mT9HintDescriptionHasCreditOrSub)) {
                // If no credit/sub hint empty, but has credit/sub hint valid, use it
                hintText = mT9HintDescriptionHasCreditOrSub;
            }
        } else {
            // Has credits or subscription
            if (!TextUtils.isEmpty(mT9HintDescriptionHasCreditOrSub)) {
                // Use has credits/sub hint text
                hintText = mT9HintDescriptionHasCreditOrSub;
            } else if (!TextUtils.isEmpty(mT9HintDescriptionNoCreditOrSub)) {
                // If has credit/sub hint empty, but no credit/sub hint valid, use it
                hintText = mT9HintDescriptionNoCreditOrSub;
            }
        }
        return hintText;
    }

    public CallCreditListenerImpl getCreditListener() {
        return mCreditListener;
    }

    public AuthenticationListenerImpl getAuthListener() {
        return mAuthListener;
    }

    public void setCreditListener(CallCreditListenerImpl creditListener) {
        mCreditListener = creditListener;
    }

    public void setAuthListener(AuthenticationListenerImpl authListener) {
        mAuthListener = authListener;
    }

}
