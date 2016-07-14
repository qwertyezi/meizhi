package com.yezi.meizhi;

import android.content.Context;
import android.content.Intent;

import com.yezi.meizhi.ui.activity.WebBrowserActivity;

public class Navigator {
    public static final String EXTRA_WEB_TITLE = "extra_web_title";
    public static final String EXTRA_WEB_URL = "extra_web_url";

    public static void startWebBrowserActivity(Context context, String title, String url) {
        Intent intent = new Intent(context, WebBrowserActivity.class);
        intent.putExtra(EXTRA_WEB_TITLE, title);
        intent.putExtra(EXTRA_WEB_URL, url);
        context.startActivity(intent);
    }

}
