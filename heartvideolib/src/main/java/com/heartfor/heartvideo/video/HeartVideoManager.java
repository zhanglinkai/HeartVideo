package com.heartfor.heartvideo.video;

import android.content.Context;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Administrator on 2018/5/2.
 */

public class HeartVideoManager {
    private static HeartVideoManager manager;
    private HeartVideo video;
    public static HeartVideoManager getInstance(){
        if (null==manager){
            manager=new HeartVideoManager();
        }
        return manager;
    }
    public void setCurrPlayVideo(HeartVideo video){
        this.video=video;
    }
    public HeartVideo getCurrPlayVideo(){
        return video;
    }
    public void release(){
        video.releasePlayer();
        video=null;
    }
    public boolean onBackPressd(){
        return video.onBackPressd();
    }
}
