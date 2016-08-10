package com.yezi.meizhi;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorInt;

import com.yezi.meizhi.ui.activity.ImageScaleActivity;
import com.yezi.meizhi.ui.activity.WebBrowserActivity;

public class Navigator {
    public static final String EXTRA_WEB_TITLE = "extra_web_title";
    public static final String EXTRA_WEB_URL = "extra_web_url";
    public static final String EXTRA_WEB_COLOR = "extra_web_color";
    public static final String EXTRA_IMG_URL = "extra_img_url";

    public static void startWebBrowserActivity(Context context, String title, String url, @ColorInt int colorRes) {
        Intent intent = new Intent(context, WebBrowserActivity.class);
        intent.putExtra(EXTRA_WEB_TITLE, title);
        intent.putExtra(EXTRA_WEB_URL, url);
        intent.putExtra(EXTRA_WEB_COLOR, colorRes);
        context.startActivity(intent);
    }

    public static void startImageScaleActivity(Context context,String url) {
        Intent intent = new Intent(context, ImageScaleActivity.class);
        intent.putExtra(EXTRA_IMG_URL,url);
        context.startActivity(intent);
    }

}
