package com.yezi.meizhi.ui.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yezi.meizhi.R;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

public class RefreshHeader extends FrameLayout implements PtrUIHandler {

    private ImageView mImageView;
    private AnimationDrawable mDrawable;
    private int mHeight;

    public RefreshHeader(Context context) {
        this(context,null);
    }

    public RefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.refresh_header, this);
        mImageView = (ImageView) view.findViewById(R.id.progress);
        mDrawable = (AnimationDrawable) mImageView.getDrawable();
    }

    private void startSlowRotateAnimation(int offset) {
        mImageView.setRotation(offset);
    }

    private void startFastRotateAnimation() {
        if (mDrawable != null) {
            mDrawable.start();
        }
    }

    private void stopFastRotateAnimation() {
        if (mDrawable != null) {
            mDrawable.stop();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        startFastRotateAnimation();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        stopFastRotateAnimation();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        float factor = (float) 45 / mHeight;
        startSlowRotateAnimation((int) (factor*ptrIndicator.getCurrentPosY()));
    }
}
