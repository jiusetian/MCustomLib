package com.lib_photo.Picker.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.lib_photo.Picker.PhotoPick;
import com.lib_photo.Picker.bean.Photo;
import com.lib_photo.Picker.bean.PhotoPreviewBean;
import com.lib_photo.Picker.controller.PhotoPickConfig;
import com.lib_photo.Picker.controller.PhotoPreviewConfig;
import com.lib_photo.Picker.utils.UtilsHelper;
import com.lib_photo.Picker.weidget.CheckBox;
import com.lib_photo.Picker.weidget.CustomViewPager;
import com.lib_photo.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by LXR ON 2018/9/28.
 * 图片选择的大图浏览act，具备编辑的功能
 */
public class PhotoPreviewEditActivity extends EditActivity implements OnPhotoTapListener, View.OnClickListener {

    private static final String TAG = "PhotoPreviewActivity";

    private ArrayList<Photo> photos;    //全部图片集合
    private ArrayList<String> selectPhotos;     //选中的图片集合
    private CheckBox checkbox;
    private RadioButton radioButton;
    private int pos; //当前pager位置
    private int maxPickSize;            //最大选择个数
    private boolean isChecked = false;
    private boolean originalPicture;    //是否选择的是原图
    private TextView editorTv; //编辑
    private Uri currentUri; //当前显示图片的uri
    ImagePagerAdapter pagerAdapter;
    CustomViewPager viewPager;
    PhotoView currentView; //当前显示view

    private View preView; //大图预览UI
    private View editView; //编辑UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        radioButton = (RadioButton) findViewById(R.id.radioButton);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        editorTv = findViewById(R.id.edit_tv); //编辑
        editorTv.setOnClickListener(this);
        //ViewPager是一个ViewGroup，就是一个view容器，pagerAdapter是一个viewpager的适配器，它决定pager所放置的内容
        viewPager = (CustomViewPager) findViewById(R.id.pager);

        toolbar.setBackgroundColor(PhotoPick.getToolbarBackGround());
        toolbar.setTitle((beginPosition + 1) + "/" + photos.size());
        setSupportActionBar(toolbar);

        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewPager.getCurrentItem();
                setEditImg(photos.get(position).getPath()); //设置当前编辑图片
                pos = position;
                position++;
                toolbar.setTitle(position + "/" + photos.size());
                if (selectPhotos != null && selectPhotos.contains(photos.get(pos).getPath())) {
                    checkbox.setChecked(true,false);
                    if (pos == 1 && selectPhotos.contains(photos.get(pos - 1).getPath())) {
                        checkbox.setChecked(true,false); //没有动画
                    }
                } else {
                    checkbox.setChecked(false,false);
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
        pagerAdapter = new ImagePagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(beginPosition);
        if (beginPosition == 0)
            pageChangeListener.onPageSelected(beginPosition);
    }

    private void setEditImg(String uri) {
        File file = new File(uri);
        currentUri = Uri.fromFile(file);
        setEditImageView();
    }

    /**
     * 刷新数据
     */
    @Override
    public void refreshDataSet() {
        currentView.setImageBitmap(getBitmap());
    }

    @Override
    public View getContentView() {
        View rootView = getLayoutInflater().inflate(R.layout.activity_preview_edit, null);
        preView = rootView.findViewById(R.id.preview_view);
        editView = rootView.findViewById(R.id.edit_view);
        return rootView;
    }

    /**
     * @return 返回当前图片的uri
     */
    @Override
    public Uri getCurrentPhotoUri() {
        return currentUri;
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
        super.onClick(v);
        int id = v.getId();

        if (id == R.id.edit_tv) { //编辑
            setEditorViewVisible(View.VISIBLE);
        }
    }

    @Override
    public void setEditorViewVisible(int visible) {
        editView.setVisibility(visible);
        preView.setVisibility(visible == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            String bigImgUrl = photos.get(position).getPath();
            View view = LayoutInflater.from(PhotoPreviewEditActivity.this).inflate(R.layout.item_photo_preview, container, false);
            PhotoView imageView = (PhotoView) view.findViewById(R.id.iv_media_image);
            imageView.setOnPhotoTapListener(PhotoPreviewEditActivity.this); //点击图片监听

            //显示图片
            PhotoPickConfig.imageLoader.displayImage(PhotoPreviewEditActivity.this, bigImgUrl, imageView, false);

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

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
            currentView = ((View) object).findViewById(R.id.iv_media_image);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.image_pager_exit_animation);
    }
}
