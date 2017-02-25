package com.basil.bluetoothdevelopment.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Basil on 2017/2/18.
 */

public class GuideLinkageLayout extends FrameLayout {
    public GuideLinkageLayout(Context context) {
        super(context);
    }

    public GuideLinkageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GuideLinkageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            try {
                child.dispatchTouchEvent(ev);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
