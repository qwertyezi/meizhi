package com.yezi.meizhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import in.srain.cube.views.ptr.PtrFrameLayout;

public class VPtrFrameLayout extends PtrFrameLayout {

    private RefreshHeader mRefreshHeader;

    public VPtrFrameLayout(Context context) {
        this(context, null);
    }

    public VPtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VPtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initViews();
    }

    private void initViews() {
        mRefreshHeader = new RefreshHeader(getContext());
        setHeaderView(mRefreshHeader);
        addPtrUIHandler(mRefreshHeader);
    }
}
