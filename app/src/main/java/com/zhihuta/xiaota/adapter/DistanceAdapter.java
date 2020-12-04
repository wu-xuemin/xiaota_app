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
import com.zhihuta.xiaota.bean.basic.DistanceData;
import com.zhihuta.xiaota.common.Constant;

import java.text.SimpleDateFormat;
import java.util.List;

public class DistanceAdapter extends RecyclerView.Adapter<DistanceAdapter.ItemViewDistanceViewHolder>implements View.OnClickListener {
//    public class LujingAdapter extends RecyclerView.Adapter<com.zhihuta.xiaota.adapter.LujingAdapter.LujingViewHolder> implements View.OnClickListener {

        private SimpleDateFormat sf3 = new SimpleDateFormat("yy/MM/dd");
    private static String TAG = "DistanceAdapter";
//    private ArrayList<DistanceData> mDistanceAdapter;
//    public DistanceAdapter(ArrayList<DistanceData> list) {
//        mDistanceAdapter = list;
//    }

    private List<DistanceData> dataList;//数据源
    private Context context;//上下文

    /// 这里，传数据
    public DistanceAdapter(List<DistanceData> list, Context context) {
        this.dataList = list;
        this.context = context;
    }
    @NonNull
    @Override
    public ItemViewDistanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_distance,parent,false);
        return new ItemViewDistanceViewHolder(view);
    }


    /**
     * 绑定数据
     */
    @Override
    public void onBindViewHolder(@NonNull ItemViewDistanceViewHolder holder, int position) {

        DistanceData data = dataList.get(position);
        holder.distanceNameTv.setText(data.getName());
//        holder.distanceNumberTv.setText(data.getSerial_number());// sf3.format(mLujingAdapter.get(position).getLujingCreatedDate())
        holder.distanceNumberTv.setText( Integer.toString(data.getQr_id()));
        holder.distanceUpBt.setTag(position);
        holder.distanceDownBt.setTag(position);
        holder.deleteDistanceBt.setTag(position);

        holder.distanceLengthTv.setText(data.getDistance());


        final ItemViewDistanceViewHolder itemView = (ItemViewDistanceViewHolder) holder;
        if(dataList.get(position).getFlag().equals(Constant.FLAG_DISTANCE_IN_LUJING)) { //
            itemView.distanceNameTv.setVisibility(View.VISIBLE);
            itemView.distanceNumberTv.setVisibility(View.VISIBLE);

            itemView.distanceUpBt.setVisibility(View.VISIBLE);
            itemView.distanceDownBt.setVisibility(View.VISIBLE);
            itemView.deleteDistanceBt.setVisibility(View.VISIBLE);

            itemView.distanceLengthTv.setVisibility(View.GONE);
        } else if(dataList.get(position).getFlag().equals(Constant.FLAG_DISTANCE_IN_CALCULATE)) { //
            itemView.distanceNameTv.setVisibility(View.VISIBLE);
            itemView.distanceNumberTv.setVisibility(View.VISIBLE);

            itemView.distanceUpBt.setVisibility(View.GONE);
            itemView.distanceDownBt.setVisibility(View.GONE);
            itemView.deleteDistanceBt.setVisibility(View.GONE);

            itemView.distanceLengthTv.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ItemViewDistanceViewHolder extends RecyclerView.ViewHolder{
        TextView distanceNameTv;
        TextView distanceNumberTv;
        Button distanceUpBt;
        Button distanceDownBt;
        Button deleteDistanceBt;
        TextView distanceLengthTv;

        ItemViewDistanceViewHolder(View itemView) {
            super(itemView);
            distanceNameTv = itemView.findViewById(R.id.textView_distanceName);
            distanceNumberTv = itemView.findViewById(R.id.textView_distanceNumber);

            distanceUpBt = itemView.findViewById(R.id.button_distance_up);
            distanceDownBt = itemView.findViewById(R.id.button_distance_down);
            deleteDistanceBt = itemView.findViewById(R.id.button_distance_delete);
            distanceLengthTv = itemView.findViewById(R.id.textView2);
            // 为ItemView添加点击事件
            distanceUpBt.setOnClickListener(DistanceAdapter.this);
            distanceDownBt.setOnClickListener(DistanceAdapter.this);
            deleteDistanceBt.setOnClickListener(DistanceAdapter.this);
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
        void onItemClick(View v, DistanceAdapter.ViewName viewName, int position);
        void onItemLongClick(View v);
    }

    private DistanceAdapter.OnItemClickListener mOnItemClickListener;//声明自定义的接口

    //定义方法并传给外面的使用者
    public void setOnItemClickListener(DistanceAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener  = listener;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();      //getTag()获取数据
        if (mOnItemClickListener != null) {
            switch (v.getId()){
                case R.id.rv_distance:
                    mOnItemClickListener.onItemClick(v, DistanceAdapter.ViewName.PRACTISE, position);
                    break;
                default:
                    mOnItemClickListener.onItemClick(v, DistanceAdapter.ViewName.ITEM, position);
                    break;
            }
        }

    }




    @Override
    public int getItemViewType(int position) {
        if (position == dataList.size()) {
            return 1;
        } else {
            return 0;
        }
    }
    public class ItemView extends RecyclerView.ViewHolder {
//        CardView installPlanLayout;
        TextView distanceNameTv;
        TextView distanceNumberTv;

        ItemView(View itemView) {
            super(itemView);
            distanceNameTv = itemView.findViewById(R.id.textView_distanceName);
            distanceNumberTv = itemView.findViewById(R.id.textView_distanceNumber);
        }
    }
}
