package com.android.phone.common.ambient;

import com.cyanogen.ambient.common.api.PendingResult;

/**
 * PendingResult wrapped with a Type
 *
 * Some queries return the same pending result type. We need a way to differentiate these items to
 * know how to process them.
 *
 * Each API should implement an object that extends this and has their static types.
 */
public class TypedPendingResult {

    PendingResult mPendingResult;
    int mType;

    public static final int NONE = 0;

    public TypedPendingResult(PendingResult pendingResult, int type) {
        this.mPendingResult = pendingResult;
        this.mType = type;
    }
}
