package com.yezi.meizhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class HorizontalPullToRefresh extends FrameLayout {

    private View mLeftHeader;
    private View mRightHeader;
    private View mContentView;
    private int mContentWidth = 0;
    private int mRightHeaderWidth = 0;
    private int mLeftHeaderWidth = 0;

    private float mResistance = 1.7f;
    private float mRatioOfHeaderHeightToRefresh = 1.2f;
    private int mDurationToClose = 200;
    private int mDurationToCloseHeader = 1000;
    private float mLastX;
    private float mDeltaX;
    private int mCurrentStatus = STATUS_INIT;
    private ScrollRunnable mScrollRunnable;

    public static final int STATUS_INIT = 1;
    public static final int STATUS_PULL = 2;
    public static final int STATUS_RELEASE = 3;
    public static final int STATUS_LOADING = 4;

    public static final int LEFT_HEADER = 5;
    public static final int RIGHT_HEADER = 6;

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
        int childCount = getChildCount();
        if (childCount != 3) {
            throw new IllegalStateException("HorizontalPullToRefresh must have three children!");
        }
        mLeftHeader = getChildAt(0);
        mRightHeader = getChildAt(1);
        mContentView = getChildAt(2);

        if (mLeftHeader != null) {
            mLeftHeader.bringToFront();
        }
        if (mRightHeader != null) {
            mRightHeader.bringToFront();
        }
        super.onFinishInflate();
    }

    private void init() {
        mScrollRunnable = new ScrollRunnable();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mLeftHeader != null) {
            mLeftHeaderWidth = mLeftHeader.getMeasuredWidth();
            mLeftHeader.layout(-mLeftHeader.getMeasuredWidth(), 0, 0, mLeftHeader.getMeasuredHeight());
        }
        if (mRightHeader != null) {
            mRightHeaderWidth = mRightHeader.getMeasuredWidth();
            mRightHeader.layout(mContentView.getMeasuredWidth(), 0,
                    mContentView.getMeasuredWidth() + mRightHeader.getMeasuredWidth(),
                    mRightHeader.getMeasuredHeight());
        }
        if (mContentView != null) {
            mContentWidth = mContentView.getMeasuredWidth();
            mContentView.layout(0, 0, mContentView.getMeasuredWidth(), mContentView.getMeasuredHeight());
        }
    }

    public interface HptrHandler {
        boolean canLeftRefresh();

        boolean canRightRefresh();

        void moveOffset(int leftOrRight, int status, int offset);

        void completeMove(int leftOrRight, int status);
    }

    private HptrHandler mHptrHandler;

    public void setHptrHandler(HptrHandler hptrHandler) {
        mHptrHandler = hptrHandler;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mScrollRunnable.abortIfWorking();
                return super.dispatchTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                mDeltaX = event.getX() - mLastX;
                mLastX = event.getX();
                if (mDeltaX > 0 && mLeftHeader != null && mHptrHandler.canLeftRefresh() ||
                        mDeltaX < 0 && mRightHeader != null && mHptrHandler.canRightRefresh() ||
                        mCurrentStatus == STATUS_PULL) {
                    mCurrentStatus = STATUS_PULL;
                    moveViews((int) (mDeltaX / mResistance));
                    return true;
                }

                return super.dispatchTouchEvent(event);
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mLeftHeader.getLeft() != -mLeftHeaderWidth && mLeftHeader != null &&
                        mHptrHandler.canLeftRefresh() ||
                        mRightHeader.getRight() != mContentWidth + mRightHeaderWidth &&
                                mRightHeader != null && mHptrHandler.canRightRefresh()) {
                    releaseViews();
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void releaseViews() {
        if (isLeftRefresh()) {
            if (mLeftHeader.getLeft() <= mRatioOfHeaderHeightToRefresh * mLeftHeaderWidth - mLeftHeaderWidth) {
                mCurrentStatus = STATUS_PULL;
                mScrollRunnable.tryToScrollTo(-mLeftHeaderWidth, mDurationToClose);
            } else {
                mCurrentStatus = STATUS_RELEASE;
                mScrollRunnable.tryToScrollTo(
                        (int) (mRatioOfHeaderHeightToRefresh * mLeftHeaderWidth - mLeftHeaderWidth),
                        mDurationToCloseHeader);
            }
        } else {
            if (mRightHeader.getLeft() >= mContentWidth - mRatioOfHeaderHeightToRefresh * mRightHeaderWidth) {
                mCurrentStatus = STATUS_PULL;
                mScrollRunnable.tryToScrollTo(-mLeftHeaderWidth, mDurationToClose);
            } else {
                mCurrentStatus = STATUS_RELEASE;
                mScrollRunnable.tryToScrollTo(
                        (int) (-mRatioOfHeaderHeightToRefresh * mRightHeaderWidth - mLeftHeaderWidth),
                        mDurationToCloseHeader);
            }
        }
    }

    private void moveViews(int offset) {
        mLeftHeader.offsetLeftAndRight(offset);
        mRightHeader.offsetLeftAndRight(offset);
        mContentView.offsetLeftAndRight(offset);
        if (mHptrHandler != null) {
            mHptrHandler.moveOffset(isLeftRefresh() ? LEFT_HEADER : RIGHT_HEADER, mCurrentStatus,
                    (int) ((float) mLeftHeader.getLeft() / (float) mLeftHeaderWidth * 180));
        }
    }

    private boolean isLeftRefresh() {
        return mDeltaX > 0 && mHptrHandler.canLeftRefresh();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mScrollRunnable != null) {
            mScrollRunnable.destroy();
        }
    }

    class ScrollRunnable implements Runnable {

        private int mLastFlingX;
        private Scroller mScroller;
        private boolean mIsRunning = false;
        private int mStart;
        private int mTo;
        private int mDistance;
        private int mTotalScroll = 0;

        public ScrollRunnable() {
            mScroller = new Scroller(getContext());
        }

        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curX = mScroller.getCurrX();
            int deltaX = curX - mLastFlingX;
            mTotalScroll += deltaX;
            if (!finish) {
                mLastFlingX = curX;
                moveViews(deltaX);
                post(this);

                //最后会产生一串delta为0，如果通过finish为true判断移动结束将会产生动画在视觉上的延迟
                if ((deltaX == -1 || deltaX == 1) && mTotalScroll == mDistance) {
                    if (mCurrentStatus == STATUS_PULL) {
                        mCurrentStatus = STATUS_INIT;
                    }
                    if (mCurrentStatus == STATUS_RELEASE) {
                        mCurrentStatus = STATUS_LOADING;
                    }
                    if (mHptrHandler != null) {
                        mHptrHandler.completeMove(isLeftRefresh() ? LEFT_HEADER : RIGHT_HEADER,
                                mCurrentStatus);
                    }
                }
            } else {
                finish();
            }
        }

        private void finish() {
            reset();
        }

        private void reset() {
            mIsRunning = false;
            mLastFlingX = 0;
            mDistance = 0;
            mTotalScroll = 0;
            removeCallbacks(this);
        }

        private void destroy() {
            reset();
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
        }

        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
            mStart = mLeftHeader.getLeft();
            mTo = to;
            mDistance = to - mStart;
            removeCallbacks(this);

            mLastFlingX = 0;

            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, mDistance, 0, duration);
            post(this);
            mIsRunning = true;
        }
    }
}
