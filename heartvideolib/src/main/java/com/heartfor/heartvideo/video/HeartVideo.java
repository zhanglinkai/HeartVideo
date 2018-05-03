package com.heartfor.heartvideo.video;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.heartfor.heartvideo.video.parent.HeartTextureView;
import com.heartfor.heartvideo.video.parent.HeartVideoParentControl;

/**
 * Created by admin on 2018/4/30.
 *
 */

public class HeartVideo extends FrameLayout implements TextureView.SurfaceTextureListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener{
    private Context context;
    private int currStatus = PlayerStatus.STATE_IDLE;
    private int mCurrentMode = PlayerStatus.MODE_NORMAL;
    private HeartVideoParentControl mControl;
    private FrameLayout mContainer;
    private HeartTextureView mTextureView;
    private MediaPlayer mMediaPlayer;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private AudioManager mAudioManager;
    private int mBufferPercentage;
    private FrameLayout textureViewLayout;
    public HeartVideo(Context context) {
        this(context,null,0);
    }

    public HeartVideo(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HeartVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initVideo();
    }

    private void initVideo() {
        mContainer = new FrameLayout(context);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
        textureViewLayout = new FrameLayout(context);
        textureViewLayout.setBackgroundColor(Color.BLACK);
        mContainer.addView(textureViewLayout, params);

    }

