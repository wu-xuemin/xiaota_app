package com.zhihuta.xiaota.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.LayoutInflaterCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.internal.LinkedTreeMap;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DistanceAdapter;
import com.zhihuta.xiaota.adapter.LujingAdapter;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.bean.basic.DistanceData;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.basic.Wires;

import com.zhihuta.xiaota.bean.response.BaseResponse;
import com.zhihuta.xiaota.bean.response.GetDistanceResponse;
import com.zhihuta.xiaota.bean.response.GetWiresResponse;
import com.zhihuta.xiaota.bean.response.LoginResponseData;
import com.zhihuta.xiaota.bean.response.NewPathDistanceQRsResponse;
import com.zhihuta.xiaota.bean.response.PathGetDistanceQr;
import com.zhihuta.xiaota.bean.response.PathGetObject;
import com.zhihuta.xiaota.bean.response.PathsResponse;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.common.RequestUrlUtility;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;
import com.zhihuta.xiaota.util.ShowMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

//public class DianxianQingCe extends AppCompatActivity {
//FragmentActivity 无法创建菜单.
public class Main extends AppCompatActivity implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate, QRCodeView.Delegate {

    public static String project_id = "0";

    private static String TAG = "Main";
    private String tabFlag = "在路径模型"; // 还有： 在电线清册、在计算中心
    //声明3个Tab的布局文件
    private LinearLayout mTabDxQingce;
    //    private LinearLayout mTabFrd;
    private LinearLayout mTabLujingMoxing;
    private LinearLayout mTabJisuan;

    //声明3个Tab的ImageButton
    private ImageButton mQingceImg;
    private ImageButton mLujingMoxingImg;
    private ImageButton mJisuanImg;

    //声明3个Tab分别对应的Fragment
    private Fragment mFragDxQingce;
    private Fragment mFragLujingMoxing;
    private Fragment mFragJisuan;

    private LinearLayout mLayoutQingCe;
    private LinearLayout mLayoutLujing;
    private LinearLayout mLayoutCompute; //计算页面， 包含 计算路径的电线长度、计算两点间距 两部分。

    // 计算路径的电线长度
    private LinearLayout mLayoutComputeDx;
    // 计算两点的间距长度
    private RelativeLayout mLayoutComputeDistance;
    // "计算路径的电线长度" 的按钮
    private Button mComputeDxBt;
    // "计算两点间距" 的按钮
    private Button mComputeDistanceBt;

    //电线清册 重置
    private Button getmResetInDxQingceBt;
    //电线清册 查找
    private SearchView mSearchViewDxQingce;
    // 电线 "手动添加" 按钮
    private Button addDxByHandBt;
    // 电线 "从文件导入" 按钮
    private Button addDxFromFileBt;
    // 查看导入记录
    private Button dxImportHistoryBt;
    // 建全新路径 按钮
    private Button addTotalNewLujingBt;

    private int  mNewPathchoice = 0;

    private Button mLujingScanBt; //路径主界面的扫码按钮，用于 筛选出需要查看或者编辑的路径, 扫的越多码筛选出的路径越精确.
    private Button mComputeScanBt; //计算中心主界面的扫码按钮
    private Button mLujingResetBt; //路径主界面的重置按钮
    private SearchView mLujingSearchView;

    private DianXianQingceAdapter mDxQingceAdapter;
    private ArrayList<DianxianQingCeData> mDianxianQingCeList = new ArrayList<>();
    private RecyclerView mQingceRV;
    private RecyclerView mLujingRV;
    private RecyclerView mLujingInCalculateRV;

    private DividerItemDecoration mDividerItemDecoration; 			//路径模型列表里的间隔
    private DividerItemDecoration mDividerItemDecorationInCalcuate;	//计算中心列表里的间隔

//    private OrderAdapter mOrderAdapter;
//    private ArrayList<OrderData> mOrderList = new ArrayList<>();

    private LujingAdapter mLujingAdapter;               //路径模型的路径的Adapter
    private LujingAdapter mLujingInCalculateAdapter;    //计算中心的路径的Adapter
    private ArrayList<LujingData> mLujingList = new ArrayList<>();
//    private ArrayList<LujingData> mLujingListInCalculate = new ArrayList<>(); //不需要用不同list,只要有不同adapter
    private LujingData mLujingToPass = new LujingData(); //传给下个页面的路径数据

    private Intent scanIntent;
    private String mStrNewPathName;

    private int mRequestCode = 0;

    HashMap<String, String> mLujingGetParameters = new HashMap<>();
    HashMap<String, String> mLujingCaculateGetParameters = new HashMap<>();

    LinkedHashMap<String, String> mDxQingCeGetParameters = new LinkedHashMap<>();

    private Network mNetwork;
    private GetDistanceListHandler  getDistanceListHandler;

    /**
     * 以下，主界面-计算中心-计算两点距离
     */
    private ZXingView mQRCodeView;

    private Button mContinueScanBt;
    private TextView mDisplayScanResultTv;
    private Button mResetInCaculateBt;          //计算中心-重置按钮 （重置清零查询路径的结果）
    private SearchView mSearchViewInCalculate;  //计算中心-按名称查找按钮

    private RecyclerView mDistanceRV;
    private DistanceAdapter mDistanceAdapter;
    private ArrayList<DistanceData> mDistanceList = new ArrayList<>();           //计算两点距离时，扫码所得的间距
    private ArrayList<DistanceData> mScanResultDistanceList = new ArrayList<>(); //路径中心，筛选路径所用
    private TextView textViewShowSumOfDistances; // 总长
    private Button mResetScanResultInCaculateBt;          //计算中心-计算两点距离-重新扫码  （重置清零扫码的结果）
    private Button mSetDistanceLengthInCaculateBt;          //计算中心-计算两点距离-设置值
    private DistanceData currentDistanceData;               //扫码得到的当前二维码，最新扫到的。

    LoginResponseData loginResponseData;

    boolean mConfirmedExit = false;

    SwipeRefreshLayout mCurrentSwipeRefreshLayout;
    SwipeRefreshLayout mWiresRefreshLayout;
    SwipeRefreshLayout mLujingRefreshLayout;
    SwipeRefreshLayout mCaculateLujingRefreshLayout;

    //进入的是哪个项目
    //private ProjectData mProject;

    //******method******/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        //get login infor from previous activity
        Intent intent = getIntent();
        String strLoginResponseJson = (String) intent.getExtras().getSerializable("loginResponseData");
        loginResponseData =JSON.parseObject(strLoginResponseJson, LoginResponseData.class);
        //

        //get project_id from project center.

        Serializable  projectSerialable = intent.getExtras().getSerializable("project_id");
        if (projectSerialable != null)
        {
            project_id = projectSerialable.toString();
        }
        //

        mNetwork = Network.Instance(getApplication());
        getDistanceListHandler = new GetDistanceListHandler();
        initViews();//初始化控件
        initEvents();//初始化事件

		//限定能获取到的范围，
		// /*try_scope:  0= only itself, 1 = department, 2=company,3=all compay*/
        mDxQingCeGetParameters.put("project_id", Main.project_id);
        mLujingGetParameters.put("project_id", Main.project_id);
        mLujingCaculateGetParameters.put("project_id", Main.project_id);


        initViewsLujing();
        initViewsCompute();
        initComputeScan();
        initCalculateDistanceList();

        initRefreshLayout();

