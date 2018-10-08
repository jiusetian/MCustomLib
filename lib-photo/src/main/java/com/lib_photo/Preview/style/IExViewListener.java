package com.lib_photo.Preview.style;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by LXR ON 2018/9/14.
 */
public interface IExViewListener {

    /**
     * 在父容器上添加一个图片索引指示器 UI 组件
     *
     * @param parent TransferImage
     */
    void attach(FrameLayout parent);

    /**
     * 显示图片索引指示器 UI 组件
     *
     * @param viewPager TransferImage
     */
    void onShow(ViewPager viewPager);

    /**
     * 隐藏图片索引指示器 UI 组件
     */
    void onHide();

    /**
     * 移除图片索引指示器 UI 组件
     */
    void onRemove();

    /**
     * 设置图标的layout参数
     */
    void onSetLayoutParams(View view);
}
