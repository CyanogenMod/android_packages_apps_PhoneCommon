package com.android.phone.common.incall;

import android.app.PendingIntent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.phone.common.R;
import com.android.phone.common.incall.CallMethodInfo;

/**
 * Helper method used to handle incall credit bars
 *
 * The credit bar's origin code comes from google. We've added a second credit bar in dialer that
 * uses these same helper method.
 */
public class CreditBarHelper {

    private static final String TAG = CreditBarHelper.class.getSimpleName();

    public interface CreditBarVisibilityListener {
        public void creditsBarVisibilityChanged(int visibility);
    }

    public static void clearCallRateInformation(ViewGroup v, CreditBarVisibilityListener cpvl) {
        setCallRateInformation(v, null, null, null, cpvl);
    }

    public static void setCallRateInformation(ViewGroup creditsBar, String countryName,
                                       String displayRate, final PendingIntent p,
                                       CreditBarVisibilityListener cpvl) {
        if (TextUtils.isEmpty(countryName) && TextUtils.isEmpty(displayRate) &&
                p == null) {
            creditsBar.setVisibility(View.GONE);
            cpvl.creditsBarVisibilityChanged(View.GONE);
            return;
        }
        creditsBar.setVisibility(View.VISIBLE);
        cpvl.creditsBarVisibilityChanged(View.VISIBLE);
        TextView ildCountry = (TextView) creditsBar.findViewById(R.id.ild_country);
        TextView ildRate = (TextView) creditsBar.findViewById(R.id.ild_rate);

        ildCountry.setText(countryName);
        ildRate.setText(displayRate);
        ildRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (p != null) {
                        p.send();
                    } else {
                        Log.wtf(TAG, "The intent we attempted to fire was null");
                    }
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void callMethodCredits(ViewGroup v, CallMethodInfo cmi, Resources r,
                                   CreditBarVisibilityListener cpvl) {
        String creditText = cmi.getCreditsDescriptionText(r);
        String buttonText = null;
        PendingIntent button;

        boolean warnIfLow = false;
        if (TextUtils.isEmpty(creditText)) {
            clearCallRateInformation(v, cpvl);
            return;
        } else {
            if (cmi.mIsAuthenticated) {
                button = cmi.mManageCreditIntent;
                if (cmi.showSubscriptions()) {
                    buttonText = cmi.mSubscriptionButtonText;
                } else {
                    if (cmi.getCurrencyAmount() <= cmi.mCreditWarn) {
                        warnIfLow = true;
                    }
                    buttonText = cmi.mCreditButtonText;
                }
            } else {
                buttonText = r.getString(R.string.sign_in_credit_banner_text);
                creditText = "";
                button = cmi.mLoginIntent;
            }
        }
        setCallRateInformation(v, creditText, buttonText, button, cpvl);
    }
}
