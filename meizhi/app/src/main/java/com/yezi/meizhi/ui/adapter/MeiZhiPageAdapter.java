package com.yezi.meizhi.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yezi.meizhi.R;
import com.yezi.meizhi.model.MeiZhiDetail;

import java.util.ArrayList;
import java.util.List;

public class MeiZhiPageAdapter extends PagerAdapter {
    private Context mContext;
    private List<MeiZhiDetail> meiZhiList;
    private List<View> viewList;

    public MeiZhiPageAdapter(Context context) {
        mContext = context;
        meiZhiList = new ArrayList<>();
        viewList = new ArrayList<>();
    }

    public void updateData(List<MeiZhiDetail> list) {
        meiZhiList.clear();
        meiZhiList.addAll(list);
        buildViews();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    private void buildViews() {
        viewList.clear();
        for (MeiZhiDetail meizhi : meiZhiList) {
            View view = View.inflate(mContext, R.layout.list_item_meizhi, null);
            SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.img_meizhi);
            draweeView.setImageURI(UriUtil.parseUriOrNull(meizhi.url));

            viewList.add(view);
        }
    }
}
