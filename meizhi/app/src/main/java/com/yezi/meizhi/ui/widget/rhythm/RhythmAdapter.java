package com.yezi.meizhi.ui.widget.rhythm;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yezi.meizhi.MeiZhiApp;
import com.yezi.meizhi.R;
import com.yezi.meizhi.model.MeiZhiDetail;

import java.util.ArrayList;
import java.util.List;

public class RhythmAdapter extends BaseAdapter {

    private int mItemWidth;
    private List<MeiZhiDetail> mMeiZhiList;
    private Context mContext;
    private LayoutInflater mInflater;
    private onUpdateViews mOnUpdateViews;

    public interface onUpdateViews {
        void updateViews();
    }

    public RhythmAdapter(Context context) {
        mContext = context;
        mMeiZhiList = new ArrayList<>();
        if (mContext != null) {
            mInflater = LayoutInflater.from(mContext);
        }
    }

    public void setData(List<MeiZhiDetail> meiZhiList) {
        mMeiZhiList.clear();
        mMeiZhiList.addAll(meiZhiList);
        notifyMyDataSetChanged();
    }

    public void setOnUpdateViews(onUpdateViews updateViews) {
        mOnUpdateViews = updateViews;
    }

    private void notifyMyDataSetChanged() {
        mOnUpdateViews.updateViews();
    }

    @Override
    public int getCount() {
        return mMeiZhiList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMeiZhiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mMeiZhiList.get(position).id);
    }

    public void setItemWidth(int itemWidth) {
        mItemWidth = itemWidth;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemHeight = MeiZhiApp.getAppResources().getDimensionPixelSize(R.dimen.rhythm_item_height);
        int itemGap = MeiZhiApp.getAppResources().getDimensionPixelSize(R.dimen.rhythm_item_gap);
        FrameLayout parentLayout = new FrameLayout(mContext);
        ViewGroup.LayoutParams parentParams = new ViewGroup.LayoutParams(mItemWidth, itemHeight);
        parentLayout.setLayoutParams(parentParams);
        parentLayout.setTranslationY(mItemWidth);

        FrameLayout childLayout = new FrameLayout(mContext);
        parentLayout.addView(childLayout);
        FrameLayout.LayoutParams childParams = (FrameLayout.LayoutParams) childLayout.getLayoutParams();
        childParams.width = mItemWidth - 2 * itemGap;
        childParams.height = itemHeight - 2 * itemGap;
        childParams.gravity = Gravity.CENTER;
        childLayout.setLayoutParams(childParams);
        childLayout.setBackgroundResource(R.drawable.home_icon_bg);

        SimpleDraweeView draweeView = new SimpleDraweeView(mContext);
        childLayout.addView(draweeView);
        FrameLayout.LayoutParams viewParams = (FrameLayout.LayoutParams) draweeView.getLayoutParams();
        viewParams.width = mItemWidth - 6 * itemGap;
        viewParams.height = mItemWidth - 6 * itemGap;
        viewParams.gravity = Gravity.CENTER_HORIZONTAL;
        viewParams.topMargin = 2 * itemGap;
        draweeView.setLayoutParams(viewParams);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(30f);
        roundingParams.setOverlayColor(Color.WHITE);
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(MeiZhiApp.getAppResources())
                .setRoundingParams(roundingParams)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .build();
        draweeView.setHierarchy(hierarchy);
        draweeView.setImageURI(UriUtil.parseUriOrNull(mMeiZhiList.get(position).url));

        return parentLayout;
    }
}
