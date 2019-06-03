package com.roobo.baselibiray.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {
    private static String FILE_COMMON = "common";

    private static String KEY_USER_NAME = "KEY_USER_NAME";

    private static String KEY_USER_EMAIL = "KEY_USER_EMAIL";

    private static String KEY_USER_ID = "KEY_USER_ID";

    private static String KEY_COMPANY_NAME = "KEY_COMPANY_NAME";

    private static String KEY_COMPANY_ID = "KEY_COMPANY_ID";

    private static String KEY_ROLE_CODE = "KEY_ROLE_CODE";

    private static String KEY_TOKEN = "KEY_TOKEN";

    private static String KEY_HOST_URL = "KEY_HOST_URL";

    private static String KEY_IS_REGISTER_VOICE = "KEY_IS_REGISTER_VOICE";

    private static String KEY_REGISTER_VOICE_SIGNATURE = "KEY_REGISTER_VOICE_SIGNATURE";

    private static String KEY_PWD = "KEY_PWD";

    public static void setPwd(Context context, String val) {
        setStringValue(context, KEY_PWD, val);
    }

    public static String getPwd(Context context, String defaultValue) {
        return getStringValue(context, KEY_PWD, defaultValue);
    }


    public static void setRegisterVoiceSignature(Context context, String val) {
        setStringValue(context, KEY_REGISTER_VOICE_SIGNATURE, val);
    }

    public static String getRegisterVoiceSignature(Context context, String defaultValue) {
        return getStringValue(context, KEY_REGISTER_VOICE_SIGNATURE, defaultValue);
    }

    public static void setIsRegisterVoice(Context context, boolean val) {
        setBooleanValue(context, KEY_IS_REGISTER_VOICE, val);
    }

    public static boolean isRegisterVoice(Context context, boolean defaultValue) {
        return getBooleanValue(context, KEY_IS_REGISTER_VOICE, defaultValue);
    }

    public static String getHostUrl(Context context, String defaultValue) {
        return getStringValue(context, KEY_HOST_URL, defaultValue);
    }

    public static void setHostUrl(Context context, String val) {
        setStringValue(context, KEY_HOST_URL, val);
    }

    public static void setUserId(Context context, String val) {
        setStringValue(context, KEY_USER_ID, val);
    }

    public static String getUserId(Context context, String defaultValue) {
        return getStringValue(context, KEY_USER_ID, defaultValue);
    }

    public static void setUserEmail(Context context, String val) {
        setStringValue(context, KEY_USER_EMAIL, val);
    }

    public static String getUserEmail(Context context, String defaultValue) {
        return getStringValue(context, KEY_USER_EMAIL, defaultValue);
    }

    public static void setUserName(Context context, String val) {
        setStringValue(context, KEY_USER_NAME, val);
    }

    public static String getUserName(Context context, String defaultValue) {
        return getStringValue(context, KEY_USER_NAME, defaultValue);
    }

    public static void setCompanyName(Context context, String val) {
        setStringValue(context, KEY_COMPANY_NAME, val);
    }

    public static String getCompanyName(Context context, String defaultValue) {
        return getStringValue(context, KEY_COMPANY_NAME, defaultValue);
    }

    public static void setCompanyId(Context context, String val) {
        setStringValue(context, KEY_COMPANY_ID, val);
    }

    public static String getCompanyId(Context context, String defaultValue) {
        return getStringValue(context, KEY_COMPANY_ID, defaultValue);
    }

    public static void setRoleCode(Context context, int val) {
        setIntValue(context, KEY_ROLE_CODE, val);
    }

    public static int getRoleCodee(Context context, int defaultValue) {
        return getIntValue(context, KEY_ROLE_CODE, defaultValue);
    }

    public static void setToken(Context context, String val) {
        setStringValue(context, KEY_TOKEN, val);
    }

    public static String getToken(Context context, String defaultValue) {
        return getStringValue(context, KEY_TOKEN, defaultValue);
    }

    private static boolean getBooleanValue(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(FILE_COMMON, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }

    private static void setBooleanValue(Context context, String key, boolean val) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(FILE_COMMON, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean(key, val);
        editor.apply();
    }

    private static String getStringValue(Context context, String key, String defaultValue) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(FILE_COMMON, Context.MODE_PRIVATE);
        return preferences.getString(key, defaultValue);
    }

    private static void setStringValue(Context context, String key, String val) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(FILE_COMMON, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(key, val);
        editor.apply();
    }

    private static int getIntValue(Context context, String key, int defaultValue) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(FILE_COMMON, Context.MODE_PRIVATE);
        return preferences.getInt(key, defaultValue);
    }

    private static void setIntValue(Context context, String key, int val) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(FILE_COMMON, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putInt(key, val);
        editor.apply();
    }
}
