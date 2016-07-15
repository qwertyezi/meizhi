package com.yezi.meizhi.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.yezi.meizhi.MeiZhiApp;
import com.yezi.meizhi.Navigator;
import com.yezi.meizhi.R;
import com.yezi.meizhi.ui.widget.ImgProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebBrowserActivity extends AppCompatActivity {

    @Bind(R.id.img_back)
    ImageView mImgBack;
    @Bind(R.id.img_forward)
    ImageView mImgForward;
    @Bind(R.id.switcher)
    ViewSwitcher mSwitcher;
    @Bind(R.id.webView)
    WebView mWebView;
    @Bind(R.id.progressBar)
    ImgProgressBar mImgProgressBar;

    private String mTitle;
    private String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_browser);
        ButterKnife.bind(this);

        parseIntent();
        initViews();
    }

    private void parseIntent() {
        mTitle = getIntent().getStringExtra(Navigator.EXTRA_WEB_TITLE);
        mUrl = getIntent().getStringExtra(Navigator.EXTRA_WEB_URL);
    }

    private void initViews() {
        mSwitcher.setDisplayedChild(0);

        mImgBack.setEnabled(false);
        mImgForward.setEnabled(false);

        getSupportActionBar().setTitle(mTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mImgProgressBar.startProgress();

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (isFinishing()) {
                    return;
                }
                if (newProgress == 100) {
                    mImgProgressBar.stopProgress();
                    mSwitcher.setDisplayedChild(1);

                    if (mWebView.canGoBack()) {
                        mImgBack.setEnabled(true);
                    } else {
                        mImgBack.setEnabled(false);
                    }

                    if (mWebView.canGoForward()) {
                        mImgForward.setEnabled(true);
                    } else {
                        mImgForward.setEnabled(false);
                    }
                }
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (request.isForMainFrame()) {
                        MeiZhiApp.showToast(error.getDescription().toString());
                    }
                }
            }
        });
    }

    @OnClick({
            R.id.img_back,
            R.id.img_forward,
            R.id.img_refresh
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                mWebView.goBack();
                break;
            case R.id.img_forward:
                mWebView.goForward();
                break;
            case R.id.img_refresh:
                mWebView.reload();
                break;
            default:
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.loadUrl(mUrl.startsWith("http") ? mUrl :
                "http://" + mUrl);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWebView.stopLoading();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.getSettings().setBuiltInZoomControls(true);
            mWebView.setVisibility(View.GONE);
            mWebView.removeAllViews();
            mWebView.destroy();
        }
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}