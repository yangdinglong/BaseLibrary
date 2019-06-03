
package com.roobo.baselibiray.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    private static Toast mToast;

    public static void toastL(Context context, int resId) {
        toast(context, resId, Toast.LENGTH_LONG);
    }

    public static void toastL(Context context, String message) {
        toast(context, message, Toast.LENGTH_LONG);
    }

    public static void toasts(Context context, String message) {
        toast(context, message, Toast.LENGTH_SHORT);
    }

    public static void toasts(Context context, int resId) {
        toast(context, resId, Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, String message, int duration) {
        /**mToast的复用*/
        if (mToast == null) {
            mToast = Toast.makeText(context, message, duration);
        } else {
            mToast.setText(message);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    public static void toast(Context context, int resId, int duration) {
        /**mToast的复用*/
        if (mToast == null) {
            mToast = Toast.makeText(context, resId, duration);
        } else {
            mToast.setText(resId);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    /***
     * 关闭Toast
     */
    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}
