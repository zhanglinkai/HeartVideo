package com.heartfor.heartvideo.video.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.heartfor.heartvideo.R;


/**
 * Created by Administrator on 2018/4/26.
 */

public class PositionDialog extends AlertDialog {
    private TextView position_dialog_tv;
    public PositionDialog(Context context) {
        super(context, R.style.LineDialog);
        setCancelable(false);//设置点击外部不消失
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_position_layout);
        position_dialog_tv=(TextView)findViewById(R.id.position_dialog_tv);
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
    public void upDataShow(String str){
        position_dialog_tv.setText(str);
    }
}
