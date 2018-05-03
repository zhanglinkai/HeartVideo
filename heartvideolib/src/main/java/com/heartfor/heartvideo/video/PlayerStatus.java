package com.heartfor.heartvideo.video;

/**
 * Created by Administrator on 2018/4/23.
 */

public @interface PlayerStatus {
    /**
     * 播放错误
     * */
    int STATE_ERROR =-1;
    /**
     * 播放未开始
     * */
    int STATE_IDLE=0;
    /**
     * 播放准备中
     * */
    int STATE_PREPARING  =1;
    /**
     * 播放准备就绪
     * */
    int STATE_PREPARED =2;
    /**
     * 正在播放
     * */
    int STATE_PLAYING =3;
    /**
     * 暂停播放
     * */
    int STATE_PAUSED =4;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     * */
    int STATE_BUFFERING_PLAYING =5;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
     * */
    int STATE_BUFFERING_PAUSED =6;
    /**
     * 播放完成
     * */
    int STATE_COMPLETED =7;
    /**
     * 播放重置
     * */
    int STATE_RESTART=10;
    /**
     * 切换视频源
     * */
    int STATE_CHANGE_LINE=11;
    /**
     * 普通模式
     * */
    int MODE_NORMAL =8;
    /**
     * 全屏模式
     * */
    int MODE_FULL_SCREEN =9;

}
