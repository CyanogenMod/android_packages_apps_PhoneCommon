package com.android.phone.common.nudge.api;

import com.android.phone.common.ambient.TypedPendingResult;
import com.cyanogen.ambient.common.api.PendingResult;
import com.cyanogen.ambient.discovery.util.NudgeKey;

import android.util.Log;

/**
 * InCall Pending result types.
 */
public class NudgeTypedResult extends TypedPendingResult {

    private static final String TAG = NudgeTypedResult.class.getSimpleName();

    public NudgeTypedResult(PendingResult pendingResult, int type) {
        super(pendingResult, type);
    }

    static int getNudgeTypeForKey(String key) {
        switch (key) {
            case NudgeKey.INCALL_CREDIT_NUDGE:
                return INCALL_CREDIT_NUDGE;
            case NudgeKey.INCALL_CONTACT_CARD_LOGIN:
                return INCALL_CONTACT_CARD_LOGIN;
            case NudgeKey.INCALL_CONTACT_CARD_DOWNLOAD:
                return INCALL_CONTACT_CARD_DOWNLOAD;
            case NudgeKey.INCALL_CONTACT_FRAGMENT_LOGIN:
                return INCALL_CONTACT_FRAGMENT_LOGIN;
            default:
                Log.e(TAG, "No nudge type!");
                return NONE;
        }
    }
}
