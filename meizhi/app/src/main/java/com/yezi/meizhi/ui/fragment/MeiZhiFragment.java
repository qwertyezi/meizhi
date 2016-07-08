package com.yezi.meizhi.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.yezi.meizhi.MeiZhiApp;
import com.yezi.meizhi.R;
import com.yezi.meizhi.api.ServiceFactory;
import com.yezi.meizhi.model.MeiZhiDetail;
import com.yezi.meizhi.model.MeiZhiMeiZhi;
import com.yezi.meizhi.ui.activity.MainActivity;
import com.yezi.meizhi.ui.adapter.MeiZhiPageAdapter;
import com.yezi.meizhi.ui.widget.ImgProgressBar;
import com.yezi.meizhi.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeiZhiFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnTouchListener {

    public static final String STATUS_TODAY = "today";
    public static final String STATUS_YESTERDAY = "yesterday";
    public static final String STATUS_BEFORE = "before";
    private int MEIZHI_COUNT = 10;
    private int MEIZHI_PAGE = 1;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.progressBar)
    ImgProgressBar mImgProgressBar;

    private MeiZhiPageAdapter mAdapter;
    private List<MeiZhiDetail> meiZhiList;
    private int mCurrentPosition = -1;
    private Context mContext;
    private onUpdateTextViewsListener mListener;
    private boolean isRequestData = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        getDatas();
    }

    public void setCurrentPage(int position) {
        if (mViewPager == null) {
            return;
        }
        mViewPager.setCurrentItem(position, true);
    }

    private void initViews() {
        mAdapter = new MeiZhiPageAdapter(mContext);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOnTouchListener(this);
    }

    private void getDatas() {
        isRequestData = true;
        mImgProgressBar.startProgress();
        ServiceFactory.getMeiZhiService().getCategoryList(MainActivity.CATEGORY_MEIZHI, MEIZHI_COUNT, MEIZHI_PAGE).
                enqueue(new Callback<MeiZhiMeiZhi>() {
                    @Override
                    public void onResponse(Call<MeiZhiMeiZhi> call, Response<MeiZhiMeiZhi> response) {
                        mImgProgressBar.stopProgress();
                        if (response.body().meizhi.size() == 0) {
                            return;
                        }
                        MeiZhiApp.showToast(R.string.get_meizhi_success);
                        meiZhiList.addAll(((MeiZhiMeiZhi) response.body()).meizhi);
                        mAdapter.updateData(((MeiZhiMeiZhi) response.body()).meizhi);
                        mCurrentPosition = 0;

                        mListener.updateTextViews(titleStatus(0), meiZhiList.get(0));
                        isRequestData = false;
                    }

                    @Override
                    public void onFailure(Call<MeiZhiMeiZhi> call, Throwable t) {
                        mImgProgressBar.stopProgress();
                        MeiZhiApp.showToast(R.string.get_meizhi_failure);
                        isRequestData = false;
                    }
                });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
        mListener.updateTextViews(titleStatus(position), meiZhiList.get(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isRequestData || mCurrentPosition != meiZhiList.size() - 1) {
            return false;
        } else {
            int firstX = -1, lastX = -1;
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    firstX = (int) event.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    lastX = (int) event.getRawX();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    lastX = (int) event.getRawX();
                    if ((lastX - firstX) >= getScreenWidth() / 4) {
                        ++MEIZHI_PAGE;
                        getDatas();
                    }
                    break;
                default:
            }
            return true;
        }
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

    private int getScreenWidth() {
        return ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
    }

    private int getScreenHeight() {
        return ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight();
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
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}