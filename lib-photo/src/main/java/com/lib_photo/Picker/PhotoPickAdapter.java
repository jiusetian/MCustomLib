package com.lib_photo.Picker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.lib_photo.Picker.bean.Photo;
import com.lib_photo.Picker.bean.PhotoPickBean;
import com.lib_photo.Picker.controller.PhotoPickConfig;
import com.lib_photo.Picker.controller.PhotoPreviewConfig;
import com.lib_photo.Picker.loader.ImageLoader;
import com.lib_photo.Picker.ui.PhotoPickActivity;
import com.lib_photo.Picker.utils.FileUtils;
import com.lib_photo.Picker.utils.UCropUtils;
import com.lib_photo.Picker.weidget.CheckBox;
import com.lib_photo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Describe : 本地所有照片列表
 * Created by Rain on 17-4-28.
 */
public class PhotoPickAdapter extends RecyclerView.Adapter {
    private String TAG=getClass().getSimpleName();
    private Context context;
    private ArrayList<Photo> photos = new ArrayList<>();
    private ArrayList<String> selectPhotos = new ArrayList<>(); //被选择的图片
    private int maxPickSize;
    private int pickMode;
    private int imageSize;
    private boolean clipCircle;
    private boolean showCamera;
    private boolean isClipPhoto;
    private boolean isOriginalPicture;
    private ImageLoader imageLoader;
    private Uri cameraUri;

    public PhotoPickAdapter(Context context, PhotoPickBean pickBean) {
        this.context = context;
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        this.imageSize = metrics.widthPixels / pickBean.getSpanCount(); //照片大小
        this.pickMode = pickBean.getPickMode();
        this.maxPickSize = pickBean.getMaxPickSize();
        this.clipCircle = pickBean.getClipMode();
        this.showCamera = pickBean.isShowCamera();
        this.isClipPhoto = pickBean.isClipPhoto();
        this.isOriginalPicture = pickBean.isOriginalPicture();
        this.imageLoader = pickBean.getImageLoader();
    }

    public void refresh(List<Photo> photos) {

        this.photos.clear();
        this.photos.addAll(photos);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_pick, null);
        return new PhotoPickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((PhotoPickViewHolder) holder).showData(position);
    }

    @Override
    public int getItemCount() {
        return showCamera ? (photos == null ? 0 : photos.size() + 1) : (photos == null ? 0 : photos.size());
    }

    private Photo getItem(int position) {
        return showCamera ? photos.get(position - 1) : photos.get(position);
    }

    private class PhotoPickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private CheckBox checkbox;

        public PhotoPickViewHolder(View view) {
            super(view);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            //设置图片的宽高
            imageView.getLayoutParams().height = imageSize;
            imageView.getLayoutParams().width = imageSize;
            //点击事件
            checkbox.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void showData(int position) {
            Log.d(TAG, "showData: 位置="+position);
            if (onUpdateListener != null) {
                onUpdateListener.onUpdateItemListener(itemView, position);
            }
            if (showCamera && position == 0) {
                checkbox.setVisibility(View.GONE);
                imageView.setImageResource(R.mipmap.take_photo); //显示拍照图片
            } else {
                Photo photo = getItem(position);
                if (isClipPhoto) {
                    checkbox.setVisibility(View.GONE); //不显示cb
                } else {
                    checkbox.setVisibility(View.VISIBLE);
                    checkbox.setChecked(selectPhotos.contains(photo.getPath()), false); //没有动画
                }
                String url = photo.getPath();
                imageLoader.displayImage(context, url, imageView, true); //加载图片
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (view.getId() == R.id.checkbox) { //点击CheckBox
                if (selectPhotos.contains(getItem(position).getPath())) {
                    checkbox.setChecked(false);
                    selectPhotos.remove(getItem(position).getPath());
                } else {
                    if (selectPhotos.size() == maxPickSize) {
                        checkbox.setChecked(false);
                        return;
                    } else {
                        checkbox.setChecked(true);
                        selectPhotos.add(getItem(position).getPath());
                    }
                }
                if (onUpdateListener != null) {
                    onUpdateListener.updateToolBarTitle(getTitle());
                }
            } else if (view.getId() == R.id.photo_pick_rl) {
                if (showCamera && position == 0) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //申请权限
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, PhotoPickActivity.REQUEST_CODE_CAMERA);
                    } else {
                        selectPicFromCamera();
                    }
                } else if (isClipPhoto) {
                    //头像裁剪
                    startClipPic(getItem(position).getPath());
                } else {
                    //查看大图
                    new PhotoPreviewConfig.Builder((Activity) context)
                            .setPosition(showCamera ? position - 1 : position) //选中图片的下标
                            .setMaxPickSize(maxPickSize)
                            .setPhotos(photos)
                            .setSelectPhotos(selectPhotos)
                            .setOriginalPicture(isOriginalPicture)
                            .build();
                }
            }
        }
    }


    /**
     * 裁剪图片
     *
     * @param picPath
     */
    public void startClipPic(String picPath) {
        File corpFile = FileUtils.createImageFile(context, "/clip");
        UCropUtils.start((Activity) context, new File(picPath), corpFile, clipCircle);
    }

    /**
     * 启动Camera拍照
     */
    public void selectPicFromCamera() {
        if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, R.string.cannot_take_pic, Toast.LENGTH_SHORT).show();
            return;
        }
        // 直接将拍到的照片存到手机默认的文件夹
/*        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        cameraUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        ((Activity) context).startActivityForResult(intent, PhotoPickActivity.REQUEST_CODE_SHOW_CAMERA);*/

        //保存到自定义目录

        File imageFile = FileUtils.createImageFile(context, "/images");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Android7.0以上URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            cameraUri = FileProvider.getUriForFile(context, "com.rain.photopicker.provider", imageFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件,私有目录读写权限
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            cameraUri = Uri.fromFile(imageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        ((Activity) context).startActivityForResult(intent, PhotoPickActivity.REQUEST_CODE_SHOW_CAMERA);
    }

    public Uri getCameraUri() {
        return cameraUri;
    }

    //如果是多选title才会变化，要不然单选的没有变
    public String getTitle() {
        String title = context.getString(R.string.select_photo);
        if (pickMode == PhotoPickConfig.MODE_PICK_MORE && selectPhotos.size() >= 1) { //不是单选，更新title
            title = selectPhotos.size() + "/" + maxPickSize;
        }
        return title;
    }

    /**
     * 获取已经选择了的图片
     *
     * @return selected photos
     */
    public ArrayList<String> getSelectPhotos() {
        return selectPhotos;
    }

    private OnUpdateListener onUpdateListener;

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public interface OnUpdateListener {
        void updateToolBarTitle(String title);

        void onUpdateItemListener(View itemView, int itemPos);
    }
}