        //after init ,select the default tab
        tabFlag = "";
        selectTab(R.id.id_tab_lujing_moxing);//默认选中第2个Tab
    }

    void initRefreshLayout()
    {
        mWiresRefreshLayout = findViewById(R.id.wires_swipeRefresh);
        CommonUtility.setDistanceToTriggerSync(mWiresRefreshLayout,this,0.6f, 400);
        mWiresRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPage(true);
            }
        });

        mLujingRefreshLayout = findViewById(R.id.lujing_swipeRefresh);
        CommonUtility.setDistanceToTriggerSync(mLujingRefreshLayout,this,0.6f, 400);
        mLujingRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPage(true);
            }
        });

        mCaculateLujingRefreshLayout = findViewById(R.id.caculate_lujing_swipeRefresh);
        CommonUtility.setDistanceToTriggerSync(mCaculateLujingRefreshLayout,this,0.6f, 400);
        mCaculateLujingRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPage(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //menu.add(Menu.NONE,  Menu.FIRST+1 , 0, "设置").setIcon(R.drawable.setting);
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case android.R.id.home:
//                this.finish(); // back button
//                return true;

            case R.id.personal_info:

                Intent intentPersonal = new Intent(this, PersonalInfoActivity.class);
                String strResponseData2 = JSON.toJSONString(loginResponseData);
                intentPersonal.putExtra("loginResponseData", (Serializable)strResponseData2);
                startActivityForResult(intentPersonal, 1);
                break;
            case R.id.projects_info:

                Intent intentProjects = new Intent(this, ProjectsCenterActivity.class);
                String strResponseData = JSON.toJSONString(loginResponseData);
                intentProjects.putExtra("loginResponseData", (Serializable)strResponseData);

                startActivity(intentProjects);
                break;
            case R.id.logout:

                String url = RequestUrlUtility.build(URL.USER_LOGOUT.replace("{account_id}", Integer.toString(loginResponseData.getId())));
                mNetwork.delete(url,null,new Handler(),(handler, msg)->{
                    XiaotaApp.getApp().ClearCookieStore();
                    if (handler!= null)
                    {
                        handler.sendMessage(msg);
                    }
                    //do not care about the response from server.
                });

                Intent intent = new Intent(this, LoginActivity.class);

                strResponseData = JSON.toJSONString(loginResponseData);
                intent.putExtra("loginResponseData", (Serializable)strResponseData);
                startActivity(intent);
                finish();//销毁此Activity

                break;

            default:
        }
        return super.onOptionsItemSelected(item);
    }
    //计算两点距离 的界面初始化
    private void initComputeScan(){

        mQRCodeView = (ZXingView) findViewById(R.id.zxingview_in_calculate);
        mQRCodeView.setDelegate(this);
        mDisplayScanResultTv = (TextView) findViewById(R.id.textView_display_scan_result_in_calculate);

        mResetScanResultInCaculateBt = (Button) findViewById(R.id.button_scan_again);
        mResetScanResultInCaculateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewShowSumOfDistances.setText("0");
                mDisplayScanResultTv.setText("");
                mDistanceList.clear();
                mScanResultDistanceList.clear();
                mDistanceAdapter.notifyDataSetChanged();

                startScan();
                mSetDistanceLengthInCaculateBt.setEnabled(false);
            }
        });

        textViewShowSumOfDistances = (TextView)findViewById(R.id.textView11);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {

        boolean bNeedReTry = true;

        try {

            mSetDistanceLengthInCaculateBt.setEnabled(true);

            //扫描得到结果震动一下表示
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(200);
            }

            // 解析数据，并填入
            try {
                currentDistanceData = JSONObject.parseObject(result, DistanceData.class);
                if(currentDistanceData == null){
                    Log.i(TAG, "二维码格式不正确");
                    Toast.makeText(Main.this, "二维码格式不正确：" + result, Toast.LENGTH_SHORT).show();

                    return;
                }
            }
            catch (Exception ex)
            {
                Log.i(TAG, "二维码解析异常");
                Toast.makeText(Main.this, "二维码解析异常：" + result, Toast.LENGTH_SHORT).show();

                return;
            }

            /**
             * 把二维码 累积起来，用于退出时筛选路径
             */
            for (DistanceData distanceData1 : mScanResultDistanceList) {
                if(distanceData1.getSerial_number().equals( currentDistanceData.getSerial_number())){
                    Toast.makeText(Main.this, "扫到码重复了,忽略", Toast.LENGTH_SHORT).show();
                    return;
                }
            }


            String rst = result.replace("distance","长度");
            rst = rst.replace("preset_name","展现名称");
            rst = rst.replace("qr_id","Id号");
            rst = rst.replace("qr_name","名称");
            rst = rst.replace("serial_number","序列号");
            rst = rst.replace("type","类型");

            if(currentDistanceData.getType().equals("0")){
                rst = rst.replace("类型\":0","类型\": 固定码");
            } else if(currentDistanceData.getType().equals("1")){
                rst = rst.replace("类型\":1","类型: 通用码");
            } else {
                //不动
                Toast.makeText(Main.this, "扫到不支持的码" + result, Toast.LENGTH_SHORT).show();

                return;
            }
            mDisplayScanResultTv.setText(rst);

            mScanResultDistanceList.add(currentDistanceData);
            if(mScanResultDistanceList.size() >1){
                //扫到第二个码时,程序自动将两个码之间所有的码自动添加进列表, 方便查看

                bNeedReTry = false;
                stopScan();

                Log.i(TAG,"扫到第二个码" + mScanResultDistanceList.get(0).getName() +"," + mScanResultDistanceList.get(1).getName());

                String url = RequestUrlUtility.build(URL.GET_DISTANCE_LIST_BY_TWO_DISTANCE
                        .replace("qrId1",String.valueOf( mScanResultDistanceList.get(0).getQr_id()))
                        .replace("qrId2",String.valueOf( mScanResultDistanceList.get(1).getQr_id()))
                        .replace("{project_id}",Main.project_id)
                );
                mNetwork.get(url,null,getDistanceListHandler,
                        (handler, msg)->{
                            getDistanceListHandler.sendMessage(msg);
                        });


            }
        }
        catch (Exception ex)
        {

        }
        finally {

            if (bNeedReTry)
            {
                //获取结果后三秒后，重新开始扫描
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mQRCodeView.startSpotAndShowRect();
                    }
                }, 3000);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }

    //计算中心-计算两点距离-扫描的结果
    private void initCalculateDistanceList(){
        //间距列表
        mDistanceRV = (RecyclerView) findViewById(R.id.rv_distance_in_calculate);
        LinearLayoutManager manager5 = new LinearLayoutManager(this);
        manager5.setOrientation(LinearLayoutManager.VERTICAL);
        mDistanceRV.setLayoutManager(manager5);
        mDistanceAdapter = new DistanceAdapter(mDistanceList, this);
        if (mDistanceRV.getItemDecorationCount() == 0)
        {
            mDistanceRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        }
        mDistanceRV.setAdapter(mDistanceAdapter);

        // 设置item及item中控件的点击事件
//        mDistanceAdapter.setOnItemClickListener(MyItemClickListener);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        /**
         * 每次登录都最新，切从其他页面返回也会刷新，暂时没必要下拉刷新了
         */
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    @SuppressLint("HandlerLeak")
    class GetLujingListHandler extends Handler {
        public  String strMode = "";
        public GetLujingListHandler(String strMode)
        {
            this.strMode = strMode;
        }
        @Override
        public void handleMessage(final Message msg) {

            try {

                String errorMsg = "";
                errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
                if (errorMsg != null)
                {
                    Log.d("路径获取 NG:", errorMsg);
                    Toast.makeText(Main.this, "路径获取失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                    return;
                }

                Result result= (Result)(msg.obj);
                PathsResponse responseData = CommonUtility.objectToJavaObject(result.getData(), PathsResponse.class);
                mLujingList = new ArrayList<>();

                for (PathGetObject pathObj : responseData.paths) {

                    LujingData lujingData = new LujingData();
                    lujingData.setId(pathObj.id);
                    lujingData.setName(pathObj.name);
                    lujingData.setCreator(pathObj.creator);
                    //lujingData.setLujingCaozuo(pathObj.);
                    lujingData.setCreate_time(pathObj.createTime);

                    mLujingList.add(lujingData);
                }

                Log.d(TAG, "获取路径: size: " + mLujingList.size());

                if (mLujingList.size() == 0) {
                    Toast.makeText(Main.this, "路径数量为0！", Toast.LENGTH_SHORT).show();
                }

                String strModeIdentifier = "";
                if (strMode.equals("在计算中心"))////在计算中心,在路径模型,在电线清册
                {
                    strModeIdentifier = Constant.FLAG_LUJING_IN_CALCULATE;
                    if (mLujingInCalculateAdapter == null)
                    {
                        mLujingInCalculateAdapter = new LujingAdapter(mLujingList, Main.this, strModeIdentifier);
                        mDividerItemDecorationInCalcuate = null;
                        mDividerItemDecorationInCalcuate = new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL);
                        mLujingInCalculateRV.addItemDecoration(mDividerItemDecorationInCalcuate);
                        mLujingInCalculateRV.setAdapter(mLujingInCalculateAdapter);

                        // 设置item及item中控件的点击事件
                        mLujingInCalculateAdapter.setOnItemClickListener(MyItemClickListener); /// adapter的 item的监听
                    }

                    if (mLujingInCalculateAdapter != null && !mLujingInCalculateAdapter.getStrMode().equals(tabFlag)) {//mode is switched,
                        mLujingInCalculateAdapter.setOnItemClickListener(null);
                        mLujingInCalculateRV.removeItemDecoration(mDividerItemDecorationInCalcuate);
                        mDividerItemDecorationInCalcuate = null;
                        mDividerItemDecorationInCalcuate = new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL);
                        mLujingInCalculateAdapter = new LujingAdapter(mLujingList, Main.this, strModeIdentifier);

                        mLujingInCalculateRV.setAdapter(mLujingInCalculateAdapter);
                        mLujingInCalculateAdapter.setOnItemClickListener(MyItemClickListener); /// adapter的 item的监听
                        mLujingInCalculateAdapter.updateDataSource(mLujingList, strModeIdentifier);
                    }


                } else if (strMode.equals("在路径模型")) {

                    strModeIdentifier = Constant.FLAG_LUJING_IN_LUJING;

                    if (mLujingAdapter == null)
                    {
                        mLujingAdapter = new LujingAdapter(mLujingList, Main.this, strModeIdentifier);
                        mDividerItemDecoration=null;
                        mDividerItemDecoration = new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL);
                        mLujingRV.addItemDecoration(mDividerItemDecoration);
                        mLujingRV.setAdapter(mLujingAdapter);

                        // 设置item及item中控件的点击事件
                        mLujingAdapter.setOnItemClickListener(MyItemClickListener); /// adapter的 item的监听
                    }

                    if (mLujingAdapter != null && !mLujingAdapter.getStrMode().equals(tabFlag)) {//mode is switched,
                        mLujingAdapter.setOnItemClickListener(null);
                        mLujingRV.removeItemDecoration(mDividerItemDecoration);
                        mDividerItemDecoration = new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL);
                        mLujingAdapter = new LujingAdapter(mLujingList, Main.this, strModeIdentifier);
                        mLujingRV.addItemDecoration(mDividerItemDecoration);
                        mLujingRV.setAdapter(mLujingAdapter);

                        // 设置item及item中控件的点击事件
                        mLujingAdapter.setOnItemClickListener(MyItemClickListener); /// adapter的 item的监听
                        mLujingAdapter.updateDataSource(mLujingList, strModeIdentifier);
                    }
                }
            }
            catch (Exception ex)
            {

            }
            finally {
                mCurrentSwipeRefreshLayout.setRefreshing(false);
            }

        }
    }

    //计算两点距离-扫2个码，得到两个码之间所有的码，如果两个码在不同路径上，返回为空
    @SuppressLint("HandlerLeak")
    class GetDistanceListHandler extends Handler {

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

            String errorMsg = "";
            errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
            if (errorMsg != null)
            {
                Log.d("路径获取 NG:", errorMsg);
                Toast.makeText(Main.this, "路径获取失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                return;
            }

            Result result= (Result)(msg.obj);

            GetDistanceResponse responseData = CommonUtility.objectToJavaObject(result.getData(), GetDistanceResponse.class);

            mDistanceList = new ArrayList<>();
            int sumOfDistances = 0;
            for (PathGetDistanceQr distanceObj : responseData.qr_list) {
                DistanceData distanceData = new DistanceData();
                distanceData.setQr_id( distanceObj.qrId );
                distanceData.setDistance(String.valueOf(distanceObj.distance));
                distanceData.setName(distanceObj.name);
                distanceData.setQr_sequence(distanceObj.qrSequence);
                String strSerialNumber = distanceObj.serialNumber;
                //strSerialNumber = strSerialNumber.substring(0,distanceObj.serialNumber.length()-4);
                distanceData.setSerial_number(strSerialNumber);
                distanceData.setFlag(Constant.FLAG_DISTANCE_IN_CALCULATE);

                mDistanceList.add( distanceData);
                sumOfDistances =   (int) (sumOfDistances + distanceObj.distance);
            }

            if (mDistanceList.size() == 0) {
                Toast.makeText(Main.this, "间距数量为0！", Toast.LENGTH_SHORT).show();
            }

            mDistanceAdapter = new DistanceAdapter(mDistanceList, Main.this);
            if (mDistanceRV.getItemDecorationCount() == 0)
            {
                mDistanceRV.addItemDecoration(new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL));
            }
            mDistanceRV.setAdapter(mDistanceAdapter);
            mDistanceAdapter.notifyDataSetChanged();

            textViewShowSumOfDistances.setText(String.valueOf(sumOfDistances));
            textViewShowSumOfDistances.setTextColor(Color.rgb(255, 0, 0));
            // 设置item及item中控件的点击事件
//                    mDistanceAdapter.setOnItemClickListener(MyItemClickListener); /// adapter的 item的监听
            // 计算完一次 就清理
            mScanResultDistanceList.clear();

            setIsGetting(false);
        }
    }
    @SuppressLint("HandlerLeak")
    class DeleteLujingHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {
//            if(mLoadingProcessDialog != null && mLoadingProcessDialog.isShowing()) {
//                mLoadingProcessDialog.dismiss();
//            }
//删除路径

            if (msg.what == Network.OK) {
                Log.d("DeleteLujingHandler", "OKKK");
                //删除后真实刷新列表
                //remove the one and use the filter to get the lujing list again.

                refreshPage(true);
//                mNetwork.get(RequestUrlUtility.build(URL.GET_LUJING_LIST), mLujingGetParameters, new GetLujingListHandler( tabFlag ),(handler, msg2)->{
//                    handler.sendMessage(msg2);
//                });

            } else {
                String errorMsg = (String) msg.obj;
                Log.d("DeleteLujingHandler NG:", errorMsg);
                Toast.makeText(Main.this, "路径删除失败！" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class GetDxListHandler extends Handler {

        private int mRequestCode=0;

        public GetDxListHandler( int requestCode)
        {
            this.mRequestCode=requestCode;
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
                    Log.d("电线获取 NG:", errorMsg);
                    Toast.makeText(Main.this, "电线获取失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                    return;
                }

                Result result= (Result)(msg.obj);

                GetWiresResponse responseData = CommonUtility.objectToJavaObject(result.getData(), GetWiresResponse.class);

                mDianxianQingCeList = new ArrayList<>();

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

                    mDianxianQingCeList.add( dianxianQingCeData);
                }

                Log.d(TAG, "电线数量: size: " + mDianxianQingCeList.size());

                if (mDianxianQingCeList.size() == 0) {
                    Toast.makeText(Main.this, "电线数量为0！", Toast.LENGTH_SHORT).show();
                }

                if (mDxQingceAdapter == null)
                {
                    mDxQingceAdapter = new DianXianQingceAdapter(mDianxianQingCeList, Main.this,mRequestCode);
                    mQingceRV.addItemDecoration(new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL));
                    mQingceRV.setAdapter(mDxQingceAdapter);
                }
                mDxQingceAdapter.setOnItemClickListener(MyItemClickListenerDx);
                mDxQingceAdapter.updateDataSoruce(mDianxianQingCeList);
            }
            catch (Exception ex)
            {
                Log.d("电线获取 NG:", ex.getMessage());
            }
            finally {
                mCurrentSwipeRefreshLayout.setRefreshing(false);
            }
        }//handle message

    }

    @SuppressLint("HandlerLeak")
    class CloneLujingHandler extends Handler {

        private String  newPathName;

        CloneLujingHandler(String newPathName)
        {
            this.newPathName = newPathName;
        }

        @Override
        public void handleMessage(final Message msg) {
//            if(mLoadingProcessDialog != null && mLoadingProcessDialog.isShowing()) {
//                mLoadingProcessDialog.dismiss();
//            }

            String errorMsg = "";
            errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
            if (errorMsg!= null)
            {
                Log.d("创建路径失败 NG:", errorMsg);
                Toast.makeText(Main.this, "创建路径失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                return;
            }

            Result result= (Result)(msg.obj);

            NewPathDistanceQRsResponse responseData = CommonUtility.objectToJavaObject(result.getData(), NewPathDistanceQRsResponse.class);

            int newPathId = responseData.id;

            LujingData lujingData = new LujingData();

            lujingData.setId(newPathId);
            lujingData.setName(newPathName);

            FinalGotoLujingActivity(Constant.REQUEST_CODE_MODIFY_LUJING, lujingData);
        }
    }

    @SuppressLint("HandlerLeak")
    class SetQrDistanceHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {

          if (msg.what == Network.OK) {
            Log.d("SetQrDistanceHandler", "OKKK");

              Toast.makeText(Main.this, "设置长度OK！", Toast.LENGTH_SHORT).show();

        } else {
            String errorMsg = (String) msg.obj;
            Log.d(TAG, errorMsg);
            Toast.makeText(Main.this, "设置长度失败！" + errorMsg, Toast.LENGTH_SHORT).show();
        }
    }
    }
    private void initEvents() {
        //初始化3个Tab的点击事件
        mTabDxQingce.setOnClickListener(this);
        mTabLujingMoxing.setOnClickListener(this);
        mTabJisuan.setOnClickListener(this);
    }

    private void initViews() {
        //初始化3个Tab的布局文件
        mTabDxQingce = (LinearLayout) findViewById(R.id.id_tab_wirelist);
        mTabLujingMoxing = (LinearLayout) findViewById(R.id.id_tab_lujing_moxing);
        mTabJisuan = (LinearLayout) findViewById(R.id.id_tab_caculatecenter);

        //初始化3个ImageButton
        mQingceImg = (ImageButton) findViewById(R.id.id_tab_cx_qingce_img);
        mLujingMoxingImg = (ImageButton) findViewById(R.id.id_tab_lujing_moxing_img);
        mJisuanImg = (ImageButton) findViewById(R.id.id_tab_setting_img);

        addDxByHandBt = (Button) findViewById(R.id.button_add_dx_by_hand);
        addDxByHandBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, AddDxQingCeByHandActivity.class);
                startActivity(intent);
            }
        });

        getmResetInDxQingceBt = (Button) findViewById(R.id.button_reset_dxQingCe);
        getmResetInDxQingceBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDxQingCeGetParameters.clear();  //重置查询条件
                mDxQingCeGetParameters.put("project_id",Main.project_id);
