package com.zhihuta.xiaota.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.common.Constant;

import java.util.List;

public class DianXianQingceAdapter extends RecyclerView.Adapter<DianXianQingceAdapter.ItemView_DianXianViewHolder> implements View.OnClickListener {

    private static String TAG = "QingceAdapter";

    private List<DianxianQingCeData> dataList;//数据源
    private Context context;//上下文
    /// 这里，传数据
    public DianXianQingceAdapter(List<DianxianQingCeData> list, Context context) {
        this.dataList = list;
        this.context = context;
    }

    public void updateDataSoruce(List<DianxianQingCeData> list)
    {
        this.dataList = list;

        notifyDataSetChanged();
    }
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dianxian_qing_ce,parent,false);
//        return new DianXianQingceAdapter.ItemView(view);
//
//    }

    @NonNull
    @Override
    public ItemView_DianXianViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dianxian_qing_ce, parent, false);
        return new ItemView_DianXianViewHolder(view);
    }

    /**
     * 绑定数据
     */
    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
    public void onBindViewHolder(@NonNull ItemView_DianXianViewHolder holder, int position) {
        final DianXianQingceAdapter.ItemView_DianXianViewHolder itemView = (DianXianQingceAdapter.ItemView_DianXianViewHolder) holder;

        DianxianQingCeData data = dataList.get(position);
        holder.dianxianBianhaoTv.setText(data.getSerial_number());
        holder.qidianTv.setText( data.getStart_point() );// sf3.format(mLujingAdapter.get(position).getLujingCreatedDate())
        holder.zhongdianTv.setText(data.getEnd_point());
        holder.modelTv.setText(data.getParts_code());
        holder.xinshuJiemianTv.setText(data.getWickes_cross_section());
        holder.dxLengthTv.setText(data.getLength());
        holder.steelHoseRedundancyTv.setText(data.getSteel_redundancy() + "/" + data.getHose_redundancy());

        holder.deleteBt.setTag(position);
        holder.dxTobeSelectCheckBox.setTag(position);

        /**
         * 不同的数据，显示不同的内容，比如在备选电线列表里，不需要显示删除按钮
         */
        if(dataList.get(position).getFlag().equals(Constant.FLAG_QINGCE_DX)) { //清册
            itemView.deleteBt.setVisibility(View.VISIBLE);
            itemView.dxTobeSelectCheckBox.setVisibility(View.GONE);
            itemView.modelTv.setVisibility(View.GONE);
            itemView.steelHoseRedundancyTv.setVisibility(View.GONE);
            itemView.xinshuJiemianTv.setVisibility(View.GONE);
        } else if(dataList.get(position).getFlag().equals(Constant.FLAG_RELATED_DX) ) { // 已关联
            itemView.deleteBt.setVisibility(View.VISIBLE);
            itemView.dxTobeSelectCheckBox.setVisibility(View.GONE);
            itemView.modelTv.setVisibility(View.GONE);
            itemView.steelHoseRedundancyTv.setVisibility(View.GONE);
            itemView.xinshuJiemianTv.setVisibility(View.GONE);
        } else if(dataList.get(position).getFlag().equals(Constant.FLAG_TOBE_SELECT_DX)) { // 可选
            itemView.deleteBt.setVisibility(View.GONE);
            itemView.dxTobeSelectCheckBox.setVisibility(View.VISIBLE);

            itemView.modelTv.setVisibility(View.GONE);
            itemView.steelHoseRedundancyTv.setVisibility(View.GONE);
            itemView.xinshuJiemianTv.setVisibility(View.GONE);
            itemView.dxLengthTv.setVisibility(View.GONE);
        } else if (dataList.get(position).getFlag().equals(Constant.FLAG_EXPORT_DX)) { //按型号导出
            itemView.dianxianBianhaoTv.setVisibility(View.GONE);
            itemView.qidianTv.setVisibility(View.GONE);
            itemView.zhongdianTv.setVisibility(View.GONE);
            itemView.deleteBt.setVisibility(View.GONE);
            itemView.dxTobeSelectCheckBox.setVisibility(View.GONE);
            itemView.steelHoseRedundancyTv.setVisibility(View.GONE);


            itemView.modelTv.setVisibility(View.VISIBLE);
            itemView.xinshuJiemianTv.setVisibility(View.VISIBLE);
            itemView.dxLengthTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ItemView_DianXianViewHolder extends RecyclerView.ViewHolder {

        TextView dianxianBianhaoTv;
        TextView qidianTv;
        TextView zhongdianTv;
        TextView modelTv;
        TextView xinshuJiemianTv;
        TextView dxLengthTv;
        TextView steelHoseRedundancyTv;

        Button deleteBt;
        CheckBox dxTobeSelectCheckBox;
        ItemView_DianXianViewHolder(View itemView) {
            super(itemView);
            dianxianBianhaoTv = itemView.findViewById(R.id.dianxianBianhaotextView);
            qidianTv = itemView.findViewById(R.id.qidianTextView);
            zhongdianTv = itemView.findViewById(R.id.zhongdiantextView);
            modelTv = itemView.findViewById(R.id.xinghaoTextView);
            xinshuJiemianTv = itemView.findViewById(R.id.textViewXinshuJiemian);
            dxLengthTv = itemView.findViewById(R.id.textViewDxLength);
            steelHoseRedundancyTv = itemView.findViewById(R.id.textViewSteelHoseRedundancy);
            deleteBt = itemView.findViewById(R.id.buttonDxDelete);
            dxTobeSelectCheckBox = itemView.findViewById(R.id.checkBox_dx_to_be_select);

            // 为ItemView添加点击事件
            deleteBt.setOnClickListener(DianXianQingceAdapter.this);
            dxTobeSelectCheckBox.setOnClickListener(DianXianQingceAdapter.this);

        }
    }
    //=======================以下为item中的button控件点击事件处理===================================
    //item里面有多个控件可以点击（item+item内部控件）
    public enum ViewName {
        ITEM,
        PRACTISE,
        DX_TO_BE_SELECT
    }
    //自定义一个回调接口来实现Click和LongClick事件
    public interface OnItemClickListener  {
        void onItemClick(View v, DianXianQingceAdapter.ViewName viewName, int position);
        void onItemLongClick(View v);
    }

    private OnItemClickListener mOnItemClickListener;//声明自定义的接口

    //定义方法并传给外面的使用者
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener  = listener;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();      //getTag()获取数据
        if (mOnItemClickListener != null) {
            switch (v.getId()){
			// todo 
                case R.id.rv_dx:
                    mOnItemClickListener.onItemClick(v, DianXianQingceAdapter.ViewName.PRACTISE, position);
                    break;
                case R.id.rv_dx_tobeSelect:
                    mOnItemClickListener.onItemClick(v, DianXianQingceAdapter.ViewName.DX_TO_BE_SELECT, position);
                    break;
                default:
                    mOnItemClickListener.onItemClick(v, DianXianQingceAdapter.ViewName.ITEM, position);
                    break;
            }
        }

    }
}
