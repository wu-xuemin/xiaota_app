package com.zhihuta.xiaota.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.ui.DianxianQingCe;

import java.util.ArrayList;

public class QingceAdapter extends RecyclerView.Adapter {

    private static String TAG = "QingceAdapter";
//    private ArrayList<InstallPlanData> mInstallPlanAdapter;
    private ArrayList<DianxianQingCeData> mDianxianQingCeAdapter;
    public QingceAdapter(ArrayList<DianxianQingCeData> list) {
        mDianxianQingCeAdapter = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dianxian_qing_ce,parent,false);
        return new QingceAdapter.ItemView(view);

    }
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//    }

    /**
     * 绑定数据
     */
    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final QingceAdapter.ItemView itemView = (QingceAdapter.ItemView) holder;
        //itemView.setIsRecyclable(false);//禁止复用
        if (mDianxianQingCeAdapter!=null && !mDianxianQingCeAdapter.isEmpty() && position < mDianxianQingCeAdapter.size()) {
            itemView.dianxianBianhaoTv.setText(mDianxianQingCeAdapter.get(position).getDxNumber());
            itemView.dianxianBianhaoTv.setSelected(true);//用于滚动显示
            itemView.qidianTv.setText(mDianxianQingCeAdapter.get(position).getStartPoint());
            itemView.zhongdianTv.setText(mDianxianQingCeAdapter.get(position).getEndPoint());
//            itemView.headCountDoneTv.setText(""+mInstallPlanAdapter.get(position).getHeadCountDone());
//            itemView.cmdInfoTv.setText(""+mInstallPlanAdapter.get(position).getCmtSend());
//            itemView.cmdInfoTv.setSelected(true);//用于滚动显示
        }else {
            Log.d(TAG, "onBindViewHolder: 没有获取到list数据");
        }
    }

    @Override
    public int getItemCount() {
        return mDianxianQingCeAdapter.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mDianxianQingCeAdapter.size()) {
            return 1;
        } else {
            return 0;
        }
    }
    public class ItemView extends RecyclerView.ViewHolder {
//        CardView installPlanLayout;
        TextView dianxianBianhaoTv;
        TextView qidianTv;
        TextView zhongdianTv;

        ItemView(View itemView) {
            super(itemView);
//            installPlanLayout = itemView.findViewById(R.id.item_install_actual_layout);
            dianxianBianhaoTv = itemView.findViewById(R.id.dianxianBianhaotextView);
            qidianTv = itemView.findViewById(R.id.qidianTextView);
            zhongdianTv = itemView.findViewById(R.id.zhongdiantextView);
        }
    }
}
