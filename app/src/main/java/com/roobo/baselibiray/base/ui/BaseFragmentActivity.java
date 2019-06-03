package com.roobo.baselibiray.base.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.roobo.meetpro.R;
import com.roobo.meetpro.utils.StatusBarUtil;
import com.roobo.meetpro.utils.ToastUtil;

import butterknife.ButterKnife;


public abstract class BaseFragmentActivity extends FragmentActivity {
    public static String TAG = "";

    public static final String TAG_FINISH="finishActivity";

    protected Resources mResources;

    protected ProgressDialog mProgressDialog;

    //是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    protected boolean useThemestatusBarColor = false;
    //是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置
    protected boolean useStatusBarColor = true;

    protected static final int DELAY_FINISH_ACTIVITY = 1000;

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Log.d(TAG, "[onCreate]");
        init();
        initTitleView();
        initView();
    }

    private void init() {
        TAG = this.getClass().getSimpleName();
        mResources = getResources();
        // 取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 竖屏锁定
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutResID());
        setStatusBar();
        ButterKnife.bind(this);
        registerReceiver(mReceiver,new IntentFilter(TAG_FINISH));
    }

    // 设置沉浸式状态栏
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setColor(this, mResources.getColor(R.color.title_bg_activity));
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
        unregisterReceiver(mReceiver);
    }
}
