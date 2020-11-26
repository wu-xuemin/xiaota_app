package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.mikepenz.iconics.context.IconicsLayoutInflater2;
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

    private ArrayList<DianxianQingCeData> mDianxianHasbeenRelated;
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

    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relate_new_dx);
        Intent intent = getIntent();
        mLujing = (LujingData) intent.getExtras().getSerializable("mLujing");

        TextView textViewTitle = findViewById(R.id.textView_relate_new_lujingNameShow);
        textViewTitle.setText(mLujing.getName());

        mDianxianHasbeenRelated = new ArrayList<>();
        Serializable serializable = intent.getExtras().getSerializable("DianxianHasbeenRelated");
        if (serializable != null)
        {
            mDianxianHasbeenRelated = (ArrayList<DianxianQingCeData>)serializable;
        }

        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mNetwork = Network.Instance(getApplication());

        initViews();

        mSwipeRefreshLayout = findViewById(R.id.relat_new_wires_swipeRefresh);
        CommonUtility.setDistanceToTriggerSync(mSwipeRefreshLayout,this,0.6f, 400);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshLayout();
            }
        });

        mDxQingCeGetParameters.put("project_id",Main.project_id);

        refreshLayout();
    }

    void  refreshLayout()
    {
        mSwipeRefreshLayout.setRefreshing(true);

        String url = RequestUrlUtility.build(URL.GET_DIANXIAN_QINGCE_LIST);
        mNetwork.get(url, mDxQingCeGetParameters, new GetCandidateDxListHandler(),(handler, msg)->{
            handler.sendMessage(msg);
        });
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

        mDianxianTobeSelectList = new ArrayList<>();
        mAddBt = (Button) findViewById(R.id.button_OK_to_add_dxTobeSelect22 ); // 添加 按钮
        mBackBt = (Button) findViewById(R.id.button4 ); //返回按钮
        mAddBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 添加所有选中的电线，如何刷新列表
                 */
                for (int k = 0; k < checkedList.size(); k++) {
                    if (checkedList.get(k)) {
                        //该电线由待选 改为 已选
                        //mDianxianTobeSelectList.get(k).setFlag(Constant.FLAG_RELATED_DX);
                        mCheckedDxList.add(mDianxianTobeSelectList.get(k));
                    }

                }

                /**
                 * 在本页添加到服务端，不要传回到路径页面去。
                 */
                LinkedHashMap<String, String> postValue = new LinkedHashMap<>();
                String IDs = null;
                for (int j = 0; j < mCheckedDxList.size(); j++) { //  "wires_id":[ 814,815]
                    if (j == 0) {
                        IDs = String.valueOf(mCheckedDxList.get(j).getId());
                    } else {
                        IDs = IDs + "," + String.valueOf(mCheckedDxList.get(j).getId());
                    }
                }

                if (IDs != null && IDs.length() != 0)
                {
                    postValue.put("wires_id", IDs);
                    String theUrl = RequestUrlUtility.build(URL.PUT_DX_OF_LUJING.replace("{lujingId}",String.valueOf(mLujing.getId())));

                    mNetwork.put(theUrl,postValue,  new PutDxHandler(), (handler, msg)->{

                        handler.sendMessage(msg);
                    });
                }
                else
                {
                    Toast.makeText(RelateNewDxActivity.this, "未选择任何电线", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBackBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelateNewDxActivity.this.finish();

            }
        });

        mSearchView = (SearchView) findViewById(R.id.sv_in_relate_new_dx);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mDxQingCeGetParameters.clear();
                mDxQingCeGetParameters.put("sn", query);
                mDxQingCeGetParameters.put("project_id",Main.project_id);

                refreshLayout();

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

        @Override
        public void handleMessage(final Message msg) {

            //////////////////////
            String errorMsg = "";

            try {

                errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
                if (errorMsg != null)
                {
                    Log.d("关联电线失败:", errorMsg);
                    Toast.makeText(RelateNewDxActivity.this, "关联电线失败！" + errorMsg, Toast.LENGTH_SHORT).show();

                    return;
                }

                Toast.makeText(RelateNewDxActivity.this, "关联电线成功！", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 500);

            }
            catch (Exception ex)
            {
                Log.d("关联电线失败:", ex.getMessage());
            }
            finally {

            }
        }
    }
    @SuppressLint("HandlerLeak")
    class GetCandidateDxListHandler extends Handler {

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
                    Log.d("电线获取 NG:", errorMsg);
                    Toast.makeText(RelateNewDxActivity.this, "电线获取失败！" + errorMsg, Toast.LENGTH_SHORT).show();

                    return;
                }

                Result result= (Result)(msg.obj);

                GetWiresResponse responseData = CommonUtility.objectToJavaObject(result.getData(), GetWiresResponse.class);

                mDianxianTobeSelectList = new ArrayList<>();

                for (Wires wire : responseData.wires) {

                    boolean bFound = false;
                    for (DianxianQingCeData hasRelated : mDianxianHasbeenRelated)
                    {
                        if (hasRelated.getId() == wire.getId().intValue())
                        {
                            bFound = true;
                            break;
                        }
                    }

                    if (!bFound)
                    {
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
                }

                Log.d(TAG, "可选电线数量: size: " + mDianxianTobeSelectList.size());

                if (mDianxianTobeSelectList.size() == 0) {
                    Toast.makeText(RelateNewDxActivity.this, "可选电线数量为0！", Toast.LENGTH_SHORT).show();
                }

                for (int k = 0; k < mDianxianTobeSelectList.size(); k++) {
                    //mDianxianTobeSelectList.get(k).setFlag(Constant.FLAG_TOBE_SELECT_DX);
                    checkedList.add(false); //初始时都是未选中。
                }

                if (mDianXianToBeSelectedAdapter == null)
                {
                    mDianXianToBeSelectedAdapter = new DianXianQingceAdapter(mDianxianTobeSelectList, RelateNewDxActivity.this, Constant.REQUEST_CODE_LUJING_CANDIDATE_WIRES);
                    mDxRV.addItemDecoration(new DividerItemDecoration(RelateNewDxActivity.this, DividerItemDecoration.VERTICAL));
                    mDxRV.setAdapter(mDianXianToBeSelectedAdapter);
                    mDianXianToBeSelectedAdapter.setOnItemClickListener(MyItemClickListener);
                }
                mDianXianToBeSelectedAdapter.updateDataSoruce(mDianxianTobeSelectList);

            }
            catch (Exception ex)
            {
                Log.d("电线获取 NG:", ex.getMessage());
            }
            finally {
                mSwipeRefreshLayout.setRefreshing(false);
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
                    //Toast.makeText(RelateNewDxActivity.this,"你点击了 备选电线的 checkbox" + (position+1),Toast.LENGTH_SHORT).show();
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