//                String url = RequestUrlUtility.build(URL.GET_DIANXIAN_QINGCE_LIST);
//                mNetwork.get(url, mDxQingCeGetParameters, new GetDxListHandler(Constant.REQUEST_CODE_DIANXIANQINCE_WIRES),(handler, msg)->{
//                    handler.sendMessage(msg);
//                });

                refreshPage(true);
            }
        });

        mSearchViewDxQingce = (SearchView) findViewById(R.id.sv_dxQingce);
        mSearchViewDxQingce.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mDxQingCeGetParameters.clear();
                mDxQingCeGetParameters.put("sn", query);
                mDxQingCeGetParameters.put("project_id", Main.project_id);

                refreshPage(true);
//                String url = RequestUrlUtility.build(URL.GET_DIANXIAN_QINGCE_LIST);
//                mNetwork.get(url, mDxQingCeGetParameters, new GetDxListHandler(Constant.REQUEST_CODE_DIANXIANQINCE_WIRES),(handler, msg)->{
//                    handler.sendMessage(msg);
//                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText))
                {
                    mDxQingCeGetParameters.clear();
                    mDxQingCeGetParameters.put("project_id",Main.project_id);

                    refreshPage(true);
                }
                return false;
            }
        });
        //监听整个控件
        mSearchViewDxQingce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // while a false will expand it.
                mSearchViewDxQingce.setIconified(false);
            }
        });

