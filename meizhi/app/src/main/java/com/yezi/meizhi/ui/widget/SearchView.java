package com.yezi.meizhi.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yezi.meizhi.MeiZhiApp;
import com.yezi.meizhi.R;

public class SearchView extends FrameLayout {

    private static final int ANIMATION_MILLIS = 200;

    private FrameLayout mSearchBar;
    private EditText mSearchSrc;
    private ImageView mCloseButton;
    private TextView mFrontIcon;
    private LinearLayout mSearchLayout;
    private TextView mTextClose;

    private int mFrontIconFirstX;

    private boolean mExpanded;

    private OnToggleListener mOnToggleListener;

    private OnQueryTextListener mOnQueryTextListener;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.view_search_view, this);

        mSearchSrc = (EditText) findViewById(R.id.search_src_text);
        mCloseButton = (ImageView) findViewById(R.id.search_close_btn);
        mFrontIcon = (TextView) findViewById(R.id.search_front_icon);
        mSearchBar = (FrameLayout) findViewById(R.id.search_bar);
        mSearchLayout = (LinearLayout) findViewById(R.id.search_layout);
        mTextClose = (TextView) findViewById(R.id.text_close);

        mSearchBar.setOnClickListener(mOnClickListener);
        mCloseButton.setOnClickListener(mOnClickListener);
        mTextClose.setOnClickListener(mOnClickListener);

        mSearchSrc.addTextChangedListener(mTextWatcher);
        mSearchSrc.setOnEditorActionListener(mOnEditorActionListener);

        mFrontIcon.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mFrontIcon.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mFrontIconFirstX = (int) mFrontIcon.getX();
            }
        });
    }

    private final OnClickListener mOnClickListener = v -> {
        switch (v.getId()) {
            case R.id.search_bar:
                toggle(true);
                break;
            case R.id.search_close_btn:
                onCloseClicked();
                break;
            case R.id.text_close:
                toggle(false);
                break;
            default:
        }
    };

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SearchView.this.onTextChanged(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private final TextView.OnEditorActionListener mOnEditorActionListener = (v, actionId, event) -> {
        onSubmitQuery();
        return true;
    };

    public boolean isExpanded() {
        return mExpanded;
    }

    private void onCloseClicked() {
        mSearchSrc.setText("");
    }

    public void toggle(final boolean expanded) {
        if (expanded == mExpanded) {
            return;
        }

        mExpanded = expanded;

        if (mOnToggleListener != null) {
            mOnToggleListener.onToggle(mExpanded);
        }

        createAnimator(mFrontIcon, mExpanded).start();
    }

    private ObjectAnimator createAnimator(View view, boolean mExpanded) {
        int largePadding = MeiZhiApp.getAppResources().getDimensionPixelSize(R.dimen.large_padding);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "x",
                mExpanded ? view.getX() : mSearchBar.getX() + largePadding,
                mExpanded ? mSearchBar.getX() + largePadding : mFrontIconFirstX - largePadding);
        animator.setDuration(ANIMATION_MILLIS);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mExpanded) {
                    mSearchLayout.setVisibility(VISIBLE);
                    view.setVisibility(INVISIBLE);
                }

                mTextClose.setVisibility(mExpanded ? VISIBLE : GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (!mExpanded) {
                    mSearchLayout.setVisibility(INVISIBLE);
                    view.setVisibility(VISIBLE);
                }
            }
        });
        return animator;
    }

    public void setOnToggleListener(OnToggleListener onToggleListener) {
        mOnToggleListener = onToggleListener;
    }

    private void onTextChanged(CharSequence newText) {
        if (mOnQueryTextListener != null) {
            mOnQueryTextListener.onQueryTextChange(newText.toString());
        }
    }

    private void onSubmitQuery() {
        CharSequence query = mSearchSrc.getText();
        if (mOnQueryTextListener != null) {
            mOnQueryTextListener.onQueryTextSubmit(query.toString());
        }
    }

    public void setOnQueryTextListener(OnQueryTextListener onQueryTextListener) {
        mOnQueryTextListener = onQueryTextListener;
    }

    public interface OnToggleListener {
        void onToggle(boolean expanded);
    }

    public interface OnQueryTextListener {
        void onQueryTextSubmit(String query);

        void onQueryTextChange(String newText);
    }

    public EditText getSearchSrc() {
        return mSearchSrc;
    }
}
