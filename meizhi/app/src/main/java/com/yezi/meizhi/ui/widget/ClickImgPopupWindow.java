package com.yezi.meizhi.ui.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.yezi.meizhi.R;


public class ClickImgPopupWindow extends PopupWindow {

    public void dismissWindow() {
        this.dismiss();
    }

    public void showWindow(Context context, View.OnClickListener big, View.OnClickListener save) {
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setFocusable(true);
        this.setOutsideTouchable(true);

        View view = View.inflate(context, R.layout.view_click_img, null);
        view.findViewById(R.id.text_big_img).setOnClickListener(big);
        view.findViewById(R.id.text_save_img).setOnClickListener(save);

        view.setOnClickListener(v -> this.dismiss());
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));

        this.setContentView(view);
        this.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        this.update();
    }
}
