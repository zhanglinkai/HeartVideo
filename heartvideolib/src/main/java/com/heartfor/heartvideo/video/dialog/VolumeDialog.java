package com.heartfor.heartvideo.video.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.heartfor.heartvideo.R;


/**
 * Created by Administrator on 2018/4/26.
 */

public class VolumeDialog extends AlertDialog {
    private ProgressBar volume_dialog_progress;
    public VolumeDialog(Context context) {
        super(context, R.style.LineDialog);
        setCancelable(false);//设置点击外部不消失
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_volume_layout);
        volume_dialog_progress=(ProgressBar)findViewById(R.id.volume_dialog_progress);
    }

    public void myshow(){
        super.show();
        //设置全屏
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
    }
    public void upDataShow(int values){
        volume_dialog_progress.setProgress(values);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        dismiss();
        return true;
    }
}
