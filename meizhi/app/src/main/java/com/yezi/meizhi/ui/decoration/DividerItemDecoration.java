

package com.yezi.meizhi.ui.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yezi.meizhi.R;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private static final int DEFAULT_DIVIDER_HEIGHT = 1;

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    public enum DividerType {BETWEEN, BOTTOM, NONE}

    private int mOrientation;
    private int padding;
    private int betweenStartpadding;
    private int betweenEndpadding;
    private int betweenDividerHeight;
    private int bottomStartpadding;
    private int bottomEndpadding;
    private int bottomDividerHeight;
    private Context mContext;
    private Paint mPaddingPaint;
    private Paint mDividerPaint;

    public DividerItemDecoration(Context context, int orientation) {
        this(context, orientation, -1, -1);
    }

    public DividerItemDecoration(Context context, int orientation, int padding, int dividerHeight) {
        setOrientation(orientation);
        mContext = context;

        init();
        if (padding != -1) this.padding = padding;
        updatePadding();
        if (dividerHeight != -1) {
            betweenDividerHeight = dividerHeight;
            bottomDividerHeight = dividerHeight;
        }
    }

    public DividerItemDecoration(Context context, int orientation, int startpadding, int endpadding, int dividerHeight) {
        setOrientation(orientation);
        mContext = context;

        init();
        if (startpadding != -1) {
            betweenStartpadding = startpadding;
            bottomStartpadding = startpadding;
        }
        if (endpadding != -1) {
            betweenEndpadding = endpadding;
            bottomEndpadding = endpadding;
        }
        if (dividerHeight != -1) {
            betweenDividerHeight = dividerHeight;
            bottomDividerHeight = dividerHeight;
        }
    }

    public DividerItemDecoration(Context context, int orientation, int betweenStartpadding,
                                 int betweenEndpadding, int betweenDividerHeight,
                                 int bottomStartpadding, int bottomEndpadding, int bottomDividerHeight) {
        setOrientation(orientation);
        mContext = context;

        init();
        if (betweenStartpadding != -1) this.betweenStartpadding = betweenStartpadding;
        if (betweenEndpadding != -1) this.betweenEndpadding = betweenEndpadding;
        if (betweenDividerHeight != -1) this.betweenDividerHeight = betweenDividerHeight;

        if (bottomStartpadding != -1) this.bottomStartpadding = bottomStartpadding;
        if (bottomEndpadding != -1) this.bottomEndpadding = bottomEndpadding;
        if (bottomDividerHeight != -1) this.bottomDividerHeight = bottomDividerHeight;
    }

    private void updatePadding() {
        betweenStartpadding = padding;
        betweenEndpadding = padding;
        bottomStartpadding = padding;
        bottomEndpadding = padding;
    }

    private void init() {
        padding = mContext.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        updatePadding();
        betweenDividerHeight = DEFAULT_DIVIDER_HEIGHT;
        bottomDividerHeight = DEFAULT_DIVIDER_HEIGHT;

        mPaddingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaddingPaint.setColor(mContext.getResources().getColor(android.R.color.white));
        mPaddingPaint.setStyle(Paint.Style.FILL);

        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(mContext.getResources().getColor(R.color.divider_color));
        mDividerPaint.setStyle(Paint.Style.FILL);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            DividerType type = getDividerType(i);
            if (DividerType.BETWEEN == getDividerType(parent.getChildAdapterPosition(child))) {

                final int top = child.getBottom() + params.bottomMargin +
                        Math.round(ViewCompat.getTranslationY(child));
                final int bottom = top + betweenDividerHeight;

                c.drawRect(left, top, left + betweenStartpadding, bottom, mPaddingPaint);
                c.drawRect(right - betweenEndpadding, top, right, bottom, mPaddingPaint);
                c.drawRect(left + betweenStartpadding, top, right - betweenEndpadding, bottom, mDividerPaint);
            }

            if (DividerType.BOTTOM == getDividerType(parent.getChildAdapterPosition(child))) {
                final int top = child.getBottom() + params.bottomMargin +
                        Math.round(ViewCompat.getTranslationY(child));
                final int bottom = top + bottomDividerHeight;

                c.drawRect(left, top, left + bottomStartpadding, bottom, mPaddingPaint);
                c.drawRect(right - bottomEndpadding, top, right, bottom, mPaddingPaint);
                c.drawRect(left + bottomStartpadding, top, right - bottomEndpadding, bottom, mDividerPaint);
            }
        }
    }

    public DividerType getDividerType(int position) {
        return DividerType.BETWEEN;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            if (DividerType.BETWEEN == getDividerType(i)) {
                final int left = child.getRight() + params.rightMargin +
                        Math.round(ViewCompat.getTranslationX(child));
                final int right = left + betweenDividerHeight;
                c.drawRect(left, top, right, top + betweenStartpadding, mPaddingPaint);
                c.drawRect(left, bottom - betweenEndpadding, right, bottom, mPaddingPaint);
                c.drawRect(left, top + betweenStartpadding, right, bottom - betweenEndpadding, mDividerPaint);
            }

            if (DividerType.BOTTOM == getDividerType(i)) {
                final int left = child.getRight() + params.rightMargin +
                        Math.round(ViewCompat.getTranslationX(child));
                final int right = left + bottomDividerHeight;
                c.drawRect(left, top, right, top + bottomStartpadding, mPaddingPaint);
                c.drawRect(left, bottom - bottomEndpadding, right, bottom, mPaddingPaint);
                c.drawRect(left, top + bottomStartpadding, right, bottom - bottomEndpadding, mDividerPaint);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (mOrientation == VERTICAL_LIST) {
            if (DividerType.BETWEEN == getDividerType(parent.getChildAdapterPosition(view))) {
                outRect.set(0, 0, 0, betweenDividerHeight);
            }

            if (DividerType.BOTTOM == getDividerType(parent.getChildAdapterPosition(view))) {
                outRect.set(0, 0, 0, bottomDividerHeight);
            }

            if (DividerType.NONE == getDividerType(parent.getChildAdapterPosition(view))) {
                outRect.set(0, 0, 0, 0);
            }
        } else {
            if (DividerType.BETWEEN == getDividerType(parent.getChildAdapterPosition(view))) {
                outRect.set(0, 0, betweenDividerHeight, 0);
            }

            if (DividerType.BOTTOM == getDividerType(parent.getChildAdapterPosition(view))) {
                outRect.set(0, 0, bottomDividerHeight, 0);
            }

            if (DividerType.NONE == getDividerType(parent.getChildAdapterPosition(view))) {
                outRect.set(0, 0, 0, 0);
            }
        }
    }

}