//        mSearchViewDxQingce.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//
//                mDxQingCeGetParameters.clear();
//                mDxQingCeGetParameters.put("project_id", Main.project_id);
//
//                refreshPage(true);
//
//                return false;
//            }
//        });

        mComputeDxBt = (Button) findViewById(R.id.button_compute_dx);
        mComputeDxBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutComputeDx.setVisibility(View.VISIBLE);

                stopScan();
                mLayoutComputeDistance.setVisibility(View.GONE);
            }
        });
        mComputeDistanceBt = (Button) findViewById(R.id.button_compute_liangdian);
        mComputeDistanceBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutComputeDistance.setVisibility(View.VISIBLE);
                mLayoutComputeDx.setVisibility(View.GONE);

                startScan();
            }
        });

        addDxFromFileBt = (Button) findViewById(R.id.button_add_dx_from_file);
        addDxFromFileBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, AddDxQingCeFromFileActivity.class);
                startActivity(intent);
            }
        });

        dxImportHistoryBt = (Button) findViewById(R.id.button_show_import_history);
        dxImportHistoryBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, DxImportHistoryActivity.class);
                startActivity(intent);
            }
        });
        /// 全新路径和基于已有路径 来新建路径
        addTotalNewLujingBt = (Button) findViewById(R.id.button_add_new_lujing);
        addTotalNewLujingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryGotoLujingActivity(Constant.REQUEST_CODE_ADD_TOTAL_NEW_LUJING, null);
            }
        });

        //电线列表
        mQingceRV = (RecyclerView) findViewById(R.id.rv_dianxian);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mQingceRV.setLayoutManager(manager);

        //路径列表
        mLujingRV = (RecyclerView) findViewById(R.id.rv_lujing);
        LinearLayoutManager manager3 = new LinearLayoutManager(this);
        manager3.setOrientation(LinearLayoutManager.VERTICAL);
        mLujingRV.setLayoutManager(manager3);

        mLujingInCalculateRV = (RecyclerView) findViewById(R.id.rv_lujing_compute);
        LinearLayoutManager manager4 = new LinearLayoutManager(this);
        manager4.setOrientation(LinearLayoutManager.VERTICAL);
        mLujingInCalculateRV.setLayoutManager(manager4); //manager4必须有

        mLayoutQingCe = (LinearLayout) findViewById(R.id.layout_dianxian_qingce_id);
