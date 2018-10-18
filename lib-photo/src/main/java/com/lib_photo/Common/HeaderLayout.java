package com.lib_photo.Common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lib_photo.R;


/**
 * 自定义标题栏
 */
public class HeaderLayout extends LinearLayout {
    LayoutInflater mInflater;
    RelativeLayout header, midContainer;
    LinearLayout leftContainer, rightContainer;
    TextView titleView;
    View midView;
    TextView leftButton;
    EditText etSearch;

    public HeaderLayout(Context context) {
        super(context);
        init();
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        header = (RelativeLayout) mInflater.inflate(R.layout.common_base_header, null, false);
        leftContainer = header.findViewById(R.id.leftContainer);
        rightContainer = header.findViewById(R.id.rightContainer);
        midContainer = header.findViewById(R.id.midContainer);
        titleView = header.findViewById(R.id.titleView);
        midView = header.findViewById(R.id.midView);
        etSearch = header.findViewById(R.id.et_search);
        addView(header);//添加view
        initBackup(leftContainer);
    }

    //初始化左边的退出按钮
    private void initBackup(View leftView) {
        leftView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() instanceof Activity)
                    ((Activity) getContext()).finish();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //设置头布局背景颜色
    public void setHeaderBackgroundColor(int color) {
        header.setBackgroundColor(color);
    }

    //设置中间显示的标题内容
    public void showTitle(int titleId) {
        titleView.setText(titleId);
        titleView.setVisibility(VISIBLE);
        midView.setVisibility(GONE);
    }

    public void showTitle(String s) {
        titleView.setText(s);
        titleView.setVisibility(VISIBLE);
        midView.setVisibility(GONE);
    }

    //设置中间的view
    public void showMidView(String searchHint, boolean isHideRightView) {
        if (midView == null) return;
//        midContainer.addView(midView);
//        LogUtils.d("容器和view的长度分别是="+midContainer.getWidth()+"。。。。。"+midView.getWidth());
        titleView.setVisibility(GONE);
        midView.setVisibility(VISIBLE);
        if (isHideRightView) //隐藏右边的view
            rightContainer.setVisibility(GONE);
        etSearch.setHint(searchHint);
    }

    //隐藏中间的view
    public void hideMidView() {
        if (midView == null) return;
        midView.setVisibility(GONE);
        titleView.setVisibility(VISIBLE);
    }

    //获取对应的view
    public View getView(int viewId) {
        return header.findViewById(viewId);
    }

    //设置左边图标按钮
    public void showLeftImageButton(int leftResId, OnClickListener listener) {
        leftButton = (TextView) leftContainer.findViewById(R.id.leftBtn);
        leftButton.setVisibility(VISIBLE);
        leftButton.setCompoundDrawables(getResources().getDrawable(leftResId),null,null,null);//通过资源id设置imageview的图标
        leftButton.setClickable(false);
        leftContainer.setOnClickListener(listener);
    }

    public boolean isMidviewVisiable() {
        if (midView == null) return false;
        return midView.getVisibility() == VISIBLE ? true : false;
    }

    public void setLeftImageButton(int leftResId) {
        leftButton.setCompoundDrawables(getResources().getDrawable(leftResId),null,null,null);
    }


    //设置右边的图标按钮
    public void showRightImageButton(int rightResId, OnClickListener listener) {
        rightContainer.removeAllViews();
        View imageViewLayout = mInflater.inflate(R.layout.common_base_header_right_image_btn, null, false);
        ImageButton rightButton = imageViewLayout.findViewById(R.id.imageBtn);
        rightButton.setImageResource(rightResId);//通过资源id设置imageview的图标
        rightButton.setClickable(false);
        rightContainer.setOnClickListener(listener);//这个listener在我们调用这个方法的时候实现
        rightContainer.addView(imageViewLayout);
    }

    //设置右边
    public void showRightTextButton(String text ,OnClickListener listener){
        rightContainer.removeAllViews();
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setClickable(false);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        rightContainer.addView(textView);
        rightContainer.setOnClickListener(listener);
    }

    public void showRightView(boolean isShow) {
        rightContainer.setVisibility(isShow ? VISIBLE : GONE);
    }


}
