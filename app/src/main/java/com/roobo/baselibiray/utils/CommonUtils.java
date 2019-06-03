package com.roobo.baselibiray.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by HP on 2019/3/19.
 */

public class CommonUtils {

    public static final String TAG = CommonUtils.class.getSimpleName();

    public static String getMD5Password(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return "";
        }
        return MD5Util.getInstance().getStringHash(pwd);
    }

    public static String toJsonString(Object value) {
        Gson gson = getGSON();
        return gson.toJson(value);
    }

    private static Gson getGSON() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson;
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        Log.d(TAG, "[getAppVersionName]");
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.d(TAG, "[getAppVersionName] Exception:" + e.getLocalizedMessage());
        }
        return versionName;
    }
}
