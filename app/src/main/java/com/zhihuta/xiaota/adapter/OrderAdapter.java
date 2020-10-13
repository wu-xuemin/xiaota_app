package com.zhihuta.xiaota.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.OrderData;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter {

    private static String TAG = "OrderAdapter";
    private ArrayList<OrderData> mOrderAdapter;
    public OrderAdapter(ArrayList<OrderData> list) {
        mOrderAdapter = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order,parent,false);
        return new OrderAdapter.ItemView(view);

    }
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//    }

    /**
     * 绑定数据
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final OrderAdapter.ItemView itemView = (OrderAdapter.ItemView) holder;
        //itemView.setIsRecyclable(false);//禁止复用
        if (mOrderAdapter!=null && !mOrderAdapter.isEmpty() && position < mOrderAdapter.size()) {
            itemView.orderNumberTv.setText(mOrderAdapter.get(position).getOrderNumber());
            itemView.orderNumberTv.setSelected(true);//用于滚动显示
            itemView.orderCreatedDateTv.setText(mOrderAdapter.get(position).getCreatedDate().toString());
            itemView.orderCreaterTv.setText(mOrderAdapter.get(position).getOrderCreater());
            itemView.orderStatusTv.setText(mOrderAdapter.get(position).getOrderStatus());
//            itemView.headCountDoneTv.setText(""+mInstallPlanAdapter.get(position).getHeadCountDone());
//            itemView.cmdInfoTv.setText(""+mInstallPlanAdapter.get(position).getCmtSend());
//            itemView.cmdInfoTv.setSelected(true);//用于滚动显示
        }else {
            Log.d(TAG, "onBindViewHolder: 没有获取到 订单 list数据");
        }
    }

    @Override
    public int getItemCount() {
        return mOrderAdapter.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mOrderAdapter.size()) {
            return 1;
        } else {
            return 0;
        }
    }
    public class ItemView extends RecyclerView.ViewHolder {
//        CardView installPlanLayout;
        TextView orderNumberTv;
        TextView orderCreatedDateTv;
        TextView orderCreaterTv;
        TextView orderStatusTv;

        ItemView(View itemView) {
            super(itemView);
//            installPlanLayout = itemView.findViewById(R.id.item_install_actual_layout);
            orderNumberTv = itemView.findViewById(R.id.orderNumbertextView);
            orderCreatedDateTv = itemView.findViewById(R.id.orderCreatedDateTextView);
            orderCreaterTv = itemView.findViewById(R.id.orderCreaterTextView);
            orderStatusTv = itemView.findViewById(R.id.orderStatusTextView);
        }
    }
}
