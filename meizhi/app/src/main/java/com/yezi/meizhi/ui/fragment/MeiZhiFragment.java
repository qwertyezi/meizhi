package com.yezi.meizhi.ui.fragment;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yezi.meizhi.MeiZhiApp;
import com.yezi.meizhi.R;
import com.yezi.meizhi.api.ServiceFactory;
import com.yezi.meizhi.model.MeiZhiDetail;
import com.yezi.meizhi.model.MeiZhiMeiZhi;
import com.yezi.meizhi.ui.activity.MainActivity;
import com.yezi.meizhi.ui.adapter.MeiZhiPageAdapter;
import com.yezi.meizhi.ui.widget.HorizontalPullToRefresh;
import com.yezi.meizhi.ui.widget.rhythm.RhythmAdapter;
import com.yezi.meizhi.ui.widget.rhythm.RhythmLayout;
import com.yezi.meizhi.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeiZhiFragment extends Fragment implements ViewPager.OnPageChangeListener {

    public static final String STATUS_TODAY = "today";
    public static final String STATUS_YESTERDAY = "yesterday";
    public static final String STATUS_BEFORE = "before";
    private int MEIZHI_COUNT = 10;
    private int MEIZHI_PAGE = 1;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.progress_left)
    ImageView mLeftProgress;
    @Bind(R.id.progress_right)
    ImageView mRightProgress;
    @Bind(R.id.refresh_content)
    HorizontalPullToRefresh mPullToRefresh;
    @Bind(R.id.rhythm_layout)
    RhythmLayout mRhythmLayout;

    private MeiZhiPageAdapter mAdapter;
    private List<MeiZhiDetail> meiZhiList;
    private Context mContext;
    private onUpdateTextViewsListener mListener;
    private AnimationDrawable mLeftAnimation;
    private AnimationDrawable mRightAnimation;
    private RhythmAdapter mRhythmAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.meizhi_layout, container, false);
    }

    @Override
    @Nullable
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        meiZhiList = new ArrayList<>();
        mContext = getContext();
        initViews();
        getDatas(0);
    }

    public void setCurrentPage(int position) {
        if (mViewPager == null) {
            return;
        }
        mViewPager.setCurrentItem(position, true);
    }

    private void initViews() {
        mRhythmAdapter = new RhythmAdapter(mContext);
        mRhythmLayout.setScrollRhythmStartDelayTime(300);
        mRhythmLayout.setRhythmAdapter(mRhythmAdapter);

        mAdapter = new MeiZhiPageAdapter(mContext);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        mLeftAnimation = (AnimationDrawable) mLeftProgress.getDrawable();
        mRightAnimation = (AnimationDrawable) mRightProgress.getDrawable();

        mPullToRefresh.setHptrHandler(new HorizontalPullToRefresh.HptrHandler() {
            @Override
            public boolean canLeftRefresh() {
                return mViewPager.getCurrentItem() == 0 &&
                        !mViewPager.canScrollHorizontally(-1);
            }

            @Override
            public boolean canRightRefresh() {
                return mAdapter.getCount() > 0 &&
                        mViewPager.getCurrentItem() == mAdapter.getCount() - 1 &&
                        !mViewPager.canScrollHorizontally(1);
            }

            @Override
            public void moveOffset(int leftOrRight, int status, int offset) {
                startSlowRotateAnimation(leftOrRight == HorizontalPullToRefresh.LEFT_HEADER ?
                        mLeftProgress : mRightProgress, offset);
            }

            @Override
            public void completeMove(int leftOrRight, int status) {
                if (status == HorizontalPullToRefresh.STATUS_LOADING) {
                    if (leftOrRight == HorizontalPullToRefresh.LEFT_HEADER) {
                        startFastRotateAnimation(mLeftAnimation);
                        MEIZHI_PAGE = 1;
                        getDatas(HorizontalPullToRefresh.LEFT_HEADER);
                    } else {
                        startFastRotateAnimation(mRightAnimation);
                        ++MEIZHI_PAGE;
                        getDatas(HorizontalPullToRefresh.RIGHT_HEADER);
                    }
                }
            }
        });
    }

    private void startSlowRotateAnimation(View view, int offset) {
        view.setRotation(offset);
    }

    private void startFastRotateAnimation(AnimationDrawable animationDrawable) {
        if (animationDrawable != null) {
            animationDrawable.start();
        }
    }

    private void stopFastRotateAnimation(AnimationDrawable animationDrawable) {
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
    }

    private void getDatas(final int leftOrRight) {
        ServiceFactory.getMeiZhiService().getCategoryList(MainActivity.CATEGORY_MEIZHI, MEIZHI_COUNT, MEIZHI_PAGE).
                enqueue(new Callback<MeiZhiMeiZhi>() {
                    @Override
                    public void onResponse(Call<MeiZhiMeiZhi> call, Response<MeiZhiMeiZhi> response) {
                        if (response.body().meizhi.size() == 0) {
                            return;
                        }
                        MeiZhiApp.showToast(R.string.get_meizhi_success);

                        if (leftOrRight == HorizontalPullToRefresh.RIGHT_HEADER) {
                            stopFastRotateAnimation(mRightAnimation);
                            meiZhiList.addAll(response.body().meizhi);
                            mAdapter.updateData(meiZhiList);
                            mRhythmAdapter.setData(meiZhiList);
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                        } else {
                            stopFastRotateAnimation(mLeftAnimation);
                            meiZhiList.clear();
                            meiZhiList.addAll(response.body().meizhi);
                            mAdapter.updateData(meiZhiList);
                            mRhythmAdapter.setData(meiZhiList);
                            mViewPager.setCurrentItem(0);
                            onPageSelected(0);
                        }
                    }

                    @Override
                    public void onFailure(Call<MeiZhiMeiZhi> call, Throwable t) {
                        MeiZhiApp.showToast(R.string.get_meizhi_failure);
                        stopFastRotateAnimation(leftOrRight == HorizontalPullToRefresh.LEFT_HEADER ?
                                mLeftAnimation : mRightAnimation);
                    }

                });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mListener.updateTextViews(titleStatus(position), meiZhiList.get(position));
        mRhythmLayout.showRhythmAtPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private String titleStatus(int position) {
        if (position == 0 || position == 1) {
            String time = meiZhiList.get(position).publishedAt;
            int month = DateUtils.getDayAndMonth(time)[1];
            int day = DateUtils.getDayAndMonth(time)[0];
            if (month == DateUtils.getCurrentMonth()) {
                if (day == DateUtils.getCurrentDay()) {
                    return STATUS_TODAY;
                } else if (day + 1 == DateUtils.getCurrentDay()) {
                    return STATUS_YESTERDAY;
                } else {
                    return STATUS_BEFORE;
                }
            } else if (month + 1 == DateUtils.getCurrentMonth() && DateUtils.getCurrentMonth() == 1) {
                if ((DateUtils.getCurrentMonth() == 2 && day == 31) || (DateUtils.getCurrentMonth() == 3 && day == 28) ||
                        (DateUtils.getCurrentMonth() == 4 && day == 31) || (DateUtils.getCurrentMonth() == 5 && day == 30) ||
                        (DateUtils.getCurrentMonth() == 6 && day == 31) || (DateUtils.getCurrentMonth() == 7 && day == 30) ||
                        (DateUtils.getCurrentMonth() == 8 && day == 31) || (DateUtils.getCurrentMonth() == 9 && day == 31) ||
                        (DateUtils.getCurrentMonth() == 10 && day == 30) || (DateUtils.getCurrentMonth() == 11 && day == 31) ||
                        (DateUtils.getCurrentMonth() == 12 && day == 30)) {
                    return STATUS_YESTERDAY;
                }
            } else {
                return STATUS_BEFORE;
            }
        }
        return STATUS_BEFORE;
    }

    public interface onUpdateTextViewsListener {
        void updateTextViews(String titleStatus, MeiZhiDetail meizhi);
    }

    public void setOnUpdateTextViewsListener(onUpdateTextViewsListener listener) {
        if (mListener == null) {
            mListener = listener;
        }
    }

    @Override
    public void onDestroy() {
        mViewPager.clearOnPageChangeListeners();
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
