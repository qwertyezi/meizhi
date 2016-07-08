package com.yezi.meizhi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yezi.meizhi.MeiZhiApp;
import com.yezi.meizhi.R;
import com.yezi.meizhi.api.ServiceFactory;
import com.yezi.meizhi.model.MeiZhiMeiZhi;
import com.yezi.meizhi.ui.activity.MainActivity;
import com.yezi.meizhi.ui.adapter.CategoryAdapter;
import com.yezi.meizhi.ui.widget.ImgProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.progressBar)
    ImgProgressBar mImgProgressBar;

    private CategoryAdapter mAdapter;
    private String mCategory;
    private int MEIZHI_COUNT = 10;
    private int MEIZHI_PAGE = 1;
    private static CategoryFragment instance;
    private boolean isRequestData = false;

    public static synchronized CategoryFragment getInstance(String category) {
        if (instance == null) {
            instance = new CategoryFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.CATEGORY, category);
        instance.setArguments(bundle);
        return instance;
    }

    public void setCategory(String category) {
        mCategory = category;
        MEIZHI_PAGE = 1;
        getDatas();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        parseData();
        initViews();
        getDatas();
    }

    private void parseData() {
        mCategory = getArguments().getString(MainActivity.CATEGORY);
    }

    private void getDatas() {
        if (isRequestData) {
            return;
        }
        isRequestData = true;
        mImgProgressBar.startProgress();
        ServiceFactory.getMeiZhiService().getCategoryList(mCategory, MEIZHI_COUNT, MEIZHI_PAGE).
                enqueue(new Callback<MeiZhiMeiZhi>() {
                    @Override
                    public void onResponse(Call<MeiZhiMeiZhi> call, Response<MeiZhiMeiZhi> response) {
                        if (response.body().meizhi.size() == 0) {
                            return;
                        }
                        MeiZhiApp.showToast(R.string.get_meizhi_success);
                        mAdapter.updateData(((MeiZhiMeiZhi) response.body()).meizhi);

                        mImgProgressBar.stopProgress();
                        isRequestData = false;
                    }

                    @Override
                    public void onFailure(Call<MeiZhiMeiZhi> call, Throwable t) {
                        MeiZhiApp.showToast(R.string.get_meizhi_failure);
                        mImgProgressBar.stopProgress();
                        isRequestData = false;
                    }
                });
    }

    private void initViews() {
        mAdapter = new CategoryAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }
}
