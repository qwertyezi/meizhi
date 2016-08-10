package com.yezi.meizhi.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class InputMethodUtils {

    public static void showSoftInputMethod(Context context) {
        try {
            InputMethodManager
                    inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
    }

    public static void setSoftInputMode(Activity activity, int flag) {
        activity.getWindow().setSoftInputMode(flag);
    }

    public static void showSoftInputMethod(Context context, View view) {
        InputMethodManager iMM = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        iMM.showSoftInput(view, 0);
    }

    public static void hideSoftInputMethod(Context context, IBinder binder) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(binder, 0);
        }
    }

    public static boolean isSoftKeyboardActive(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputMethodManager.isActive(view);
    }

    public static boolean isSoftKeyboardActive(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputMethodManager.isActive();
    }
}
