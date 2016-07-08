package com.yezi.meizhi;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MeiZhiApp extends Application {
    private static Context sAppContext;
    private static MeiZhiApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        sAppContext = getApplicationContext();

        Fresco.initialize(this);
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static Resources getAppResources() {
        return getAppContext().getResources();
    }

    public static MeiZhiApp getInstance() {
        return mInstance;
    }

    public static Toast showToast(String s) {
        Toast toast = Toast.makeText(getAppContext(), s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return toast;
    }

    public static Toast showToast(@StringRes int s) {
        Toast toast = Toast.makeText(getAppContext(), s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return toast;
    }
}
