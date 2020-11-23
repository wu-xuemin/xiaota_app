package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.net.Network;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WiresExportActivity extends AppCompatActivity {
    private static String TAG = "WiresExportActivity";

    private Network mNetwork;
    private LujingData mLujing;
    private DianXianQingceAdapter mDianXianAdapter;
    private ArrayList<DianxianQingCeData> mDianxianList;
    private RecyclerView mDxRV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wires_export);

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

        mDxRV = (RecyclerView) findViewById(R.id.rv_wires_in_export);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mDxRV.setLayoutManager(manager);
        getExportDataOfTheLujing();
    }

    //根据路径查询该路径的导出数据（按电线型号）
    private void getExportDataOfTheLujing(){
        LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
        mPostValue.put("account","NO USE"); //paths/{lujingId}/wires?serial_number={dxSN}&parts_code={dxPartsCode}
        String theUrl = Constant.getExportWiresOfLujingUrl.replace("{path_id}", String.valueOf(mLujing.getId()));
//        theUrl = theUrl.replace("{dxSN}","").replace("{dxPartsCode}","");
        Log.i(TAG,"按型号导出该路径的数据 " + theUrl);
        mNetwork.fetchExportDataOfLujing(theUrl, mPostValue, new GetSummarizeDxListOfLujingHandler());///
    }

    @SuppressLint("HandlerLeak")
    class GetSummarizeDxListOfLujingHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {

            if (msg.what == Network.OK) {
                Log.d("GetDxListOfLujingHand", "OKKK");
                mDianxianList = (ArrayList<DianxianQingCeData>) msg.obj;

                if (mDianxianList == null) {
                    Log.d(TAG, "handleMessage: " + "路径的电线数量为0或异常");
                    mDianxianList = new ArrayList<>();
                } else {
                    if (mDianxianList.size() == 0) {
                        Toast.makeText(WiresExportActivity.this, "已关联的电线数量为0！", Toast.LENGTH_SHORT).show();
                    } else {

                        mDianXianAdapter = new DianXianQingceAdapter(mDianxianList, WiresExportActivity.this,Constant.REQUEST_CODE_CALCULATE_SUMMARIZE_WIRES);
                        if (mDxRV.getItemDecorationCount() == 0)
                        {
                            mDxRV.addItemDecoration(new DividerItemDecoration(WiresExportActivity.this, DividerItemDecoration.VERTICAL));
                        }
                        mDxRV.setAdapter(mDianXianAdapter);
                        mDianXianAdapter.notifyDataSetChanged();
                    }
                }
            } else {
                String errorMsg = (String) msg.obj;
                Log.d("GetDxListOfLujingHd NG:", errorMsg);
                Toast.makeText(WiresExportActivity.this, "按型号导出失败！" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}