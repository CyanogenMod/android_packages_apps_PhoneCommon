package com.android.phone.common.incall;

import android.content.Context;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import com.android.phone.common.R;

import java.util.ArrayList;
import java.util.List;

import static com.cyanogen.ambient.incall.util.InCallHelper.NO_COLOR;

/**
 * Basic Utils for call method modifications
 */
public class CallMethodUtils {

    public final static String PREF_SPINNER_COACHMARK_SHOW = "pref_spinner_coachmark_shown";
    public final static String PREF_LAST_ENABLED_PROVIDER = "pref_last_enabled_provider";
    public final static String PREF_INTERNATIONAL_CALLS = "pref_international_calls";

    public static CallMethodInfo getDefaultSimInfo(Context context) {
        final TelecomManager telecomMgr =
                (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        PhoneAccountHandle handle =
                telecomMgr.getDefaultOutgoingPhoneAccount(PhoneAccount.SCHEME_TEL);
        if (handle == null) {
            return null;
        }
        return phoneToCallMethod(context, handle);
    }

    public static List<CallMethodInfo> getSimInfoList(Context context) {
        final TelecomManager telecomMgr =
                (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        final List<PhoneAccountHandle> accountHandles = telecomMgr.getCallCapablePhoneAccounts();
        ArrayList<CallMethodInfo> callMethodInfoList = new ArrayList<CallMethodInfo>();
        for (PhoneAccountHandle accountHandle : accountHandles) {
            CallMethodInfo info = phoneToCallMethod(context, accountHandle);
            if (info != null) {
                callMethodInfoList.add(info);
            }
        }
        return callMethodInfoList;
    }

    private static String getPhoneAccountName(Context context, PhoneAccount phoneAccount,
                                              int slotId) {
        if (phoneAccount == null) {
            // Slot IDs are zero based
            return context.getString(R.string.call_method_spinner_item_unknown_sim, slotId + 1);
        } else if (phoneAccount.getLabel() != null) {
            return phoneAccount.getLabel().toString();
        }
        return null;
    }

    private static int getPhoneAccountColor(SubscriptionInfo info) {
        if (info != null) {
            return info.getIconTint();
        } else {
            return NO_COLOR;
        }
    }

    private static CallMethodInfo phoneToCallMethod(Context context,
                                                    PhoneAccountHandle phoneAccountHandle) {
        final SubscriptionManager subMgr = (SubscriptionManager) context
                .getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        final TelecomManager telecomMgr =
                (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        final TelephonyManager telephonyMgr =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneAccount phoneAccount = telecomMgr.getPhoneAccount(phoneAccountHandle);

        if (phoneAccount == null) {
            return null;
        }

        CallMethodInfo callMethodInfo = new CallMethodInfo();

        callMethodInfo.mComponent = phoneAccountHandle.getComponentName();
        callMethodInfo.mId = phoneAccountHandle.getId();
        callMethodInfo.mUserHandle = phoneAccountHandle.getUserHandle();
        callMethodInfo.mSubId = telephonyMgr.getSubIdForPhoneAccount(phoneAccount);
        callMethodInfo.mSlotId = SubscriptionManager.getSlotId(callMethodInfo.mSubId);
        callMethodInfo.mName = getPhoneAccountName(context, phoneAccount, callMethodInfo.mSlotId);
        callMethodInfo.mColor =
                getPhoneAccountColor(subMgr.getActiveSubscriptionInfo(callMethodInfo.mSubId));
        callMethodInfo.mIsInCallProvider = false;

        final int simState = telephonyMgr.getSimState(callMethodInfo.mSlotId);
        if ((simState == TelephonyManager.SIM_STATE_ABSENT) ||
                (simState == TelephonyManager.SIM_STATE_UNKNOWN)) {
            return null;
        }

        return callMethodInfo;
    }
}
