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
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.internal.LinkedTreeMap;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.SettingFragment;
import com.zhihuta.xiaota.WeixinFragment;
import com.zhihuta.xiaota.adapter.LujingAdapter;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.net.Network;
import com.zhihuta.xiaota.util.ShowMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

//public class DianxianQingCe extends AppCompatActivity {
public class Main extends FragmentActivity implements View.OnClickListener {

    private static String TAG = "Main";
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
    private LinearLayout mLayoutComputeDistance;
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


    private Button mComputeScanBt; //计算中心主界面的扫码按钮
    private Button mLujingScanBt; //路径主界面的扫码按钮，用于 筛选出需要查看或者编辑的路径, 扫的越多码筛选出的路径越精确.
    private Button mLujingResetBt; //路径主界面的重置按钮
    private SearchView mSearchView;

    private DianXianQingceAdapter mDxQingceAdapter;
    private ArrayList<DianxianQingCeData> mDianxianQingCeList = new ArrayList<>();
    private RecyclerView mQingceRV;
    private RecyclerView mLujingRV;
//    private OrderAdapter mOrderAdapter;
//    private ArrayList<OrderData> mOrderList = new ArrayList<>();

    private LujingAdapter mLujingAdapter;
    private ArrayList<LujingData> mLujingList = new ArrayList<>();
    private LujingData mLujingToPass = new LujingData(); //传给下个页面的路径数据


    private int mRequestCode = 0;


    private LujingAdapter mLujingShaixuanAdapter;
    private ArrayList<LujingData> mLujingShaixuanList = new ArrayList<>();

//    private ArrayList<DistanceData> mDistanceForShaixuanList = new ArrayList<>(); //从扫码筛选获取的间距列表
    private boolean isBackFromFilterPath = false;// 是否为从扫码筛选界面返回，如果是，不需要去刷新获取路径

