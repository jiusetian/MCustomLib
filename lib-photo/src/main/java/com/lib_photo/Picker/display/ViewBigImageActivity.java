package com.lib_photo.Picker.display;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.lib_photo.R;
import com.lib_photo.Picker.bean.PhotosGridInfo;
import com.lib_photo.Picker.utils.ImageUtils;
import com.lib_photo.Picker.utils.ToastUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * 用于查看大图
 *
 * @author jingbin
 */
public class ViewBigImageActivity extends FragmentActivity implements OnPageChangeListener, OnPhotoTapListener {

    private String TAG = "tag";
    public static final String BIGIMAGE_INFO = "big_image_info";
    // 保存图片
    private TextView tv_save_big_image;
    // 接收传过来的uri地址
    List<String> imageuri;
    /**
     * 本应用图片的id
     */
    private List<Integer> resIds;
    // 接收穿过来当前选择的图片是第几张
    int seq;
    //图片资源的数量
    int imgNum;

    //照片位置大小相关信息
    private int intentTop;
    private int intentLeft;
    private int intentWidth;
    private int intentHeight;

    //缩放动画相关参数
    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;

    // 用于管理图片的滑动
    ViewPager very_image_viewpager;
    // 当前页数
    private int page;
    private ImageView photoImageView; //显示照片的img
    private boolean isEnter; //是否进入动画

    /**
     * 显示当前图片的页数
     */
    TextView very_image_viewpager_text;

