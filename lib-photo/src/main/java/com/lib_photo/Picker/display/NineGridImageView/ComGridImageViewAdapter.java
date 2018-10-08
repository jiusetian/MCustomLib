package com.lib_photo.Picker.display.NineGridImageView;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lib_photo.R;
import com.lib_photo.Picker.bean.PhotoInfo;
import com.lib_photo.Picker.bean.PhotosGridInfo;
import com.lib_photo.Picker.display.ViewBigImageActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 普通的九宫格适配
 */
public class ComGridImageViewAdapter extends NineGridImageViewAdapter<PhotoInfo> {

    @Override
    protected void onDisplayImage(Context context, ImageView imageView, PhotoInfo photosInfo) {
        if (photosInfo.getResId() != 0) {
            //显示图片
            imageView.setImageResource(photosInfo.getResId());
        } else if (photosInfo.getUri() != null) {
            Glide.with(context)
                    .load(photosInfo.getUri())
                    .crossFade(700) //动画
                    .error(R.mipmap.error_image)
                    .into(imageView);
        }
    }

    @Override
    protected void onItemImageClick(Context context, ImageView imageView, int index, List<PhotoInfo> list) {
        super.onItemImageClick(context, imageView, index, list);
        Intent intent = new Intent();
        //img的起始位置
        int[] screenLocation = new int[2];
        imageView.getLocationOnScreen(screenLocation);

        PhotosGridInfo photosGridInfo = new PhotosGridInfo();
        if (list.get(index).getResId() != 0) {
            List<Integer> resIds = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                resIds.add(list.get(i).getResId());
            }
            photosGridInfo.setResIds(resIds);
        } else if (list.get(index).getUri() != null) {
            List<String> uris = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                uris.add(list.get(i).getUri());
            }
            photosGridInfo.setImgUris(uris);
        }
        photosGridInfo.setSeq(index);
        photosGridInfo.setLeft(screenLocation[0]);
        photosGridInfo.setTop(screenLocation[1]);
        photosGridInfo.setWidth(imageView.getWidth());
        photosGridInfo.setHeight(imageView.getHeight());
        intent.putExtra(ViewBigImageActivity.BIGIMAGE_INFO, photosGridInfo);
        intent.setClass(context, ViewBigImageActivity.class);
        context.startActivity(intent);
    }
}
