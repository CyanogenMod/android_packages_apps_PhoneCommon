package com.android.phone.common.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Utility class around Image loading and Bitmap effects
 */
public class ImageUtils {

    /**
     * Used to convert a drawable into a bitmap.
     *
     * Drawables that don't have intrinsic dimensions are excluded from this conversion. This
     * includes drawables such as ColorDrawables
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        if (drawable.getIntrinsicHeight() <= 0 || drawable.getIntrinsicWidth() <= 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getMinimumHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
