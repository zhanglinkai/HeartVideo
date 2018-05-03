package com.example.heartvideo;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.MyAdapter;
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoInfo;
import com.heartfor.heartvideo.video.HeartVideoManager;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static com.heartfor.heartvideo.video.HeartVideoInfo.Builder;

public class MainActivity extends AppCompatActivity {
    private HeartVideo heartVideo;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearmanager;
    private int firstVisibleItem;
    private int lastVisibleItem;
    private int visibleCount;
    private int scrollState=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recy);
        linearmanager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearmanager);
        MyAdapter adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);
        //重复使用监听
        recyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                HeartVideo heartVideo = ((MyAdapter.ViewHolder) holder).myvideo;
                if (heartVideo == HeartVideoManager.getInstance().getCurrPlayVideo()) {
                    HeartVideoManager.getInstance().release();
                }
            }
        });
        //滑动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case SCROLL_STATE_IDLE: //滚动停止
                        autoPlayVideo(recyclerView);
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = linearmanager.findFirstVisibleItemPosition();
                lastVisibleItem = linearmanager.findLastVisibleItemPosition();
                visibleCount = lastVisibleItem - firstVisibleItem;
            }
        });
    }
        private void autoPlayVideo(RecyclerView recyclerview) {
            RecyclerView.LayoutManager layoutManager = recyclerview.getLayoutManager();
            for (int i = 0; i < visibleCount; i++) {
                if (layoutManager != null && layoutManager.getChildAt(i) != null && layoutManager.getChildAt(i).findViewById(R.id.myvideo) != null) {
                    HeartVideo heartVideo = (HeartVideo) layoutManager.getChildAt(i).findViewById(R.id.myvideo);
                    Rect rect = new Rect();
                    heartVideo.getLocalVisibleRect(rect);
                    int videoheight = heartVideo.getHeight();
                    if (rect.top == 0 && rect.bottom == videoheight) {
                        heartVideo.start();
                        return;
                    }

                }
            }
        }

    @Override
    protected void onStop() {
        super.onStop();
        HeartVideoManager.getInstance().release();
    }

    @Override
    public void onBackPressed() {
        if (HeartVideoManager.getInstance().onBackPressd()) return;
        super.onBackPressed();
    }
}