    public void setHeartVideoContent(HeartVideoParentControl control){
        mContainer.removeView(mControl);
        mControl=control;
        mControl.reset();
        control.setVideoPlayer(this);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mControl, params);
    }

    public HeartVideoParentControl getHeartVideoControl(){
        return mControl;
    }

    public void start() {
        if (currStatus == PlayerStatus.STATE_IDLE) {
            if (HeartVideoManager.getInstance().getCurrPlayVideo()!=null) {
                HeartVideoManager.getInstance().getCurrPlayVideo().pause();
                HeartVideoManager.getInstance().getCurrPlayVideo().releasePlayer();
            }
            HeartVideoManager.getInstance().setCurrPlayVideo(this);
            initAudioManager();
            initMediaPlayer();
            initTextureView();
            addTextureView();
        }
    }
    public void restart() {
        if (currStatus == PlayerStatus.STATE_PAUSED) {
            mMediaPlayer.start();
            currStatus = PlayerStatus.STATE_PLAYING;
            mControl.onPlayStateChanged(currStatus);
        } else if (currStatus == PlayerStatus.STATE_BUFFERING_PAUSED) {
            mMediaPlayer.start();
            currStatus = PlayerStatus.STATE_BUFFERING_PLAYING;
            mControl.onPlayStateChanged(currStatus);
        } else if (currStatus == PlayerStatus.STATE_COMPLETED) {
            mMediaPlayer.reset();
            openMediaPlayer(mSurfaceTexture);
            currStatus=PlayerStatus.STATE_RESTART;
            mControl.onPlayStateChanged(currStatus);
        }else if(currStatus == PlayerStatus.STATE_ERROR){
            mMediaPlayer.reset();
            openMediaPlayer(mSurfaceTexture);
            currStatus=PlayerStatus.STATE_RESTART;
            mControl.onPlayStateChanged(currStatus);
        }else {
            mMediaPlayer.reset();
            openMediaPlayer(mSurfaceTexture);
            currStatus=PlayerStatus.STATE_CHANGE_LINE;
            mControl.onPlayStateChanged(currStatus);
        }
    }

    public void pause() {
        if (currStatus == PlayerStatus.STATE_PLAYING ||
                currStatus == PlayerStatus.STATE_PREPARED ||
                currStatus == PlayerStatus.STATE_COMPLETED) {
            mMediaPlayer.pause();
            currStatus = PlayerStatus.STATE_PAUSED;
            mControl.onPlayStateChanged(currStatus);
        }
        if (currStatus == PlayerStatus.STATE_BUFFERING_PLAYING) {
            mMediaPlayer.pause();
            currStatus = PlayerStatus.STATE_BUFFERING_PAUSED;
            mControl.onPlayStateChanged(currStatus);
        }
    }

    public void releasePlayer() {
        if (mControl.getInfo().isSaveProgress()) {
            //存储进度
            if (null != mControl.getInfo().getPathMap()) {
                for (String key : mControl.getInfo().getPathMap().keySet()) {
                    savePlayProgress(mControl.getInfo().getPathMap().get(key), getCurrentPosition());
                }
            } else {
                savePlayProgress(mControl.getInfo().getPath(), getCurrentPosition());
            }
        }
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(null);
            mAudioManager = null;
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
        textureViewLayout.removeView(mTextureView);
        if (null != mSurface) {
            mSurface.release();
            mSurface = null;
        }
        if (null != mSurfaceTexture) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        currStatus = PlayerStatus.STATE_IDLE;
        mCurrentMode=PlayerStatus.MODE_NORMAL;

        if (mControl!=null){
            mControl.reset();
        }
        Runtime.getRuntime().gc();
    }

    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new HeartTextureView(context);
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    private void addTextureView() {
        textureViewLayout.removeView(mTextureView);
        LayoutParams textureView_lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        textureViewLayout.addView(mTextureView, textureView_lp);
    }

    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnSeekCompleteListener(seekCompleteListener);
        }
    }

    private void initAudioManager() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    private void openMediaPlayer(SurfaceTexture surface) {
        try {
            mMediaPlayer.setDataSource(mControl.getInfo().getPath());
            mSurface = new Surface(surface);
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.prepareAsync();
            currStatus = PlayerStatus.STATE_PREPARING;
            mControl.onPlayStateChanged(currStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean exitFullScreen() {
        if (mCurrentMode == PlayerStatus.MODE_FULL_SCREEN) {
            //设置退出全屏
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
            ((Activity)getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
            ViewGroup contentView = (ViewGroup) ((Activity)getContext()).findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            // 将Container添加至NiceMediaPlayer这个FrameLayout
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);

            mCurrentMode = PlayerStatus.MODE_NORMAL;
            mControl.onPlayModeChanged(mCurrentMode);
            return true;
        }
        return false;
    }

    public boolean enterFullScreen() {
        if (mCurrentMode == PlayerStatus.MODE_FULL_SCREEN) {
            return false;
        } else {
            //设置进入全屏
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
            ((Activity)getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
            this.removeView(mContainer);
            // 将Container添加至contentView
            ViewGroup contentView = (ViewGroup) ((Activity)getContext()).findViewById(android.R.id.content);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            contentView.addView(mContainer, params);
            mCurrentMode = PlayerStatus.MODE_FULL_SCREEN;
            mControl.onPlayModeChanged(mCurrentMode);
            return true;
        }
    }

    public int getCurrModeStatus() {
        return mCurrentMode;
    }

    public int getCurrStatus() {
        return currStatus;
    }

    public int getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }

    public int getCurrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    public void seekTo(int var1) {
        if (mMediaPlayer != null) {
            mControl.LoadingDialogShow(true);
            mMediaPlayer.seekTo(var1);
        }
    }

    public int getMaxVolume() {
        if (mAudioManager != null) {
            return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    public void setVolume(int volume) {
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        }
    }

    public int getVolume() {
        if (mAudioManager != null) {
            return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }
    public void savePlayProgress(String key, long result) {
        getContext().getSharedPreferences("progress", Context.MODE_PRIVATE).edit().putLong(key, result).apply();
    }
    public long getPlayProgress(String key) {
        return getContext().getSharedPreferences("progress", Context.MODE_PRIVATE).getLong(key, 0);
    }

    public boolean onBackPressd() {
        if (mMediaPlayer != null) {
            if (getCurrModeStatus()==PlayerStatus.MODE_FULL_SCREEN) {
                return exitFullScreen();
            }
        }
        return false;
    }

    public int getBufferPercentage() {
        return mBufferPercentage;
    }

    private MediaPlayer.OnSeekCompleteListener seekCompleteListener=new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            mControl.LoadingDialogShow(false);
        }
    };

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        mBufferPercentage = i;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        currStatus = PlayerStatus.STATE_COMPLETED;
        mControl.onPlayStateChanged(currStatus);
        // 清除屏幕常亮
        mContainer.setKeepScreenOn(false);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        if (currStatus!=PlayerStatus.STATE_CHANGE_LINE) {
            currStatus = PlayerStatus.STATE_ERROR;
            mControl.onPlayStateChanged(currStatus);
        }
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int i1) {
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            // 播放器开始渲染
            currStatus = PlayerStatus.STATE_PLAYING;
            mControl.onPlayStateChanged(currStatus);
            Log.d("ttt", "onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING");
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            //MEDIA_INFO_BUFFERING_START
            // MediaPlayer暂时不播放，以缓冲更多的数据
            if (currStatus == PlayerStatus.STATE_PAUSED || currStatus == PlayerStatus.STATE_BUFFERING_PAUSED) {
                currStatus = PlayerStatus.STATE_BUFFERING_PAUSED;
                Log.d("ttt", "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED");
            } else {
                currStatus = PlayerStatus.STATE_BUFFERING_PLAYING;
                Log.d("ttt", "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING");
            }
            mControl.onPlayStateChanged(currStatus);
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            //MediaPlayer在缓冲完后继续播放
            // 填充缓冲区后，MediaPlayer恢复播放/暂停
            if (currStatus == PlayerStatus.STATE_BUFFERING_PLAYING) {
                currStatus = PlayerStatus.STATE_PLAYING;
                mControl.onPlayStateChanged(currStatus);
                Log.d("ttt", "onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING");
            }
            if (currStatus == PlayerStatus.STATE_BUFFERING_PAUSED) {
                currStatus = PlayerStatus.STATE_PAUSED;
                mControl.onPlayStateChanged(currStatus);
                Log.d("ttt", "onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED");
            }
        } else if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
            //媒体不支持Seek
            Log.d("ttt", "视频不能seekTo，为直播视频");
        } else{
            Log.d("ttt", "onInfo ——> what：" + what);
        }
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (null != mediaPlayer) {
            currStatus = PlayerStatus.STATE_PREPARED;
            mControl.onPlayStateChanged(currStatus);
            mediaPlayer.start();
            if (mControl.getInfo().isSaveProgress()){
                seekTo((int) getPlayProgress(mControl.getInfo().getPath()));
            }
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surfaceTexture;
            openMediaPlayer(mSurfaceTexture);
        } else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