    ViewPagerAdapter adapter;
    //背景布局
    RelativeLayout actBg;
    private ColorDrawable colorDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_big_image);

        getView();
    }

    /**
     * Glide 获得图片缓存路径
     */
    private String getImagePath(String imgUrl) {
        String path = null;
        FutureTarget<File> future = Glide.with(ViewBigImageActivity.this)
                .load(imgUrl)
                .downloadOnly(500, 500);
        try {
            File cacheFile = future.get();
            path = cacheFile.getAbsolutePath();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return path;
    }


    /*
     * 接收控件
     */
    private void getView() {
        /************************* 接收控件 ***********************/
        very_image_viewpager_text = (TextView) findViewById(R.id.very_image_viewpager_text);
        tv_save_big_image = (TextView) findViewById(R.id.tv_save_big_image);
        very_image_viewpager = (ViewPager) findViewById(R.id.very_image_viewpager);
        actBg = findViewById(R.id.act_bg);
        colorDrawable = new ColorDrawable(Color.BLACK);

        //布局背景设置
        actBg.setBackgroundDrawable(colorDrawable);

        tv_save_big_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ToastUtil.showToast(ViewBigImageActivity.this, "开始下载图片");
                if (resIds != null) { // 本地图片
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resIds.get(page));
                    if (bitmap != null) {
                        ImageUtils.saveImageToGallery(ImageUtils.getImagePath(ViewBigImageActivity.this, "/image/"), bitmap, ViewBigImageActivity.this);
                        ToastUtil.showToast(ViewBigImageActivity.this, "保存成功");
                    }

                } else { // 网络图片
                    final BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 子线程获得图片路径
                            final String imagePath = getImagePath(imageuri.get(page));
                            // 主线程更新
                            ViewBigImageActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (imagePath != null) {
                                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
                                        if (bitmap != null) {
                                            String filePath = ImageUtils.getImagePath(ViewBigImageActivity.this, "/image/") + ImageUtils.createFile();
                                            Log.e("Rain", "run: " + filePath);
                                            boolean state = ImageUtils.saveImageToGallery(filePath, bitmap, ViewBigImageActivity.this);
                                            String tips = state ? ("保存成功！") : "保存失败！";
                                            ToastUtil.showToast(ViewBigImageActivity.this, tips);
                                        }
                                    }
                                }
                            });
                        }
                    }).start();
                }
            }
        });

        /************************* 接收传值 ***********************/
        PhotosGridInfo imageBean = (PhotosGridInfo) getIntent().getSerializableExtra(BIGIMAGE_INFO);
        seq = imageBean.getSeq(); //当前是选择了第几张图片
        resIds = imageBean.getResIds(); //应用内图片资源
        imageuri = imageBean.getImgUris(); //应用外图片资源
        page = seq; //初始化当前页数
        imgNum = resIds != null ? resIds.size() : imageuri != null ? imageuri.size() : 0; //图片资源的数量
        intentLeft = imageBean.getLeft();
        intentTop = imageBean.getTop();
        intentHeight = imageBean.getHeight();
        intentWidth = imageBean.getWidth();

        /**
         * 给viewpager设置适配器
         */
        if (resIds != null && resIds.size() > 1) { //应用内的图片资源
            MyPageAdapter myPageAdapter = new MyPageAdapter();
            very_image_viewpager.setAdapter(myPageAdapter);
        } else if (imageuri != null) { //非应用内
            adapter = new ViewPagerAdapter();
            very_image_viewpager.setAdapter(adapter);
        }
        very_image_viewpager.setCurrentItem(seq);
        page = seq;
        very_image_viewpager.setOnPageChangeListener(this);
        very_image_viewpager.setEnabled(false);
        // 设定当前的页数和总页数
        if (imgNum > 1) {
            very_image_viewpager_text.setText((seq + 1) + " / " + imgNum);
        }
        very_image_viewpager.setCurrentItem(seq);
    }

    @Override
    public void onPhotoTap(ImageView view, float x, float y) {
        finish();
//        exitAnimation(view, new Runnable() {
//            @Override
//            public void run() {
//                finish();
//                //取消activity动画
//                overridePendingTransition(0, 0);
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        exitAnimation(photoImageView, new Runnable() {
//            @Override
//            public void run() {
//                finish();
//                //取消activity动画
//                overridePendingTransition(0, 0);
//            }
//        });
    }

    /**
     * 本应用图片适配器,主要是来自资源文件的图片
     */

    class MyPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (resIds == null || resIds.size() == 0)
                return 0;
            return resIds.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(R.layout.viewpager_very_image, container, false);
            //view.findViewById(R.id.pager_bg).setBackgroundDrawable(colorDrawable); //viewpager的背景
            final PhotoView zoom_image_view = (PhotoView) view.findViewById(R.id.zoom_image_view);
            photoImageView = zoom_image_view;
            ProgressBar spinner = (ProgressBar) view.findViewById(R.id.loading);
            spinner.setVisibility(View.GONE);
            if (resIds != null) {
                zoom_image_view.setImageResource(resIds.get(position));
            }
            zoom_image_view.setOnPhotoTapListener(ViewBigImageActivity.this);
            Log.d(TAG, "instantiateItem: 进行了初始化");
            //照片动画设置

//            if (position == seq && !isEnter) {
//                isEnter = true; //已经执行过进入动画
//                ViewTreeObserver observer = zoom_image_view.getViewTreeObserver();
//                observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                    @Override
//                    public boolean onPreDraw() {
//                        zoom_image_view.getViewTreeObserver().removeOnPreDrawListener(this);
//                        //坐标的获取设置
//                        int[] screenLocation = new int[2];
//                        zoom_image_view.getLocationOnScreen(screenLocation);
//                        mLeftDelta = intentLeft - screenLocation[0];
//                        mTopDelta = intentTop - screenLocation[1];
//
//                        mWidthScale = (float) intentWidth / zoom_image_view.getWidth();
//                        mHeightScale = (float) intentHeight / zoom_image_view.getHeight();
//
//                        //开启缩放动画
//                        enterAnimation(zoom_image_view);
//
//                        return true;
//                    }
//                });
//            }

            container.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    //进入动画
    public void enterAnimation(ImageView imageView) {
        //设置imageview动画的初始值
        imageView.setPivotX(0);
        imageView.setPivotY(0);
        imageView.setScaleX(mWidthScale);
        imageView.setScaleY(mHeightScale);
        imageView.setTranslationX(mLeftDelta);
        imageView.setTranslationY(mTopDelta);
        //设置动画
        TimeInterpolator sDecelerator = new LinearInterpolator();
        //设置imageview缩放动画，以及缩放开始位置
        imageView.animate().setDuration(5000).scaleX(1).scaleY(1).
                translationX(0).translationY(0).setInterpolator(sDecelerator);

        // 设置activity主布局背景颜色DURATION毫秒内透明度从透明到不透明
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0, 1);
        bgAnim.setDuration(5000);
        bgAnim.setTarget(actBg);
        bgAnim.start();
    }

    //退出动画
    public void exitAnimation(ImageView imageView, final Runnable endAction) {

        TimeInterpolator sInterpolator = new AccelerateInterpolator();
        //设置imageview缩放动画，以及缩放结束位置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.animate().setDuration(500).scaleX(mWidthScale).scaleY(mHeightScale).
                    translationX(mLeftDelta).translationY(mTopDelta)
                    .setInterpolator(sInterpolator).withEndAction(endAction);
        }

        // 设置activity主布局背景颜色DURATION毫秒内透明度从不透明到透明
        ObjectAnimator bgAnim = ObjectAnimator.ofFloat(actBg, "alpha", 1,0);
        bgAnim.setDuration(500);
        bgAnim.start();
    }

    /**
     * ViewPager的适配器
     *
     * @author guolin
     */
    class ViewPagerAdapter extends PagerAdapter {

        LayoutInflater inflater;

        ViewPagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.viewpager_very_image, container, false);
            final PhotoView zoom_image_view = (PhotoView) view.findViewById(R.id.zoom_image_view);
            photoImageView = zoom_image_view;
            final ProgressBar spinner = (ProgressBar) view.findViewById(R.id.loading);
            // 保存网络图片的路径
            String adapter_image_Entity = (String) getItem(position);
            String imageUrl;
            if (adapter_image_Entity.startsWith("file://")) { //如果手机本地图片
                //imageUrl = "file://" + adapter_image_Entity;
                tv_save_big_image.setVisibility(View.GONE);
            }
            imageUrl = adapter_image_Entity;

            spinner.setVisibility(View.VISIBLE);
            spinner.setClickable(false);
            //利用glide加载
            Glide.with(ViewBigImageActivity.this)
                    .load(imageUrl)
                    .crossFade(700) //动画
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Toast.makeText(getApplicationContext(), "资源加载异常", Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.GONE);
                            return false;
                        }

                        //这个用于监听图片是否加载完成
                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            spinner.setVisibility(View.GONE);

                            /**这里应该是加载成功后图片的高*/
                            int height = zoom_image_view.getHeight();

                            int wHeight = getWindowManager().getDefaultDisplay().getHeight();
                            if (height > wHeight) {
                                zoom_image_view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            } else {
                                zoom_image_view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            }
                            return false;
                        }
                    }).into(zoom_image_view);

            zoom_image_view.setOnPhotoTapListener(ViewBigImageActivity.this);
            container.addView(view, 0);
            return view;
        }

        @Override
        public int getCount() {
            if (imageuri == null || imageuri.size() == 0) {
                return 0;
            }
            return imageuri.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        Object getItem(int position) {
            return imageuri.get(position);
        }
    }

    /**
     * 下面是对Viewpager的监听
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    /**
     * 本方法主要监听viewpager滑动的时候的操作
     */
    @Override
    public void onPageSelected(int arg0) {
        Log.d(TAG, "onPageSelected: 滑动的位置=" + arg0);
        // 每当页数发生改变时重新设定一遍当前的页数和总页数
        very_image_viewpager_text.setText((arg0 + 1) + " / " + imgNum);
        page = arg0;
    }


}