    private Network mNetwork;
    private GetUserHandler getUserHandler;
    private GetDxListHandler getDxListHandler;
    private GetLujingListHandler getLujingListHandler;

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
//                    } else {
                    }
                }
                mLujingAdapter = new LujingAdapter(mLujingList, Main.this);
                mLujingRV.addItemDecoration(new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL));
                mLujingRV.setAdapter(mLujingAdapter);
                mLujingAdapter.notifyDataSetChanged();
                // 设置item及item中控件的点击事件
                mLujingAdapter.setOnItemClickListener(MyItemClickListener); /// adapter的 item的监听

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
//                mLujingList.remove(position);
//                mLujingAdapter.notifyDataSetChanged();

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

        //获取传递过来的信息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mDianxianQingCeList = (ArrayList<DianxianQingCeData>) bundle.getSerializable("mDianxianQingCeList");

        if (mDianxianQingCeList != null) {
            Toast.makeText(this, "得到 电线清单 size:" + mDianxianQingCeList.size(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "电线清单 为空！！！", Toast.LENGTH_SHORT).show();
        }
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
//


        mLujingShaixuanList = (ArrayList<LujingData>) bundle.getSerializable("mLujingShaixuanList");

        //计算路径，扫描筛选得到的路径列表
        RecyclerView mLujingShaixuanRV = (RecyclerView) findViewById(R.id.rv_lujing_compute);
        LinearLayoutManager manager4 = new LinearLayoutManager(this);
        manager4.setOrientation(LinearLayoutManager.VERTICAL);
        mLujingShaixuanRV.setLayoutManager(manager4);
//        mLujingShaixuanAdapter = new LujingAdapter(mLujingShaixuanList);
        mLujingShaixuanAdapter = new LujingAdapter(mLujingShaixuanList, this);
        mLujingShaixuanRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mLujingShaixuanRV.setAdapter(mLujingShaixuanAdapter);

        mLayoutQingCe = (LinearLayout) findViewById(R.id.layout_dianxian_qingce_id);
//        mLayoutOrder = (LinearLayout)findViewById(R.id.layout_order_id);
        mLayoutLujing = (LinearLayout) findViewById(R.id.layout_lujing);

        mLayoutCompute = (LinearLayout) findViewById(R.id.layout_compute);
        mLayoutComputeDx = (LinearLayout) findViewById(R.id.layout_compute_dianxian);
        mLayoutComputeDistance = (LinearLayout) findViewById(R.id.layout_compute_dis);

        initViewsCompute();
        initViewsLujing();
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
        mComputeScanBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    private void initViewsLujing() {
            mLujingScanBt = (Button) findViewById(R.id.button_scan_lujing);
            mLujingScanBt.setOnClickListener(new View.OnClickListener() {
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
            });
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
                    Toast.makeText(Main.this,"你点击了 删除路径 按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    //TODO 警告之后再删除
                    LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
//                    mPostValue.put("ids", "[" + String.valueOf(mLujingList.get(position).getId()) + "]" );
//                    mPostValue.put("ids", "[" + String.valueOf(mLujingList.get(position).getId()) + "]");
                    String IDs = "[" + String.valueOf(mLujingList.get(position).getId()) + "]";
                    /// TODO : 删除失败
//                    mNetwork.deleteLujing(Constant.deleteLujingUrl, new Gson().toJson(IDs), new DeleteLujingHandler());

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
            mNetwork.fetchLujingListData(Constant.getLujingListUrl8083, mPostValue, getLujingListHandler);//ok
        }
    }

    @Override
    protected void onDestroy() {
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
//            case R.id.id_tab_frd:
//                selectTab(1);
//                break;
            case R.id.id_tab_lujing_moxing:
                selectTab(1);
                LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
                mPostValue.put("account","z");
                mPostValue.put("password", "a");
                mPostValue.put("meid", XiaotaApp.getApp().getIMEI());
//                mNetwork.getUserList(getUserListUrl, mPostValue, getUserHandler);
//                mNetwork.fetchLoginData(loginUrl8004, mPostValue, getUserHandler);          /// OK
//                mNetwork.fetchUserListData(getUserListUrl8004, mPostValue, getUserHandler); /// oK
//                mNetwork.fetchDxListData(getDxListUrl8083, mPostValue, getDxListHandler);///ok
//                mNetwork.fetchLujingListData(getLujingListUrl8083, mPostValue, getLujingListHandler);///ok



//                mNetwork.getDxList(getDxListUrl, mPostValue, getUserHandler);

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

//        Button addDianxianQinCeButton = (Button) findViewById(R.id.button5);///
//        Button showDianxianQinCeButton = (Button) findViewById(R.id.button6);///
//        Button addLujinMoxingButton = (Button) findViewById(R.id.button4);///
//        Button showLujingMoxingButton = (Button) findViewById(R.id.button7);///
//        Button calculateDianXianLengthButton = (Button) findViewById(R.id.button8);///
//        Button showDianXianLengthButton = (Button) findViewById(R.id.button9);///
        switch (i) {
            //当选中点击的是微信的Tab时
            case 0:
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
                break;
            case 1:
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
//                Toast.makeText(this, "按下路径模型", Toast.LENGTH_SHORT).show();
                break;
            case 2:
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
//                Toast.makeText(this, "按下路径计算", Toast.LENGTH_SHORT).show();
                break;
        }
        //不要忘记提交事务
        transaction.commit();
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


    /// todo 这里只有筛选的要留着，其他的应该可以删除
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constant.REQUEST_CODE_ADD_TOTAL_NEW_LUJING:
                if (resultCode == RESULT_OK) {
                    // 取出Intent里的新路径信息
                    LujingData lujingData = (LujingData) data.getSerializableExtra("mNewLujing");

                    Toast.makeText(this, " 新路径名称：" + lujingData.getName(), Toast.LENGTH_LONG).show();

                    mLujingList.add(lujingData);
                    mLujingAdapter.notifyDataSetChanged();
                }
                break;
                case Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING:
                if (resultCode == RESULT_OK) {
                    isBackFromFilterPath = true;
                    //把筛选结果返回给主页面
                    ArrayList<LujingData> list = (ArrayList<LujingData>) data.getSerializableExtra("mFilterLujingList");
                    mLujingList = (ArrayList<LujingData>) list.clone();

                    Toast.makeText(this, " 筛选得到" + mLujingList.size() + " 条路径", Toast.LENGTH_LONG).show();

                    //要重新绑定adapter 否则界面的路径不会更新
                    mLujingAdapter = new LujingAdapter(mLujingList, Main.this);
                    mLujingRV.addItemDecoration(new DividerItemDecoration(Main.this, DividerItemDecoration.VERTICAL));
                    mLujingRV.setAdapter(mLujingAdapter);
                    mLujingAdapter.notifyDataSetChanged();
                    mLujingAdapter.setOnItemClickListener(MyItemClickListener);
                }
                break;

            case Constant.REQUEST_CODE_MODIFY_LUJING:
                if (resultCode == RESULT_OK) {
                    // 取出Intent里的新路径信息
                    LujingData lujingData = (LujingData) data.getSerializableExtra("mLujingDataToBeModified");

                    Toast.makeText(this, " 被修改路径：" + lujingData.getName(), Toast.LENGTH_LONG).show();
                    mLujingAdapter.notifyDataSetChanged();
                }
                break;
            case Constant.REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST:
                if (resultCode == RESULT_OK) {
                    // 取出Intent里的新路径信息
                    LujingData lujingData = (LujingData) data.getSerializableExtra("mOldBasedNewLujing");
                    //保存到服务器
                    Toast.makeText(this, " 基于已有路径，新建的新路径名称：" + lujingData.getName(), Toast.LENGTH_LONG).show();

                    mLujingList.add(lujingData);
                    mLujingAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }
}