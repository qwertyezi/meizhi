package com.yezi.meizhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class DotTextView extends TextView {

    public DotTextView(Context context) {
        this(context, null);
    }

    public DotTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }
}
