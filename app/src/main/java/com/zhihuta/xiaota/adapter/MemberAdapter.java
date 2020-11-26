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
import com.zhihuta.xiaota.bean.basic.MemberData;

import java.text.SimpleDateFormat;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ItemViewMemberViewHolder> implements View.OnClickListener {
    private SimpleDateFormat sf3 = new SimpleDateFormat("yy/MM/dd");
    private static String TAG = "MemberAdapter";

    private List<MemberData> dataList;//数据源
    private Context context;//上下文

    private String  strMode;
    /// 这里，传数据
    public MemberAdapter(List<MemberData> list, Context context, String strMode) {
        this.dataList = list;
        this.context = context;
        this.strMode = strMode;
    }

    public void updateDataSource(List<MemberData> list, String strMode)
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
    public ItemViewMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_member,parent,false);
        return new ItemViewMemberViewHolder(view);
    }

    //绑定数据
    @Override
    public void onBindViewHolder(@NonNull ItemViewMemberViewHolder holder, int position) {

        MemberData data = dataList.get(position);
        holder.memberAccountTv.setText(data.getAccount());
        holder.MemberAccountDisableTv.setTag(position);

        holder.memberAccountTv.setTag(position);
        holder.MemberAccountDisableTv.setTag(position);

    }

   //有多少个item？
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ItemViewMemberViewHolder extends RecyclerView.ViewHolder{
        TextView memberAccountTv;
        Button MemberAccountDisableTv;


        ItemViewMemberViewHolder(View itemView) {
            super(itemView);
            memberAccountTv = itemView.findViewById(R.id.projectMemberAccountTextView);
            MemberAccountDisableTv = itemView.findViewById(R.id.projectMemberAccountDisableBt);


            memberAccountTv.setOnClickListener(MemberAdapter.this);
            MemberAccountDisableTv.setOnClickListener(MemberAdapter.this);
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
