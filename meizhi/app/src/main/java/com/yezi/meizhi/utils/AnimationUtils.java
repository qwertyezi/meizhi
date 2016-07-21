package com.yezi.meizhi.utils;

import android.support.annotation.ColorInt;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;

public class AnimationUtils {
    public static void changeBgColor(View view, @ColorInt int preColor,
                                     @ColorInt int curColor, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofInt(view, "backgroundColor", new int[]{preColor, curColor});
        animator.setDuration(duration);
        animator.setEvaluator(new ArgbEvaluator());
        animator.start();
    }

    public static Animator moveScrollViewToX(View view, int toX, int time,
                                             int delayTime, boolean isStart) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(view, "scrollX", new int[]{toX});
        objectAnimator.setDuration(time);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.setStartDelay(delayTime);
        if (isStart)
            objectAnimator.start();
        return objectAnimator;
    }

    public static Animator showUpAndDownBounce(View view, int translationY, int animatorTime,
                                               boolean isStartAnimator, boolean isStartInterpolator) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", translationY);
        if (isStartInterpolator) {
            objectAnimator.setInterpolator(new OvershootInterpolator());
        }
        objectAnimator.setDuration(animatorTime);
        if (isStartAnimator) {
            objectAnimator.start();
        }
        return objectAnimator;
    }
}
