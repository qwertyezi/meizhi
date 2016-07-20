package com.yezi.meizhi.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yezi.meizhi.Navigator;
import com.yezi.meizhi.R;
import com.yezi.meizhi.model.MeiZhiDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryVH> {

    private List<MeiZhiDetail> mTextList;
    private List<MeiZhiDetail> mMeiZhiList;

    public void updateTextData(List<MeiZhiDetail> list) {
        mTextList.clear();
        mTextList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateMeiZhiData(List<MeiZhiDetail> list) {
        mMeiZhiList.clear();
        mMeiZhiList.addAll(list);
        notifyDataSetChanged();
    }

    public CategoryAdapter() {
        mTextList = new ArrayList<>();
        mMeiZhiList = new ArrayList<>();
    }

    @Override
    public CategoryVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_category, parent, false);
        return new CategoryVH(view);
    }

    @Override
    public void onBindViewHolder(CategoryVH holder, int position) {
        holder.bindText(getDataSafe(mTextList, position));
        holder.bindMeiZhi(getDataSafe(mMeiZhiList, position));
    }

    private MeiZhiDetail getDataSafe(List<MeiZhiDetail> list, int position) {
        return (list == null || list.size() <= position) ?
                new MeiZhiDetail() : list.get(position);
    }

    @Override
    public int getItemCount() {
        return mTextList.size();
    }

    public static class CategoryVH extends RecyclerView.ViewHolder {

        @Bind(R.id.img_avatar)
        SimpleDraweeView mImgAvatar;
        @Bind(R.id.text_title)
        TextView mTextTitle;
        @Bind(R.id.text_author)
        TextView mTextAuthor;

        public CategoryVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindText(final MeiZhiDetail meizhi) {
            mTextTitle.setText(meizhi.desc);
            mTextAuthor.setText(meizhi.who);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigator.startWebBrowserActivity(v.getContext(), meizhi.desc, meizhi.url);
                }
            });
        }

        public void bindMeiZhi(final MeiZhiDetail meizhi) {
            mImgAvatar.setImageURI(UriUtil.parseUriOrNull(meizhi.url));
        }
    }
}
