package com.android.phone.common.incall.api;

import com.android.phone.common.ambient.TypedPendingResult;
import com.cyanogen.ambient.common.api.PendingResult;

/**
 * InCall Pending result types.
 */
public class InCallTypedResult extends TypedPendingResult {

    // Define some of our more generic Result types and mimes, all the mimes, so many mimes
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

    public InCallTypedResult(PendingResult pendingResult, int type) {
        super(pendingResult, type);
    }
}
