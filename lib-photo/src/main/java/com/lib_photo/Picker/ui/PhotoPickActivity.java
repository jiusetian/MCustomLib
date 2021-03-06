package com.lib_photo.Picker.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lib_photo.Common.GridItemDivider;
import com.lib_photo.Common.ScreenUtils;
import com.lib_photo.Picker.BaseActivity;
import com.lib_photo.Picker.PhotoGalleryAdapter;
import com.lib_photo.Picker.PhotoPickAdapter;
import com.lib_photo.Picker.bean.Photo;
import com.lib_photo.Picker.bean.PhotoDirectory;
import com.lib_photo.Picker.bean.PhotoPickBean;
import com.lib_photo.Picker.controller.PhotoPickConfig;
import com.lib_photo.Picker.controller.PhotoPreviewConfig;
import com.lib_photo.Picker.loader.MediaStoreHelper;
import com.lib_photo.Picker.utils.FileUtils;
import com.lib_photo.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Descriptions :照片选择器
 * GitHub : https://github.com/Rain0413
 * Blog   : http://blog.csdn.net/sinat_33680954
 * Created by Rain on 16-12-7.
 */
public class PhotoPickActivity extends BaseActivity {

    private String TAG = getClass().getSimpleName();
    //权限相关
    public static final int REQUEST_CODE_SDCARD = 100;             //读写权限请求码
    public static final int REQUEST_CODE_CAMERA = 200;             //拍照权限请求码

