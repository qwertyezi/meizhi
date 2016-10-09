package com.yezi.meizhi.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.yezi.meizhi.R;
import com.yezi.meizhi.model.MeiZhiDetail;
import com.yezi.meizhi.ui.fragment.CategoryFragment;
import com.yezi.meizhi.ui.fragment.MeiZhiFragment;
import com.yezi.meizhi.ui.widget.SearchView;
import com.yezi.meizhi.ui.widget.SideMenu;
import com.yezi.meizhi.utils.DateUtils;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MeiZhiFragment.onUpdateTextViewsListener {

    public static final String CATEGORY = "category";

    public static final String CATEGORY_MEIZHI = "福利";
    public static final String CATEGORY_ANDROID = "Android";
    public static final String CATEGORY_IOS = "iOS";
    public static final String CATEGORY_VIDEO = "休息视频";
    public static final String CATEGORY_APP = "App";
    public static final String CATEGORY_FROND = "前端";
    public static final String CATEGORY_RECOMMEND = "瞎推荐";
    public static final String CATEGORY_RESOURCE = "拓展资源";
    public static final String CATEGORY_SEARCH = "搜索";

    public static final String FRAGMENT_MEIZHI = "fragment_meizhi";
    public static final String FRAGMENT_CATEGORY = "fragment_category";

    private static final long sSideMenuCloseTime = 250;
    private int mPreColor;
    private BgColorRunnable mBgColorRunnable;

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ((BgColorRunnable) msg.obj).run();
        }
    };

    //    main view
    @Bind(R.id.text_today_meizhi)
    TextView mTextTodayMeiZhi;
    @Bind(R.id.text_day)
    TextView mTextDay;
    @Bind(R.id.text_month_week)
    TextView mTextMonthWeek;
    @Bind(R.id.text_home_sidebar)
    TextView mTextHomeSidebar;
    @Bind(R.id.img_backtohome)
    ImageView mImgBacktohome;
    @Bind(R.id.layout_time)
    RelativeLayout mLayoutTime;
    @Bind(R.id.side_menu)
    SideMenu mSideMenu;
    @Bind(R.id.search_view)
    SearchView mSearchView;

    //    leftmenu view
    @Bind(R.id.img_left_menu_meizhi)
    SimpleDraweeView mImgLeftMenuMeiZhi;
    @Bind(R.id.text_left_meizhi)
    TextView mTextLeftMeiZhi;
    @Bind(R.id.text_left_android)
    TextView mTextLeftAndroid;
    @Bind(R.id.text_left_ios)
    TextView mTextLeftIos;
    @Bind(R.id.text_left_app)
    TextView mTextLeftApp;
    @Bind(R.id.text_left_video)
    TextView mTextLeftVideo;
    @Bind(R.id.text_left_frond)
    TextView mTextLeftFrond;
    @Bind(R.id.text_left_recommend)
    TextView mTextLeftRecommend;
    @Bind(R.id.text_left_resource)
    TextView mTextLeftResource;
    @Bind(R.id.img_left_aboutme)
    ImageView mImgAboutMe;

    private MeiZhiFragment mMeiZhiFragment;
    private final int[] mLeftMenuTextIds = new int[]{
            R.id.text_left_meizhi, R.id.text_left_android,
            R.id.text_left_ios, R.id.text_left_app,
            R.id.text_left_frond, R.id.text_left_video,
            R.id.text_left_recommend, R.id.text_left_resource,
            R.id.text_left_search
    };

    private final int[] mBgColors = new int[]{
            R.color.colorA, R.color.colorB, R.color.colorC,
            R.color.colorD, R.color.colorE, R.color.colorF,
            R.color.colorG, R.color.colorH, R.color.colorI,
            R.color.colorJ, R.color.colorK
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initBgColor();
        initViews();
    }

    public
    @ColorInt
    int getCurrentColor() {
        return mPreColor;
    }

    private void initBgColor() {
        mPreColor = getResources().getColor(getRandomColor());
        getWindow().setStatusBarColor(mPreColor);
        mSideMenu.setBackgroundColor(mPreColor);
    }

    public SearchView getSearchView() {
        return mSearchView;
    }


    private int getRandomColor() {
        return mBgColors[new Random().nextInt(mBgColors.length)];
    }

    private void initViews() {
        changeToMeiZhiFragment();

        mSideMenu.setOnToggleListener(new SideMenu.onToggleListener() {
            @Override
            public void open() {
                mTextHomeSidebar.setVisibility(View.INVISIBLE);
                hideSoftInputMethod();
            }

            @Override
            public void close() {
                mTextHomeSidebar.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void updateTextViews(String titleStatus, MeiZhiDetail meizhi) {
        int curColor = getResources().getColor(getRandomColor());

        //无法给状态栏颜色的改变提供渐变的效果，因此使用下面手动的方式实现
//        com.yezi.meizhi.utils.AnimationUtils.changeBgColor(getWindow().getDecorView(), mPreColor, curColor, 200);
//        com.yezi.meizhi.utils.AnimationUtils.changeBgColor(mSideMenu, mPreColor, curColor, 200);
//        getWindow().setStatusBarColor(curColor);

        if (mBgColorRunnable == null) {
            mBgColorRunnable = new BgColorRunnable();
        }
        mBgColorRunnable.startAnimation(200, mPreColor, curColor);
        mPreColor = curColor;

        switch (titleStatus) {
            case MeiZhiFragment.STATUS_TODAY:
                mTextTodayMeiZhi.setVisibility(View.VISIBLE);
                mLayoutTime.setVisibility(View.INVISIBLE);

                mTextTodayMeiZhi.setText(getString(R.string.today_meizhi,
                        getString(R.string.app_name)));
                break;
            case MeiZhiFragment.STATUS_YESTERDAY:
                mTextTodayMeiZhi.setVisibility(View.VISIBLE);
                mLayoutTime.setVisibility(View.INVISIBLE);

                mTextTodayMeiZhi.setText(getString(R.string.yesterday_meizhi,
                        getString(R.string.app_name)));
                break;
            case MeiZhiFragment.STATUS_BEFORE:
                mTextTodayMeiZhi.setVisibility(View.INVISIBLE);
                mLayoutTime.setVisibility(View.VISIBLE);

                String time = meizhi.publishedAt;
                mTextDay.setText(String.valueOf(DateUtils.getDayAndMonth(time)[0]));
                mTextMonthWeek.setText(getString(R.string.month, DateUtils.getDayAndMonth(time)[1])
                        + "\n" + getString(R.string.week, DateUtils.getWeek(time)));

                mTextDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
            default:
        }
    }

    @OnClick({
            R.id.img_backtohome,
            R.id.text_home_sidebar,

            R.id.text_left_android,
            R.id.text_left_ios,
            R.id.text_left_app,
            R.id.text_left_video,
            R.id.text_left_frond,
            R.id.text_left_meizhi,
            R.id.text_left_resource,
            R.id.text_left_recommend,
            R.id.text_left_search,
            R.id.img_left_aboutme
    })
    public void click(View view) {
        switch (view.getId()) {
            case R.id.img_backtohome:
                mMeiZhiFragment.setCurrentPage(0);
                break;
            case R.id.text_home_sidebar:
                mSideMenu.toggle();
                break;
            case R.id.text_left_meizhi:
                clickLeftMenu(R.id.text_left_meizhi, null, true);
                break;
            case R.id.text_left_android:
                clickLeftMenu(R.id.text_left_android, CATEGORY_ANDROID, false);
                break;
            case R.id.text_left_ios:
                clickLeftMenu(R.id.text_left_ios, CATEGORY_IOS, false);
                break;
            case R.id.text_left_video:
                clickLeftMenu(R.id.text_left_video, CATEGORY_VIDEO, false);
                break;
            case R.id.text_left_frond:
                clickLeftMenu(R.id.text_left_frond, CATEGORY_FROND, false);
                break;
            case R.id.text_left_app:
                clickLeftMenu(R.id.text_left_app, CATEGORY_APP, false);
                break;
            case R.id.text_left_recommend:
                clickLeftMenu(R.id.text_left_recommend, CATEGORY_RECOMMEND, false);
                break;
            case R.id.text_left_resource:
                clickLeftMenu(R.id.text_left_resource, CATEGORY_RESOURCE, false);
                break;
            case R.id.text_left_search:
                clickLeftMenu(R.id.text_left_search, CATEGORY_SEARCH, false);
                break;
            case R.id.img_left_aboutme:

                break;
            default:
        }
    }

    private void clickLeftMenu(@IdRes int textId, final String category, final boolean isMeizhi) {
        updateLeftMenuText(textId);
        mLayoutTime.setVisibility(View.INVISIBLE);
        if (!TextUtils.isEmpty(category) && category.equals(CATEGORY_SEARCH)) {
            mTextTodayMeiZhi.setVisibility(View.INVISIBLE);
            mSearchView.setVisibility(View.VISIBLE);
        } else {
            mSearchView.setVisibility(View.INVISIBLE);
            mTextTodayMeiZhi.setVisibility(View.VISIBLE);
            mTextTodayMeiZhi.setText(category);
        }
        mSideMenu.toggle();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (!isMeizhi) {
                        changeToCategoryFragment(category);
                    } else {
                        changeToMeiZhiFragment();
                    }
                });
            }
        }, sSideMenuCloseTime);
    }

    private void updateLeftMenuText(@IdRes int textId) {
        for (int id : mLeftMenuTextIds) {
            TextView textView = (TextView) findViewById(id);
            if (id == textId) {
                textView.setEnabled(false);
            } else {
                textView.setEnabled(true);
            }
        }
    }

    private void changeToMeiZhiFragment() {
        mMeiZhiFragment = (MeiZhiFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_MEIZHI);
        if (mMeiZhiFragment == null) {
            mMeiZhiFragment = new MeiZhiFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layout_container, mMeiZhiFragment, FRAGMENT_MEIZHI);
        transaction.setCustomAnimations(R.anim.fragment_show, R.anim.fragment_hidden);
        transaction.commit();

        mMeiZhiFragment.setOnUpdateTextViewsListener(this);
    }

    private void changeToCategoryFragment(String category) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_CATEGORY);
        if (fragment == null) {
            fragment = CategoryFragment.getInstance(category);
        } else {
            ((CategoryFragment) fragment).setCategory(category);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layout_container, fragment, FRAGMENT_CATEGORY);
        transaction.setCustomAnimations(R.anim.fragment_show, R.anim.fragment_hidden);
        transaction.commit();
    }

    class BgColorRunnable implements Runnable {
        private long mDuration;
        private long mStartTime;
        private boolean mIsFinished = true;
        private int mPColor;
        private int mCColor;

        public BgColorRunnable() {
        }

        public void abortAnimation() {
            mIsFinished = true;
        }

        public boolean isFinished() {
            return mIsFinished;
        }

        @Override
        public void run() {
            if (mIsFinished) {
                return;
            }
            long currentTime = SystemClock.currentThreadTimeMillis();
            if (currentTime - mStartTime <= mDuration) {
                int color = (int) new ArgbEvaluator().evaluate(0.3f, mPColor, mCColor);
                mSideMenu.setBackgroundColor(color);
                getWindow().setStatusBarColor(color);
                mPColor = color;
                if (currentTime - mStartTime == mDuration) {
                    mSideMenu.setBackgroundColor(mCColor);
                    getWindow().setStatusBarColor(mCColor);
                }
                mHandler.post(this);
                return;
            }
            mIsFinished = true;
        }

        public void startAnimation(long duration, int preColor, int curColor) {
            mStartTime = SystemClock.currentThreadTimeMillis();
            mDuration = duration;
            mPColor = preColor;
            mCColor = curColor;
            mIsFinished = false;
            mHandler.post(this);
        }
    }
}
