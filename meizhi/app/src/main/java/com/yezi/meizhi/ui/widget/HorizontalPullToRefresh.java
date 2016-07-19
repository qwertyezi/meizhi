package com.yezi.meizhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

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

    public static final int STATUS_INIT = 1;
    public static final int STATUS_PULL = 2;
    public static final int STATUS_RELEASE = 3;
    public static final int STATUS_LOADING = 4;

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
        if (childCount != 2) {
            throw new IllegalStateException("HorizontalPullToRefresh must have two children!");
        }
        mHeader = getChildAt(0);
        mContent = getChildAt(1);

        if (mHeader != null) {
            mHeader.bringToFront();
        }
        super.onFinishInflate();
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
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("LUCK", "-->down");
                mLastX = event.getX();
                mScrollRunnable.abortIfWorking();
                return super.dispatchTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                Log.i("LUCK", "-->move");
                mDeltaX = event.getX() - mLastX;
                mLastX = event.getX();
                if (mDeltaX > 0 && mHeader != null && mHptrHandler.canRefresh() ||
                        mCurrentStatus == STATUS_PULL) {
                    mCurrentStatus = STATUS_PULL;
                    moveViews((int) (mDeltaX / mResistance));
                    return true;
                }
                return super.dispatchTouchEvent(event);
            case MotionEvent.ACTION_UP:
                Log.i("LUCK", "-->up");
                if (mHeader.getLeft() != -mHeaderWidth && mHeader != null &&
                        mHptrHandler.canRefresh()) {
                    releaseViews();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i("LUCK", "-->cancel");
                if (mHeader.getLeft() != -mHeaderWidth && mHeader != null &&
                        mHptrHandler.canRefresh()) {
                    releaseViews();
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public interface HptrHandler {
        boolean canRefresh();

        void moveOffset(int status, int offset);

        void completeMove(int status);
    }

    private HptrHandler mHptrHandler;

    public void setHptrHandler(HptrHandler hptrHandler) {
        mHptrHandler = hptrHandler;
    }

    private void releaseViews() {
        if (mHeader.getLeft() <= mRatioOfHeaderHeightToRefresh * mHeaderWidth - mHeaderWidth) {
            mCurrentStatus = STATUS_PULL;
            mScrollRunnable.tryToScrollTo(-mHeaderWidth, mDurationToClose);
        } else {
            mCurrentStatus = STATUS_RELEASE;
            mScrollRunnable.tryToScrollTo(
                    (int) (mRatioOfHeaderHeightToRefresh * mHeaderWidth - mHeaderWidth),
                    mDurationToCloseHeader);
        }
    }

    private void moveViews(int offset) {
        mHeader.offsetLeftAndRight(offset);
        mContent.offsetLeftAndRight(offset);
        if (mHptrHandler != null) {
            mHptrHandler.moveOffset(mCurrentStatus,
                    (int) ((float) mHeader.getLeft() / (float) mHeaderWidth * 180));
        }
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
                if (deltaX == -1 && mTotalScroll == mDistance) {
                    if (mCurrentStatus == STATUS_PULL) {
                        mCurrentStatus = STATUS_INIT;
                    }
                    if (mCurrentStatus == STATUS_RELEASE) {
                        mCurrentStatus = STATUS_LOADING;
                    }
                    if (mHptrHandler != null) {
                        mHptrHandler.completeMove(mCurrentStatus);
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
            mStart = mHeader.getLeft();
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
