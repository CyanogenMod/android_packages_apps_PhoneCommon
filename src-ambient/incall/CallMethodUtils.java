package com.android.phone.common.incall;

import android.content.ComponentName;
import android.content.Context;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.android.contacts.common.model.AccountTypeManager;
import com.android.contacts.common.model.account.AccountType;
import com.android.contacts.common.model.account.AccountWithDataSet;
import com.android.phone.common.R;

import java.util.ArrayList;
import java.util.List;

import static com.cyanogen.ambient.incall.util.InCallHelper.NO_COLOR;

/**
 * Basic Utils for call method modifications
 */
public class CallMethodUtils {
    private final static String TAG = CallMethodUtils.class.getSimpleName();
    private final static boolean DEBUG = false;
    public final static String PREF_SPINNER_COACHMARK_SHOW = "pref_spinner_coachmark_shown";
    public final static String PREF_LAST_ENABLED_PROVIDER = "pref_last_enabled_provider";
    public final static String PREF_INTERNATIONAL_CALLS = "pref_international_calls";
    public final static String PREF_WIFI_CALL = "pref_wifi_call";

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

    /*
    * Look up the currently logged in plugin account in case plugin fails to return a valid
    * account handle
    */
    public static String lookupAccountHandle(Context context, String targetAccountType) {
        // Gather account handles logged into the device as a backup, in case
        // plugins fail to return the account handle even when it reports its
        // state as authenticated
        AccountTypeManager accountTypes = AccountTypeManager.getInstance(context);
        List<AccountWithDataSet> accounts = accountTypes.getAccounts(false);
        ArrayMap<String, String> accountMap = new ArrayMap<String, String>();

        for (AccountWithDataSet account : accounts) {
            AccountType accountType =
                    accountTypes.getAccountType(account.type, account.dataSet);
            if (accountType.isExtension() && !account.hasData(context)) {
                // Hide extensions with no raw_contacts.
                continue;
            }
            if (DEBUG) {
                Log.d(TAG, "account.type: " + account.type + "account.name: " + account.name);
            }
            // currently only handle one account per account type use case
            accountMap.put(account.type, account.name);
        }
        return accountMap.containsKey(targetAccountType) ? accountMap.get(targetAccountType) : "";
    }

    /*
    * Return if the plugin is a soft logged-out state (authenticated is false and there's still
    * an account saved in account manager)
    */
    public static boolean isSoftLoggedOut(Context context, CallMethodInfo cmi) {
        return (!cmi.mIsAuthenticated && !TextUtils.isEmpty(lookupAccountHandle(context,
                cmi.mAccountType)));
    }
}
