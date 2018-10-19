package com.lib_photo.Common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by XR_liu on 2018/10/18.
 * recylerview网格布局的时候，添加分割线
 */
public class GridItemDivider extends RecyclerView.ItemDecoration {

    private String TAG = getClass().getSimpleName();
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable divider;

    public GridItemDivider(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        divider = a.getDrawable(0);
        a.recycle();
    }

    public GridItemDivider(Drawable drawable) {
        divider = drawable;
    }

    public GridItemDivider(int height, int color) {
        GradientDrawable shapeDrawable = new GradientDrawable();
        shapeDrawable.setColor(color);
        shapeDrawable.setShape(GradientDrawable.RECTANGLE);
        shapeDrawable.setSize(height, height);
        divider = shapeDrawable;
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        drawHorizontal(c, parent);
        drawVertical(c, parent);

    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount(); //获取可见item的数量
        int spanCount = getSpanCount(parent);
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + divider.getIntrinsicWidth();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
            if (i < spanCount) { //画第一行顶部的分割线
                drawHorizontalForFirstRow(c, child);
            }
        }
    }

    private void drawHorizontalForFirstRow(Canvas c, View child) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                .getLayoutParams();
        int left = child.getLeft() - params.leftMargin - divider.getIntrinsicWidth();
        int top = child.getTop() - params.topMargin - divider.getIntrinsicHeight();
        int right = child.getRight() + params.rightMargin + divider.getIntrinsicWidth();
        int bottom = top + divider.getIntrinsicHeight();
        divider.setBounds(left, top, right, bottom);
        divider.draw(c);
    }

    private void drawVerticalForFirstColum(Canvas c, View child) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                .getLayoutParams();
        int left = child.getLeft() - params.leftMargin - divider.getIntrinsicWidth();
        int top = child.getTop() - params.topMargin;
        int right = child.getLeft() - params.leftMargin;
        int bottom = top + child.getHeight() + divider.getIntrinsicHeight();
        divider.setBounds(left, top, right, bottom);
        divider.draw(c);
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + divider.getIntrinsicWidth();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
            if (isFirstColum(parent, i, getSpanCount(parent))) { //画第一列左边分割线
                drawVerticalForFirstColum(c, child);
            }
        }
    }

    private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
                                int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else
            {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    //是否为第一列
    private boolean isFirstColum(RecyclerView parent, int pos, int spanCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) { //网格布局
            if ((pos + 1) % spanCount == 1) {
                return true;
            }
        }
        return false;
    }

    //是否为第一行
    private boolean isFirstRaw(int pos, int spanCount) {
        if (pos < spanCount) {
            return true;
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition,
                               RecyclerView parent) {
        int spanCount = getSpanCount(parent); //列数

        if (itemPosition == 0) { //第一行第一个，四边都画
            outRect.set(divider.getIntrinsicWidth(), divider.getIntrinsicHeight(),
                    divider.getIntrinsicWidth(), divider.getIntrinsicHeight());
        } else if (isFirstRaw(itemPosition, spanCount)) { //第一行，画上下右三边
            outRect.set(0, divider.getIntrinsicHeight(), divider.getIntrinsicWidth(), divider.getIntrinsicHeight());
        } else if (isFirstColum(parent, itemPosition, spanCount)) { //第一列，画左右下三边
            outRect.set(divider.getIntrinsicWidth(), 0, divider.getIntrinsicWidth(), divider.getIntrinsicHeight());
        } else { //其他，画右下两边
            outRect.set(0, 0, divider.getIntrinsicWidth(), divider.getIntrinsicHeight());
        }

    }

}
