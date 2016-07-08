package com.yezi.meizhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yezi.meizhi.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ImgProgressBar extends FrameLayout {

    private static final int[] ImgList = {
            R.mipmap.loading_1, R.mipmap.loading_2,
            R.mipmap.loading_3, R.mipmap.loading_4,
            R.mipmap.loading_5, R.mipmap.loading_6,
            R.mipmap.loading_7, R.mipmap.loading_8
    };

    private static final long GAP_TIME = 100;

    private int startIndex = 0;
    private Timer mTimer;
    private Context mContext;

    public ImgProgressBar(Context context) {
        this(context, null);
    }

    public ImgProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImgProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        startIndex = getRandom();
        setBackgroundResource(ImgList[startIndex]);
    }

    private int getRandom() {
        return new Random().nextInt(8);
    }

    public void startProgress() {
        this.setVisibility(VISIBLE);
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (++startIndex > 7) {
                            startIndex = 0;
                        }
                        setBackgroundResource(ImgList[startIndex]);
                    }
                });
            }
        }, 0, GAP_TIME);
    }

    public void stopProgress() {
        this.setVisibility(INVISIBLE);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void rotateByTouch(int dx) {
        stopProgress();
        this.setVisibility(VISIBLE);
        this.setRotation(dx);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopProgress();
    }
}
