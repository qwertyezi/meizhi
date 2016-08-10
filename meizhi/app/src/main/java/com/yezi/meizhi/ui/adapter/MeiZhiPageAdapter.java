package com.yezi.meizhi.ui.adapter;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yezi.meizhi.MeiZhiApp;
import com.yezi.meizhi.Navigator;
import com.yezi.meizhi.R;
import com.yezi.meizhi.model.MeiZhiDetail;
import com.yezi.meizhi.utils.PopupWindowUtils;
import com.yezi.meizhi.utils.ScreenSizeUtil;

import java.util.ArrayList;
import java.util.List;

public class MeiZhiPageAdapter extends PagerAdapter {
    private Context mContext;
    private List<MeiZhiDetail> meiZhiList;

    public MeiZhiPageAdapter(Context context) {
        mContext = context;
        meiZhiList = new ArrayList<>();
    }

    public void updateData(List<MeiZhiDetail> list) {
        meiZhiList.clear();
        meiZhiList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return meiZhiList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = buildView(meiZhiList.get(position));
        container.addView(view);
        return view;
    }

    private View buildView(MeiZhiDetail meizhi) {
        View view = View.inflate(mContext, R.layout.list_item_meizhi, null);
        SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.img_meizhi);
        draweeView.setImageURI(UriUtil.parseUriOrNull(meizhi.url));
        Point point = new Point();
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        point.x = (int) event.getRawX();
                        point.y = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(point.x - event.getRawX()) < 5 * ScreenSizeUtil.getTouchSlop(v.getContext()) &&
                                Math.abs(point.y - event.getRawY()) < 5 * ScreenSizeUtil.getTouchSlop(v.getContext())) {
                            PopupWindowUtils.getInstance().togglePopupWindow(v.getContext(), (int) event.getRawX(), (int) event.getRawY());
                        }
                        break;
                }
                return true;
            }
        });
        PopupWindowUtils.getInstance().setOnTextClickListener(new PopupWindowUtils.onTextClickListener() {
            @Override
            public void clickBigImg(View view) {
                Navigator.startImageScaleActivity(view.getContext(), meizhi.url);
                PopupWindowUtils.getInstance().dismissPopupWindow();
            }

            @Override
            public void clickSaveImg(View view) {
                MeiZhiApp.showToast("右");
            }
        });
        return view;
    }
}
