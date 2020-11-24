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
import com.zhihuta.xiaota.bean.basic.PathWiresPartsCodeSum;


import java.util.List;

public class LujingSummarizeWiresAdapter extends RecyclerView.Adapter<LujingSummarizeWiresAdapter.ItemView_DianXianViewHolder> implements View.OnClickListener {

    private static String TAG = "LujingSummarizeWiresAdapter";

    private int mRequestCode = 0;

    private List<PathWiresPartsCodeSum> dataList;//数据源
    private Context context;//上下文
    /// 这里，传数据
    public LujingSummarizeWiresAdapter(List<PathWiresPartsCodeSum> list, Context context, int requestCode) {
        this.dataList = list;
        this.context = context;
        mRequestCode = requestCode;
    }

    public void updateDataSoruce(List<PathWiresPartsCodeSum> list)
    {
        this.dataList = list;

        notifyDataSetChanged();
    }

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
    public void onBindViewHolder(@NonNull ItemView_DianXianViewHolder holder, int position) {
        final ItemView_DianXianViewHolder itemView = (ItemView_DianXianViewHolder) holder;

        if (dataList == null || dataList.size() == 0)
        {
            return;
        }

        PathWiresPartsCodeSum data = dataList.get(position);

        holder.modelTv.setText(data.parts_code);
        holder.xinshuJiemianTv.setText(data.wickes_cross_section);
        holder.dxLengthTv.setText(Double.toString(data.length));

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

            modelTv = itemView.findViewById(R.id.xinghaoTextView);
            xinshuJiemianTv = itemView.findViewById(R.id.textViewXinshuJiemian);
            dxLengthTv = itemView.findViewById(R.id.textViewDxLength);

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
            deleteBt.setOnClickListener(LujingSummarizeWiresAdapter.this);
            dxTobeSelectCheckBox.setOnClickListener(LujingSummarizeWiresAdapter.this);
            dianxianBianhaoTv.setOnClickListener(LujingSummarizeWiresAdapter.this);
            qidianTv.setOnClickListener(LujingSummarizeWiresAdapter.this);
            zhongdianTv.setOnClickListener(LujingSummarizeWiresAdapter.this);
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
        void onItemClick(View v, ViewName viewName, int position);
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
                    mOnItemClickListener.onItemClick(v, ViewName.PRACTISE, position);
                    break;
                case R.id.rv_dx_tobeSelect:
                    mOnItemClickListener.onItemClick(v, ViewName.DX_TO_BE_SELECT, position);
                    break;
                default:
                    mOnItemClickListener.onItemClick(v, ViewName.ITEM, position);
                    break;
            }
        }

    }
}