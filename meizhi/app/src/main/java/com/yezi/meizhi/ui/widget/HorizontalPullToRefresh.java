package com.yezi.meizhi.ui.widget;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class HorizontalPullToRefresh extends FrameLayout {

    private View mHeader;
    private View mContent;
    private float mResistance = 1.7f;
    private float mRatioOfHeaderHeightToRefresh = 1.2f;
    private int mDurationToClose = 200;
    private int mDurationToCloseHeader = 1000;
    private int mHeaderWidth = 0;
    private float mLastX;
    private float mDeltaX;
    private int mCurrentStatus = STATUS_INIT;
    private ScrollRunnable mScrollRunnable;
    private boolean mTouchCanRefresh = false;

    private static final int STATUS_INIT = 1;
    private static final int STATUS_PULL = 2;
    private static final int STATUS_RELEASE = 3;
    private static final int STATUS_LOADING = 4;

    public HorizontalPullToRefresh(Context context) {
        this(context, null);
    }

    public HorizontalPullToRefresh(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalPullToRefresh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount != 2) {
            throw new IllegalStateException("HorizontalPullToRefresh must have two children!");
        }
        mHeader = getChildAt(0);
        mContent = getChildAt(1);
    }

    private void init() {
        mScrollRunnable = new ScrollRunnable();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mHeader != null) {
            mHeaderWidth = mHeader.getMeasuredWidth();
            mHeader.layout(-mHeader.getMeasuredWidth(), 0, 0, mHeader.getMeasuredHeight());
        }
        if (mContent != null) {
            mContent.layout(0, 0, mContent.getMeasuredWidth(), mContent.getMeasuredHeight());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mCanRefresh.canRefresh();
    }

    public interface onCanRefresh {
        boolean canRefresh();
    }

    private onCanRefresh mCanRefresh;

    public void setOnCanRefresh(onCanRefresh canRefresh) {
        mCanRefresh = canRefresh;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                mDeltaX = event.getX() - mLastX;
                if (mDeltaX < 0) {
                    ((ViewPager)mContent).onTouchEvent(event);
                    return true;
                }
                mLastX = event.getX();
                moveViews();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mDeltaX = event.getX() - mLastX;
                mLastX = event.getX();
                releaseViews();
                break;
        }
        return true;
    }

    private void releaseViews() {
        if (mHeader.getLeft() <= mRatioOfHeaderHeightToRefresh * mHeaderWidth - mHeaderWidth) {
            mCurrentStatus = STATUS_PULL;
            mScrollRunnable.startScroll(-mHeaderWidth, mDurationToCloseHeader);
        } else {
            mCurrentStatus = STATUS_RELEASE;
            mScrollRunnable.startScroll((int) (mRatioOfHeaderHeightToRefresh * mHeaderWidth), mDurationToClose);
            mScrollRunnable.setOnFinishScrollListener(new onFinishScrollListener() {
                @Override
                public void onFinishScroll() {

                }
            });
        }
    }

    private void moveViews() {
        int offset = (int) (mDeltaX / mResistance);
        mHeader.offsetLeftAndRight(offset);
        mContent.offsetLeftAndRight(offset);
    }

    interface onFinishScrollListener {
        void onFinishScroll();
    }

    class ScrollRunnable implements Runnable {

        private int mTargetLeft;
        private int mDistance;
        private long mDuration;
        private long mStartTime;
        private onFinishScrollListener mFinishScrollListener;

        public void setOnFinishScrollListener(onFinishScrollListener listener) {
            mFinishScrollListener = listener;
        }

        @Override
        public void run() {
            if (mHeader.getLeft() > mTargetLeft) {
                int k = (int) ((SystemClock.currentThreadTimeMillis() - mStartTime) / mDuration);
                mHeader.offsetLeftAndRight(k * mDistance);
                mContent.offsetLeftAndRight(k * mDistance);
                mStartTime = SystemClock.currentThreadTimeMillis();
                HorizontalPullToRefresh.this.post(this);
            } else {
                if (mFinishScrollListener != null) {
                    mFinishScrollListener.onFinishScroll();
                }
            }
        }

        public void startScroll(int targetLeft, int duration) {
            mTargetLeft = targetLeft;
            mDistance = mTargetLeft - mHeader.getLeft();
            mDuration = duration;
            mStartTime = SystemClock.currentThreadTimeMillis();
            mFinishScrollListener = null;
            HorizontalPullToRefresh.this.post(this);
        }
    }
}
