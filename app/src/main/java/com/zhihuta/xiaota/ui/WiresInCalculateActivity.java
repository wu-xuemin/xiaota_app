package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.adapter.LujingAdapter;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.net.Network;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WiresInCalculateActivity extends AppCompatActivity {

    private static String TAG = "WiresInCalculateActivity";

    private Network mNetwork;
    private LujingData mLujing;
    private DianXianQingceAdapter mDianXianAdapter;
    private ArrayList<DianxianQingCeData> mDianxianList;
    private RecyclerView mDxRV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wires_in_calculate);

        mNetwork = Network.Instance(getApplication());
        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getDataFromPrev();
        initViews();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDataFromPrev() {

        Intent intent = getIntent();
        mLujing = (LujingData) intent.getExtras().getSerializable("mLujingToPass");
        Log.i(TAG,"获取的路径：" + mLujing.getName());
        setTitle( mLujing.getName() +"的电线清册");
    }

    private void initViews() {

        mDxRV = (RecyclerView) findViewById(R.id.rv_wires_in_cal);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mDxRV.setLayoutManager(manager);
        getWiresOftheLujing();
          }
    private void getWiresOftheLujing(){
        /**
         * 获取该路径的电线列表
         */
        LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
        mPostValue.put("account","NO USE"); //paths/{lujingId}/wires?serial_number={dxSN}&parts_code={dxPartsCode}
        String theUrl = Constant.getDxOfLujingInCaculateUrl.replace("{path_id}", String.valueOf(mLujing.getId()));

        Log.i(TAG,"获取该路径的电线列表 " + theUrl);
        mNetwork.fetchDxListOfLujing(theUrl, mPostValue, new GetDxListOfLujingHandler());///
    }

    @SuppressLint("HandlerLeak")
    class GetDxListOfLujingHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {

            if (msg.what == Network.OK) {
                Log.d("GetDxListOfLujingHand", "OKKK");
                mDianxianList = (ArrayList<DianxianQingCeData>)msg.obj;

                if (mDianxianList == null) {
                    Log.d(TAG, "handleMessage: " + "路径的电线数量为0或异常"  );
                    mDianxianList = new ArrayList<>();
                } else {
                    if (mDianxianList.size() == 0) {
                        Toast.makeText(WiresInCalculateActivity.this, "已关联的电线数量为0！", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int k = 0; k < mDianxianList.size(); k++) {
                            mDianxianList.get(k).setFlag(Constant.FLAG_RELATED_DX);
                        }
                        mDianXianAdapter = new DianXianQingceAdapter(mDianxianList, WiresInCalculateActivity.this);
                        mDxRV.addItemDecoration(new DividerItemDecoration(WiresInCalculateActivity.this, DividerItemDecoration.VERTICAL));
                        mDxRV.setAdapter(mDianXianAdapter);
                        mDianXianAdapter.notifyDataSetChanged();

                        mDianXianAdapter.setOnItemClickListener(MyItemClickListenerDx);
                        mDianXianAdapter.updateDataSoruce(mDianxianList);
                    }
                }
            } else {
                String errorMsg = (String)msg.obj;
                Log.d("GetDxListOfLujingHd NG:", errorMsg);
                Toast.makeText(WiresInCalculateActivity.this, "电线获取失败！" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDxList(){
        //电线列表
        mDianXianAdapter = new DianXianQingceAdapter(mDianxianList,this);
        mDxRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mDxRV.setAdapter(mDianXianAdapter);

        // 设置item及item中控件的点击事件
        mDianXianAdapter.setOnItemClickListener(MyItemClickListenerDx);
        mDianXianAdapter.updateDataSoruce(mDianxianList);
    }
    /**
     * 路径Adapter里item的控件点击监听事件
     */
    private DianXianQingceAdapter.OnItemClickListener MyItemClickListenerDx = new DianXianQingceAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, DianXianQingceAdapter.ViewName viewName, int position) {
            //viewName可区分item及item内部控件
            switch (v.getId()){
                case R.id.dianxianBianhaotextView:
                case R.id.qidianTextView:
                case R.id.zhongdiantextView:
                    View view = getLayoutInflater().inflate(R.layout.dialog_dx, null);
                    final TextView tvDxSName = (TextView) view.findViewById(R.id.textView_dilag_bianhao);
                    final TextView tvDxQidian = (TextView) view.findViewById(R.id.textView_dialog_qidian);
                    final TextView tvDxZhongdian = (TextView) view.findViewById(R.id.textView15);
                    final TextView tvDxModel = (TextView) view.findViewById(R.id.textView16);
                    final TextView tvDxXinshuJiemian = (TextView) view.findViewById(R.id.textView17);
                    final TextView tvDxLength = (TextView) view.findViewById(R.id.textView18);
                    final TextView tvDxSteel = (TextView) view.findViewById(R.id.textView19);
                    final TextView tvDxHose = (TextView) view.findViewById(R.id.textView20);
                    tvDxSName.setText(  mDianxianList.get(position).getSerial_number());
                    tvDxQidian.setText( mDianxianList.get(position).getStart_point());
                    tvDxZhongdian.setText( mDianxianList.get(position).getEnd_point());
                    tvDxModel.setText( mDianxianList.get(position).getParts_code());
                    tvDxXinshuJiemian.setText( mDianxianList.get(position).getWickes_cross_section());
                    tvDxLength.setText( mDianxianList.get(position).getLength());
                    tvDxSteel.setText( mDianxianList.get(position).getSteel_redundancy());
                    tvDxHose.setText( mDianxianList.get(position).getHose_redundancy());
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WiresInCalculateActivity.this);
                    alertDialogBuilder.setTitle("电线详情")
                            .setView(view)
                            .setPositiveButton("关闭",null)
//                            .setNegativeButton("OK", null)
                            .show();
                    break;
                default:
                    Toast.makeText(WiresInCalculateActivity.this,"你点击了item按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onItemLongClick(View v) {

        }
    };
}