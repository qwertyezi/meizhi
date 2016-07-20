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
import com.yezi.meizhi.Navigator;
import com.yezi.meizhi.R;
import com.yezi.meizhi.api.ServiceFactory;
import com.yezi.meizhi.model.MeiZhiDetail;
import com.yezi.meizhi.model.MeiZhiMeiZhi;
import com.yezi.meizhi.ui.activity.MainActivity;
import com.yezi.meizhi.ui.adapter.CategoryAdapter;
import com.yezi.meizhi.ui.decoration.DividerItemDecoration;
import com.yezi.meizhi.ui.widget.VPtrFrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.vptr_layout)
    VPtrFrameLayout mVPtrFrameLayout;

    private CategoryAdapter mAdapter;
    private String mCategory;
    private int MEIZHI_COUNT = 10;
    private int MEIZHI_PAGE = 1;
    private static CategoryFragment instance;
    private static final String sMeizhiType = "福利";
    private List<MeiZhiDetail> mTextList;
    private List<MeiZhiDetail> mMeiZhiList;

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
        getDatas(false);
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
        getDatas(false);
    }

    private void parseData() {
        mTextList = new ArrayList<>();
        mMeiZhiList = new ArrayList<>();
        mCategory = getArguments().getString(MainActivity.CATEGORY);
    }

    private void getDatas(final boolean isLoadMore) {
        ServiceFactory.getMeiZhiService().getCategoryList(mCategory, MEIZHI_COUNT, MEIZHI_PAGE).
                enqueue(new Callback<MeiZhiMeiZhi>() {
                    @Override
                    public void onResponse(Call<MeiZhiMeiZhi> call, Response<MeiZhiMeiZhi> response) {
                        if (response.body().meizhi.size() == 0) {
                            return;
                        }
                        MeiZhiApp.showToast(R.string.get_meizhi_success);

                        if (!isLoadMore) {
                            mTextList.clear();
                        }
                        mTextList.addAll(response.body().meizhi);
                        mAdapter.updateTextData(mTextList);
                        if (isLoadMore) {
                            hideMoreProgress();
                        }
                    }

                    @Override
                    public void onFailure(Call<MeiZhiMeiZhi> call, Throwable t) {
                        MeiZhiApp.showToast(R.string.get_meizhi_failure);
                        if (isLoadMore) {
                            hideMoreProgress();
                        }
                    }
                });
        ServiceFactory.getMeiZhiService().getCategoryList(sMeizhiType, MEIZHI_COUNT, MEIZHI_PAGE).
                enqueue(new Callback<MeiZhiMeiZhi>() {
                    @Override
                    public void onResponse(Call<MeiZhiMeiZhi> call, Response<MeiZhiMeiZhi> response) {
                        if (response.body().meizhi.size() == 0) {
                            return;
                        }

                        if (!isLoadMore) {
                            mMeiZhiList.clear();
                        }
                        mMeiZhiList.addAll(response.body().meizhi);
                        mAdapter.updateMeiZhiData(mMeiZhiList);
                        if (isLoadMore) {
                            hideMoreProgress();
                        }
                    }

                    @Override
                    public void onFailure(Call<MeiZhiMeiZhi> call, Throwable t) {
                        if (isLoadMore) {
                            hideMoreProgress();
                        }
                    }
                });
    }

    private void initViews() {
        mVPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                MEIZHI_PAGE = 1;
                getDatas(false);
                mVPtrFrameLayout.refreshComplete();
            }
        });

        mAdapter = new CategoryAdapter();
        mAdapter.setOnItemClickListener(new CategoryAdapter.onItemClickListener() {
            @Override
            public void onItemClick(MeiZhiDetail meiZhiDetail) {
                Navigator.startWebBrowserActivity(getContext(), meiZhiDetail.desc, meiZhiDetail.url,
                        ((MainActivity) getActivity()).getCurrentColor());
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL,
                MeiZhiApp.getAppResources().getDimensionPixelSize(R.dimen.large_padding),
                MeiZhiApp.getAppResources().getDimensionPixelSize(R.dimen.divider_height)));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (((LinearLayoutManager) recyclerView.getLayoutManager()).
                        findLastVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1 &&
                        !((CategoryAdapter) recyclerView.getAdapter()).isShowingFooter()) {
                    onLoadMore();
                }
            }
        });
    }

    private void onLoadMore() {
        showMoreProgress();
        ++MEIZHI_PAGE;
        getDatas(true);
    }

    private void showMoreProgress() {
        mAdapter.showFooter();
    }

    private void hideMoreProgress() {
        mAdapter.hideFooter();
    }

    @Override
    public void onDestroy() {
        mRecyclerView.clearOnScrollListeners();
        super.onDestroy();
    }
}
