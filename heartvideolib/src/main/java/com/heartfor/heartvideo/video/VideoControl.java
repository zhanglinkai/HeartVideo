package com.heartfor.heartvideo.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.heartfor.heartvideo.R;
import com.heartfor.heartvideo.video.dialog.BrightnessDialog;
import com.heartfor.heartvideo.video.dialog.LinePathDialog;
import com.heartfor.heartvideo.video.dialog.PositionDialog;
import com.heartfor.heartvideo.video.dialog.VolumeDialog;
import com.heartfor.heartvideo.video.parent.HeartVideoParentControl;

import static com.heartfor.heartvideo.video.HeartVideoUtil.getTimeProgressFormat;

/**
 * Created by admin on 2018/4/30.
 */

public class VideoControl extends HeartVideoParentControl implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{
    private Context context;
    private LinearLayout control_title_layout;
    private ImageView control_title_back;
    private TextView control_title_tv;
    private ImageView control_title_battery;
    private LinearLayout control_bottom_layout;
    private ImageView control_start_btn;
    private SeekBar control_seekbar;
    private TextView control_time;
    private TextView control_line;
    private ImageView control_full;
    private LinearLayout control_loading_layout;
    private ImageView control_loading_iv;
    private ImageView placeiv;
    private TextView control_loading_tv;
    private PositionDialog positionDialog;
    private BrightnessDialog brightnessDialog;
    private VolumeDialog volumeDialog;
    private LinePathDialog linePathDialog;
    private HeartVideo player;
    private HeartVideoInfo info;
    private Handler upHandler = new Handler();
    private RotateAnimation rotate;
    private ImageView control_center_play;
    public VideoControl(Context context) {
        this(context,null,0);
    }

