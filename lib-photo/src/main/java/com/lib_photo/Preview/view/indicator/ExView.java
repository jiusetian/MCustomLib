package com.lib_photo.Preview.view.indicator;

import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lib_photo.Preview.style.IExViewListener;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by LXR ON 2018/9/14.
 * 添加小图标
 */
public class ExView implements IExViewListener {

    private boolean top; //是否在顶部
    private View exView;

    public ExView(View exView, boolean top) {
        this.exView = exView;
        this.top = top;
    }

    private ViewPager mViewPager;
    private final ViewPager.OnPageChangeListener mInternalPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            //viewpager被选中的pos
            if (mViewPager.getAdapter() == null || mViewPager.getAdapter().getCount() <= 0)
                return;

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public void setViewPager(ViewPager viewPager) {
        if (viewPager != null && viewPager.getAdapter() != null) {
            mViewPager = viewPager;
            mViewPager.removeOnPageChangeListener(mInternalPageChangeListener);
            mViewPager.addOnPageChangeListener(mInternalPageChangeListener);
            mInternalPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
        }
    }

    @Override
    public void attach(FrameLayout parent) {
        if (exView == null) return;
        onSetLayoutParams(exView);
        parent.addView(exView);
    }

    @Override
    public void onShow(ViewPager viewPager) {
        exView.setVisibility(View.VISIBLE);
        setViewPager(viewPager); //设置viewpager相关
    }

    @Override
    public void onHide() {
        if (exView == null) return;
        exView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRemove() {
        if (exView == null) return;
        ViewGroup vg = (ViewGroup) exView.getParent();
        if (vg != null) {
            vg.removeView(exView);
        }
    }

    @Override
    public void onSetLayoutParams(View exView) {
        FrameLayout.LayoutParams indexLp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int gravityLoca = top == false ? Gravity.BOTTOM : Gravity.TOP;
        indexLp.gravity = gravityLoca | Gravity.RIGHT;
        if (top) {
            exView.setPadding(15, 30, 15, 10);
        } else {
            exView.setPadding(15, 10, 15, 30);
        }
        exView.setLayoutParams(indexLp);
    }
}
