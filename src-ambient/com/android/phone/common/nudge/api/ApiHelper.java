package com.android.phone.common.nudge.api;

import com.cyanogen.ambient.discovery.NudgeApi;
import com.cyanogen.ambient.discovery.NudgeServices;

/**
 * Helper for getting Nudge Api
 */
public class ApiHelper {

    static NudgeApi thisApi() {
        return NudgeServices.NudgeApi;
    }

}
