package com.lib_photo.Picker;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lib_photo.R;

/**
 * Descriptions : Activity基类
 * GitHub : https://github.com/Rain0413
 * Blog   : http://blog.csdn.net/sinat_33680954
 * Created by Rain on 16-12-7.
 */
public class BaseActivity extends AppCompatActivity {
    public Toolbar toolbar;
    //protected ImmersionBar immersionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 5.0系统以上才开启沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        //初始化沉浸式
//        if (isImmersionBarEnabled())
//            initImmersionBar();
    }

//    protected void initImmersionBar() {
//        //在BaseActivity里初始化
//        immersionBar = ImmersionBar.with(this);
//        immersionBar.init();
//    }

    protected void setContentView(int layoutId, boolean hasTitle) {
        setContentView(layoutId);
        if (hasTitle) {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
    }

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
