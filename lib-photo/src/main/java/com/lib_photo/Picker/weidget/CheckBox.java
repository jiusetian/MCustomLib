package com.lib_photo.Picker.weidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lib_photo.R;


/**
 * 本视图包含标题功能
 *
 * @author liujingxing on 16/7/18.
 */
public class CheckBox extends LinearLayout {
    private String TAG="tag";
    protected final String NAME_SPACE = "http://schemas.android.com/apk/res/android";

    public int mDefaultSize;

    private TextView mTextView;
    private CheckView mCheckView;

    private OnCheckedChangeListener mListener;

    public CheckBox(Context context) {
        this(context, null);
    }

    public CheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        //setBackgroundResource(R.drawable.button);
        mDefaultSize = dp2px(context, 10);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {
        boolean clickable = attrs.getAttributeBooleanValue(NAME_SPACE, "clickable", true);
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        setClickable(clickable);

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CheckBox);
        String text = ta.getString(R.styleable.CheckBox_text);
        int textColor = ta.getColor(R.styleable.CheckBox_cTextColor, Color.BLACK);
        int bgColor=ta.getColor(R.styleable.CheckBox_bgColor, Color.TRANSPARENT);
        float textSize = ta.getDimension(R.styleable.CheckBox_cTextSize, dp2px(context, 17));
        int middlePadding = ta.getDimensionPixelOffset(R.styleable.CheckBox_middlePadding, mDefaultSize*0);
        int checkBoxWidth = ta.getDimensionPixelOffset(R.styleable.CheckBox_checkBoxWidth, mDefaultSize * 2);
        int checkBoxHeight = ta.getDimensionPixelOffset(R.styleable.CheckBox_checkBoxHeight, mDefaultSize * 2);
        ta.recycle();

        LayoutParams mCheckParams = new LayoutParams(checkBoxWidth, checkBoxHeight);
        mCheckView = new CheckView(context, attrs);
        mCheckView.setBackgroundColor(bgColor); //背景色

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mTextView = new TextView(context);
        params.leftMargin = middlePadding;
        mTextView.setLayoutParams(params);
        mTextView.setText(text);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mTextView.setTextColor(textColor);

        addView(mCheckView, mCheckParams); //先添加选择图标
        addView(mTextView, params); //后添加文本view

        if (!isClickable()) return;
        //点击是有动画的
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckView.toggle(true);
            }
        });
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
//        int defaultPadding = dp2px(getContext(), 10);
//        if (left == 0) left = defaultPadding;
//        if (top == 0) top = defaultPadding;
//        if (right == 0) right = defaultPadding;
//        if (bottom == 0) bottom = defaultPadding;
        super.setPadding(left, top, right, bottom);
    }

    public int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void toggle() {
        toggle(false);
    }

    public void toggle(boolean anim) {
        mCheckView.toggle(anim);
    }

    public boolean isChecked() {
        return mCheckView.isChecked();
    }

    public void setChecked(boolean checked) {
        setChecked(checked, true);
    }

    public void setChecked(boolean checked, boolean anim) {
        mCheckView.setChecked(checked, anim);
        if (mListener != null) {
            mListener.onCheckedChanged(this, mCheckView.isChecked());
        }
    }

    public void setCheckedColor(int checkedColor) {
        mCheckView.setCheckedColor(checkedColor);
    }

    public void setShape(int shape) {
        mCheckView.setShape(shape);
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public String getText(String text) {
        return mTextView.getText().toString();
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.mListener = l;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CheckBox checkBox, boolean isChecked);
    }
}
