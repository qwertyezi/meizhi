package com.yezi.meizhi.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.yezi.meizhi.MeiZhiApp;
import com.yezi.meizhi.R;
import com.yezi.meizhi.utils.ScreenSizeUtil;

public class SideMenu extends FrameLayout {

    private static final float SCALE_FACTOR = 0.8f;

    private View leftView = null;
    private View mainView = null;
    private ViewDragHelper mDragHelper;
    private int mDragDistance;
    private Context mContext;
    private int mSensitivity;
    private float mDownX;
    private float mUpX;
    private onToggleListener mListener;

    public SideMenu(Context context) {
        this(context, null);
    }

    public SideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mDragHelper = ViewDragHelper.create(this, 1.0f, mCallback);
        mSensitivity = (int) (5 * mContext.getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("SideMenu must have two children!");
        }
        leftView = getChildAt(0);
        mainView = getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mainView != null) {
            mainView.layout(0, 0, mainView.getMeasuredWidth(), mainView.getMeasuredHeight());
        }
        if (leftView != null) {
            mDragDistance = leftView.getMeasuredWidth();
            leftView.layout(-leftView.getMeasuredWidth(), 0, 0, leftView.getMeasuredHeight());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isOpen()) {
            return event.getRawX() > mDragDistance || mDragHelper.shouldInterceptTouchEvent(event);
        } else {
            return (event.getRawX() < mContext.getResources().getDimensionPixelSize(R.dimen.edge_touch_width) &&
                    event.getRawY() < ScreenSizeUtil.getScreenHeight(mContext) -
                            MeiZhiApp.getAppResources().getDimensionPixelSize(R.dimen.edge_touch_height));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                if (!isOpen() && (event.getRawY() > ScreenSizeUtil.getScreenHeight(mContext) -
                        MeiZhiApp.getAppResources().getDimensionPixelSize(R.dimen.edge_touch_height) ||
                        event.getRawX() > MeiZhiApp.getAppResources().getDimensionPixelSize(R.dimen.edge_touch_width))) {
                    return false;
                }
                mDownX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isInLeftView(event)) {
                    mDragHelper.captureChildView(mainView, event.getPointerId(0));
                }
                break;
            case MotionEvent.ACTION_UP:
                mUpX = event.getRawX();
                break;
            default:
        }
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private boolean isInLeftView(MotionEvent event) {
        Rect rect = new Rect();
        leftView.getHitRect(rect);
        return rect.contains((int) event.getX(), (int) event.getY());
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mainView;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return Math.min(mDragDistance, Math.max(0, left));
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mDragDistance;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (mDownX > mDragDistance && mUpX > mDragDistance
                    && Math.abs(mUpX - mDownX) < mSensitivity) {
                toggle();
                return;
            }
            float minSpeed = mDragHelper.getMinVelocity();
            if (xvel > 0) {
                if (mainView.getX() >= getScreenWidth() / 2 || xvel > minSpeed) {
                    smoothOpen();
                } else {
                    smoothClose();
                }
            } else {
                if (mainView.getX() < getScreenWidth() / 2 || xvel < -minSpeed) {
                    smoothClose();
                } else {
                    smoothOpen();
                }
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mainView) {
                leftView.setX(left - mDragDistance);
                leftView.setScaleY(getScaleNumber());
                leftView.setScaleX(getScaleNumber());
                ViewCompat.postInvalidateOnAnimation(SideMenu.this);
            }
        }
    };

    private int getScreenWidth() {
        return ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
    }

    private void smoothOpen() {
        mDragHelper.smoothSlideViewTo(mainView, mDragDistance, 0);
        ViewCompat.postInvalidateOnAnimation(SideMenu.this);
        mListener.open();
    }

    private void smoothClose() {
        mDragHelper.smoothSlideViewTo(mainView, 0, 0);
        ViewCompat.postInvalidateOnAnimation(SideMenu.this);
        mListener.close();
    }

    private float getScaleNumber() {
        return mainView.getX() / mDragDistance * (1 - SCALE_FACTOR) + SCALE_FACTOR;
    }

    private boolean isOpen() {
        return mainView.getX() != 0;
    }

    public void toggle() {
        if (isOpen()) {
            smoothClose();
        } else {
            smoothOpen();
        }
    }

    public interface onToggleListener {
        void open();

        void close();
    }

    public void setOnToggleListener(onToggleListener listener) {
        if (mListener == null) {
            mListener = listener;
        }
    }
}
