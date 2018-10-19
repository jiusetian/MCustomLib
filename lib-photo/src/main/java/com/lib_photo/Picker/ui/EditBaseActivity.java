package com.lib_photo.Picker.ui;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.ViewSwitcher;

import com.gyf.barlibrary.ImmersionBar;
import com.lib_photo.Common.HeaderLayout;
import com.lib_photo.Editor.IMGTextEditDialog;
import com.lib_photo.Editor.core.IMGMode;
import com.lib_photo.Editor.core.IMGText;
import com.lib_photo.Editor.view.IMGColorGroup;
import com.lib_photo.Editor.view.IMGView;
import com.lib_photo.R;

/**
 * Created by XR_liu on 2018/9/28.
 */
public abstract class EditBaseActivity extends AppCompatActivity
        implements View.OnClickListener,
        IMGTextEditDialog.Callback, RadioGroup.OnCheckedChangeListener,
        DialogInterface.OnShowListener, DialogInterface.OnDismissListener {

    private static String TAG="tag";
    protected IMGView mImgView;

    private RadioGroup mModeGroup;

    private IMGColorGroup mColorGroup;

    private IMGTextEditDialog mTextDialog;

    private View mLayoutOpSub;

    private ViewSwitcher mOpSwitcher, mOpSubSwitcher;

    public static final int OP_HIDE = -1;

    public static final int OP_NORMAL = 0;

    public static final int OP_CLIP = 1;

    public static final int OP_SUB_DOODLE = 0;

    public static final int OP_SUB_MOSAIC = 1;

    public Toolbar toolbar;
    public HeaderLayout headerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        initViews();
        onCreated();
    }

    public void onCreated() {
        // 5.0系统以上才开启沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT); //设置成透明
        }
    }

    /**
     * 设置当前编辑的图片
     */
    public void setEditImageView() {
        Bitmap bitmap = getBitmap(); //通过图片的uri去解析得到bitmap
        mImgView.setImageBitmap(bitmap);
    }

    /***
     * 获取act的contentview
     * @return
     */
    public abstract View getContentView();

    public abstract Uri getCurrentPhotoUri();

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        headerLayout=toolbar.findViewById(R.id.header);
        mImgView = findViewById(R.id.image_canvas);
        mModeGroup = findViewById(R.id.rg_modes);
        mOpSwitcher = findViewById(R.id.vs_op);
        mOpSubSwitcher = findViewById(R.id.vs_op_sub);

        mColorGroup = findViewById(R.id.cg_colors);
        mColorGroup.setOnCheckedChangeListener(this);

        mLayoutOpSub = findViewById(R.id.layout_op_sub);
        initImmersionBar();
    }

    protected ImmersionBar immersionBar;
    protected void initImmersionBar() {
        //在BaseActivity里初始化
        immersionBar = ImmersionBar.with(this);
        immersionBar.init();
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.rb_doodle) {
            onModeClick(IMGMode.DOODLE);
        } else if (vid == R.id.btn_text) {
            onTextModeClick();
        } else if (vid == R.id.rb_mosaic) {
            onModeClick(IMGMode.MOSAIC);
        } else if (vid == R.id.btn_clip) {
            onModeClick(IMGMode.CLIP);
        } else if (vid == R.id.btn_undo) {
            onUndoClick();
        } else if (vid == R.id.tv_done) {
            onDoneClick();
        } else if (vid == R.id.tv_cancel) {
            onCancelClick();
        } else if (vid == R.id.ib_clip_cancel) {
            onCancelClipClick();
        } else if (vid == R.id.ib_clip_done) {
            onDoneClipClick();
        } else if (vid == R.id.tv_clip_reset) {
            onResetClipClick();
        } else if (vid == R.id.ib_clip_rotate) {
            onRotateClipClick();
        }
    }

    public void updateModeUI() {
        IMGMode mode = mImgView.getMode();
        switch (mode) {
            case DOODLE:
                mModeGroup.check(R.id.rb_doodle);
                setOpSubDisplay(OP_SUB_DOODLE);
                break;
            case MOSAIC:
                mModeGroup.check(R.id.rb_mosaic);
                setOpSubDisplay(OP_SUB_MOSAIC);
                break;
            case NONE:
                mModeGroup.clearCheck();
                setOpSubDisplay(OP_HIDE);
                break;
        }
    }

    public void onTextModeClick() {
        if (mTextDialog == null) {
            mTextDialog = new IMGTextEditDialog(this, this);
            mTextDialog.setOnShowListener(this);
            mTextDialog.setOnDismissListener(this);
        }
        mTextDialog.show();
    }

    @Override
    public final void onCheckedChanged(RadioGroup group, int checkedId) {
        onColorChanged(mColorGroup.getCheckColor());
    }

    public void setOpDisplay(int op) {
        if (op >= 0) {
            mOpSwitcher.setDisplayedChild(op);
        }
    }

    public void setOpSubDisplay(int opSub) {
        if (opSub < 0) {
            mLayoutOpSub.setVisibility(View.GONE);
        } else {
            mOpSubSwitcher.setDisplayedChild(opSub);
            mLayoutOpSub.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        mOpSwitcher.setVisibility(View.GONE);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mOpSwitcher.setVisibility(View.VISIBLE);
    }

    public abstract Bitmap getBitmap();

    public abstract void onModeClick(IMGMode mode);

    public abstract void onUndoClick();

    public abstract void onCancelClick();

    public abstract void onDoneClick();

    public abstract void onCancelClipClick();

    public abstract void onDoneClipClick();

    public abstract void onResetClipClick();

    public abstract void onRotateClipClick();

    public abstract void onColorChanged(int checkedColor);

    public abstract void setEditorViewVisible(int visible);

    public abstract void refreshDataSet();

    @Override
    public abstract void onText(IMGText text);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
