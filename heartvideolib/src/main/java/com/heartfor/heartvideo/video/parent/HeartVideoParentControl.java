package com.heartfor.heartvideo.video.parent;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoInfo;
import com.heartfor.heartvideo.video.PlayerStatus;

import static com.heartfor.heartvideo.video.HeartVideoUtil.getTimeProgressFormat;

/**
 * Created by admin on 2018/4/30.
 */

public abstract class HeartVideoParentControl extends FrameLayout implements View.OnTouchListener{
    private float mDownX;
    private float mDownY;
    private boolean mNeedChangePosition = false;
    private boolean mNeedChangeVolume = false;
    private boolean mNeedChangeBrightness = false;

    private long mGestureDownPosition;
    private float mGestureDownBrightness;
    private int mGestureDownVolume;
    private long mNewPosition;
    public HeartVideoParentControl(Context context) {
        this(context,null,0);
    }

    public HeartVideoParentControl(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HeartVideoParentControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnTouchListener(this);
        init(context);
    }
    /**
     * 初始化
     * */
    public abstract void init(Context context);
    /**
     * 设置播放器
     * */
    public abstract void setVideoPlayer(HeartVideo player);
    /**
     * 获取播放器
     * */
    public abstract HeartVideo getVideoPlayer();
    /**
     * 设置基础信息
     * */
    public abstract void setInfo(HeartVideoInfo info);
    /**
     * 获取基础信息
     * */
    public abstract HeartVideoInfo getInfo();
    /**
     *设置标题
     * */
    public abstract void setTitle();
    /**
     *播放状态回调
     * */
    public abstract void onPlayStateChanged(int currStatus);
    /**
     * 播放模式状态改变回调--全屏and正常
     */
    public abstract void onPlayModeChanged(int mCurrentMode);
    /**
     * 进度dialog显示/隐藏
     * */
    public abstract void positionDialogShow(boolean isVisiable);
    /**
     * 进度数据回调
     * */
    public abstract void positionTouchData(String changeTime,int changeProgress);
    /**
     * 亮度dialog显示/隐藏
     * */
    public abstract void brightnessDialogShow(boolean isVisiable);
    /**
     * 亮度数据回调
     * */
    public abstract void brightnessTouchData(int changeProgress);
    /**
     * 声音dialog显示/隐藏
     * */
    public abstract void volumeDialogShow(boolean isVisiable);
    /**
     * 声音数据回调
     * */
    public abstract void volumeTouchData(int changeProgress);
    /**
     * title-layout/progress-layout--显示隐藏
     * */
    public abstract void invokeTopAndBottomLayout(boolean iaVisiable);
    /**
     * handler启动更新进度
     * */
    public abstract void handlerUpProgress();
    /**
     * handler取消更新进度
     * */
    public abstract void handlerCancleUpProgress();
    /**
     * handler启动几秒后隐藏title-progress layout
     * */
    public abstract void handlerUpTopAndBottom();
    /**
     * handler取消几秒后隐藏title-progress layout
     * */
    public abstract void handlerCancleUpTopAndBottom();
    /**
     * loading显示隐藏
     * */
    public abstract void LoadingDialogShow(boolean isVisiable);
    /**
     * 选择播放源
     * */
    public abstract void invokeLineShowTv();
    /**
     * 重置
     * */
    public abstract void reset();
    /**
     * 滑动监听
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //只有在全屏下可以滑动
        if (getVideoPlayer().getCurrModeStatus() != PlayerStatus.MODE_FULL_SCREEN) {
            return false;
        }
        //只有在播放、暂停、缓存下可以滑动
        if (getVideoPlayer().getCurrStatus()==PlayerStatus.STATE_ERROR||
                getVideoPlayer().getCurrStatus()==PlayerStatus.STATE_COMPLETED||
                getVideoPlayer().getCurrStatus()==PlayerStatus.STATE_IDLE||
                getVideoPlayer().getCurrStatus()==PlayerStatus.STATE_PREPARING||
                getVideoPlayer().getCurrStatus()==PlayerStatus.STATE_PREPARED){
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mNeedChangePosition = false;
                mNeedChangeVolume = false;
                mNeedChangeBrightness = false;
                positionDialogShow(false);
                brightnessDialogShow(false);
                volumeDialogShow(false);
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mDownX;
                float deltaY = y - mDownY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                //防止在滑动的时候出现同时改变亮度-进度-音量的情况
                if (!mNeedChangePosition && !mNeedChangeVolume && !mNeedChangeBrightness) {
                    if (absDeltaX >= 80) {
                        //改变进度
                        mNeedChangePosition = true;
                        mGestureDownPosition = getVideoPlayer().getCurrentPosition();
                        handlerCancleUpProgress();
                        handlerCancleUpTopAndBottom();
                        invokeTopAndBottomLayout(true);
                        positionDialogShow(true);
                    } else if (absDeltaY >= 80) {
                        if (mDownX < getWidth() * 0.5f) {
                            // 左侧改变亮度
                            mNeedChangeBrightness = true;
                            mGestureDownBrightness = ((Activity) getContext()).getWindow().getAttributes().screenBrightness;
                            if (mGestureDownBrightness == -1) {
                                mGestureDownBrightness = 0.5f;
                            }
                            brightnessDialogShow(true);
                        } else {
                            // 右侧改变声音
                            mNeedChangeVolume = true;
                            mGestureDownVolume = getVideoPlayer().getVolume();
                            volumeDialogShow(true);
                        }
                    }
                }
                //改变进度
                if (mNeedChangePosition) {
                    long duration = getVideoPlayer().getDuration();
                    long toPosition = (long) (mGestureDownPosition + duration * deltaX / getWidth());
                    mNewPosition = Math.max(0, Math.min(duration, toPosition));
                    String str_position = getTimeProgressFormat((int) mNewPosition, (int) duration);
                    int newPositionProgress = (int) (100f * mNewPosition / duration);
                    positionTouchData(str_position,newPositionProgress);
                }
                //改变亮度
                if (mNeedChangeBrightness) {
                    deltaY = -deltaY;
                    float deltaBrightness = deltaY * 3 / getHeight();
                    float newBrightness = mGestureDownBrightness + deltaBrightness;
                    newBrightness = Math.max(0, Math.min(newBrightness, 1));
                    float newBrightnessPercentage = newBrightness;
                    WindowManager.LayoutParams params = ((Activity) getContext()).getWindow().getAttributes();
                    params.screenBrightness = newBrightnessPercentage;
                    ((Activity) getContext()).getWindow().setAttributes(params);
                    int newBrightnessProgress = (int) (100f * newBrightnessPercentage);
                    brightnessTouchData(newBrightnessProgress);
                }
                //改变音量
                if (mNeedChangeVolume) {
                    deltaY = -deltaY;
                    int maxVolume = getVideoPlayer().getMaxVolume();
                    int deltaVolume = (int) (maxVolume * deltaY * 3 / getHeight());
                    int newVolume = mGestureDownVolume + deltaVolume;
                    newVolume = Math.max(0, Math.min(maxVolume, newVolume));
                    getVideoPlayer().setVolume(newVolume);
                    int newVolumeProgress = (int) (100f * newVolume / maxVolume);
                    volumeTouchData(newVolumeProgress);
                }

                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mNeedChangePosition) {
                    getVideoPlayer().seekTo((int) mNewPosition);
                    handlerUpProgress();
                    handlerUpTopAndBottom();
                    positionDialogShow(false);
                    return true;
                }
                if (mNeedChangeBrightness) {
                    brightnessDialogShow(false);

                    return true;
                }
                if (mNeedChangeVolume) {
                    volumeDialogShow(false);
                    return true;
                }
                break;
        }
        return false;
    }

}
