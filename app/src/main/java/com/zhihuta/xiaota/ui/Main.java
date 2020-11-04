package com.zhihuta.xiaota.ui;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

import com.google.gson.internal.LinkedTreeMap;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.SettingFragment;
import com.zhihuta.xiaota.WeixinFragment;
import com.zhihuta.xiaota.adapter.DistanceAdapter;
import com.zhihuta.xiaota.adapter.LujingAdapter;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.bean.basic.DistanceData;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.net.Network;
import com.zhihuta.xiaota.util.ShowMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ScheduledExecutorService;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

//public class DianxianQingCe extends AppCompatActivity {
public class Main extends FragmentActivity implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate, QRCodeView.Delegate {

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

    // 电线 "手动添加" 按钮
    private Button addDxByHandBt;
    // 电线 "从文件导入" 按钮
    private Button addDxFromFileBt;
    // 建全新路径 按钮
    private Button addTotalNewLujingBt;


    private Button mLujingScanBt; //路径主界面的扫码按钮，用于 筛选出需要查看或者编辑的路径, 扫的越多码筛选出的路径越精确.
    private Button mComputeScanBt; //计算中心主界面的扫码按钮
    private Button mLujingResetBt; //路径主界面的重置按钮
    private SearchView mSearchView;

    private DianXianQingceAdapter mDxQingceAdapter;
    private ArrayList<DianxianQingCeData> mDianxianQingCeList = new ArrayList<>();
    private RecyclerView mQingceRV;
    private RecyclerView mLujingRV;
    private RecyclerView mLujingInCalculateRV;
//    private OrderAdapter mOrderAdapter;
//    private ArrayList<OrderData> mOrderList = new ArrayList<>();

    private LujingAdapter mLujingAdapter;               //路径模型的路径的Adapter
    private LujingAdapter mLujingInCalculateAdapter;    //计算中心的路径的Adapter
    private ArrayList<LujingData> mLujingList = new ArrayList<>();
//    private ArrayList<LujingData> mLujingListInCalculate = new ArrayList<>(); //不需要用不同list,只要有不同adapter
    private LujingData mLujingToPass = new LujingData(); //传给下个页面的路径数据


    private int mRequestCode = 0;


//    private LujingAdapter mLujingShaixuanAdapter;
//    private ArrayList<LujingData> mLujingShaixuanList = new ArrayList<>();

    private boolean isBackFromFilterPath = false;// 是否为从扫码筛选界面返回，如果是，不需要去刷新获取路径

    private Network mNetwork;
    private GetUserHandler getUserHandler;
    private GetDxListHandler getDxListHandler;
    private GetLujingListHandler getLujingListHandler;


    private ArrayList<DistanceData> mDistanceListForCalculate = new ArrayList<>(); //从扫码筛选获取的间距列表, 在计算中心
    private DistanceAdapter mDistanceCalculateAdapter;

    /**
     * 以下，主界面-计算中心-计算两点距离
     */
    private ZXingView mQRCodeView;
    private ScheduledExecutorService mStopScanTimer;
    private Button mContinueScanBt;
    private Button mFinishScanBt;
    private TextView mDisplayScanResultTv;
    private Button mResetInCaculateBt;          //计算中心-重置按钮
    private SearchView mSearchViewInCalculate;  //计算中心-按名称查找按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);


        mNetwork = Network.Instance(getApplication());
        getUserHandler = new Main.GetUserHandler();
        getDxListHandler = new GetDxListHandler();
        getLujingListHandler = new GetLujingListHandler();
        initViews();//初始化控件
        initEvents();//初始化事件
        selectTab(1);//默认选中第2个Tab

        LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
        mPostValue.put("account", "z");
        mPostValue.put("password", "a");
        mPostValue.put("meid", XiaotaApp.getApp().getIMEI());
        /// mPostValue 在后续会用到，比如不同用户，获取各自公司的电线
        mNetwork.fetchDxListData(Constant.getDxListUrl8083, mPostValue, getDxListHandler);///ok
        mNetwork.fetchLujingListData(Constant.getLujingListUrl8083, mPostValue, getLujingListHandler);//ok
        initComputeScan();
    }
    //计算两点距离 的界面初始化
    private void initComputeScan(){

        mQRCodeView = (ZXingView) findViewById(R.id.zxingview_in_calculate);
        mQRCodeView.setDelegate(this);
        mDisplayScanResultTv = (TextView) findViewById(R.id.textView_display_scan_result_in_calculate);
        mContinueScanBt = (Button) findViewById(R.id.button_continue_scan_in_calculate);
        mContinueScanBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mQRCodeView.startCamera();
                mQRCodeView.showScanRect();
                Log.d(TAG, "继续扫描");
                mQRCodeView.startSpot(); ///开启扫描  --要重新开启扫描，否则扫描不出下一个新的二维码
            }
        });

        mFinishScanBt = (Button) findViewById(R.id.button_scan_again);
        mFinishScanBt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "点击了结束按钮");

            }
        });
    }

    @Override
    public void onScanQRCodeSuccess(String result) {

    }

    @Override
    public void onScanQRCodeOpenCameraError() {

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
        @Override
        public void handleMessage(final Message msg) {
//            if(mLoadingProcessDialog != null && mLoadingProcessDialog.isShowing()) {
//                mLoadingProcessDialog.dismiss();
//            }

            if (msg.what == Network.OK) {
                Log.d("GetLujingListHandler", "OKKK");
                mLujingList = (ArrayList<LujingData>) msg.obj;
                if (mLujingList == null) {
                    mLujingList = new ArrayList<>();
                    Log.d(TAG, "handleMessage: " + "路径获取异常");
                } else {
                    if (mLujingList.size() == 0) {
                        Toast.makeText(Main.this, "路径数量为0！", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int k = 0; k < mLujingList.size(); k++) {
                            mLujingList.get(k).setFlag(Constant.FLAG_LUJING_IN_LUJING);
                        }
                    }
                }
                mLujingAdapter = new LujingAdapter(mLujingList, Main.this);
                mLujingRV.addItemDecoration(new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL));
                mLujingRV.setAdapter(mLujingAdapter);
                mLujingAdapter.notifyDataSetChanged();
                // 设置item及item中控件的点击事件
                mLujingAdapter.setOnItemClickListener(MyItemClickListener); /// adapter的 item的监听

                /**
                 * 计算中心
                 */
//                mLujingListInCalculate = (ArrayList<LujingData>) mLujingList.clone();
                mLujingInCalculateAdapter = new LujingAdapter(mLujingList, Main.this);
                mLujingInCalculateRV.addItemDecoration(new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL));
                mLujingInCalculateRV.setAdapter(mLujingInCalculateAdapter);
                mLujingInCalculateAdapter.notifyDataSetChanged();
                mLujingInCalculateAdapter.setOnItemClickListener(MyItemClickListener); /// adapter的 item的监听

            } else {
                String errorMsg = (String) msg.obj;
                Log.d("GetLujingListHand NG:", errorMsg);
                Toast.makeText(Main.this, "路径获取失败！" + errorMsg, Toast.LENGTH_SHORT).show();
            }
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
                LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
                mPostValue.put("account", "z");
                mNetwork.fetchLujingListData(Constant.getLujingListUrl8083, mPostValue, getLujingListHandler);
            } else {
                String errorMsg = (String) msg.obj;
                Log.d("DeleteLujingHandler NG:", errorMsg);
                Toast.makeText(Main.this, "路径删除失败！" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class GetDxListHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {
//            if(mLoadingProcessDialog != null && mLoadingProcessDialog.isShowing()) {
//                mLoadingProcessDialog.dismiss();
//            }

            if (msg.what == Network.OK) {
                Log.d("GetDxListHandler", "OKKK");
                mDianxianQingCeList = (ArrayList<DianxianQingCeData>) msg.obj;

                if (mDianxianQingCeList == null) {
                    Log.d(TAG, "handleMessage: " + "电线 获取异常");
                } else {
                    if (mDianxianQingCeList.size() == 0) {
                        Toast.makeText(Main.this, "电线数量为0！", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int k = 0; k < mDianxianQingCeList.size(); k++) {
                            mDianxianQingCeList.get(k).setFlag(Constant.FLAG_QINGCE_DX);
                        }
                    }
                    mDxQingceAdapter = new DianXianQingceAdapter(mDianxianQingCeList, Main.this);
                    mQingceRV.addItemDecoration(new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL));
                    mQingceRV.setAdapter(mDxQingceAdapter);
                    mDxQingceAdapter.notifyDataSetChanged();
                }

            } else {
                String errorMsg = (String) msg.obj;
                Log.d("GetDxListHandler NG:", errorMsg);
                Toast.makeText(Main.this, "电线获取失败！" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class GetUserHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {


            if (msg.what == Network.OK) {
                Log.d("GetUserHandler", "OKKK");

            } else {
                String errorMsg = (String) msg.obj;
                Log.d("GetUserHandler NG:", errorMsg);
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
        mTabDxQingce = (LinearLayout) findViewById(R.id.id_tab_weixin);
        mTabLujingMoxing = (LinearLayout) findViewById(R.id.id_tab_lujing_moxing);
        mTabJisuan = (LinearLayout) findViewById(R.id.id_tab_setting);

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

        mComputeDxBt = (Button) findViewById(R.id.button_compute_dx);
        mComputeDxBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutComputeDx.setVisibility(View.VISIBLE);
                mLayoutComputeDistance.setVisibility(View.GONE);
            }
        });
        mComputeDistanceBt = (Button) findViewById(R.id.button_compute_liangdian);
        mComputeDistanceBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutComputeDistance.setVisibility(View.VISIBLE);
                mLayoutComputeDx.setVisibility(View.GONE);
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
        /// 全新路径和基于已有路径 来新建路径
        addTotalNewLujingBt = (Button) findViewById(R.id.button_add_new_lujing);
        addTotalNewLujingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryGotoLujingActivity(Constant.REQUEST_CODE_ADD_TOTAL_NEW_LUJING, null);
            }
        });

