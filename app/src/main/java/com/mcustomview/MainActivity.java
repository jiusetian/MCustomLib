package com.mcustomview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lib_photo.Picker.PhotoPick;
import com.lib_photo.Picker.controller.PhotoPickConfig;

public class MainActivity extends AppCompatActivity {
    String TAG="tag";
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PhotoPick.init(this,R.color.colorAccent); //初始化
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_single).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoPickConfig
                        .Builder(MainActivity.this)
                        //.imageLoader(PhotoPickConfig.GLIDE_LOADER)                //图片加载方式，支持任意第三方图片加载库
                        .spanCount(PhotoPickConfig.GRID_SPAN_COUNT)         //相册列表每列个数，默认为3
                        .pickMode(PhotoPickConfig.MODE_PICK_SINGLE)           //设置照片选择模式为单选，默认为单选
                        .maxPickSize(PhotoPickConfig.DEFAULT_CHOOSE_SIZE)   //多选时可以选择的图片数量，默认为1张
                        .showCamera(true)           //是否展示相机icon，默认展示
                        .clipPhoto(false)            //是否开启裁剪照片功能，默认关闭
                        .clipCircle(false)          //是否裁剪方式为圆形，默认为矩形
                        .build();

                //测试大图片查看
//                Intent intent = new Intent();
//                PhotosGridInfo photosGridInfo = new PhotosGridInfo();
//                List<Integer> resIds=new ArrayList<>();
//                resIds.add(R.mipmap.ic_launcher);
//                resIds.add(R.mipmap.error_image);
//                photosGridInfo.setResIds(resIds);
//                //photosGridInfo.setImgResid(R.mipmap.ic_launcher);
//                intent.putExtra(ViewBigImageActivity.BIGIMAGE_INFO, photosGridInfo);
//                intent.setClass(MainActivity.this, ViewBigImageActivity.class);
//                startActivity(intent);
            }
        });

        findViewById(R.id.btn_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoPickConfig
                        .Builder(MainActivity.this)
                        .imageLoader(PhotoPickConfig.GLIDE_LOADER)
                        .pickMode(PhotoPickConfig.MODE_PICK_MORE)
                        .maxPickSize(3)
                        .build();
            }
        });

        findViewById(R.id.btn_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,UniversalNormalActivity.class));
            }
        });
    }
}
