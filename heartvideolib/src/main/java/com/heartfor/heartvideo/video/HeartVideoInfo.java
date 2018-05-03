package com.heartfor.heartvideo.video;

import android.text.TextUtils;

import java.util.LinkedHashMap;

/**
 * Created by admin on 2018/4/30.
 */

public class HeartVideoInfo {
    private String title;
    private String path;
    private LinkedHashMap<String,String> pathMap;
    private boolean isSaveProgress;
    private String imagePath;

    public HeartVideoInfo(Builder builder){
        this.title=builder.title;
        this.path=builder.path;
        this.pathMap=builder.pathMap;
        this.isSaveProgress=builder.isSaveProgress;
        this.imagePath=builder.imagePath;
        if (null!=pathMap){
            for (String key:pathMap.keySet()) {
                setPath(pathMap.get(key));
                return;
            }
        }if (!TextUtils.isEmpty(path)){
            setPath(path);
        }else{
            throw new NullPointerException("播放地址为空");
        }
    }
    public static Builder Builder(){
        return new Builder();
    }

    public String getTitle() {
        return title;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public LinkedHashMap<String, String> getPathMap() {
        return pathMap;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isSaveProgress() {
        return isSaveProgress;
    }

    public static class Builder{
        //标题
        private String title;
        //当前播放地址
        private String path;
        //当前播放集合
        private LinkedHashMap<String,String> pathMap;
        //是否保存进度，true--保存并从上次开始播放，false--从心开始播放
        private boolean isSaveProgress;
        //占位图
        private String imagePath;

        public Builder(){}
        public HeartVideoInfo builder(){
            return new HeartVideoInfo(this);
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setPath(LinkedHashMap<String, String> map) {
            this.pathMap = map;
            return this;
        }

        public Builder setSaveProgress(boolean saveProgress) {
            isSaveProgress = saveProgress;
            return this;
        }

        public Builder setImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }
    }
}
