package com.roobo.baselibiray.utils.net;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.roobo.baselibiray.HttpConstants;
import com.roobo.baselibiray.base.ui.BaseActivity;
import com.roobo.baselibiray.utils.SharedPreferencesUtil;
import com.roobo.baselibiray.utils.ToastUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public abstract class CommonResultListener<T> implements ResultListener {

    private static final String TAG = CommonResultListener.class.getSimpleName();

    public static final String CLASS_OBJECT = "Object";
    public static final String CLASS_STRING = "String";

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private Context mContext;

    public CommonResultListener(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    public void onSuccess(String result) {
        Type genericSuperclass = getClass().getGenericSuperclass();
        Type genericType;
        if (genericSuperclass instanceof ParameterizedType) {
            genericType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        } else {
            genericType = Object.class;
        }
        boolean returnJson = false;
        if (genericType instanceof Class) {
            switch (((Class) genericType).getSimpleName()) {
                case CLASS_OBJECT:
                case CLASS_STRING:
                    returnJson = true;
                    break;
                default:
                    break;
            }

            try {
                Log.d(TAG, "data result:" + result);
                if (returnJson) {
                    postResultSuccess((T) result);
                } else {
                    T t = (new Gson()).fromJson(result, genericType);
                    postResultSuccess(t);
                }
            } catch (Exception e) {
                Log.e(TAG, "call back exception!", e);
                postResultFailed(HttpConstants.CODE_DEFAULT_ERROR, e.getMessage());
            }

        } else {
            postResultFailed(HttpConstants.CODE_DEFAULT_ERROR, HttpConstants.MSG_DEFAULT);
        }
    }

    private void postResultSuccess(final T t) {
        Log.d(TAG, "[postResultSuccess]");
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                onResultSuccess(t);
            }
        });
    }

    private void postResultFailed(final int code, final String msg) {
        Log.d(TAG, "[postResultFailed] code:" + code + " msg:" + msg);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (code == HttpConstants.CODE_TOKEN_EXPIRE) {//token超时
                    onResultFail(code, msg);
                    postTokenExpired();
                } else {
                    onResultFail(code, msg);
                }
            }
        });
    }

    @Override
    public void onFail(int code, String msg) {
        postResultFailed(code, msg);
    }

    public abstract void onResultSuccess(T response);

    public abstract void onResultFail(int code, String msg);

    private void postTokenExpired() {
        SharedPreferencesUtil.setUserId(mContext, "");
        SharedPreferencesUtil.setUserName(mContext, "");
        SharedPreferencesUtil.setCompanyName(mContext, "");
        SharedPreferencesUtil.setCompanyId(mContext, "");
        SharedPreferencesUtil.setToken(mContext, "");
        SharedPreferencesUtil.setRoleCode(mContext, -1);
        SharedPreferencesUtil.setPwd(mContext, "");
        ToastUtil.toasts(mContext, HttpConstants.MSG_TOKEN_EXPIRE);

    }

}
