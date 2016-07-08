package com.yezi.meizhi.utils;

import android.support.annotation.ColorInt;
import android.view.View;

import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;

public class AnimationUtils {
    public static void changeBgColor(View view, @ColorInt int preColor, @ColorInt int curColor, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofInt(view, "backgroundColor", new int[]{preColor, curColor});
        animator.setDuration(duration);
        animator.setEvaluator(new ArgbEvaluator());
        animator.start();
    }
}
