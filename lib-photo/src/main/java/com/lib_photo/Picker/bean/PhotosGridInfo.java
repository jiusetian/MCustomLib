package com.lib_photo.Picker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LXR ON 2018/9/7.
 *
 */
public class PhotosGridInfo implements Serializable {

    // 接收传过来的uri地址
    List<String> imgUris;
    // 接收穿过来当前选择的图片是第几张
    int seq;
    //应用内资源id
    List<Integer> resIds;

    //相片的起初位置
    int left;
    int top;
    int width;
    int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }


    public List<String> getImgUris() {
        return imgUris;
    }

    public void setImgUris(List<String> imgUris) {
        this.imgUris = imgUris;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public List<Integer> getResIds() {
        return resIds;
    }

    public void setResIds(List<Integer> resIds) {
        this.resIds = resIds;
    }
}
