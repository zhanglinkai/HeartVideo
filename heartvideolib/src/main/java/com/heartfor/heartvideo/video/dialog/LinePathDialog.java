package com.heartfor.heartvideo.video.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.heartfor.heartvideo.R;
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoInfo;
import com.heartfor.heartvideo.video.LinePathRecycleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/25.
 */

public class LinePathDialog extends AlertDialog implements LinePathRecycleAdapter.LinePathClick{
    private RecyclerView line_recycle;
    private static Context mContext;
    private static HeartVideo mPlayer;
    private static HeartVideoInfo mInfo;
    private  List<Map<String, String>> lineList;
    private LinePathRecycleAdapter adapter;

    public static LinePathDialog builder(Context context, HeartVideo player, HeartVideoInfo info){
        mContext=context;
        mPlayer=player;
        mInfo=info;
        LinePathDialog linePathDialog=new LinePathDialog(context);
        return linePathDialog;
    }

    public LinePathDialog(Context context) {
        super(context, R.style.LineDialog);
        setCancelable(false);//设置点击外部不消失
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_line_layout);

        line_recycle = (RecyclerView)findViewById(R.id.line_recycle);
        line_recycle.setLayoutManager(new LinearLayoutManager(mContext));
        lineList = new ArrayList<>();
        for (String key : mInfo.getPathMap().keySet()) {
            Map<String, String> newMap = new HashMap<>();
            newMap.put("linename", key);
            newMap.put("lineaddress", mInfo.getPathMap().get(key));
            if (mInfo.getPathMap().get(key).equals(mInfo.getPath())) {
                newMap.put("select", "yes");
            } else {
                newMap.put("select", "no");
            }
            lineList.add(newMap);
        }
        adapter = new LinePathRecycleAdapter(mContext);
        adapter.setClick(this);
        adapter.setList(lineList);
        line_recycle.setAdapter(adapter);
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

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        int top = line_recycle.getTop() ;
        int bottom = line_recycle.getBottom() ;
        int left = line_recycle.getLeft() ;
        int right = line_recycle.getRight() ;
        int x = ( int ) event.getX();
        int y = ( int ) event.getY();
        if (event.getAction() == MotionEvent. ACTION_UP ) {
            if (y < top||y>bottom||x<left||x>right) {
                dismiss() ;
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void linePathclick(int position) {
        for (int i = 0; i < lineList.size(); i++) {
            if (i==position){
                lineList.get(i).put("select","yes");
            }else{
                lineList.get(i).put("select","no");
            }
            String p=lineList.get(i).get("lineaddress");
            mPlayer.savePlayProgress(p,mPlayer.getCurrentPosition());
        }
        adapter.setList(lineList);
        String path=lineList.get(position).get("lineaddress");
        mInfo.setPath(path);
        mPlayer.restart();
        dismiss();
    }
}
