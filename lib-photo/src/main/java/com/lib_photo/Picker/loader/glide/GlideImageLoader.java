package com.lib_photo.Picker.loader.glide;

import android.content.Context;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lib_photo.Picker.loader.ImageLoader;
import com.lib_photo.R;

/**
 * Describe :GlideImageLoader
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */

public class GlideImageLoader implements ImageLoader {

    private final static String TAG = "GlideImageLoader";
    private Context context;

    @Override
    public void displayImage(Context context, String path, ImageView imageView, boolean resize) {
        if (context == null) this.context = context;
        DrawableRequestBuilder builder = null;

        builder = Glide.with(context)
                .load(path);
        if (resize)
            builder = builder.centerCrop();
         builder.crossFade(300)
                .error(context.getResources().getDrawable(R.mipmap.error_image))
                .diskCacheStrategy(DiskCacheStrategy.NONE) //禁止磁盘缓存
                .skipMemoryCache(true) //禁止内存缓存
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Glide.get(context).clearMemory();
        }
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