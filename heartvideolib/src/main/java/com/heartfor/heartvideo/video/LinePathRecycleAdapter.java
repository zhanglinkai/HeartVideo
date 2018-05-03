package com.heartfor.heartvideo.video;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.heartfor.heartvideo.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/25.
 */

public class LinePathRecycleAdapter extends RecyclerView.Adapter<LinePathRecycleAdapter.ViewHolder>{
    private Context context;
    private List<Map<String,String>> list;
    public LinePathRecycleAdapter(Context context){
        this.context=context;
    }

    public void setList(List<Map<String,String>> list){
        this.list=list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_line_path_item,parent,false);
        ViewHolder holder=new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.lint_path_item_tv.setText(list.get(position).get("linename"));
        String tag=list.get(position).get("select");
        if (tag.equals("yes")){
            holder.lint_path_item_tv.setSelected(true);
        }else{
            holder.lint_path_item_tv.setSelected(false);
        }
        holder.lint_path_item_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click.linePathclick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null==list?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView lint_path_item_tv;
        public ViewHolder(View itemView) {
            super(itemView);
            lint_path_item_tv=(TextView)itemView.findViewById(R.id.lint_path_item_tv);
        }
    }
    private LinePathClick click;
    public void setClick(LinePathClick click){
        this.click=click;
    }
    public interface LinePathClick{
        void linePathclick(int position);
    }
}
