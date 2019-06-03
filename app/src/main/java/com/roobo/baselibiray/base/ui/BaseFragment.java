package com.roobo.baselibiray.base.ui;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roobo.meetpro.utils.ToastUtil;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    public static String TAG = "";

    protected Resources mResources;

    protected ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        TAG = this.getClass().getSimpleName();
        super.onCreate(savedInstanceState);
        Log.d(TAG, "[onCreate]");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "[onCreateView]");
        View view = inflater.inflate(getLayoutResID(), container, false);
        mResources = getResources();
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "[onViewCreated]");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "[onActivityCreated]");
    }

    protected abstract int getLayoutResID();

    protected abstract void init();

    protected abstract void onViewClick(View view);

    public void showLoadingDialog() {
        if (getActivity().isFinishing()) return;
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void hideLoadingDialog() {
        if (getActivity().isFinishing()) return;
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void showToast(String msg) {
        ToastUtil.toasts(getActivity().getApplicationContext(), msg);
    }

    protected void showToast(int resId) {
        ToastUtil.toasts(getActivity().getApplicationContext(), getResString(resId));
    }

    protected void showLToast(String msg) {
        ToastUtil.toastL(getActivity().getApplicationContext(), msg);
    }

    protected void showLToast(int resId) {
        ToastUtil.toastL(getActivity().getApplicationContext(), getResString(resId));
    }

    protected String getResString(int resId) {
        return getResources().getString(resId);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "[onStart]");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "[onResume]");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "[onPause]");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "[onStop]");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "[onDestroyView]");
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "[onDestroy]");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "[onDetach]");
    }
}
