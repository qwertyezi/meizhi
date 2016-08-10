package com.yezi.meizhi.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.yezi.meizhi.MeiZhiApp;
import com.yezi.meizhi.R;

public class PopupWindowUtils {

    private static PopupWindowUtils popupWindow = null;
    private View mView;
    private Context mContext;

    public synchronized static PopupWindowUtils getInstance() {
        if (popupWindow == null) {
            popupWindow = new PopupWindowUtils();
        }
        return popupWindow;
    }

    private onTextClickListener mListener;

    public void setOnTextClickListener(onTextClickListener listener) {
        mListener = listener;
    }

    public interface onTextClickListener {
        void clickBigImg(View view);

        void clickSaveImg(View view);
    }

    private WindowManager getWindowManager() {
        return (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    private WindowManager.LayoutParams getLP() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.format = PixelFormat.TRANSPARENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        return params;
    }

    private View getView() {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.view_popup_window, null);
        view.findViewById(R.id.text_big_img).setOnClickListener(v -> {
            if (mListener != null) {
                mListener.clickBigImg(v);
            }
        });
        view.findViewById(R.id.text_save_img).setOnClickListener(v -> {
            if (mListener != null) {
                mListener.clickSaveImg(v);
            }
        });
        view.findViewById(R.id.layout_container).setOnClickListener(v -> {
            dismissPopupWindow();
        });
        return view;
    }

    private boolean isShowing() {
        return mView != null;
    }

    public void togglePopupWindow(Context context, int locationX, int locationY) {
        if (isShowing()) {
            dismissPopupWindow();
        } else {
            showPopupWindow(context, locationX, locationY);
        }
    }

    public void showPopupWindow(Context context, int locationX, int locationY) {
        mContext = context;
        mView = getView();
        mView.measure(0, 0);
        WindowManager.LayoutParams lp = getLP();
        lp.height = ScreenSizeUtil.getScreenHeight(context) - ScreenSizeUtil.getStatusBarHeight(context);
        LinearLayout layoutContent = (LinearLayout) mView.findViewById(R.id.layout_content);
        layoutContent.setX(locationX - mView.getMeasuredWidth() / 2);
        layoutContent.setY(locationY - mView.getMeasuredHeight() - ScreenSizeUtil.getStatusBarHeight(context) -
                MeiZhiApp.getAppResources().getDimensionPixelSize(R.dimen.popup_window_offset));
        getWindowManager().addView(mView, lp);
    }

    public void dismissPopupWindow() {
        if (!isShowing()) {
            return;
        }
        getWindowManager().removeView(mView);
        mView = null;
    }
}
