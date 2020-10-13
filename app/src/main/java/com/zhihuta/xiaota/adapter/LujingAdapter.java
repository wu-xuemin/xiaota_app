package com.zhihuta.xiaota.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.bean.basic.OrderData;

import java.util.ArrayList;

public class LujingAdapter extends RecyclerView.Adapter {

    private static String TAG = "LujingAdapter";
    private ArrayList<LujingData> mLujingAdapter;
    public LujingAdapter(ArrayList<LujingData> list) {
        mLujingAdapter = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lujing,parent,false);
        return new LujingAdapter.ItemView(view);

    }

    /**
     * 绑定数据
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final LujingAdapter.ItemView itemView = (LujingAdapter.ItemView) holder;
        //itemView.setIsRecyclable(false);//禁止复用
        if (mLujingAdapter!=null && !mLujingAdapter.isEmpty() && position < mLujingAdapter.size()) {
            itemView.lujingNameTv.setText(mLujingAdapter.get(position).getLujingName());
            itemView.lujingNameTv.setSelected(true);//用于滚动显示
            itemView.lujingCreatedDateTv.setText(mLujingAdapter.get(position).getLujingCreatedDate().toString());
            itemView.lujingCreaterTv.setText(mLujingAdapter.get(position).getLujingCreater());
            itemView.lujingCaozuoTv.setText(mLujingAdapter.get(position).getLujingCaozuo());
        }else {
            Log.d(TAG, "onBindViewHolder: 没有获取到 路径 list数据");
        }
    }

    @Override
    public int getItemCount() {
        return mLujingAdapter.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mLujingAdapter.size()) {
            return 1;
        } else {
            return 0;
        }
    }
    public class ItemView extends RecyclerView.ViewHolder {
//        CardView installPlanLayout;
        TextView lujingNameTv;
        TextView lujingCreatedDateTv;
        TextView lujingCreaterTv;
        TextView lujingCaozuoTv;

        ItemView(View itemView) {
            super(itemView);
            lujingNameTv = itemView.findViewById(R.id.lujingMingChenTextView);
            lujingCreatedDateTv = itemView.findViewById(R.id.lujingCreateDateTextView);
            lujingCreaterTv = itemView.findViewById(R.id.lujingCreaterTextView);
            lujingCaozuoTv = itemView.findViewById(R.id.lujingCaozuoTextView);
        }
    }
}
