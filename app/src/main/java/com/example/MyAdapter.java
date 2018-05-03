package com.example;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.heartvideo.R;
import com.heartfor.heartvideo.video.VideoControl;
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoInfo;

/**
 * Created by Administrator on 2018/4/27.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private Context context;
    public MyAdapter(Context context){
        this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String path="https://v4.438vip.com/20180311/3Q4fNFYh/index.m3u8";
        String image="http://pic39.nipic.com/20140226/18071023_164300608000_2.jpg";
        HeartVideoInfo info=HeartVideoInfo.Builder().setTitle("xxxxx").setPath(path).setImagePath(image).setSaveProgress(true).builder();
        VideoControl control=new VideoControl(context);
        control.setInfo(info);
        holder.myvideo.setHeartVideoContent(control);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public HeartVideo myvideo;
        public ViewHolder(View itemView) {
            super(itemView);
            myvideo=(HeartVideo)itemView.findViewById(R.id.myvideo);
        }
    }
}