//        //获取传递过来的信息
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        mDianxianQingCeList = (ArrayList<DianxianQingCeData>) bundle.getSerializable("mDianxianQingCeList");
//
//        if (mDianxianQingCeList != null) {
//            Toast.makeText(this, "得到 电线清单 size:" + mDianxianQingCeList.size(), Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "电线清单 为空！！！", Toast.LENGTH_SHORT).show();
//        }
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

        initViewsLujing();
        initViewsCompute();
    }

    /**
     * 在进入路径界面前，如果是 全新新建、基于旧的新建，包括修改路径， 都应该先让用户设置好路径名称。
     * 如果在路径界面临时写路径名称，会有很多逻辑。
     */
    private void tryGotoLujingActivity(int requestCode, LujingData lujingData) {
        mRequestCode = requestCode;
        /**
         * 在主界面定好路径名称后，无论是哪种模式，都应该确保有路径数据
         * 新建路径 -- 要创建,创建成功了再跳转
         * 基于旧路径 新建路径 --要创建,创建成功了再跳转
         * 修改路径 -- 要更新路径 -->暂时先不修改名称， 直接跳转
         */
        if (mRequestCode == Constant.REQUEST_CODE_ADD_TOTAL_NEW_LUJING ) {
            final EditText et = new EditText(this);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Main.this);
            alertDialogBuilder.setTitle("输入路径名称：")
                    .setView(et)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
                            String name = et.getText().toString();
                            if (name == null || name.isEmpty()) { //不允许名称为空
                                Toast.makeText(Main.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                            } else {
                                mLujingToPass.setName(et.getText().toString());
//                            mPostValue.put("name", new Gson().toJson(mLujingToPass.getName()));
                                mPostValue.put("name", (mLujingToPass.getName()));
                                mNetwork.addNewLujing(Constant.addNewLujingUrl, mPostValue, new LujingHandler());
                            }
                        }
                    })
                    .show();
        } else if ( mRequestCode == Constant.REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST) {

            mLujingToPass = lujingData;
            final EditText et = new EditText(this);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Main.this);
            alertDialogBuilder.setTitle("输入路径名称：")
                    .setView(et)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
                            mLujingToPass.setName(et.getText().toString());
                            mPostValue.put("name", mLujingToPass.getName());
                            mNetwork.addNewLujing(Constant.addNewLujingUrl, mPostValue, new LujingHandler());
                        }
                    })
                    .show();
        } else {
            mLujingToPass = lujingData;
            FinalGotoLujingActivity(); // 编辑路径
        }
    }

    @SuppressLint("HandlerLeak")
    class LujingHandler extends Handler {
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
                int oldLujingID = mLujingToPass.getId();
                mLujingToPass.setId(lujingID);
                Intent intent =getIntent();
//                intent.setClass(Main.this, Main.class);
                intent.setClass(Main.this, LujingActivity.class);

                intent.putExtra("requestCode", (Serializable) mRequestCode);
                intent.putExtra("mLujingToPass", (Serializable) mLujingToPass);

                intent.putExtra("oldLujingID", (Serializable) oldLujingID ); ///旧路径的ID
                startActivityForResult(intent, mRequestCode);
            }else {
                ShowMessage.showDialog(Main.this,"添加路径出错！");
            }
        }
    }

    /**
     * 真正带着数据切换到路径界面
     */
    private void FinalGotoLujingActivity(){
        Intent intent = new Intent(Main.this, LujingActivity.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable("requestCode", (Serializable) mRequestCode);
        bundle.putSerializable("mLujingToPass", mLujingToPass);
        intent.putExtras(bundle);
        startActivityForResult(intent, mRequestCode);
    }
    private void initViewsCompute() {
        mComputeScanBt = (Button) findViewById(R.id.button_compute_scan);
        mComputeScanBt.setOnClickListener(new MyOnclickListenrOnScanBts());

        mSearchViewInCalculate = (SearchView) findViewById(R.id.searchViewInCalculate);
        mSearchViewInCalculate.setQueryHint("查找"); //按名称
        mSearchViewInCalculate.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
                mPostValue.put("account", "z");
                String theUrl = Constant.getFilterLujingListByNameUrl.replace("{LujingName}", query);
                mNetwork.fetchLujingListData(theUrl, mPostValue, getLujingListHandler);//ok
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
    }

    /**
     * 扫描按钮共用
     */
    class MyOnclickListenrOnScanBts implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Main.this, ZxingScanActivity.class);

            //运行时权限
            if (ContextCompat.checkSelfPermission(Main.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(Main.this,new String[]{Manifest.permission.CAMERA},1);
            }else {
                /**
                 * requestCode为：
                 */
                intent.putExtra("requestCode", Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING);
                startActivityForResult(intent, Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING);
            }
        }
    }

    private void initViewsLujing() {
        mLujingScanBt = (Button) findViewById(R.id.button_scan_lujing);
        mLujingScanBt.setOnClickListener(new MyOnclickListenrOnScanBts());
        mSearchView = (SearchView) findViewById(R.id.searchLujingByName);
        mSearchView.setQueryHint("查找"); //按名称
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
                mPostValue.put("account", "z");
                String theUrl = Constant.getFilterLujingListByNameUrl.replace("{LujingName}", query);
                mNetwork.fetchLujingListData(theUrl, mPostValue, getLujingListHandler);//ok
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
        mResetInCaculateBt = (Button) findViewById(R.id.button_reset_in_calculate);
        mResetInCaculateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
                mPostValue.put("account", "z");
                mNetwork.fetchLujingListData(Constant.getLujingListUrl8083, mPostValue, getLujingListHandler);
            }
        });
    }

    /**
     * 路径Adapter里item的控件点击监听事件
     */
    private LujingAdapter.OnItemClickListener MyItemClickListener = new LujingAdapter.OnItemClickListener() {

        int positionPass;
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
                    Toast.makeText(Main.this,"你点击了 删除路径 按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    // 警告之后再删除
//                    final EditText et = new EditText(Main.this);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Main.this);
                    alertDialogBuilder.setTitle("确认删除路径 " + mLujingList.get(position).getName() + "吗？" )
//                            .setView(et)
                            .setNegativeButton("取消", null)
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String IDs =  "{ids:[" + String.valueOf(mLujingList.get(positionPass).getId())  + "]}"; /// {ids:[91]}
                                    mNetwork.deleteLujing(Constant.deleteLujingUrl, IDs, new DeleteLujingHandler());
                                }
                            })
                            .show();
                    break;

                case R.id.wiresListBt:
                    Log.i(TAG,"电线信息" +(position+1));
                    break;
                case R.id.exportAccordModelBt:
                    Log.i(TAG,"电线型号" +(position+1));
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


    @Override
    protected void onResume() {
        super.onResume();
        LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
        mPostValue.put("account", "z"); ///TODO
        mNetwork.fetchDxListData(Constant.getDxListUrl8083, mPostValue, getDxListHandler);///ok
        /**
         * 如果是从筛选界面返回，则使用返回的筛选结果，不要去服务器获取。
         */
        if (isBackFromFilterPath) {
            Log.i(TAG, "从筛选返回,不刷新");
            isBackFromFilterPath = false;
        } else {
            Log.i(TAG, "不是从筛选返回,要刷新");
            mNetwork.fetchLujingListData(Constant.getLujingListUrl8083, mPostValue, getLujingListHandler);
        }
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
            case R.id.id_tab_weixin:
                selectTab(0);//当点击Tab就选中该的Tab
                break;
            case R.id.id_tab_lujing_moxing:
                selectTab(1);
                break;
            case R.id.id_tab_setting:
                selectTab(2);
                break;
        }

    }

    //进行选中Tab的处理
    private void selectTab(int i) {
        //获取FragmentManager对象
        FragmentManager manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        FragmentTransaction transaction = manager.beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);

        switch (i) {
            //当选中点击的是微信的Tab时
            case 0:
                tabFlag = "在电线清册";
                //设置微信的ImageButton为绿色
                mQingceImg.setImageResource(R.mipmap.tab_dx_qingce_pressed);
                //如果微信对应的Fragment没有实例化，则进行实例化，并显示出来
                if (mFragDxQingce == null) {
                    mFragDxQingce = new WeixinFragment();
//                    transaction.add(R.id.layout_dianxian_qingce_id, mFragWeinxin);
                    transaction.add(R.id.layout_dianxian_qingce_id, mFragDxQingce);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.show(mFragDxQingce);
                }
                mLayoutQingCe.setVisibility(View.VISIBLE);
//                mLayoutOrder.setVisibility(View.GONE);
                mLayoutLujing.setVisibility(View.GONE);
                mLayoutCompute.setVisibility(View.GONE);
                stopScan();
                break;
            case 1:
                tabFlag = "在路径模型";
                setLujingListFlag();
                mLayoutQingCe.setVisibility(View.GONE);
                mLayoutLujing.setVisibility(View.VISIBLE);
                mLayoutCompute.setVisibility(View.GONE);
                mLujingMoxingImg.setImageResource(R.mipmap.tab_lujing_moxing_pressed);
                if (mFragLujingMoxing == null) {
                    mFragLujingMoxing = new AddressFragment();
                    transaction.add(R.id.layout_dianxian_qingce_id, mFragLujingMoxing);
                } else {
                    transaction.show(mFragLujingMoxing);
                }

                stopScan();
                break;
            case 2:
                tabFlag = "在计算中心";
                setLujingListFlag();
                mLayoutQingCe.setVisibility(View.GONE);
                mLayoutLujing.setVisibility(View.GONE);
                mLayoutCompute.setVisibility(View.VISIBLE);

                // 在计算tab 默认看到的是计算路径电线长度，隐藏两点间距的
                mLayoutComputeDistance.setVisibility(View.GONE);
                mJisuanImg.setImageResource(R.mipmap.tab_compute_pressed);
                if (mFragJisuan == null) {
                    mFragJisuan = new SettingFragment();
                    transaction.add(R.id.layout_dianxian_qingce_id, mFragJisuan);
                } else {
                    transaction.show(mFragJisuan);
                }

            //    startScan();
                break;
        }
        //不要忘记提交事务
        transaction.commit();
    }

    private void setLujingListFlag(){
        //要设定flag
        if(tabFlag.equals("在路径模型")) {
            for (int k = 0; k < mLujingList.size(); k++) {
                mLujingList.get(k).setFlag(Constant.FLAG_LUJING_IN_LUJING);
            }

            mLujingAdapter = new LujingAdapter(mLujingList, Main.this);
            mLujingRV.addItemDecoration(new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL));
            mLujingRV.setAdapter(mLujingAdapter);
            mLujingAdapter.notifyDataSetChanged();
            mLujingAdapter.setOnItemClickListener(MyItemClickListener);
        } else  if(tabFlag.equals("在计算中心")) {
            for (int k = 0; k < mLujingList.size(); k++) {
                mLujingList.get(k).setFlag(Constant.FLAG_LUJING_IN_CALCULATE);
            }

            mLujingInCalculateAdapter = new LujingAdapter(mLujingList, Main.this);
            mLujingInCalculateRV.addItemDecoration(new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL));
            mLujingInCalculateRV.setAdapter(mLujingInCalculateAdapter);
            mLujingInCalculateAdapter.notifyDataSetChanged();
            mLujingInCalculateAdapter.setOnItemClickListener(MyItemClickListener);
        }


    }
    private void  startScan(){

        if(mQRCodeView != null) {
            mQRCodeView.startCamera();
            mQRCodeView.showScanRect();
            Log.d(TAG, "onStart: startCamera");
            mQRCodeView.startSpot(); ///开启扫描
            //org.apache.commons.lang3.concurrent.BasicThreadFactory
//            mStopScanTimer = new ScheduledThreadPoolExecutor(1);
//            mStopScanTimer.schedule(new Runnable() {
//                @Override
//                public void run() {
////                ToastUtils.showShort("扫描失败，切换至手动模式！");
//                    mQRCodeView.post(new Runnable() {
//                        @Override
//                        public void run() {
////                        mQRCodeView.stopSpot(); /// 关闭扫描
////                        showDialog(null);
//                        }
//                    });
//                }
//            }, 10, TimeUnit.SECONDS);
        }
    }

    private void stopScan(){
        if(mQRCodeView != null) {
            mQRCodeView.stopCamera();
        }
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

    // onActivityResult 先于 resueme 执行
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
                case Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING:
                if (resultCode == RESULT_OK) {
                    isBackFromFilterPath = true;
                    //把筛选结果返回给主页面
                    ArrayList<LujingData> list = (ArrayList<LujingData>) data.getSerializableExtra("mFilterLujingList");
                    mLujingList = (ArrayList<LujingData>) list.clone();
                    Log.i(TAG," 筛选得到" + mLujingList.size() + " 条路径");
                    Toast.makeText(this, " 筛选得到" + mLujingList.size() + " 条路径", Toast.LENGTH_LONG).show();

//                    mLujingListInCalculate = (ArrayList<LujingData>) mLujingList.clone();
                    //要设定flag
                    setLujingListFlag();
                }
                break;

            default:
                break;
        }
    }
}