    public VideoControl(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init(Context context) {
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.layout_control,this,true);
        control_title_layout=(LinearLayout)findViewById(R.id.control_title_layout);
        control_title_back=(ImageView)findViewById(R.id.control_title_back);
        control_title_tv=(TextView)findViewById(R.id.control_title_tv);
        control_title_battery=(ImageView)findViewById(R.id.control_title_battery);
        control_bottom_layout=(LinearLayout) findViewById(R.id.control_bottom_layout);
        control_start_btn=(ImageView)findViewById(R.id.control_start_btn);
        control_seekbar=(SeekBar)findViewById(R.id.control_seekbar);
        control_time=(TextView)findViewById(R.id.control_time);
        control_line=(TextView)findViewById(R.id.control_line);
        control_full=(ImageView)findViewById(R.id.control_full);
        control_loading_layout=(LinearLayout)findViewById(R.id.control_loading_layout);
        control_loading_iv=(ImageView)findViewById(R.id.control_loading_iv);
        LoadingDialogShow(false);
        placeiv=(ImageView)findViewById(R.id.placeiv);
        control_center_play=(ImageView)findViewById(R.id.control_center_play);
        control_loading_tv=(TextView)findViewById(R.id.control_loading_tv);
        positionDialog = new PositionDialog(getContext());
        brightnessDialog = new BrightnessDialog(getContext());
        volumeDialog = new VolumeDialog(getContext());
        rotate  = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000);//设置动画持续周期
        rotate.setRepeatCount(-1);//设置重复次数
        control_loading_iv.startAnimation(rotate);
        setClick();
    }

    private void setClick() {
        control_title_back.setOnClickListener(this);
        control_start_btn.setOnClickListener(this);
        control_line.setOnClickListener(this);
        control_full.setOnClickListener(this);
        control_seekbar.setOnSeekBarChangeListener(this);
        this.setOnClickListener(this);
        control_center_play.setOnClickListener(this);
    }

    @Override
    public void setVideoPlayer(HeartVideo player) {
        this.player=player;
        //初始化title
        setTitle();
        //初始化
        if (player.getCurrModeStatus()==PlayerStatus.MODE_FULL_SCREEN){
            control_title_battery.setVisibility(VISIBLE);//初始化电量的显示状态
            control_title_back.setVisibility(VISIBLE);//初始化title返回键显示状态
            invokeLineShowTv();//初始化播放源的显示状态
            control_bottom_layout.setVisibility(VISIBLE);//初始化进度layout的显示状态
        }else{
            control_title_battery.setVisibility(GONE);//初始化电量的显示状态
            control_title_back.setVisibility(GONE);//初始化title返回键显示状态
            control_line.setVisibility(GONE);//初始化播放源的显示状态
            control_bottom_layout.setVisibility(GONE);//初始化进度layout的显示状态
        }
    }

    @Override
    public HeartVideo getVideoPlayer() {
        return player;
    }

    @Override
    public void setInfo(HeartVideoInfo info) {
        this.info=info;
        linePathDialog = LinePathDialog.builder(getContext(), player, info);
        linePathDialog.setOnDismissListener(lineDialogDissClick);
        Glide.with(context).load(info.getImagePath()).into(placeiv);
    }

    @Override
    public HeartVideoInfo getInfo() {
        return info;
    }

    @Override
    public void setTitle() {
        control_title_tv.setText(info.getTitle());
    }

    @Override
    public void onPlayStateChanged(int currStatus) {
        switch (currStatus) {
            case PlayerStatus.STATE_IDLE:
                //空闲状态
                placeiv.setVisibility(VISIBLE);
                control_center_play.setVisibility(VISIBLE);
                LoadingDialogShow(false);
                break;
            case PlayerStatus.STATE_PREPARING:
                //准备中
                placeiv.setVisibility(GONE);
                control_center_play.setVisibility(GONE);
                LoadingDialogShow(true);
                control_loading_tv.setText("视频加载中...");
                break;
            case PlayerStatus.STATE_PREPARED:
                //准备完成
                placeiv.setVisibility(GONE);
                control_center_play.setVisibility(GONE);
                LoadingDialogShow(false);
                control_start_btn.setImageResource(R.mipmap.icon_video_play);
                int currentPosition = player.getCurrentPosition();
                int duration = player.getDuration();
                control_time.setText(getTimeProgressFormat(currentPosition, duration));
                upHandler.removeCallbacks(topAndbottomRunable);
                invokeTopAndBottomLayout(true);
                break;
            case PlayerStatus.STATE_PLAYING:
                //播放中
                placeiv.setVisibility(GONE);
                control_center_play.setVisibility(GONE);
                LoadingDialogShow(false);
                control_start_btn.setImageResource(R.mipmap.icon_video_pause);
                upHandler.post(upRunable);
                upHandler.post(topAndbottomRunable);
                break;
            case PlayerStatus.STATE_PAUSED:
                //暂停
                placeiv.setVisibility(GONE);
                control_center_play.setVisibility(VISIBLE);
                LoadingDialogShow(false);
                control_start_btn.setImageResource(R.mipmap.icon_video_play);
                upHandler.removeCallbacks(topAndbottomRunable);
                invokeTopAndBottomLayout(true);
                break;
            case PlayerStatus.STATE_BUFFERING_PLAYING:
                //播放状态下缓存
                placeiv.setVisibility(GONE);
                control_center_play.setVisibility(GONE);
                LoadingDialogShow(true);
                control_loading_tv.setText("视频加载中...");
                control_start_btn.setImageResource(R.mipmap.icon_video_pause);
                break;
            case PlayerStatus.STATE_BUFFERING_PAUSED:
                //暂停状态下缓存
                placeiv.setVisibility(GONE);
                control_center_play.setVisibility(VISIBLE);
                LoadingDialogShow(true);
                control_loading_tv.setText("视频加载中...");
                control_start_btn.setImageResource(R.mipmap.icon_video_play);
                upHandler.removeCallbacks(topAndbottomRunable);
                invokeTopAndBottomLayout(true);
                break;
            case PlayerStatus.STATE_ERROR:
                //错误
                placeiv.setVisibility(GONE);
                control_center_play.setVisibility(VISIBLE);
                LoadingDialogShow(true);
                control_loading_tv.setText("视频解析错误");
                upHandler.removeCallbacks(upRunable);
                upHandler.removeCallbacks(topAndbottomRunable);
                invokeTopAndBottomLayout(false);
                control_start_btn.setImageResource(R.mipmap.icon_video_play);
                break;
            case PlayerStatus.STATE_COMPLETED:
                //播放结束
                placeiv.setVisibility(GONE);
                control_center_play.setVisibility(VISIBLE);
                LoadingDialogShow(false);
                control_start_btn.setImageResource(R.mipmap.icon_video_play);
                upHandler.removeCallbacks(upRunable);
                upHandler.removeCallbacks(topAndbottomRunable);
                invokeTopAndBottomLayout(true);
                break;
            case PlayerStatus.STATE_RESTART:
                //播放重置

                break;
            case PlayerStatus.STATE_CHANGE_LINE:
                //切换视频源
                placeiv.setVisibility(GONE);
                Toast.makeText(getContext(), "正在切换", Toast.LENGTH_SHORT).show();
                upHandler.removeCallbacks(upRunable);
                upHandler.removeCallbacks(topAndbottomRunable);
                break;
        }
    }

    @Override
    public void onPlayModeChanged(int mCurrentMode) {
        if (mCurrentMode == PlayerStatus.MODE_FULL_SCREEN) {
            //全屏
            control_title_back.setVisibility(VISIBLE);
            control_title_battery.setVisibility(VISIBLE);
            control_full.setImageResource(R.mipmap.iconfont_exit);
            getContext().registerReceiver(mBatterReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            invokeLineShowTv();
        } else if (mCurrentMode == PlayerStatus.MODE_NORMAL) {
            //正常
            control_title_back.setVisibility(GONE);
            control_title_battery.setVisibility(GONE);
            control_full.setImageResource(R.mipmap.iconfont_enter_32);
            getContext().unregisterReceiver(mBatterReceiver);
            control_line.setVisibility(GONE);
        }
    }

    @Override
    public void positionDialogShow(boolean isVisiable) {
        if (isVisiable){
            positionDialog.myshow();
        }else{
            if (positionDialog.isShowing()){
                positionDialog.dismiss();
            }
        }
    }

    @Override
    public void positionTouchData(String changeTime, int changeProgress) {
        control_time.setText(changeTime);
        control_seekbar.setProgress(changeProgress);
        positionDialog.upDataShow(changeTime);
    }

    @Override
    public void brightnessDialogShow(boolean isVisiable) {
        if (isVisiable){
            brightnessDialog.myshow();
        }else{
            if (brightnessDialog.isShowing()){
                brightnessDialog.dismiss();
            }
        }
    }

    @Override
    public void brightnessTouchData(int changeProgress) {
        brightnessDialog.upDataShow(changeProgress);
    }

    @Override
    public void volumeDialogShow(boolean isVisiable) {
        if (isVisiable){
            volumeDialog.myshow();
        }else{
            if (volumeDialog.isShowing()){
                volumeDialog.dismiss();
            }
        }
    }

    @Override
    public void volumeTouchData(int changeProgress) {
        volumeDialog.upDataShow(changeProgress);
    }

    @Override
    public void invokeTopAndBottomLayout(boolean iaVisiable) {
        if (iaVisiable) {
            //显示
            control_title_layout.setVisibility(VISIBLE);
            control_bottom_layout.setVisibility(VISIBLE);
        } else {
            //隐藏
            control_title_layout.setVisibility(GONE);
            control_bottom_layout.setVisibility(GONE);
        }
    }

    @Override
    public void handlerUpProgress() {
        upHandler.post(upRunable);
    }

    @Override
    public void handlerCancleUpProgress() {
        upHandler.removeCallbacks(upRunable);
    }

    @Override
    public void handlerUpTopAndBottom() {
        upHandler.post(topAndbottomRunable);
    }

    @Override
    public void handlerCancleUpTopAndBottom() {
        upHandler.removeCallbacks(topAndbottomRunable);
    }

    @Override
    public void LoadingDialogShow(boolean isVisiable) {
        if (isVisiable){
            control_loading_layout.setVisibility(VISIBLE);
        }else{
            control_loading_layout.setVisibility(GONE);
        }
    }
    @Override
    public void invokeLineShowTv() {
        if (null != info.getPathMap()) {
            control_line.setVisibility(VISIBLE);
            for (String key : info.getPathMap().keySet()) {
                if (info.getPath().equals(info.getPathMap().get(key))) {
                    control_line.setText(key);
                }
            }
        }else{
            control_line.setVisibility(GONE);
        }
    }

    @Override
    public void reset() {
        handlerCancleUpProgress();
        handlerCancleUpTopAndBottom();
        control_seekbar.setProgress(0);
        control_seekbar.setSecondaryProgress(0);
        placeiv.setVisibility(VISIBLE);
        control_loading_layout.setVisibility(GONE);
        control_bottom_layout.setVisibility(GONE);
        control_loading_iv.startAnimation(rotate);
    }

    /**
     * 电池状态即电量变化广播接收器
     */
    private BroadcastReceiver mBatterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN);
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                // 充电中
                control_title_battery.setImageResource(R.mipmap.battery_charging);
            } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                // 充电完成
                control_title_battery.setImageResource(R.mipmap.battery_full);
            } else {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int percentage = (int) (((float) level / scale) * 100);
                if (percentage <= 10) {
                    control_title_battery.setImageResource(R.mipmap.battery_10);
                } else if (percentage <= 20) {
                    control_title_battery.setImageResource(R.mipmap.battery_20);
                } else if (percentage <= 50) {
                    control_title_battery.setImageResource(R.mipmap.battery_50);
                } else if (percentage <= 80) {
                    control_title_battery.setImageResource(R.mipmap.battery_80);
                } else if (percentage <= 100) {
                    control_title_battery.setImageResource(R.mipmap.battery_100);
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.control_center_play){
            if (player.getCurrStatus()==PlayerStatus.STATE_IDLE) {
                player.start();
            }else{
                player.restart();
            }
            placeiv.setVisibility(GONE);
            control_center_play.setVisibility(GONE);
        }else if (view.getId()==R.id.control_title_back){
            int currMode = player.getCurrModeStatus();
            if (currMode == PlayerStatus.MODE_FULL_SCREEN) {
                //全屏状态下的返回
                player.exitFullScreen();
            } else if (currMode == PlayerStatus.MODE_NORMAL) {
                //正常状态下的返回
            }
        }else if (view.getId()==R.id.control_start_btn){
            if (player.getCurrStatus()==PlayerStatus.STATE_PLAYING ||
                    player.getCurrStatus()==PlayerStatus.STATE_BUFFERING_PLAYING) {
                player.pause();
            } else if (player.getCurrStatus()==PlayerStatus.STATE_PAUSED ||
                    player.getCurrStatus()==PlayerStatus.STATE_BUFFERING_PAUSED
                    || player.getCurrStatus()==PlayerStatus.STATE_ERROR) {
                player.restart();
            } else if (player.getCurrStatus()==PlayerStatus.STATE_COMPLETED) {
                player.restart();
            }
        }else if (view.getId()==R.id.control_line){
            if (null != linePathDialog) {
                linePathDialog.myshow();
                invokeTopAndBottomLayout(false);
            }
        }else if (view.getId()==R.id.control_full){
            int currPlayMode = player.getCurrModeStatus();
            if (currPlayMode == PlayerStatus.MODE_FULL_SCREEN) {
                //全屏状态下
                player.exitFullScreen();//退出全屏
            } else if (currPlayMode == PlayerStatus.MODE_NORMAL) {
                //正常状态下
                player.enterFullScreen();//进入全屏
            }
        }else if (view==this){
            if (player.getCurrStatus()==PlayerStatus.STATE_PLAYING
                    || player.getCurrStatus()==PlayerStatus.STATE_PAUSED
                    || player.getCurrStatus()==PlayerStatus.STATE_BUFFERING_PLAYING
                    || player.getCurrStatus()==PlayerStatus.STATE_BUFFERING_PAUSED) {
                invokeTopAndBottomLayout(!(control_bottom_layout.getVisibility() == VISIBLE));
                upHandler.removeCallbacks(topAndbottomRunable);
                upHandler.postDelayed(topAndbottomRunable, 2500);
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        long position = (long) (player.getDuration() * seekBar.getProgress() / 100f);
        int duration = player.getDuration();
        control_time.setText(getTimeProgressFormat((int) position, duration));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        upHandler.removeCallbacks(topAndbottomRunable);
        upHandler.removeCallbacks(upRunable);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (player.getCurrStatus()==PlayerStatus.STATE_BUFFERING_PAUSED||
                player.getCurrStatus()==PlayerStatus.STATE_PAUSED) {
            player.restart();
        }
        long position = (long) (player.getDuration() * seekBar.getProgress() / 100f);
        player.seekTo((int) position);
        control_loading_layout.setVisibility(VISIBLE);
        control_loading_tv.setText("视频加载中...");
        if (player.getCurrStatus()==PlayerStatus.STATE_COMPLETED && seekBar.getProgress() < 100) {
            player.pause();
        }
        upHandler.postDelayed(topAndbottomRunable, 2500);
        upHandler.post(upRunable);
    }
    /**
     * handler更新进度
     */
    private Runnable upRunable = new Runnable() {
        @Override
        public void run() {
            int currentPosition = player.getCurrentPosition();
            int duration = player.getDuration();
            control_time.setText(getTimeProgressFormat(currentPosition, duration));
            upSeekbar();
            upHandler.postDelayed(upRunable, 1000);
        }
    };
    private void upSeekbar() {
        int currentPosition = player.getCurrentPosition();
        int duration = player.getDuration();
        int pro = player.getBufferPercentage();
        control_seekbar.setProgress((int) (100f * currentPosition / duration));
        control_seekbar.setSecondaryProgress(pro);
    }
    /**
     * 上下隐藏显示Runable
     * */
    private Runnable topAndbottomRunable = new Runnable() {
        @Override
        public void run() {
            if (control_bottom_layout.getVisibility() == VISIBLE ) {
                invokeTopAndBottomLayout(false);
            }
        }
    };

    private DialogInterface.OnDismissListener lineDialogDissClick = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialogInterface) {
            invokeTopAndBottomLayout(true);
            upHandler.postDelayed(topAndbottomRunable, 2500);
        }
    };
}
