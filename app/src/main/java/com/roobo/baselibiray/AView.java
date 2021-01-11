package com.roobo.baselibiray;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

public class AView extends ViewGroup {
    public AView(Context context) {
        super(context);
    }

    public AView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AView(final Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                View cover=new View(context);
                cover.setLayoutParams(new LayoutParams());
                AView.this.addView(new View(context));
               cover.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        });
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }
}
