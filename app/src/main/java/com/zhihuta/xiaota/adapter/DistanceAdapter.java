package com.zhihuta.xiaota.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.DistanceData;
import com.zhihuta.xiaota.bean.basic.LujingData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DistanceAdapter extends RecyclerView.Adapter {
    private SimpleDateFormat sf3 = new SimpleDateFormat("yy/MM/dd");
    private static String TAG = "DistanceAdapter";
    private ArrayList<DistanceData> mDistanceAdapter;
    public DistanceAdapter(ArrayList<DistanceData> list) {
        mDistanceAdapter = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_distance,parent,false);
        return new DistanceAdapter.ItemView(view);

    }

    /**
     * 绑定数据
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final DistanceAdapter.ItemView itemView = (DistanceAdapter.ItemView) holder;
        //itemView.setIsRecyclable(false);//禁止复用
        if (mDistanceAdapter!=null && !mDistanceAdapter.isEmpty() && position < mDistanceAdapter.size()) {
            itemView.distanceNameTv.setText(mDistanceAdapter.get(position).getDistanceName());
            itemView.distanceNameTv.setSelected(true);//用于滚动显示

            itemView.distanceNumberTv.setText(mDistanceAdapter.get(position).getDistanceNumber());
        }else {
            Log.d(TAG, "onBindViewHolder: 没有获取到 路径 list数据");
        }
    }

    @Override
    public int getItemCount() {
        return mDistanceAdapter.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mDistanceAdapter.size()) {
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