    public static final int REQUEST_CODE_SHOW_CAMERA = 0;// 拍照
    public static final int REQUEST_CODE_CLIP = 1;//裁剪头像

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private PhotoGalleryAdapter galleryAdapter;
    private PhotoPickAdapter adapter;
    private PhotoPickBean pickBean;
    private Uri cameraUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pick, true);

        Bundle bundle = getIntent().getBundleExtra(PhotoPickConfig.EXTRA_PICK_BUNDLE);
        if (bundle == null) {
            throw new NullPointerException("bundle is null,please init it");
        }
        pickBean = bundle.getParcelable(PhotoPickConfig.EXTRA_PICK_BEAN);
        if (pickBean == null) {
            finish();
            return;
        }

        //申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermission();
        else init();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        immersionBar
                .titleBar(toolbar, false)
                .transparentBar()
                .addViewSupportTransformColor(toolbar, R.color.image_color_black)
                .navigationBarColor(R.color.image_color_black)
                .barAlpha(0.7f)
                .init();
    }

    /**
     * 初始化控件
     */
    private void init() {
        initToolbar(); //初始化标题栏
        //全部相册照片列表
        RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, pickBean.getSpanCount()));
        recyclerView.addItemDecoration(new GridItemDivider(5, Color.WHITE));
        adapter = new PhotoPickAdapter(this, pickBean);
        recyclerView.setAdapter(adapter);
        //相册列表d
        RecyclerView gallery_rv = (RecyclerView) this.findViewById(R.id.gallery_rcl);
        gallery_rv.setLayoutManager(new LinearLayoutManager(this));
        galleryAdapter = new PhotoGalleryAdapter(this);
        gallery_rv.setAdapter(galleryAdapter);

        //当选择照片的时候更新toolbar的标题
        adapter.setOnUpdateListener(new PhotoPickAdapter.OnUpdateListener() {
            @Override
            public void updateToolBarTitle(String title) {
                headerLayout.showTitle(title);
            }

            @Override
            public void onUpdateItemListener(View itemView, int itemPos) {

                //第一行的item添加顶部间隔
                if (itemPos < pickBean.getSpanCount() && itemView.getPaddingTop() == 0) {
                    Log.d(TAG, "onUpdateItemListener: 添加顶部间隔=" + itemPos);
                    itemView.setPadding(0, ScreenUtils.getStatusHeight(PhotoPickActivity.this) + headerLayout.getHeight(), 0, 0);
                } else if (itemPos >= pickBean.getSpanCount() && itemView.getPaddingTop() > 0) {
                    itemView.setPadding(0, 0, 0, 0);
                }
            }
        });

        //相册列表item选择的时候关闭slidingUpPanelLayout并更新照片adapter
        galleryAdapter.setOnItemClickListener(new PhotoGalleryAdapter.OnItemClickListener() {
            @Override
            public void onClick(List<Photo> photos) {
                if (adapter != null) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    adapter.refresh(photos);
                }
            }
        });

        //获取全部照片
        MediaStoreHelper.getPhotoDirs(this, new MediaStoreHelper.PhotosResultCallback() {
            @Override
            public void onResultCallback(final List<PhotoDirectory> directories) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //directories中第一个是全部照片
                        adapter.refresh(directories.get(0).getPhotos());
                        galleryAdapter.refresh(directories);
                    }
                });
            }
        });

        slidingUpPanelLayout = (SlidingUpPanelLayout) this.findViewById(R.id.slidingUpPanelLayout);
        slidingUpPanelLayout.setAnchorPoint(0.5f);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });
        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

    }

    //请求权限(先检查)
    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_SDCARD);
        } else {
            init();
        }
    }


    //权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_SDCARD) { //读写权限
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("温馨提示");
                builder.setMessage(getString(R.string.permission_tip_SD));
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { //跳转到设备界面
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        } else if (requestCode == REQUEST_CODE_CAMERA) { //拍照权限
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPicFromCamera();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("温馨提示");
                builder.setMessage(getString(R.string.permission_tip_video));
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        }
    }


    /**
     * 启动Camera拍照
     */
    public void selectPicFromCamera() {
        if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.cannot_take_pic, Toast.LENGTH_SHORT).show();
            return;
        }
        // 直接将拍到的照片存到手机默认的文件夹
       /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        cameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);*/

        //保存到自定义目录

        File imageFile = FileUtils.createImageFile(this, "/images");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Android7.0以上URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            cameraUri = FileProvider.getUriForFile(this, "com.rain.photopicker.provider", imageFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件,私有目录读写权限
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            cameraUri = Uri.fromFile(imageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, PhotoPickActivity.REQUEST_CODE_SHOW_CAMERA);
    }

    //初始化标题栏
    private void initToolbar() {
        //设置ToolBar
        headerLayout.showTitle("选择图片");
        if (!pickBean.isClipPhoto()) {
            headerLayout.showRightTextButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    if (adapter != null && !adapter.getSelectPhotos().isEmpty()) {
                        if (adapter.getSelectPhotos().size() != 1) {
                            intent.putStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, adapter.getSelectPhotos());
                        } else
                            intent.putExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, adapter.getSelectPhotos().get(0));
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_SHOW_CAMERA://相机
                findPhoto(adapter.getCameraUri());
                break;
            case UCrop.REQUEST_CROP:    //裁剪
                findClipPhoto(UCrop.getOutput(data));
                break;
            case UCrop.RESULT_ERROR:
                Throwable cropError = UCrop.getError(data);
                break;
            case PhotoPreviewConfig.REQUEST_CODE:
                ArrayList<String> photoLists = data.getStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST);
                if (photoLists == null) {
                    return;
                }
                ArrayList<String> selectedList = adapter.getSelectPhotos();//之前已经选了的图片
                List<String> deleteList = new ArrayList<>();//这是图片预览界面需要删除的图片
                //遍历之前选择的图片，如果后面选择的图片不包含，那么就是要删除的
                for (String s : selectedList) {
                    if (!photoLists.contains(s)) {
                        deleteList.add(s);
                    }
                }
                selectedList.removeAll(deleteList);//删除预览界面取消选择的图片
                deleteList.clear();
                //合并相同的数据,通过hashset去除重复的数据
                HashSet<String> set = new HashSet<>(photoLists);
                for (String s : selectedList) {
                    set.add(s);
                }
                selectedList.clear();
                selectedList.addAll(set);
                headerLayout.showTitle(adapter.getTitle());
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void findClipPhoto(Uri uri) {
        Intent intent = new Intent();
        intent.putExtra(PhotoPickConfig.EXTRA_CLIP_PHOTO, uri.toString());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void findPhoto(Uri imageUri) {

        // String filePath = UtilsHelper.getRealPathFromURI(imageUri, this);
        if (imageUri == null) {
            Toast.makeText(this, R.string.unable_find_pic, Toast.LENGTH_LONG).show();
        } else {
            if (pickBean.isClipPhoto()) {//拍完照之后，如果要启动裁剪，则去裁剪再把地址传回来
                adapter.startClipPic(FileUtils.getImagePath());
            } else {
                Intent intent = new Intent();
                intent.putExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, FileUtils.getImagePath());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(0, R.anim.image_pager_exit_animation);
    }

}