//        mLayoutOrder = (LinearLayout)findViewById(R.id.layout_order_id);
        mLayoutLujing = (LinearLayout) findViewById(R.id.layout_lujing);

        mLayoutCompute = (LinearLayout) findViewById(R.id.layout_compute);
        mLayoutComputeDx = (LinearLayout) findViewById(R.id.layout_compute_dianxian);
        mLayoutComputeDistance = (RelativeLayout) findViewById(R.id.layout_compute_dis);


        if (mDxQingceAdapter == null)
        {
            mDxQingceAdapter = new DianXianQingceAdapter(mDianxianQingCeList, Main.this,Constant.REQUEST_CODE_DIANXIANQINCE_WIRES);
            mQingceRV.addItemDecoration(new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL));
            mQingceRV.setAdapter(mDxQingceAdapter);
        }
        mDxQingceAdapter.setOnItemClickListener(MyItemClickListenerDx);
        mDxQingceAdapter.updateDataSoruce(mDianxianQingCeList);
    }

    /**
     * 在进入路径界面前，如果是 全新新建、基于旧的新建，包括修改路径， 都应该先让用户设置好路径名称。
     * 如果在路径界面临时写路径名称，会有很多逻辑。
     */
    private void tryGotoLujingActivity(int requestCode, LujingData baselujingData) {
        mRequestCode = requestCode;
        /**
         * 在主界面定好路径名称后，无论是哪种模式，都应该确保有路径数据
         * 新建路径 -- 要创建,创建成功了再跳转
         * 基于旧路径 新建路径 --要创建,创建成功了再跳转
         * 修改路径 -- 要更新路径 -->暂时先不修改名称， 直接跳转
         */
        if (requestCode == Constant.REQUEST_CODE_ADD_TOTAL_NEW_LUJING ) {
            final EditText et = new EditText(this);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Main.this);
            alertDialogBuilder.setTitle("输入路径名称：")
                    .setView(et)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LinkedHashMap<String, String> newPathParameters = new LinkedHashMap<>();
                            String strNewPathName = et.getText().toString();
                            if (strNewPathName == null || strNewPathName.isEmpty()) { //不允许名称为空
                                Toast.makeText(Main.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                            } else {

                                newPathParameters.put("name",  strNewPathName);
                                //todo
                                String theUrl =  RequestUrlUtility.build(URL.POST_ADD_NEW_LUJING.replace("{project_id}",  project_id));
                                mNetwork.addNewLujing(theUrl, newPathParameters, new NewLujingHandler(strNewPathName));
                            }
                        }
                    })
                    .show();
        } else if ( requestCode == Constant.REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST) {

            final CharSequence items[] = { "追加模式", "子路径模式", "分叉模式" };

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Main.this);
            alertDialogBuilder.setTitle("选择模式：")
                    .setSingleChoiceItems(items, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    mNewPathchoice = which;
                                }
                            })
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            AlertDialog.Builder inputPathNameDialogBuilder = new AlertDialog.Builder(Main.this);
                            final EditText et = new EditText(inputPathNameDialogBuilder.getContext());

                            inputPathNameDialogBuilder.setTitle("请输入路径名称").setView(et)
                                    .setNegativeButton("取消", null)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                                scanIntent = new Intent(Main.this, ZxingScanActivity.class);
                                                mStrNewPathName = et.getText().toString().trim();
                                                if (mStrNewPathName.isEmpty())
                                                {
                                                    ShowMessage.showToast(Main.this,"路径名不能为空！",ShowMessage.MessageDuring.SHORT);
                                                    return;
                                                }

                                                switch (mNewPathchoice)
                                                {
                                                    case 0://clone mode
                                                          ////
                                                        mScanResultDistanceList.clear();

                                                        HashMap postValue = new HashMap<>();
                                                        postValue.put("name", mStrNewPathName);

                                                        //call the to create a new path
                                                        String url = Constant.addNewLujingCopyOnOldUrl.replace("{id}",
                                                                Integer.toString(baselujingData.getId()));

                                                        mNetwork.post(url,postValue,new CloneLujingHandler(
                                                                mStrNewPathName),
                                                                (handler, msg)->{
                                                                    handler.sendMessage(msg);
                                                                } );

                                                        ////
                                                        break;
                                                    case 1://子路径模式
                                                        //start the qr scan to get a qr id to sub on
                                                        scanIntent.putExtra("requestCode", (Serializable) Constant.REQUEST_CODE_SCAN_TO_SUB_ON_PATH);
                                                        scanIntent.putExtra("mLujingToPass", (Serializable) baselujingData);

                                                        //运行时权限
                                                        if (ContextCompat.checkSelfPermission(Main.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                                                         {
                                                                ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.CAMERA}, Constant.REQUEST_CODE_SCAN_TO_SUB_ON_PATH);
                                                         }
                                                        }else {


                                                            startActivityForResult(scanIntent, Constant.REQUEST_CODE_SCAN_TO_SUB_ON_PATH);
                                                        }

                                                        break;
                                                    case 2://分叉模式
                                                        //start the qr scan to get a qr id to branch on
                                                        scanIntent.putExtra("requestCode", (Serializable) Constant.REQUEST_CODE_SCAN_TO_BRANCH_ON_PATH);
                                                        scanIntent.putExtra("mLujingToPass", (Serializable) baselujingData);

                                                        //运行时权限
                                                        if (ContextCompat.checkSelfPermission(Main.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                                                            ActivityCompat.requestPermissions(Main.this,new String[]{Manifest.permission.CAMERA},Constant.REQUEST_CODE_SCAN_TO_BRANCH_ON_PATH);
                                                        }else {
                                                            /**
                                                             * requestCode为：
                                                             */


                                                            startActivityForResult(scanIntent, Constant.REQUEST_CODE_SCAN_TO_BRANCH_ON_PATH);
                                                        }
                                                        break;
                                                }

                                        }
                                    }).show();
                        }
                    })
                    .show();
        } else if (requestCode == Constant.REQUEST_CODE_MODIFY_LUJING){

            FinalGotoLujingActivity(requestCode, baselujingData); // 编辑路径
        }
    }

    /**
     *  把路径传给电线计算页面，电线页面再去查询电线列表
     */
    private void gotoWiresCalculateActivity(int requestCode, LujingData lujingData) {
        Intent intent = new Intent(Main.this, WiresInCalculateActivity.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable("requestCode", (Serializable) requestCode);
        bundle.putSerializable("mLujingToPass", lujingData);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    private void gotoWiresExportActivity(int requestCode, LujingData lujingData) {
        Intent intent = new Intent(Main.this, LujingWiresSummarizeExportActivity.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable("requestCode", (Serializable) requestCode);
        bundle.putSerializable("mLujingToPass", lujingData);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }
    @SuppressLint("HandlerLeak")
    class NewLujingHandler extends Handler {

        String newPathName;

        public NewLujingHandler(String newPathName)
        {
            this.newPathName = newPathName;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Network.OK) {
                ShowMessage.showToast(Main.this,"添加路径成功！",ShowMessage.MessageDuring.SHORT);
                /**
                 * 解析获取路径的ID ,
                 */
                String idStr = ((LinkedTreeMap) msg.obj).get("id").toString(); ///  {errorCode=0.0, id=56.0}
                int lujingID = Double.valueOf(idStr).intValue();


                LujingData newLujingData = new LujingData();

                newLujingData.setId(lujingID);
                newLujingData.setName(newPathName);

                Intent intent =getIntent();

                intent.setClass(Main.this, LujingActivity.class);

                intent.putExtra("requestCode", (Serializable) mRequestCode);
                intent.putExtra("mLujingToPass", (Serializable) newLujingData);

                startActivityForResult(intent, mRequestCode);
            }else {
                ShowMessage.showDialog(Main.this,"添加路径出错！");
            }
        }
    }

    private void FinalGotoLujingActivity(int requestCode, LujingData lujingData){
        Intent intent = new Intent(Main.this, LujingActivity.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable("requestCode", (Serializable) requestCode);
        bundle.putSerializable("mLujingToPass", lujingData);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    private void initViewsCompute() {

        /// 各一个, 先建，后建
        if (mLujingInCalculateAdapter == null)
        {
            mLujingInCalculateAdapter = new LujingAdapter(mLujingList, Main.this, Constant.FLAG_LUJING_IN_CALCULATE);
            mDividerItemDecoration = new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL);
            mLujingInCalculateRV.addItemDecoration(mDividerItemDecoration);
            mLujingInCalculateRV.setAdapter(mLujingInCalculateAdapter);

            // 设置item及item中控件的点击事件
            mLujingInCalculateAdapter.setOnItemClickListener(MyItemClickListener); /// adapter的 item的监听
        }
        if (mLujingAdapter == null)
        {
            mLujingAdapter = new LujingAdapter(mLujingList, Main.this, Constant.FLAG_LUJING_IN_LUJING);
            mDividerItemDecorationInCalcuate = new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL);
            mLujingRV.addItemDecoration(mDividerItemDecorationInCalcuate);
            mLujingRV.setAdapter(mLujingAdapter);

            // 设置item及item中控件的点击事件
            mLujingAdapter.setOnItemClickListener(MyItemClickListener); /// adapter的 item的监听
        }

        mComputeScanBt = (Button) findViewById(R.id.button_compute_scan);
        mComputeScanBt.setOnClickListener(new MyOnclickListenrOnScanBts(Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING_CACULATE));

        mSearchViewInCalculate = (SearchView) findViewById(R.id.searchViewInCalculate);
        mSearchViewInCalculate.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mLujingCaculateGetParameters.clear();
                mLujingCaculateGetParameters.put("name", query);
                mLujingCaculateGetParameters.put("project_id",Main.project_id);

                refreshPage(true);
//                mNetwork.get(RequestUrlUtility.build(URL.GET_LUJING_LIST), mLujingCaculateGetParameters, new GetLujingListHandler(tabFlag), (handler, msg) -> {
//                    handler.sendMessage(msg);
//                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText))
                {
                    mLujingCaculateGetParameters.clear();
                    mLujingCaculateGetParameters.put("project_id",Main.project_id);

                    refreshPage(true);
                }
                return false;
            }
        });

        //监听整个控件
        mSearchViewInCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // while a false will expand it.
                mSearchViewInCalculate.setIconified(false);
            }
        });

