package com.lib_photo.Preview.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;

public class GlideImageLoader implements com.lib_photo.Preview.loader.ImageLoader {

    private Context context;

    private GlideImageLoader(Context context) {
        this.context = context;
    }

    public static GlideImageLoader with(Context context) {
        return new GlideImageLoader(context);
    }

    @Override
    public void showImage(String imageUrl, ImageView imageView,
                          Drawable placeholder, final SourceCallback sourceCallback) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(placeholder)
                .into(new GlideDrawableImageViewTarget(imageView) {
                    /**
                     * 开始下载
                     */
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        if (sourceCallback != null)
                            sourceCallback.onStart();
                    }

                    /**
                     * 下载失败
                     */
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (sourceCallback != null)
                            sourceCallback.onDelivered(STATUS_DISPLAY_FAILED);
                    }

                    /**
                     * 下载成功
                     */
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        if (sourceCallback != null) {
                            sourceCallback.onFinish();
                            sourceCallback.onDelivered(STATUS_DISPLAY_SUCCESS);
                        }
                    }
                });
    }

    @Override
    public void loadImageAsync(String imageUrl, final ThumbnailCallback callback) {
        Glide.with(context)
                .load(imageUrl)
                .listener(new RequestListener<String, GlideDrawable>() {

                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                               boolean isFirstResource) {
                        callback.onFinish(null);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        callback.onFinish(resource);
                        return true;
                    }
                });
    }

    @Override
    public Drawable loadImageSync(String imageUrl) {
        try {
            Bitmap bitmap=Glide.with(context)
                    .load(imageUrl)
                    .asBitmap()
                    .into(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                    .get();
            return new BitmapDrawable(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isLoaded(String url) {
        return false;
    }

    @Override
    public void clearCache() {

    }
}
