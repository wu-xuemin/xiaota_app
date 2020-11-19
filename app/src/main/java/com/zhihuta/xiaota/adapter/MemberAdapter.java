package com.zhihuta.xiaota.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.ProjectData;

import java.text.SimpleDateFormat;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ItemViewProjectViewHolder> implements View.OnClickListener {
    private SimpleDateFormat sf3 = new SimpleDateFormat("yy/MM/dd");
    private static String TAG = "MemberAdapter";

    private List<ProjectData> dataList;//数据源
    private Context context;//上下文

    private String  strMode;
    /// 这里，传数据
    public MemberAdapter(List<ProjectData> list, Context context, String strMode) {
        this.dataList = list;
        this.context = context;
        this.strMode = strMode;
    }

    public void updateDataSource(List<ProjectData> list, String strMode)
    {
        this.strMode = strMode;
        this.dataList = list;
        notifyDataSetChanged();
    }

    public String getStrMode( ) {

        return this.strMode ;
    }


    @NonNull
    @Override
    public ItemViewProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project,parent,false);
        return new ItemViewProjectViewHolder(view);
    }

    //绑定数据
    @Override
    public void onBindViewHolder(@NonNull ItemViewProjectViewHolder holder, int position) {
//
//        ProjectData data = dataList.get(position);
//        holder.projectNameTv.setText(data.getName());
//        holder.projectCreatedDateTv.setText(data.getCreate_time().substring(2));// sf3.format(mLujingAdapter.get(position).getLujingCreatedDate())
//        holder.projectCreaterTv.setText(data.getCreator());
//        holder.memberManageBt.setTag(position);
//        holder.deleteProjectBt.setTag(position);
//
//        holder.projectNameTv.setTag(position);
//        holder.projectCreaterTv.setTag(position);
//        holder.projectCreatedDateTv.setTag(position);

    }

   //有多少个item？
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //创建LujingViewHolder继承RecyclerView.ViewHolder
    public class ItemViewProjectViewHolder extends RecyclerView.ViewHolder{
        TextView projectNameTv;
        TextView projectCreatedDateTv;
        TextView projectCreaterTv;
        Button deleteProjectBt;
        Button memberManageBt;


        ItemViewProjectViewHolder(View itemView) {
            super(itemView);
            projectNameTv = itemView.findViewById(R.id.lujingMingChenTextView);
            projectCreatedDateTv = itemView.findViewById(R.id.lujingCreateDateTextView);
            projectCreaterTv = itemView.findViewById(R.id.lujingCreaterTextView);
            memberManageBt = itemView.findViewById(R.id.button_create_lujing_base_exist);
            deleteProjectBt = itemView.findViewById(R.id.button_delete_lujing);

            // 为ItemView添加点击事件
            memberManageBt.setOnClickListener(MemberAdapter.this);
            deleteProjectBt.setOnClickListener(MemberAdapter.this);

            projectNameTv.setOnClickListener(MemberAdapter.this);
            projectCreaterTv.setOnClickListener(MemberAdapter.this);
            projectCreatedDateTv.setOnClickListener(MemberAdapter.this);
        }
    }


    //=======================以下为item中的button控件点击事件处理===================================
    //item里面有多个控件可以点击（item+item内部控件）
    public enum ViewName {
        ITEM,
        PRACTISE
    }
    //自定义一个回调接口来实现Click和LongClick事件
    public interface OnItemClickListener  {
        void onItemClick(View v, ViewName viewName, int position);
        void onItemLongClick(View v);
    }

    private OnItemClickListener mOnItemClickListener;//声明自定义的接口

    //定义方法并传给外面的使用者
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener  = listener;
    }

    @Override
    public void onClick(View v) { ///这里的v是item里的Button, 统一传到外部Mian里处理。
        int position = (int) v.getTag();      //getTag()获取数据
        if (mOnItemClickListener != null) {
            switch (v.getId()){
                default:
                    mOnItemClickListener.onItemClick(v, ViewName.ITEM, position);
                    break;
            }
        }

    }

}
