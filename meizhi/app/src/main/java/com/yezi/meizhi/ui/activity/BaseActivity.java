package com.yezi.meizhi.ui.activity;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

import com.yezi.meizhi.R;
import com.yezi.meizhi.utils.InputMethodUtils;

import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {
    private boolean mHasAnim;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        mHasAnim = true;
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    public void setContentView(@LayoutRes int layoutResID, boolean hasAnim) {
        mHasAnim = hasAnim;
        if (mHasAnim) {
            overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
        }
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void hideSoftInputMethod() {
        try {
            InputMethodUtils.hideSoftInputMethod(this, getCurrentFocus().getWindowToken());
        } catch (Exception e) {
        }
    }

    @Override
    public void finish() {
        hideSoftInputMethod();
        super.finish();
        if (mHasAnim) {
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out);
        }
    }

    public void showSoftInputMethod() {
        try {
            InputMethodUtils.showSoftInputMethod(this);
        } catch (Exception e) {
        }
    }

    public boolean isImmActive() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            return imm.isActive();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
