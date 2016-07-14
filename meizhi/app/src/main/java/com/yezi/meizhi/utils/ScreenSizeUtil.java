package com.yezi.meizhi.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.WindowManager;

public class ScreenSizeUtil {

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getHeight();
    }

    public static int getStatusBarHeight(Context context) {
        Rect frame = new Rect();
        ((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }
}