//        mSearchViewInCalculate.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                mLujingCaculateGetParameters.clear();
//                mLujingCaculateGetParameters.put("project_id",Main.project_id);
//
//                refreshPage(true);
//
//                return false;
//            }
//        });

        mResetInCaculateBt = (Button) findViewById(R.id.button_reset_in_calculate);
        mResetInCaculateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mLujingCaculateGetParameters.clear();
                mLujingCaculateGetParameters.put("project_id",Main.project_id);

                refreshPage(true);
//                mNetwork.get(RequestUrlUtility.build(URL.GET_LUJING_LIST), mLujingCaculateGetParameters, new GetLujingListHandler(tabFlag), (handler, msg) -> {
//                    handler.sendMessage(msg);
//                });
            }
        });

        mSetDistanceLengthInCaculateBt  = (Button) findViewById(R.id.button5);
        //初始为不可点击，扫码有结果时才enable
        mSetDistanceLengthInCaculateBt.setEnabled(false);
        mSetDistanceLengthInCaculateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(Main.this);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Main.this);
                alertDialogBuilder.setTitle("输入间距长度：")
                        .setView(et)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                             Log.i(TAG, "输入了长度： " + et.getText());
                                String theUrl = Constant.putQrDistanceUrl.replace("{qr_id}", String.valueOf(currentDistanceData.getQr_id()) ); //"/distance/qr/{qr_id}/{distance}/changeDistance";
                                theUrl = theUrl.replace("{distance}",et.getText());
                                mNetwork.put(theUrl, null, new SetQrDistanceHandler(), (handler, msg) -> {
                                    handler.sendMessage(msg);
                                });
                            }
                        })
                        .show();
            }
        });

    }

    /**
     * 扫描按钮共用
     */
    class MyOnclickListenrOnScanBts implements View.OnClickListener
    {
        int mRequestCode;

        public MyOnclickListenrOnScanBts(int mRequestCode)
        {
            this.mRequestCode = mRequestCode;
        }
        @Override
        public void onClick(View v) {
            scanIntent = new Intent(Main.this, ZxingScanActivity.class);
            scanIntent.putExtra("requestCode", mRequestCode);

            //运行时权限
            if (ContextCompat.checkSelfPermission(Main.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(Main.this,new String[]{Manifest.permission.CAMERA},Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING_LUJING);
            }else {
                /**
                 * requestCode为：
                 */
                startActivityForResult(scanIntent, mRequestCode);
            }
        }
    }

    private void initViewsLujing() {
        mLujingScanBt = (Button) findViewById(R.id.button_scan_lujing);
        mLujingScanBt.setOnClickListener(new MyOnclickListenrOnScanBts(Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING_LUJING));
        mLujingResetBt = (Button) findViewById(R.id.button_reset_lujing);;
        mLujingResetBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //clear the filters and get all the lujing list
                mLujingGetParameters.clear();
                mLujingGetParameters.put("project_id",Main.project_id);

                refreshPage(true);
//                mNetwork.get(RequestUrlUtility.build(URL.GET_LUJING_LIST), mLujingGetParameters, new GetLujingListHandler(tabFlag),
//                        (handler, msg) -> {
//                            handler.sendMessage(msg);
//                        });
            }
        });


        mLujingSearchView = (SearchView) findViewById(R.id.searchLujingByName);
        mLujingSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mLujingGetParameters.clear();
                mLujingGetParameters.put("name",query);
                mLujingGetParameters.put("project_id",Main.project_id);

                refreshPage(true);
//                mNetwork.get(RequestUrlUtility.build(URL.GET_LUJING_LIST), mLujingGetParameters, new GetLujingListHandler(tabFlag),
//                        (handler, msg) -> {
//                            handler.sendMessage(msg);
//                        });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText))
                {
                    mLujingGetParameters.clear();
                    mLujingGetParameters.put("project_id",Main.project_id);

                    refreshPage(true);
                }
                return false;
            }
        });

        //监听整个控件
        mLujingSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // while a false will expand it.
                mLujingSearchView.setIconified(false);
            }
        });

//        mLujingSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//
//                mLujingGetParameters.clear();
//                mLujingGetParameters.put("project_id",Main.project_id);
//
//                refreshPage(true);
//
//                return false;
//            }
//        });

    }

    /**
     * 路径Adapter里item的控件点击监听事件
     */
    private LujingAdapter.OnItemClickListener MyItemClickListener = new LujingAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, LujingAdapter.ViewName viewName, int position) {
            //viewName可区分item及item内部控件
            switch (v.getId()){
                case R.id.button_modify_lujing:
//                    Toast.makeText(Main.this,"你点击了 修改路径 按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    tryGotoLujingActivity(Constant.REQUEST_CODE_MODIFY_LUJING, mLujingList.get(position)); ///这里的id为000000000
                    break;
                case R.id.button_create_lujing_base_exist:
                    tryGotoLujingActivity(Constant.REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST, mLujingList.get(position));
                    break;
                case R.id.button_delete_lujing:
                    //Toast.makeText(Main.this,"你点击了 删除路径 按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    // 警告之后再删除
//                    final EditText et = new EditText(Main.this);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Main.this);
                    alertDialogBuilder.setTitle("确认删除路径 " + mLujingList.get(position).getName() + "吗？" )
//                            .setView(et)
                            .setNegativeButton("取消", null)
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String IDs =  "{ids:[" + String.valueOf(mLujingList.get(position).getId())  + "]}"; /// {ids:[91]}
                                    mNetwork.deleteLujing(Constant.deleteLujingUrl, IDs, new DeleteLujingHandler());
                                }
                            })
                            .show();
                    break;

                case R.id.wiresListBt:
                    Log.i(TAG,"电线清单 按钮" +(position+1) + mLujingList.get(position).getName());
                    gotoWiresCalculateActivity(Constant.REQUEST_CODE_CALCULATE_WIRES, mLujingList.get(position));
                    break;
                case R.id.exportAccordModelBt:
                    Log.i(TAG,"型号导出 按钮" +(position+1));
                    gotoWiresExportActivity(Constant.REQUEST_CODE_CALCULATE_WIRES, mLujingList.get(position));
                    break;
                case R.id.lujingMingChenTextView:
                case R.id.lujingCreaterTextView:
                case R.id.lujingCreateDateTextView:
                    View view = getLayoutInflater().inflate(R.layout.dialog_lujing, null);
                    final TextView tvLujingName = (TextView) view.findViewById(R.id.textViewDialogName2);
                    final TextView tvLujingCreator = (TextView) view.findViewById(R.id.textViewDialogCreator2);
                    final TextView tvLujingCreatTime = (TextView) view.findViewById(R.id.textViewDialogCreateTime2);
                    tvLujingName.setText( mLujingList.get(position).getName());
                    tvLujingCreator.setText( mLujingList.get(position).getCreator());
                    tvLujingCreatTime.setText( mLujingList.get(position).getCreate_time());
                    Log.i(TAG," 点击了路径名称或创建人或创建日期" +(position+1));
                    AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(Main.this);
                    alertDialogBuilder2.setView(view).setTitle("详情")
                            .setNegativeButton("OK", null)
                            .show();
                    break;
                default:
                    Toast.makeText(Main.this,"你点击了item按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onItemLongClick(View v) {

        }
    };
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
                    tvDxSName.setText( mDianxianQingCeList.get(position).getSerial_number());
                    tvDxQidian.setText( mDianxianQingCeList.get(position).getStart_point());
                    tvDxZhongdian.setText( mDianxianQingCeList.get(position).getEnd_point());
                    tvDxModel.setText( mDianxianQingCeList.get(position).getParts_code());
                    tvDxXinshuJiemian.setText( mDianxianQingCeList.get(position).getWickes_cross_section());
                    tvDxLength.setText( mDianxianQingCeList.get(position).getLength());
                    tvDxSteel.setText( mDianxianQingCeList.get(position).getSteel_redundancy());
                    tvDxHose.setText( mDianxianQingCeList.get(position).getHose_redundancy());
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Main.this);
                    alertDialogBuilder.setTitle("电线详情")
                            .setView(view)
                            .setPositiveButton("关闭",null)
