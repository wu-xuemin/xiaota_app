package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.LujingSummarizeWiresAdapter;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.bean.basic.PathWiresPartsCodeSum;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.response.GetWiresByPartsCodeResonse;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.common.RequestUrlUtility;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;

import java.util.ArrayList;
import java.util.List;

public class LujingWiresSummarizeExportActivity extends AppCompatActivity {
    private static String TAG = "WiresExportActivity";

    private Network mNetwork;
    private LujingData mLujing;
    private LujingSummarizeWiresAdapter mWiresSummarizeAdapter;
    private List<PathWiresPartsCodeSum> mDianxianList;
    private RecyclerView mDxRV;

    private Button mExportToExcelBt;

    SwipeRefreshLayout mSummarizeWiresRefreshLayout;

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

        refreshPage();
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

        mSummarizeWiresRefreshLayout = findViewById(R.id.lujing_summarize_wires_swipeRefresh);
        CommonUtility.setDistanceToTriggerSync(mSummarizeWiresRefreshLayout,this,0.6f, 400);
        mSummarizeWiresRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPage();
            }
        });
        mExportToExcelBt = (Button)findViewById(R.id.buttonExportByModel);
        mExportToExcelBt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String url = Constant.getPathWiresExportToExcelByPartsCodeUrl.replace("{path_id}",String.valueOf(mLujing.getId()));

                //返回的直接是文件，没有Msg等
                mNetwork.getFile(url, null, new  ExportToExcelHandler(),(handler, msg)->{
                            handler.sendMessage(msg);
                        },
                        mLujing.getName()+"按型号导出的电线清册",
                        LujingWiresSummarizeExportActivity.this);

            }
        });
    }

    @SuppressLint("HandlerLeak")
    class ExportToExcelHandler extends Handler {

        private boolean bIsGetting = false;

        public boolean getIsGetting()
        {
            return bIsGetting;
        }

        public void setIsGetting(boolean getting)
        {
            bIsGetting = getting;
        }

        @Override
        public void handleMessage(final Message msg) {
//            if(mLoadingProcessDialog != null && mLoadingProcessDialog.isShowing()) {
//                mLoadingProcessDialog.dismiss();
//            }

            //////////////////////
            String errorMsg = "";

            try {
                errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
                if (errorMsg != null)
                {
                    Log.d("导出失败:", errorMsg);
                    Toast.makeText(LujingWiresSummarizeExportActivity.this, "导出失败！" + errorMsg, Toast.LENGTH_SHORT).show();

                    return;
                }

                Toast.makeText(LujingWiresSummarizeExportActivity.this, "导出成功, 请到下载目录查看！", Toast.LENGTH_SHORT).show();

            }
            catch (Exception ex)
            {
                Log.d("导出失败:", ex.getMessage());
            }
            finally {
                setIsGetting(false);
            }
        }//handle message

    }
    //根据路径查询该路径的导出数据（按电线型号）
    private void refreshPage(){

        mSummarizeWiresRefreshLayout.setRefreshing(true);

        String theUrl = RequestUrlUtility.build(URL.GET_SUMMARIZE_WIRES_OF_LUJING.replace("{path_id}", String.valueOf(mLujing.getId())));
        Log.i(TAG,"获取路径的电线清册 " + theUrl);

        mNetwork.get(theUrl,null, new GetSummarizeDxListOfLujingHandler(),(hanlder, msg)->{
            hanlder.sendMessage(msg);
        });
    }

    @SuppressLint("HandlerLeak")
    class GetSummarizeDxListOfLujingHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {

            //////////////////////
            String errorMsg = "";

            try {
                errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
                if (errorMsg != null)
                {
                    Log.d("获取路径的电线清册失败:", errorMsg);
                    Toast.makeText(LujingWiresSummarizeExportActivity.this, "获取路径的电线清册失败！" + errorMsg, Toast.LENGTH_SHORT).show();

                    return;
                }

                Result result= (Result)(msg.obj);
                GetWiresByPartsCodeResonse response = CommonUtility.objectToJavaObject(result.getData(), GetWiresByPartsCodeResonse.class);

                mDianxianList =  new ArrayList<>();
                if (response.wires != null)
                {
                    mDianxianList = response.wires;
                }

                if (mDianxianList.size() == 0) {
                    Toast.makeText(LujingWiresSummarizeExportActivity.this, "已关联的电线数量为0！", Toast.LENGTH_SHORT).show();
                }

                mWiresSummarizeAdapter = new LujingSummarizeWiresAdapter(mDianxianList, LujingWiresSummarizeExportActivity.this,Constant.REQUEST_CODE_CALCULATE_SUMMARIZE_WIRES);
                if (mDxRV.getItemDecorationCount() == 0)
                {
                    mDxRV.addItemDecoration(new DividerItemDecoration(LujingWiresSummarizeExportActivity.this, DividerItemDecoration.VERTICAL));
                }
                mDxRV.setAdapter(mWiresSummarizeAdapter);
                mWiresSummarizeAdapter.notifyDataSetChanged();

            }
            catch (Exception ex)
            {
                Log.d("获取路径的电线清册失败:", ex.getMessage());
            }
            finally {
                mSummarizeWiresRefreshLayout.setRefreshing(false);
            }
        }
    }
}