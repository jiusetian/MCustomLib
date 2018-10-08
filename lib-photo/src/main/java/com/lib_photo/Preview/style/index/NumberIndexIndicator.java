package com.lib_photo.Preview.style.index;

import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lib_photo.Preview.style.IIndexIndicator;
import com.lib_photo.Preview.view.indicator.NumberIndicator;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 图片翻页时使用 {@link NumberIndicator} 去指示当前图片的位置
 * <p>
 * Created by Hitomis on 2017/4/23 0023.
 * <p>
 * email: 196425254@qq.com
 */
public class NumberIndexIndicator implements IIndexIndicator {

    private NumberIndicator numberIndicator;
    private boolean isTop; //是否在顶部


    /**
     * 是否在顶部，默认在底部
     *
     * @param isTop
     */
    public NumberIndexIndicator(boolean isTop) {
        this.isTop = isTop;
    }

    @Override
    public void attach(FrameLayout parent) {
        FrameLayout.LayoutParams indexLp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int gravityLoca = isTop == false ? Gravity.BOTTOM : Gravity.TOP;
        indexLp.gravity = gravityLoca | Gravity.CENTER_HORIZONTAL;

        if (isTop)
            indexLp.topMargin = 30;
        else
            indexLp.bottomMargin = 30;

        numberIndicator = new NumberIndicator(parent.getContext());
        numberIndicator.setLayoutParams(indexLp);

        parent.addView(numberIndicator);
    }

    @Override
    public void onShow(ViewPager viewPager) {
        numberIndicator.setVisibility(View.VISIBLE);
        numberIndicator.setViewPager(viewPager);
    }

    @Override
    public void onHide() {
        if (numberIndicator == null) return;
        numberIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onRemove() {
        if (numberIndicator == null) return;
        ViewGroup vg = (ViewGroup) numberIndicator.getParent();
        if (vg != null) {
            vg.removeView(numberIndicator);
        }
    }
}