//                            .setNegativeButton("OK", null)
                            .show();
                    break;
                case R.id.buttonDxDelete:
                    alertDialogBuilder = new AlertDialog.Builder(Main.this);
                    alertDialogBuilder.setTitle("确认删除？")
                            .setPositiveButton("取消",null)
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String url= RequestUrlUtility.build(URL.DEL_DIANXIAN_QINGCE_LIST.replace("{project_id}",Main.project_id));
                                    LinkedHashMap deleteParame = new LinkedHashMap();

                                    //like [1,5,6].
                                    String strIds = "["+ mDianxianQingCeList.get(position).getId()+"]";
                                    deleteParame.put("ids",strIds);

                                    mNetwork.delete(url, deleteParame, new Handler(){
                                        @Override
                                        public void handleMessage(@NonNull Message msg) {

                                            String errorMsg = "";

                                            errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
                                            if (errorMsg != null)
                                            {
                                                Log.d("删除电线失败 NG:", errorMsg);
                                                Toast.makeText(Main.this, "删除电线失败！" + errorMsg, Toast.LENGTH_SHORT).show();

                                                return;
                                            }

                                            Result result= (Result)(msg.obj);

                                            BaseResponse responseData = CommonUtility.objectToJavaObject(result.getData(), BaseResponse.class);


                                            Toast.makeText(Main.this, "删除电线成功！", Toast.LENGTH_SHORT).show();

                                            refreshPage(true);

//                                            String url = RequestUrlUtility.build(URL.GET_DIANXIAN_QINGCE_LIST);
//                                            mNetwork.get(url, mDxQingCeGetParameters, new GetDxListHandler(Constant.REQUEST_CODE_DIANXIANQINCE_WIRES),(handler, msg2)->{
//                                                handler.sendMessage(msg2);
//                                            });
                                        }
                                    } ,(hanlder, msg)->{
                                        hanlder.sendMessage(msg);
                                    });
                                }
                            })
                            .show();
                    break;
                default:
                    Toast.makeText(Main.this,"你点击了item按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onItemLongClick(View v) {

        }
    };
