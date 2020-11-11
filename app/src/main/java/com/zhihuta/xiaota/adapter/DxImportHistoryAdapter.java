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
import com.zhihuta.xiaota.bean.basic.DxImportHistoryData;
import com.zhihuta.xiaota.common.Constant;

import java.util.List;

public class DxImportHistoryAdapter extends RecyclerView.Adapter<DxImportHistoryAdapter.ItemView_DianXianViewHolder> implements View.OnClickListener {

    private static String TAG = "DxImportHistoryAdapter";

    private List<DxImportHistoryData> dataList;//数据源
    private Context context;//上下文
    /// 这里，传数据
    public DxImportHistoryAdapter(List<DxImportHistoryData> list, Context context) {
        this.dataList = list;
        this.context = context;
    }

    public void updateDataSoruce(List<DxImportHistoryData> list)
    {
        this.dataList = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemView_DianXianViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dx_import_history, parent, false);
        return new ItemView_DianXianViewHolder(view);
    }

    /**
     * 绑定数据
     */
    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
    public void onBindViewHolder(@NonNull ItemView_DianXianViewHolder holder, int position) {
        final DxImportHistoryAdapter.ItemView_DianXianViewHolder itemView = (DxImportHistoryAdapter.ItemView_DianXianViewHolder) holder;

        DxImportHistoryData data = dataList.get(position);
        holder.dxImportFileNameTv.setText(data.getFileName());
        holder.dxImportHistoryOperatorTv.setText( data.getOperator() );// sf3.format(mLujingAdapter.get(position).getLujingCreatedDate())
        holder.dxImportHistoryFileNameDateTv.setText(data.getOperate_time().toString());

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ItemView_DianXianViewHolder extends RecyclerView.ViewHolder {

        TextView dxImportFileNameTv;
        TextView dxImportHistoryOperatorTv;
        TextView dxImportHistoryFileNameDateTv;

        ItemView_DianXianViewHolder(View itemView) {
            super(itemView);
            dxImportFileNameTv = itemView.findViewById(R.id.dxImportHistoryFileNametextView);
            dxImportHistoryOperatorTv = itemView.findViewById(R.id.dxImportHistoryOperator);
            dxImportHistoryFileNameDateTv = itemView.findViewById(R.id.dxImportHistoryFileNameDate);

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
        void onItemClick(View v, DxImportHistoryAdapter.ViewName viewName, int position);
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
                default:
                    mOnItemClickListener.onItemClick(v, DxImportHistoryAdapter.ViewName.ITEM, position);
                    break;
            }
        }

    }
}
