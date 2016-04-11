package com.android.phone.common.incall;

import android.app.PendingIntent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        setCallRateInformation(null, v, null, null, null, false, cpvl);
    }

    public static void setCallRateInformation(Resources res, ViewGroup creditsBar,
            String creditText, String buttonText, final PendingIntent buttonIntent,
            boolean warnIfLow, CreditBarVisibilityListener cpvl) {
        if (TextUtils.isEmpty(creditText) && TextUtils.isEmpty(buttonText) &&
                buttonIntent == null) {
            if (creditsBar.getVisibility() != View.GONE) {
                creditsBar.setVisibility(View.GONE);
                cpvl.creditsBarVisibilityChanged(View.GONE);
            }
            return;
        }
        if (creditsBar.getVisibility() != View.VISIBLE) {
            creditsBar.setVisibility(View.VISIBLE);
            cpvl.creditsBarVisibilityChanged(View.VISIBLE);
        }

        // These views already exist, we are hijacking them.
        TextView credit = (TextView) creditsBar.findViewById(R.id.ild_country);
        TextView button = (TextView) creditsBar.findViewById(R.id.ild_rate);

        if (res != null) {
            int color = warnIfLow ?
                    res.getColor(R.color.credit_banner_alert_color) :
                    res.getColor(R.color.credit_banner_text);
            credit.setTextColor(color);
        }

        credit.setText(creditText);
        button.setText(buttonText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (buttonIntent != null) {
                        buttonIntent.send();
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
        setCallRateInformation(r, v, creditText, buttonText, button, warnIfLow, cpvl);
    }
}