/*
一个Activity启动另一个Activity: onPause()->onStop(),再返回：onRestart()->onStart()->onResume()
程序按home 退出： onPause()->onStop(),再进入：onRestart()->onStart()->onResume();

程序按back 退出： onPause()->onStop()->onDestory(),再进入：onCreate()->onStart()->onResume();
* */
    @Override
    protected void onRestart()
    {//use the filter to request data
        super.onRestart();
        Log.i(TAG, "onRestart");
        refreshPage(true);
    }

    void  refreshPage( boolean bRefresh)
    {
        if (!bRefresh)
        {
            return;
        }

        if (mCurrentSwipeRefreshLayout == null)
        {
            return;
        }

        mCurrentSwipeRefreshLayout.setRefreshing(true);

        if (tabFlag.equals("在电线清册") )
        {
            String url = RequestUrlUtility.build(URL.GET_DIANXIAN_QINGCE_LIST);
            mNetwork.get(url, mDxQingCeGetParameters, new GetDxListHandler(Constant.REQUEST_CODE_DIANXIANQINCE_WIRES),(handler, msg)->{
                handler.sendMessage(msg);
            });
        }
        else if (tabFlag.equals("在路径模型") )
        {
            //筛选界面负责将参数传进来！
            mNetwork.get(RequestUrlUtility.build(URL.GET_LUJING_LIST), mLujingGetParameters, new GetLujingListHandler(tabFlag),(handler, msg2)->{
                handler.sendMessage(msg2);
            });

        }
        else if (tabFlag.equals ("在计算中心"))
        {
            mNetwork.get(RequestUrlUtility.build(URL.GET_LUJING_LIST), mLujingCaculateGetParameters, new GetLujingListHandler(tabFlag),(handler, msg2)->{
                handler.sendMessage(msg2);
            });
        }
        else
        {
            mCurrentSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {

        mQRCodeView.onDestroy();
        super.onDestroy();

        Log.i(TAG,"main Destroyed!");
    }

    //处理Tab的点击事件
    @Override
    public void onClick(View v) {
        //先将3个ImageButton置为灰色
        resetImgs();
        switch (v.getId()) {
            case R.id.id_tab_wirelist:
            case R.id.id_tab_lujing_moxing:
            case R.id.id_tab_caculatecenter:

                selectTab(v.getId());
                break;
        }

    }

    //进行选中Tab的处理
    private void selectTab(int i) {

        boolean bRefresh = false;

        String oldTabTag = tabFlag;
        switch (i) {
            case R.id.id_tab_wirelist:
                tabFlag = "在电线清册";
                //设置ImageButton为选中色
                mQingceImg.setImageResource(R.mipmap.tab_dx_qingce_pressed);
                if(!oldTabTag.equals(tabFlag) )
                {
                    mLayoutQingCe.setVisibility(View.VISIBLE);
                    mLayoutLujing.setVisibility(View.GONE);
                    mLayoutCompute.setVisibility(View.GONE);
                    stopScan();

                    bRefresh = true;
                }

                mCurrentSwipeRefreshLayout = mWiresRefreshLayout;

                break;
            case R.id.id_tab_lujing_moxing:

                tabFlag = "在路径模型";
                mLujingMoxingImg.setImageResource(R.mipmap.tab_lujing_moxing_pressed);
                if(!oldTabTag.equals(tabFlag) )
                {
                    mLayoutQingCe.setVisibility(View.GONE);
                    mLayoutLujing.setVisibility(View.VISIBLE);
                    mLayoutCompute.setVisibility(View.GONE);

                    stopScan();

                    bRefresh = true;
                }

                mCurrentSwipeRefreshLayout = mLujingRefreshLayout;

                break;
            case R.id.id_tab_caculatecenter:
                tabFlag = "在计算中心";
                mJisuanImg.setImageResource(R.mipmap.tab_compute_pressed);
                if(!oldTabTag.equals(tabFlag) )
                {
                    mLayoutQingCe.setVisibility(View.GONE);
                    mLayoutLujing.setVisibility(View.GONE);
                    mLayoutCompute.setVisibility(View.VISIBLE);
                    // 在计算tab 默认看到的是计算路径电线长度，隐藏两点间距的
                    mLayoutComputeDistance.setVisibility(View.GONE);

                    bRefresh = true;
                }
                mCurrentSwipeRefreshLayout = mCaculateLujingRefreshLayout;
                break;
        }

        refreshPage(bRefresh);
    }

    private void  startScan(){

        if(mQRCodeView != null) {

            mDisplayScanResultTv.setText("请对准二维码");
            //mQRCodeView.
            mQRCodeView.startCamera();
            mQRCodeView.startSpotAndShowRect();
            Log.d(TAG, "onStart: startCamera");
        }
    }

    private void stopScan(){
        if(mQRCodeView != null) {
            mQRCodeView.stopSpotAndHiddenRect();
            mQRCodeView.stopCamera();
        }
        mSetDistanceLengthInCaculateBt.setEnabled(false);
    }
    //将3个的Fragment隐藏
    private void hideFragments(FragmentTransaction transaction) {
        if (mFragDxQingce != null) {
            transaction.hide(mFragDxQingce);
        }
//        if (mFragFrd != null) {
//            transaction.hide(mFragFrd);
//        }
        if (mFragLujingMoxing != null) {
            transaction.hide(mFragLujingMoxing);
        }
        if (mFragJisuan != null) {
            transaction.hide(mFragJisuan);
        }
    }

    //将3个ImageButton置为灰色
    private void resetImgs() {
        mQingceImg.setImageResource(R.mipmap.tab_dx_qingce_normal);
//        mFrdImg.setImageResource(R.mipmap.tab_find_frd_normal);
        mLujingMoxingImg.setImageResource(R.mipmap.tab_address_normal);
        mJisuanImg.setImageResource(R.mipmap.tab_compute_normal);
    }

    // onActivityResult 先于 resueme 执行
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (intent == null)
        {
            return;
        }

        ArrayList<DistanceData> distanceQrDatalist;
        LujingData lujing;
        DistanceData distanceData;
        String url;
        HashMap<String, String> postValue;

        switch (requestCode){
                case Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING_LUJING:
                if (resultCode == RESULT_OK) {

                    //把筛选结果返回给主页面
                    ArrayList<LujingData> list = (ArrayList<LujingData>) intent.getSerializableExtra("mFilterLujingList");
                    mLujingList = (ArrayList<LujingData>) list.clone();
                    Log.i(TAG," 筛选得到" + mLujingList.size() + " 条路径");
                    Toast.makeText(this, " 筛选得到" + mLujingList.size() + " 条路径", Toast.LENGTH_LONG).show();

                    if (mLujingAdapter == null)
                    {
                        mLujingAdapter = new LujingAdapter(mLujingList, Main.this,Constant.FLAG_LUJING_IN_LUJING);
                        mLujingRV.addItemDecoration(new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL));
                        mLujingRV.setAdapter(mLujingAdapter);

                        // 设置item及item中控件的点击事件
                        mLujingAdapter.setOnItemClickListener(MyItemClickListener); /// adapter的 item的监听
                    }
                    mLujingAdapter.updateDataSource(mLujingList, Constant.FLAG_LUJING_IN_LUJING);

                    Serializable serializable  = intent.getSerializableExtra("getParameters");

                    HashMap<String, String> params = (HashMap<String, String>)(serializable);

                    //保存路劲筛选的参数，
                    mLujingGetParameters = params;
                    mLujingGetParameters.put("try_scope","2");
                }
                break;
            case Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING_CACULATE:
                {
                    if (resultCode == RESULT_OK) {

                        //把筛选结果返回给主页面
                        ArrayList<LujingData> list = (ArrayList<LujingData>) intent.getSerializableExtra("mFilterLujingList");
                        mLujingList = (ArrayList<LujingData>) list.clone();
                        Log.i(TAG," 筛选得到" + mLujingList.size() + " 条路径");
                        Toast.makeText(this, " 筛选得到" + mLujingList.size() + " 条路径", Toast.LENGTH_LONG).show();

                        if (mLujingAdapter == null)
                        {
                            mLujingAdapter = new LujingAdapter(mLujingList, Main.this,Constant.FLAG_LUJING_IN_LUJING);
                            mLujingRV.addItemDecoration(new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL));
                            mLujingRV.setAdapter(mLujingAdapter);

                            // 设置item及item中控件的点击事件
                            mLujingAdapter.setOnItemClickListener(MyItemClickListener); /// adapter的 item的监听
                        }
                        mLujingAdapter.updateDataSource(mLujingList, Constant.FLAG_LUJING_IN_LUJING);

                        Serializable serializable  = intent.getSerializableExtra("getParameters");

                        HashMap<String, String> params = (HashMap<String, String>)(serializable);

                        //保存路劲筛选的参数，
                        mLujingCaculateGetParameters = params;
                        mLujingCaculateGetParameters.put("try_scope","2");
                    }
                }
                break;
            case Constant.REQUEST_CODE_SCAN_TO_BRANCH_ON_PATH:

                mScanResultDistanceList.clear();

                //the call back should only contains only one qr and non-empty list
                Serializable result = intent.getSerializableExtra("mScanResultDistanceList");

                distanceQrDatalist = result == null? null:(ArrayList<DistanceData>)result;

                if (distanceQrDatalist != null && !distanceQrDatalist.isEmpty())
                {

                    lujing = (LujingData) intent.getSerializableExtra("mLujing");

                    distanceData = distanceQrDatalist.get(0);


                    postValue = new LinkedHashMap<>();
                    postValue.put("name", mStrNewPathName);
                    postValue.put("branch_qr_id", Integer.toString( distanceData.getQr_id()) );

                    //call the to create a new path
                    url = Constant.addNewLujingBranchOnOldUrl.replace("{id}", Integer.toString(lujing.getId()));

                    mNetwork.post(url,postValue,new CloneLujingHandler(mStrNewPathName),(handler, msg)->{
                        handler.sendMessage(msg);
                    } );
                }

                break;
            case Constant.REQUEST_CODE_SCAN_TO_SUB_ON_PATH:

                mScanResultDistanceList.clear();

                //the call back should only contains only one qr and non-empty list
                Serializable result2 = intent.getSerializableExtra("mScanResultDistanceList");
                distanceQrDatalist = result2 == null? null:(ArrayList<DistanceData>)result2;

                if (distanceQrDatalist != null && (distanceQrDatalist.size()>1) )
                {
                    distanceData = distanceQrDatalist.get(0);
                    postValue = new LinkedHashMap<>();

                    postValue.put("sub_qr_id_start", Integer.toString( distanceData.getQr_id()) );

                    distanceData = distanceQrDatalist.get(1);
                    postValue.put("sub_qr_id_end", Integer.toString( distanceData.getQr_id()) );

                    postValue.put("name", mStrNewPathName);

                    //call the to create a new path
                    lujing = (LujingData) intent.getSerializableExtra("mLujing");
                    url = Constant.addNewLujingSubOnOldUrl.replace("{id}", Integer.toString(lujing.getId()));

                    mNetwork.post(url,postValue,new CloneLujingHandler(mStrNewPathName),(handler, msg)->{
                        handler.sendMessage(msg);
                    } );

                }

                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mConfirmedExit == false)
        {
            if(keyCode== KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Main.this);
                alertDialogBuilder.setTitle("确定退出程序？")
                        .setNegativeButton("否", null)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

//                                if (MyActivityManager.getInstance().getCurrentActivity().equals(Main.this))
//                                {
//                                    mConfirmedExit = true;
//                                    Main.this.onKeyDown(keyCode, event);
//                                }

                                finish();
                            }
                        })
                        .show();

                return  true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //开启权限
            switch (requestCode)
            {
                case Constant.REQUEST_CODE_SCAN_TO_SUB_ON_PATH:
                    startActivityForResult(scanIntent, requestCode );
                    break;
                case Constant.REQUEST_CODE_SCAN_TO_BRANCH_ON_PATH:
                    startActivityForResult(scanIntent, requestCode );
                    break;
                case Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING_LUJING:
                    startActivityForResult(scanIntent, requestCode );
                    break;
                case Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING_CACULATE:
                    startActivityForResult(scanIntent, requestCode );
                    break;
                default:
            }
        } else {

            scanIntent = null;
            Toast.makeText(Main.this,"您拒绝了权限申请，无法使用相机扫描",Toast.LENGTH_SHORT).show();
        }
    }

}