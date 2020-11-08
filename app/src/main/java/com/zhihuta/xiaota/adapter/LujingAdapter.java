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
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.common.Constant;

import java.text.SimpleDateFormat;
import java.util.List;
//MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> implements View.OnClickListener{

public class LujingAdapter extends RecyclerView.Adapter<LujingAdapter.ItemViewLujingViewHolder> implements View.OnClickListener {
    private SimpleDateFormat sf3 = new SimpleDateFormat("yy/MM/dd");
    private static String TAG = "LujingAdapter";
//    private ArrayList<LujingData> mLujingAdapter;
//    public LujingAdapter(ArrayList<LujingData> list) {
//        mLujingAdapter = list;
//    }

    private List<LujingData> dataList;//数据源
    private Context context;//上下文

    /// 这里，传数据
    public LujingAdapter(List<LujingData> list, Context context) {
        this.dataList = list;
        this.context = context;
    }

    public void updateDataSource(List<LujingData> list)
    {
        this.dataList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewLujingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lujing,parent,false);
        return new ItemViewLujingViewHolder(view);
    }

    //绑定数据
    @Override
    public void onBindViewHolder(@NonNull ItemViewLujingViewHolder holder, int position) {

        LujingData data = dataList.get(position);
        holder.lujingNameTv.setText(data.getName());
        holder.lujingCreatedDateTv.setText(data.getCreate_time().substring(2));// sf3.format(mLujingAdapter.get(position).getLujingCreatedDate())
        holder.lujingCreaterTv.setText(data.getCreator());
        holder.modifyLujingBt.setTag(position);
        holder.createLujingBaseOnExistBt.setTag(position);
        holder.deleteLujingBt.setTag(position);
        holder.wiresListBt.setTag(position);
        holder.exportAccordModelBt.setTag(position);

        final LujingAdapter.ItemViewLujingViewHolder itemView = (LujingAdapter.ItemViewLujingViewHolder) holder;

        if(dataList.get(position).getFlag().equals(Constant.FLAG_LUJING_IN_LUJING)) { //路径模型中
            itemView.modifyLujingBt.setVisibility(View.VISIBLE);
            itemView.createLujingBaseOnExistBt.setVisibility(View.VISIBLE);
            itemView.deleteLujingBt.setVisibility(View.VISIBLE);

            itemView.exportAccordModelBt.setVisibility(View.GONE);
            itemView.wiresListBt.setVisibility(View.GONE);
        } else if (dataList.get(position).getFlag().equals(Constant.FLAG_LUJING_IN_CALCULATE)){  //计算模型中
            itemView.modifyLujingBt.setVisibility(View.GONE);
            itemView.createLujingBaseOnExistBt.setVisibility(View.GONE);
            itemView.deleteLujingBt.setVisibility(View.GONE);

            itemView.exportAccordModelBt.setVisibility(View.VISIBLE);
            itemView.wiresListBt.setVisibility(View.VISIBLE);
        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position == mLujingAdapter.size()) {
//            return 1;
//        } else {
//            return 0;
//        }
//    }
    //有多少个item？
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //创建LujingViewHolder继承RecyclerView.ViewHolder
    public class ItemViewLujingViewHolder extends RecyclerView.ViewHolder{
        TextView lujingNameTv;
        TextView lujingCreatedDateTv;
        TextView lujingCreaterTv;
        Button modifyLujingBt;
        Button createLujingBaseOnExistBt;
        Button deleteLujingBt;

        //计算界面的路径，包括 “电线信息”、“按型号汇总”2个按钮
        Button wiresListBt;
        Button exportAccordModelBt;

        ItemViewLujingViewHolder(View itemView) {
            super(itemView);
            lujingNameTv = itemView.findViewById(R.id.lujingMingChenTextView);
            lujingCreatedDateTv = itemView.findViewById(R.id.lujingCreateDateTextView);
            lujingCreaterTv = itemView.findViewById(R.id.lujingCreaterTextView);
            modifyLujingBt = itemView.findViewById(R.id.button_modify_lujing);
            createLujingBaseOnExistBt = itemView.findViewById(R.id.button_create_lujing_base_exist);
            deleteLujingBt = itemView.findViewById(R.id.button_delete_lujing);
            wiresListBt = itemView.findViewById(R.id.wiresListBt);
            exportAccordModelBt = itemView.findViewById(R.id.exportAccordModelBt);

            // 为ItemView添加点击事件
            modifyLujingBt.setOnClickListener(LujingAdapter.this);
            createLujingBaseOnExistBt.setOnClickListener(LujingAdapter.this);
            deleteLujingBt.setOnClickListener(LujingAdapter.this);
            wiresListBt.setOnClickListener(LujingAdapter.this);
            exportAccordModelBt.setOnClickListener(LujingAdapter.this);

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
//                case R.id.rv_lujing:
//                    mOnItemClickListener.onItemClick(v, ViewName.PRACTISE, position);//
//                    break;

                default:
                    mOnItemClickListener.onItemClick(v, ViewName.ITEM, position);
                    break;
            }
        }

    }

}
