package com.lib_photo.Picker.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.lib_photo.Picker.BaseActivity;
import com.lib_photo.Picker.bean.Photo;
import com.lib_photo.Picker.bean.PhotoPreviewBean;
import com.lib_photo.Picker.controller.PhotoPickConfig;
import com.lib_photo.Picker.controller.PhotoPreviewConfig;
import com.lib_photo.Picker.utils.UtilsHelper;
import com.lib_photo.Picker.weidget.CheckBox;
import com.lib_photo.Picker.weidget.CustomViewPager;
import com.lib_photo.R;

import java.util.ArrayList;


/**
 * Describe :仿微信图片预览
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */
public class PhotoPreviewActivity extends BaseActivity implements OnPhotoTapListener, View.OnClickListener {

    private static final String TAG = "PhotoPreviewActivity";

    private ArrayList<Photo> photos;    //全部图片集合
    private ArrayList<String> selectPhotos;     //选中的图片集合
    private CheckBox checkbox;
    private RadioButton radioButton;
    private int pos;
    private int maxPickSize;            //最大选择个数
    private boolean isChecked = false;
    private boolean originalPicture;    //是否选择的是原图
    private TextView editorTv; //编辑

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Bundle bundle = getIntent().getBundleExtra(PhotoPreviewConfig.EXTRA_BUNDLE);
        if (bundle == null) {
            throw new NullPointerException("bundle is null,please init it");
        }
        PhotoPreviewBean bean = bundle.getParcelable(PhotoPreviewConfig.EXTRA_BEAN);
        if (bean == null) {
            finish();
            return;
        }
        photos = bean.getPhotos();
        if (photos == null || photos.isEmpty()) {
            finish();
            return;
        }
        originalPicture = bean.isOriginalPicture();
        maxPickSize = bean.getMaxPickSize();
        selectPhotos = bean.getSelectPhotos();
        final int beginPosition = bean.getPosition();
        setContentView(R.layout.activity_photo_select);

        radioButton = (RadioButton) findViewById(R.id.radioButton);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        editorTv = findViewById(R.id.edit_tv); //编辑
        editorTv.setVisibility(View.INVISIBLE);
        //ViewPager是一个ViewGroup，就是一个view容器，pagerAdapter是一个viewpager的适配器，它决定pager所放置的内容
        CustomViewPager viewPager = (CustomViewPager) findViewById(R.id.pager);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(PhotoPick.getToolbarBackGround());
//        toolbar.setTitle((beginPosition + 1) + "/" + photos.size());
//        setSupportActionBar(toolbar);

        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
                position++;
                //toolbar.setTitle(position + "/" + photos.size());
                if (selectPhotos != null && selectPhotos.contains(photos.get(pos).getPath())) {
                    checkbox.setChecked(true);
                    if (pos == 1 && selectPhotos.contains(photos.get(pos - 1).getPath())) {
                        checkbox.setChecked(true);
                    }
                } else {
                    checkbox.setChecked(false);
                }
                if (originalPicture) {
                    radioButton.setText(getString(R.string.image_size, UtilsHelper.formatFileSize(photos.get(pos).getSize())));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        //照片滚动监听，更改ToolBar数据
        viewPager.addOnPageChangeListener(pageChangeListener);

        //选中
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPhotos == null) {
                    selectPhotos = new ArrayList<>();
                }
                String path = photos.get(pos).getPath();
                if (selectPhotos.contains(path)) {
                    selectPhotos.remove(path);
                    checkbox.setChecked(false);
                } else {
                    if (maxPickSize == selectPhotos.size()) {
                        checkbox.setChecked(false);
                        return;
                    }
                    selectPhotos.add(path);
                    checkbox.setChecked(true);
                }
                updateMenuItemTitle();
            }
        });

        //原图
        if (originalPicture) {
            radioButton.setText(getString(R.string.image_size, UtilsHelper.formatFileSize(photos.get(beginPosition).getSize())));
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isChecked) {
                        radioButton.setChecked(false);
                        isChecked = false;
                    } else {
                        radioButton.setChecked(true);
                        isChecked = true;
                    }
                }
            });
        } else {
            radioButton.setVisibility(View.GONE);
        }

        viewPager.setAdapter(new ImagePagerAdapter());

        viewPager.setCurrentItem(beginPosition);
        if (beginPosition == 0)
            pageChangeListener.onPageSelected(beginPosition);
    }

    private void updateMenuItemTitle() {
        if (selectPhotos.isEmpty()) {
            menuItem.setTitle(R.string.send);
        } else {
            menuItem.setTitle(getString(R.string.sends, String.valueOf(selectPhotos.size()), String.valueOf(maxPickSize)));
        }
    }

    private MenuItem menuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ok, menu);
        menuItem = menu.findItem(R.id.ok);
        updateMenuItemTitle();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ok) { //发送
            if (maxPickSize == 1) { //单选
                if (selectPhotos.size() == 0) {
                    selectPhotos.add(photos.get(pos).getPath());
                }
                backTo();

            } else { //多选
                if (selectPhotos.size() == 0) return true;
                backTo();
            }

            return true;
        } else if (item.getItemId() == android.R.id.home) { //返回
            backTo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void backTo() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, selectPhotos);
        intent.putExtra(PhotoPreviewConfig.EXTRA_ORIGINAL_PIC, originalPicture);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        backTo();
        super.onBackPressed();
    }

    private boolean toolBarStatus = true;

    //隐藏ToolBar
    private void hideViews() {
        toolBarStatus = false;
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    //显示ToolBar
    private void showViews() {
        toolBarStatus = true;
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    //单击图片时操作
    @Override
    public void onPhotoTap(ImageView view, float x, float y) {
        backTo();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.edit_tv) { //编辑

        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {

            String bigImgUrl = photos.get(position).getPath();
            View view = LayoutInflater.from(PhotoPreviewActivity.this).inflate(R.layout.item_photo_preview, container, false);
            PhotoView imageView = (PhotoView) view.findViewById(R.id.iv_media_image);
            imageView.setOnPhotoTapListener(PhotoPreviewActivity.this); //点击图片监听

            //显示图片
            PhotoPickConfig.imageLoader.displayImage(PhotoPreviewActivity.this, bigImgUrl, imageView, false);

            container.addView(view, 0);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.image_pager_exit_animation);
    }

}

/*
 *   ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 *     ┃　　　┃
 *     ┃　　　┃
 *     ┃　　　┗━━━┓
 *     ┃　　　　　　　┣┓
 *     ┃　　　　　　　┏┛
 *     ┗┓┓┏━┳┓┏┛
 *       ┃┫┫　┃┫┫
 *       ┗┻┛　┗┻┛
 *        神兽保佑
 *        代码无BUG!
 */
