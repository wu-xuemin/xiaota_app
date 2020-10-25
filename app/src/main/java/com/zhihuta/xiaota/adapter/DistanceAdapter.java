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

import java.text.SimpleDateFormat;
import java.util.List;

public class DistanceAdapter extends RecyclerView.Adapter<DistanceAdapter.DistanceViewHolder>implements View.OnClickListener {
//    public class LujingAdapter extends RecyclerView.Adapter<com.zhihuta.xiaota.adapter.LujingAdapter.LujingViewHolder> implements View.OnClickListener {

        private SimpleDateFormat sf3 = new SimpleDateFormat("yy/MM/dd");
    private static String TAG = "DistanceAdapter";
//    private ArrayList<DistanceData> mDistanceAdapter;
//    public DistanceAdapter(ArrayList<DistanceData> list) {
//        mDistanceAdapter = list;
//    }

    private List<DistanceData> list;//数据源
    private Context context;//上下文

    /// 这里，传数据
    public DistanceAdapter(List<DistanceData> list, Context context) {
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public DistanceAdapter.DistanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_distance,parent,false);
        return new DistanceAdapter.DistanceViewHolder(view);
    }


    /**
     * 绑定数据
     */
    @Override
    public void onBindViewHolder(@NonNull DistanceAdapter.DistanceViewHolder holder, int position) {

        DistanceData data = list.get(position);
        holder.distanceNameTv.setText(data.getDistanceName());
        holder.distanceNumberTv.setText(data.getDistanceNumber());// sf3.format(mLujingAdapter.get(position).getLujingCreatedDate())
        holder.distanceUpBt.setTag(position);
        holder.distanceDownBt.setTag(position);
        holder.deleteDistanceBt.setTag(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DistanceViewHolder extends RecyclerView.ViewHolder{
        TextView distanceNameTv;
        TextView distanceNumberTv;
        Button distanceUpBt;
        Button distanceDownBt;
        Button deleteDistanceBt;

        DistanceViewHolder(View itemView) {
            super(itemView);
            distanceNameTv = itemView.findViewById(R.id.textView_distanceName);
            distanceNumberTv = itemView.findViewById(R.id.textView_distanceNumber);

            distanceUpBt = itemView.findViewById(R.id.button_distance_up);
            distanceDownBt = itemView.findViewById(R.id.button_distance_down);
            deleteDistanceBt = itemView.findViewById(R.id.button_distance_delete);

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
        if (position == list.size()) {
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
