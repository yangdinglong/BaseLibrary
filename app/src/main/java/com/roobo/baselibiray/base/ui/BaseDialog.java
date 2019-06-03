package com.roobo.baselibiray.base.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.roobo.meetpro.R;

/**
 * Created by HP on 2019/3/18.
 */

public abstract class BaseDialog extends Dialog {

    protected Context mContext;

    public BaseDialog(Context context) {
        super(context, R.style.BaseDialog);
        this.mContext = context;
    }

    public abstract int getLayoutResId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(getLayoutResId());
            initViews();

            Window dialogWindow = getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.CENTER);

            lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度
            lp.height = WindowManager.LayoutParams.MATCH_PARENT; // 高度

            dialogWindow.setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void initViews();

    @Override
    public void show() {
        try {
            Context context = getContext();
            if (context != null && context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    return;
                }
            }
            if (isShowing()) {
                return;
            }
            super.show();
        } catch (Throwable t) {
        }
    }

    @Override
    public void dismiss() {
        try {
            Context context = getContext();
            if (context != null && context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    return;
                }
            }
            if (isShowing()) {
                super.dismiss();
            }
        } catch (Throwable t) {

        }
    }
}
