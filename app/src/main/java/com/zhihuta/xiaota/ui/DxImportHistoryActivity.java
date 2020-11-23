package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DxImportHistoryAdapter;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.DxImportHistoryData;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.response.DxImportHistoryResponse;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.common.RequestUrlUtility;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DxImportHistoryActivity extends AppCompatActivity {

    private static String TAG = "DxImportHistoryActivity";
    private SimpleDateFormat sf3 = new SimpleDateFormat("yy/MM/dd");
    private RecyclerView mDxHistoryRV;
    private DxImportHistoryAdapter dxImportHistoryAdapter;
    private ArrayList<DxImportHistoryData> dxImportHistoryList = new ArrayList<>();

    private Network mNetwork;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dx_import_history);

        mNetwork = Network.Instance(getApplication());
        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initDxLayout();

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

    @Override
    protected void onResume() {
        super.onResume();
        getHistoryList();
    }

    private void getHistoryList(  ) {
        //获取 路径对应的间距列表

        String url = RequestUrlUtility.build(URL.GET_DX_IMPORT_HISTORY);
        HashMap<String,String> queryValue = new HashMap<>();
        queryValue.put("project_id",Main.project_id);
        mNetwork.get(url,queryValue,new GetDxImportHistoryListHandler(Constant.FLAG_DISTANCE_IN_LUJING),
                (handler, msg)->{
                    handler.sendMessage(msg);
                });
    }
    @SuppressLint("HandlerLeak")
    class GetDxImportHistoryListHandler extends Handler {
        private String strMode = "";
        public GetDxImportHistoryListHandler(String strMode)
        {
            this.strMode = strMode;
        }
        @Override
        public void handleMessage(final Message msg) {
            String errorMsg = "";

            errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
            if(errorMsg != null)
            {
                Toast.makeText(DxImportHistoryActivity.this, "获取导入历史失败！" + errorMsg, Toast.LENGTH_SHORT).show();

                return;
            }

            Result result= (Result)(msg.obj);

            DxImportHistoryResponse responseData = CommonUtility.objectToJavaObject(result.getData(),DxImportHistoryResponse.class);

            dxImportHistoryList = new ArrayList<>();

            for (DxImportHistoryData dxImportHistory : responseData.records) {

                DxImportHistoryData dxImportHistoryData = new DxImportHistoryData();
                dxImportHistoryData.setFileName( dxImportHistory.getFileName());
                dxImportHistoryData.setId(dxImportHistory.getId());
                dxImportHistoryData.setOperator(dxImportHistory.getAccount());
                long dateValue = Long.valueOf(dxImportHistory.getOperate_time());
                dxImportHistoryData.setOperate_time( sf3.format(dateValue));
                dxImportHistoryList.add( dxImportHistoryData);
            }

            Log.d(TAG, "获取导入历史: size: " + dxImportHistoryList.size());
            if (dxImportHistoryList.size() == 0) {
                Toast.makeText(DxImportHistoryActivity.this, "导入历史为空！", Toast.LENGTH_SHORT).show();
            }

            dxImportHistoryAdapter = new DxImportHistoryAdapter(dxImportHistoryList, DxImportHistoryActivity.this);
            if (mDxHistoryRV.getItemDecorationCount() == 0){
                mDxHistoryRV.addItemDecoration(new DividerItemDecoration(DxImportHistoryActivity.this, DividerItemDecoration.VERTICAL));
            }
            mDxHistoryRV.setAdapter(dxImportHistoryAdapter);
            dxImportHistoryAdapter.notifyDataSetChanged();
        }
    }

    private void initDxLayout(){
        //间距列表
        mDxHistoryRV = (RecyclerView) findViewById(R.id.rv_dx_history);
        LinearLayoutManager manager5 = new LinearLayoutManager(this);
        manager5.setOrientation(LinearLayoutManager.VERTICAL);
        mDxHistoryRV.setLayoutManager(manager5);
        dxImportHistoryAdapter = new DxImportHistoryAdapter(dxImportHistoryList, this);
        if (mDxHistoryRV.getItemDecorationCount() == 0){
            mDxHistoryRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        }
        mDxHistoryRV.setAdapter(dxImportHistoryAdapter);

    }
}