package com.yezi.meizhi.ui.widget.rhythm;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.yezi.meizhi.utils.AnimationUtils;
import com.yezi.meizhi.utils.ScreenSizeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RhythmLayout extends HorizontalScrollView {

    private Handler mHandler;
    private Context mContext;
    private int mItemWidth;
    private int mCurrentItemPosition;
    private int mMaxTranslationHeight;
    private int mIntervalHeight;
    private int mScreenWidth;
    private LinearLayout mLinearLayout;
    private RhythmAdapter mRhythmAdapter;
    private ShiftMonitorTimer mMonitorTimer;
    private long mFingerDownTime;
    private int mLastDisplayItemPosition;
    private int mScrollStartDelayTime;

    private static final int ITEMS_DISPLAY_NUM = 7;
    private static final int ITEM_SWIPE_BOUNCE_DURATION = 180;
    public static final int ITEM_BOUNCE_DURATION = 350;
    private static final int ITEM_DOWN_DELAY = 200;


    public RhythmLayout(Context context) {
        this(context, null);
    }

    public RhythmLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RhythmLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        init();
    }

    private void init() {
        mHandler = new Handler();
        mScreenWidth = ScreenSizeUtil.getScreenWidth(mContext);
        mItemWidth = mScreenWidth / ITEMS_DISPLAY_NUM;
        mCurrentItemPosition = -1;
        mLastDisplayItemPosition = -1;
        mScrollStartDelayTime = 0;
        mMaxTranslationHeight = mItemWidth;
        mIntervalHeight = mItemWidth / ITEMS_DISPLAY_NUM - 1;
        mMonitorTimer = new ShiftMonitorTimer();
        mMonitorTimer.startMonitor();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFingerDownTime = System.currentTimeMillis();
                updateItemHeight(ev.getX());
                break;
            case MotionEvent.ACTION_MOVE:
                mMonitorTimer.monitorTouchPosition(ev.getX());
                updateItemHeight(ev.getX());
                break;
            case MotionEvent.ACTION_UP:
                mMonitorTimer.stopShift();
                actionUp();
                break;
        }
        return true;
    }

    private void actionUp() {
        if (mCurrentItemPosition < 0) {
            return;
        }
        final List<View> viewList = getVisibleViews();
        viewList.remove(mCurrentItemPosition);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < viewList.size(); i++) {
                            shootDownItem(viewList.get(i), true);
                        }
                    }
                });
            }
        }, ITEM_DOWN_DELAY);
        mCurrentItemPosition = -1;
        vibrate(20L);
    }

    public int getSize() {
        if (mLinearLayout == null) {
            return 0;
        }
        return mLinearLayout.getChildCount();
    }

    public Animator shootDownItem(int viewPosition, boolean isStart) {
        if ((viewPosition >= 0) && (mLinearLayout != null) && (getSize() > viewPosition))
            return shootDownItem(getItemView(viewPosition), isStart);
        return null;
    }

    public Animator shootDownItem(View view, boolean isStart) {
        if (view != null)
            return AnimationUtils.showUpAndDownBounce(view, mMaxTranslationHeight, 350, isStart, true);
        return null;
    }

    public Animator bounceUpItem(int viewPosition, boolean isStart) {
        if (viewPosition >= 0)
            return bounceUpItem(getItemView(viewPosition), isStart);
        return null;
    }

    public Animator bounceUpItem(View view, boolean isStart) {
        if (view != null)
            return AnimationUtils.showUpAndDownBounce(view, 0, 350, isStart, true);
        return null;
    }

    private void vibrate(long l) {
        ((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(new long[]{0L, l}, -1);
    }

    private void updateItemHeight(float scrollX) {
        int position = (int) (scrollX / mItemWidth);
        if (position == mCurrentItemPosition || position >= mLinearLayout.getChildCount()) {
            return;
        }
        mCurrentItemPosition = position;
        makeItems(mCurrentItemPosition, getVisibleViews());
    }

    private void makeItems(int currentItemPosition, List<View> visibleViews) {
        if (currentItemPosition >= visibleViews.size()) {
            return;
        }
        for (int i = 0; i < visibleViews.size(); ++i) {
            int translateY =
                    Math.min(Math.abs(currentItemPosition - i) * mIntervalHeight, mMaxTranslationHeight);
            updateItemHeightAnimator(visibleViews.get(i), translateY);
        }
    }

    private void updateItemHeightAnimator(View view, int translateY) {
        if (view == null) {
            return;
        }
        AnimationUtils.showUpAndDownBounce(view, translateY, ITEM_SWIPE_BOUNCE_DURATION, true, true);
    }

    private List<View> getVisibleViews() {
        List<View> viewList = new ArrayList<>();
        if (mLinearLayout == null) {
            return viewList;
        }
        int firstPosition = getFirstVisibleItemPosition();
        int lastPosition = mLinearLayout.getChildCount() < ITEMS_DISPLAY_NUM ?
                mLinearLayout.getChildCount() : firstPosition + ITEMS_DISPLAY_NUM;
        for (int i = firstPosition; i < lastPosition; ++i) {
            viewList.add(mLinearLayout.getChildAt(i));
        }
        return viewList;
    }

    private int getFirstVisibleItemPosition() {
        if (mLinearLayout == null) {
            return 0;
        }
        for (int i = 0; i < mLinearLayout.getChildCount(); ++i) {
            if (getScrollX() < mLinearLayout.getChildAt(i).getX() + mItemWidth / 2) {
                return i;
            }
        }
        return 0;
    }

    public void setRhythmAdapter(RhythmAdapter adapter) {
        mRhythmAdapter = adapter;
        mRhythmAdapter.setItemWidth(mItemWidth);
        if (mLinearLayout == null) {
            mLinearLayout = (LinearLayout) getChildAt(0);

        }
        mRhythmAdapter.setOnUpdateViews(new RhythmAdapter.onUpdateViews() {
            @Override
            public void updateViews() {
                for (int i = 0; i < mRhythmAdapter.getCount(); i++) {
                    mLinearLayout.addView(mRhythmAdapter.getView(i, null, null));
                }
            }
        });
    }

    class ShiftMonitorTimer extends Timer {
        private TimerTask mTimerTask;
        private boolean mCanShift = false;
        private float mX;

        void monitorTouchPosition(float x) {
            mX = x;
            if (x > mItemWidth && x < mScreenWidth - mItemWidth) {
                mCanShift = false;
                mFingerDownTime = System.currentTimeMillis();
                return;
            }
            mCanShift = true;
        }

        public void stopShift() {
            mCanShift = false;
        }

        void startMonitor() {
            if (mTimerTask == null) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        long duration = System.currentTimeMillis() - mFingerDownTime;
                        if (mCanShift && duration > 1000) {
                            int firstPosition = getFirstVisibleItemPosition();
                            int toPosition = 0;
                            boolean isForward = false;
                            boolean isBackward = false;
                            if (mX < mItemWidth) {
                                if (firstPosition - 1 >= 0) {
                                    mCurrentItemPosition = 0;
                                    toPosition = firstPosition - 1;
                                    isForward = true;
                                    isBackward = false;
                                }
                            }
                            if (mX > mScreenWidth - mItemWidth) {
                                if (mLinearLayout.getChildCount() >= 1 + firstPosition + 7) {
                                    mCurrentItemPosition = 7;
                                    toPosition = firstPosition + 1;
                                    isForward = false;
                                    isBackward = true;
                                }
                            }
                            if (isForward || isBackward) {
                                final List<View> localList = getVisibleViews(isForward, isBackward);
                                final int finalToPosition = toPosition;
                                mHandler.post(new Runnable() {
                                    public void run() {
                                        makeItems(mCurrentItemPosition, localList);
                                        scrollToPosition(finalToPosition, 200, 0, true);
                                        vibrate(10L);
                                    }
                                });
                            }
                        }
                    }
                };
                schedule(mTimerTask, 0, 250);
            }
        }
    }

    private View getItemView(int position) {
        return mLinearLayout.getChildAt(position);
    }

    private Animator scrollToPosition(int position, int duration, int startDelay, boolean isStart) {
        int viewX = (int) getItemView(position).getX();
        return smoothScrollX(viewX, duration, startDelay, isStart);
    }

    private Animator scrollToPosition(int position, int startDelay, boolean isStart) {
        int viewX = (int) getItemView(position).getX();
        return smoothScrollX(viewX, 300, startDelay, isStart);
    }

    private Animator smoothScrollX(int position, int duration, int startDelay, boolean isStart) {
        return AnimationUtils.moveScrollViewToX(this, position, duration, startDelay, isStart);
    }

    private List<View> getVisibleViews(boolean isForward, boolean isBackward) {
        List<View> viewList = new ArrayList<>();
        if (mLinearLayout == null)
            return viewList;
        int firstPosition = getFirstVisibleItemPosition();
        int lastPosition = mLinearLayout.getChildCount() < 7 ?
                mLinearLayout.getChildCount() : firstPosition + 7;
        if (isForward && firstPosition > 0)
            firstPosition--;
        if (isBackward && lastPosition < mLinearLayout.getChildCount())
            lastPosition++;
        for (int i = firstPosition; i < lastPosition; i++)
            viewList.add(mLinearLayout.getChildAt(i));
        return viewList;
    }

    public void showRhythmAtPosition(int position) {
        if (mLastDisplayItemPosition == position)
            return;
        Animator scrollAnimator;
        Animator bounceUpAnimator;
        Animator shootDownAnimator;

        if (mLastDisplayItemPosition < 0 || mRhythmAdapter.getCount() <= 7 || position <= 3) {
            scrollAnimator = scrollToPosition(0, mScrollStartDelayTime, false);
        } else if (mRhythmAdapter.getCount() - position <= 3) {
            scrollAnimator = scrollToPosition(mRhythmAdapter.getCount() - 7, mScrollStartDelayTime, false);
        } else {
            scrollAnimator = scrollToPosition(position - 3, mScrollStartDelayTime, false);
        }
        bounceUpAnimator = bounceUpItem(position, false);
        shootDownAnimator = shootDownItem(mLastDisplayItemPosition, false);
        AnimatorSet animatorSet1 = new AnimatorSet();
        if (bounceUpAnimator != null) {
            animatorSet1.playTogether(bounceUpAnimator);
        }
        if (shootDownAnimator != null) {
            animatorSet1.playTogether(shootDownAnimator);
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playSequentially(new Animator[]{scrollAnimator, animatorSet1});
        animatorSet2.start();
        mLastDisplayItemPosition = position;
    }

    public int getRhythmItemWidth() {
        return mItemWidth;
    }

    public void setScrollRhythmStartDelayTime(int scrollStartDelayTime) {
        mScrollStartDelayTime = scrollStartDelayTime;
    }
}
