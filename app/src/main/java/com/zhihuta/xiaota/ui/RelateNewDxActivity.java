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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.basic.Wires;
import com.zhihuta.xiaota.bean.response.GetWiresResponse;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.common.RequestUrlUtility;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class RelateNewDxActivity extends AppCompatActivity {

    private static String TAG = "RelateNewDxActivity";
    private ArrayList<DianxianQingCeData> mDianxianTobeSelectList;
    private Button mAddBt;
    private Button mBackBt;
    private Network mNetwork;

    private DianXianQingceAdapter mDianXianToBeSelectedAdapter;
    private  RecyclerView mDxRV;
    private ArrayList<Boolean> checkedList = new ArrayList<>(); //用于记录哪些被选中了
    // 选中的电线，
    private ArrayList<DianxianQingCeData> mCheckedDxList = new ArrayList<>();
    private LujingData mLujing;

    LinkedHashMap<String, String> mDxQingCeGetParameters = new LinkedHashMap<>();

    private SearchView mSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relate_new_dx);
        Intent intent = getIntent();
        mLujing = (LujingData) intent.getExtras().getSerializable("mLujing");
        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mNetwork = Network.Instance(getApplication());

        initViews();
//        showTobeSelectedDxList();
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

    private void initViews() {

        mDxRV = (RecyclerView) findViewById(R.id.rv_dx_tobeSelect);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mDxRV.setLayoutManager(manager);

        LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();

        mPostValue.put("meid", XiaotaApp.getApp().getIMEI());

        /// mPostValue 在后续会用到，比如不同用户，获取各自公司的电线
        mPostValue.put("project_id",Main.project_id);
        String url = RequestUrlUtility.build(URL.GET_DIANXIAN_QINGCE_LIST);
        mNetwork.get(url, mPostValue, new GetDxListHandler(),(handler, msg)->{
            handler.sendMessage(msg);
        });

        mDianxianTobeSelectList = new ArrayList<>();
        mAddBt = (Button) findViewById(R.id.button_OK_to_add_dxTobeSelect22 ); // 添加 按钮
        mBackBt = (Button) findViewById(R.id.button4 ); //返回按钮
        mAddBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 添加所有选中的电线，如何刷新列表
                 */
                for(int k=0; k< checkedList.size(); k++){
                    if(checkedList.get(k)){
                        //该电线由待选 改为 已选
                        mDianxianTobeSelectList.get(k).setFlag(Constant.FLAG_RELATED_DX);
                        mCheckedDxList.add(mDianxianTobeSelectList.get(k));
                    }

                }

                /**
                 * 在本页添加到服务端，不要传回到路径页面去。
                 */
                LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
                String IDs = null;
                for(int j=0; j<mCheckedDxList.size(); j++) { //  "wires_id":[ 814,815]
                    if(j == 0) {
                        IDs = String.valueOf(mCheckedDxList.get(j).getId());
                    } else {
                        IDs = IDs + "," + String.valueOf(mCheckedDxList.get(j).getId());
                    }
                }
                mPostValue.put("wires_id", IDs);
                String theUrl = Constant.putDxOfLujingUrl.replace("{lujingId}", String.valueOf(mLujing.getId()));
                mNetwork.putPathWires(theUrl, mPostValue, new PutDxHandler());

                ///todo 刷新列表 ？ 否则用户不清楚哪些已经加过了，或者回去电线界面时删除多余的。

            }
        });
        mBackBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelateNewDxActivity.this.finish();

            }
        });

        mSearchView = (SearchView) findViewById(R.id.sv_in_relate_new_dx);
        mSearchView.setQueryHint("查找"); //按名称
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mDxQingCeGetParameters.clear();
                mDxQingCeGetParameters.put("sn", query);
                mDxQingCeGetParameters.put("project_id",Main.project_id);

                String url = RequestUrlUtility.build(URL.GET_DIANXIAN_QINGCE_LIST);
                mNetwork.get(url, mDxQingCeGetParameters, new GetDxListHandler(),(handler, msg)->{
                    handler.sendMessage(msg);
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                if (TextUtils.isEmpty(newText))
//                    lv.clearTextFilter();
//                else
//                    lv.setFilterText(newText);
                return true;
            }
        });

        //监听整个控件
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // while a false will expand it.
                mSearchView.setIconified(false);
            }
        });

    }

    @SuppressLint("HandlerLeak")
    class PutDxHandler extends Handler {
//todo
    }
    @SuppressLint("HandlerLeak")
    class GetDxListHandler extends Handler {

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

                if (msg.what == Network.OK) {
                    Result result= (Result)(msg.obj);

                    GetWiresResponse responseData = CommonUtility.objectToJavaObject(result.getData(), GetWiresResponse.class);

                    if (responseData != null &&responseData.errorCode == 0)
                    {
                        mDianxianTobeSelectList = new ArrayList<>();

                        for (Wires wire : responseData.wires) {

                            DianxianQingCeData dianxianQingCeData = new DianxianQingCeData();
                            dianxianQingCeData.setId(wire.getId());
                            dianxianQingCeData.setEnd_point(wire.getEndPoint());
                            dianxianQingCeData.setHose_redundancy(Double.toString(wire.getHoseRedundancy()));
                            dianxianQingCeData.setLength(wire.getLength());
                            dianxianQingCeData.setParts_code(wire.getPartsCode());
                            dianxianQingCeData.setSerial_number(wire.getSerialNumber());
                            dianxianQingCeData.setStart_point(wire.getStartPoint());
                            dianxianQingCeData.setSteel_redundancy(Double.toString(wire.getSteelRedundancy()));
                            dianxianQingCeData.setWickes_cross_section(wire.getWickesCrossSection());

                            mDianxianTobeSelectList.add( dianxianQingCeData);
                        }

                        Log.d(TAG, "电线数量: size: " + mDianxianTobeSelectList.size());

                        if (mDianxianTobeSelectList.size() == 0) {
                            Toast.makeText(RelateNewDxActivity.this, "电线数量为0！", Toast.LENGTH_SHORT).show();
                        }

                        for (int k = 0; k < mDianxianTobeSelectList.size(); k++) {
                            mDianxianTobeSelectList.get(k).setFlag(Constant.FLAG_TOBE_SELECT_DX);
                            checkedList.add(false); //初始时都是未选中。
                        }

                        if (mDianXianToBeSelectedAdapter == null)
                        {
                            mDianXianToBeSelectedAdapter = new DianXianQingceAdapter(mDianxianTobeSelectList, RelateNewDxActivity.this);
                            mDxRV.addItemDecoration(new DividerItemDecoration(RelateNewDxActivity.this, DividerItemDecoration.VERTICAL));
                            mDxRV.setAdapter(mDianXianToBeSelectedAdapter);
                            mDianXianToBeSelectedAdapter.setOnItemClickListener(MyItemClickListener);
                        }
                        mDianXianToBeSelectedAdapter.updateDataSoruce(mDianxianTobeSelectList);
                    }
                    else
                    {
                        errorMsg =  "电线获取异常:"+ result.getCode() + result.getMessage();
                        Log.d(TAG, errorMsg );
                    }
                }
                else
                {
                    errorMsg = (String) msg.obj;
                }

                if (!errorMsg.isEmpty())
                {
                    Log.d("电线获取 NG:", errorMsg);
                    Toast.makeText(RelateNewDxActivity.this, "电线获取失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                Log.d("电线获取 NG:", ex.getMessage());
            }
            finally {
                setIsGetting(false);
            }

            ////////////////////////
        }
    }

    /**
     * 备选电线item 里的控件点击监听事件
     */
    private DianXianQingceAdapter.OnItemClickListener MyItemClickListener = new DianXianQingceAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, DianXianQingceAdapter.ViewName viewName, int position) {
            //viewName可区分item及item内部控件
            switch (v.getId()){

                case R.id.checkBox_dx_to_be_select:
                    Toast.makeText(RelateNewDxActivity.this,"你点击了 备选电线的 checkbox" + (position+1),Toast.LENGTH_SHORT).show();
                    //对原先的值取反，比如原先是未选中则改为选中。
                    checkedList.set(position,  !checkedList.get(position) );
                    break;
                default:
                    View view = getLayoutInflater().inflate(R.layout.dialog_dx, null);
                    final TextView tvDxSName = (TextView) view.findViewById(R.id.textView_dilag_bianhao);
                    final TextView tvDxQidian = (TextView) view.findViewById(R.id.textView_dialog_qidian);
                    final TextView tvDxZhongdian = (TextView) view.findViewById(R.id.textView15);
                    final TextView tvDxModel = (TextView) view.findViewById(R.id.textView16);
                    final TextView tvDxXinshuJiemian = (TextView) view.findViewById(R.id.textView17);
                    final TextView tvDxLength = (TextView) view.findViewById(R.id.textView18);
                    final TextView tvDxSteel = (TextView) view.findViewById(R.id.textView19);
                    final TextView tvDxHose = (TextView) view.findViewById(R.id.textView20);
                    tvDxSName.setText( mDianxianTobeSelectList.get(position).getSerial_number());
                    tvDxQidian.setText( mDianxianTobeSelectList.get(position).getStart_point());
                    tvDxZhongdian.setText( mDianxianTobeSelectList.get(position).getEnd_point());
                    tvDxModel.setText( mDianxianTobeSelectList.get(position).getParts_code());
                    tvDxXinshuJiemian.setText( mDianxianTobeSelectList.get(position).getWickes_cross_section());
                    tvDxLength.setText( mDianxianTobeSelectList.get(position).getLength());
                    tvDxSteel.setText( mDianxianTobeSelectList.get(position).getSteel_redundancy());
                    tvDxHose.setText( mDianxianTobeSelectList.get(position).getHose_redundancy());
                    AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(RelateNewDxActivity.this);
                    alertDialogBuilder2.setTitle("电线详情")
                            .setView(view)
                            .setPositiveButton("关闭",null)
//                            .setNegativeButton("OK", null)
                            .show();
                    break;
            }
        }

        @Override
        public void onItemLongClick(View v) {

        }
    };
}