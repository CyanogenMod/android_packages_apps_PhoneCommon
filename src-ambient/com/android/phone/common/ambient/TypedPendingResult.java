package com.android.phone.common.ambient;

import com.cyanogen.ambient.common.api.PendingResult;
import com.cyanogen.ambient.common.api.ResultCallback;

import java.util.concurrent.TimeUnit;

/**
 * PendingResult wrapped with a Type
 *
 * Some queries return the same pending result type. We need a way to differentiate these items to
 * know how to process them.
 */
public class TypedPendingResult {

    PendingResult mPendingResult;
    int mType;

    // None should be used when a unique Result will be returned from our ResultCallback.
    public static final int NONE = 0;

    // 1 - 1000 Reserved for INCALL API
    public static final int GENERAL_MIME_TYPE = 1;
    public static final int IM_MIME_TYPE = 2;
    public static final int VIDEO_MIME_TYPE = 3;

    public static final int SETTINGS_INTENT = 4;
    public static final int CREDIT_INTENT = 5;
    public static final int LOGIN_INTENT = 6;
    public static final int DEFAULT_DIRECTORY_SEARCH_INTENT = 7;

    public static final int GENERAL_DATA = 8;
    public static final int STATUS = 9;
    public static final int AUTHENTICATION = 10;
    public static final int CREDIT_INFO = 11;
    public static final int ACCOUNT_HANDLE = 12;
    public static final int HINT_TEXT = 13;

    // 1001 - 2000 Reserved for Nudges
    public static final int INCALL_CREDIT_NUDGE = 1001;
    public static final int INCALL_CONTACT_CARD_LOGIN = 1002;
    public static final int INCALL_CONTACT_CARD_DOWNLOAD = 1003;
    public static final int INCALL_CONTACT_FRAGMENT_LOGIN = 1004;

    // 2000 - 3000 Reserved for invite intents
    public static final int DIRECTORY_SEARCH_INTENT = 2000;
    public static final int INVITE_INTENT = 2001;

    public TypedPendingResult(PendingResult pendingResult, int type) {
        this.mPendingResult = pendingResult;
        this.mType = type;
    }

    public void setResultCallback(ResultCallback resultCallback, long length, TimeUnit unit) {
        this.mPendingResult.setResultCallback(resultCallback, length, unit);
    }

    public void setResultCallback(ResultCallback resultCallback) {
        this.mPendingResult.setResultCallback(resultCallback);
    }

    public PendingResult getPendingResult() {
        return mPendingResult;
    }

    public int getType() {
        return mType;
    }
}
