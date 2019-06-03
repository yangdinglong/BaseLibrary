package com.roobo.baselibiray.base.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.roobo.meetpro.R;
import com.roobo.meetpro.utils.StatusBarUtil;
import com.roobo.meetpro.utils.ToastUtil;

import butterknife.ButterKnife;

/**
 * @author li.xy
 * @ClassName: BaseActivity
 * @Description: 基础activity
 * @date 2014-6-18 下午5:23:25
 */
public abstract class BaseActivity extends Activity {

    public static String TAG = "";

    public static final String TAG_FINISH = "finishActivity";

    protected Resources mResources;

    protected ProgressDialog mProgressDialog;

    protected Handler mHandler = new Handler();

    public static final int DELAY_FINISH_ACTIVITY = 1000;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Log.d(TAG, "[onCreate]");
        initActivity();
        initTitleView();
        initView();
    }

    // 设置沉浸式状态栏
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setColor(this, mResources.getColor(R.color.title_bg_activity));
    }

    private void initActivity() {
        TAG = this.getClass().getSimpleName();
        mResources = getResources();
        // 取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 竖屏锁定
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutResID());
        setStatusBar();
        ButterKnife.bind(this);
        registerReceiver(mReceiver, new IntentFilter(TAG_FINISH));
    }

    protected abstract void initTitleView();

    protected abstract void initView();

    protected abstract int getLayoutResID();

    protected abstract void onViewClick(View view);

    public void showLoadingDialog() {
        if (isFinishing()) return;
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        if (!mProgressDialog.isShowing() && !isFinishing()) {
            mProgressDialog.show();
        }
    }

    public void showLoadingDialog(boolean isCancel) {
        if (isFinishing()) return;
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setCancelable(isCancel);
        if (!mProgressDialog.isShowing() && !isFinishing()) {
            mProgressDialog.show();
        }
    }

    public void showLoadingDialog(String message) {
        if (isFinishing()) return;
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setMessage(message);
        if (!mProgressDialog.isShowing() && !isFinishing()) {
            mProgressDialog.show();
        }
    }

    public void showLoadingDialog(String message, boolean isCancel) {
        if (isFinishing()) return;
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setCancelable(isCancel);
        mProgressDialog.setMessage(message);
        if (!mProgressDialog.isShowing() && !isFinishing()) {
            mProgressDialog.show();
        }
    }

    public void hideLoadingDialog() {
        if (mProgressDialog != null && !isFinishing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void showToast(String msg) {
        ToastUtil.toasts(getApplicationContext(), msg);
    }

    protected void showToast(int resId) {
        ToastUtil.toasts(getApplicationContext(), getResString(resId));
    }

    protected void showLToast(String msg) {
        ToastUtil.toastL(getApplicationContext(), msg);
    }

    protected void showLToast(int resId) {
        ToastUtil.toastL(getApplicationContext(), getResString(resId));
    }

    protected String getResString(int resId) {
        return getResources().getString(resId);
    }


    @Override
    protected void onStart() {
        Log.d(TAG, "[onStart]");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "[onRestart]");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "[onResume]");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "[onPause]");
        super.onPause();
        ToastUtil.cancelToast();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "[onStop]");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "[onDestroy]");
        super.onDestroy();
        ButterKnife.unbind(this);
        hideLoadingDialog();
        unregisterReceiver(mReceiver);
    }
}
