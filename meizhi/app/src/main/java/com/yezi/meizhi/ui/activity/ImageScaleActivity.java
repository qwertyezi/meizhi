package com.yezi.meizhi.ui.activity;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yezi.meizhi.Navigator;
import com.yezi.meizhi.R;

import butterknife.Bind;

public class ImageScaleActivity extends BaseActivity {

    @Bind(R.id.img_scale)
    SimpleDraweeView mImgScale;
    @Bind(R.id.layout_container)
    FrameLayout mContainer;

    private String mUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_scale);

        parseData();
        initViews();
        showImg();
    }

    private void initViews() {
        mContainer.setOnClickListener(v -> finish());
        GenericDraweeHierarchy hierarchy = mImgScale.getHierarchy();
        hierarchy.setProgressBarImage(new ProgressBarDrawable());
        mImgScale.setHierarchy(hierarchy);
    }

    private void parseData() {
        mUrl = getIntent().getStringExtra(Navigator.EXTRA_IMG_URL);
    }

    private void showImg() {
        mImgScale.setImageURI(UriUtil.parseUriOrNull(mUrl));
    }


}
