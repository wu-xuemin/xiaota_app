package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DistanceAdapter;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.bean.basic.DistanceData;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;
import com.zhihuta.xiaota.util.ShowMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import static java.nio.file.Paths.get;

public class LujingActivity extends AppCompatActivity {

    private static String TAG = "LujingActivity";
    private DistanceAdapter mDistanceAdapter;
    private ArrayList<DistanceData> mDistanceList = new ArrayList<>();
    private RecyclerView mDistanceRV;

    private Network mNetwork;
    //获取 路径对应的间距列表 (编辑路径时，或 在基于已有路径 新建路径时)
    String getLujingDistanceListUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIP() + URL.GET_LUJING_DISTANCE_LIST;
    String addNewLujingUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIP() + URL.POST_ADD_NEW_LUJING;
    String modifyLujingUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIP() + URL.PUT_MODIFY_LUJING_NAME;

    private GetLujingDistanceListHandler getLujingDistanceListHandler;

    private Button mButtonScanToAddXianduan; // 扫码去添加线段
    private Button mButtonRelateDx; // 去关联电缆电线
    private Button mButtonOk; // 完成按钮 (包括： 全新新建、修改、基于旧路径新建)

    private int mRequestCodeFromMain =0 ; //标记, 来自Main界面， 是 全新新建/修改/基于旧的新建
    private static final int REQUEST_CODE_SCAN_QRCODE_START = 1;
    private static final int REQUEST_CODE_RELATEd_DX =2;

    private LujingData mNewLujing = new LujingData(); //新建的路径
    private LujingData mLujingDataToBeModified = new LujingData(); //待修改的路径
    private LujingData mOldBasedNewLujing = new LujingData(); //基于旧路径 新建路径
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_lujing);

        mNetwork = Network.Instance(getApplication());

        getLujingDistanceListHandler = new GetLujingDistanceListHandler();
        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initViews();//初始化控件


    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        getGetLujingDistanceList(intent);
    }
    private void initViews() {

        //获取传递过来的信息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mRequestCodeFromMain = (int) intent.getExtras().getSerializable("requestCode");
        if (mRequestCodeFromMain == Constant.REQUEST_CODE_ADD_TOTAL_NEW_LUJING){
//            mNewLujing
        } else if(mRequestCodeFromMain == Constant.REQUEST_CODE_MODIFY_LUJING ){
            // 如果是修改路径，那路径名称也可改, 标题设为 编辑路径
            mLujingDataToBeModified = (LujingData) intent.getExtras().getSerializable("tobeModifiedOrBasedLujing");
            TextInputEditText lujingNameTv = (TextInputEditText) findViewById(R.id.inputEditText_lujingName);

            lujingNameTv.setText( mLujingDataToBeModified.getName());
//            lujingNameTv.setEnabled(false);
            this.setTitle("编辑路径");
            getGetLujingDistanceList(intent);
        }
        // 在基于已有路径 新建路径
        else if(mRequestCodeFromMain == Constant.REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST ){

            getGetLujingDistanceList(intent);
            mOldBasedNewLujing = (LujingData) intent.getExtras().getSerializable("tobeModifiedOrBasedLujing");
        }

        showDistanceList();
        mButtonScanToAddXianduan = (Button) findViewById(R.id.button_scan_to_add_xianduan);
        mButtonScanToAddXianduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();

                Bundle bundle2 = new Bundle();
                if( mRequestCodeFromMain == Constant.REQUEST_CODE_ADD_TOTAL_NEW_LUJING ) {
                    /**
                     * 全新路径时，要输入路径名称才允许去扫码，因为扫码时就要直接加间距到该路径了。
                     * 使用该名称生成新路径，再去扫码界面。
                     */
                    TextInputEditText lujingNameTv = (TextInputEditText) findViewById(R.id.inputEditText_lujingName);
                    if(lujingNameTv.getText().toString().equals("")){
                        Toast.makeText(LujingActivity.this, " 扫码前，请输入路径名称", Toast.LENGTH_LONG).show();
                        return;
                    }
                    mNewLujing.setName(lujingNameTv.getText().toString());
                    LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
                    mPostValue.put("name", new Gson().toJson(mNewLujing.getName()));
                    mNetwork.addNewLujing(addNewLujingUrl, mPostValue, new AddNewLujingBeforeGotoScanHandler());

                } else if (mRequestCodeFromMain == Constant.REQUEST_CODE_MODIFY_LUJING ) {
                    bundle2.putSerializable("mLujingDataToBeModified", (Serializable) mLujingDataToBeModified);
                    Intent intent2 = new Intent(LujingActivity.this, ZxingScanActivity.class);

                    bundle2.putSerializable("requestCode", (Serializable) mRequestCodeFromMain);
                    intent2.putExtras(bundle2);

                    //运行时权限
                    if (ContextCompat.checkSelfPermission(LujingActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(LujingActivity.this,new String[]{Manifest.permission.CAMERA},1);
                    }else {
                        startActivityForResult(intent2, mRequestCodeFromMain);
                    }
                } else if (mRequestCodeFromMain == Constant.REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST ) {
                    bundle2.putSerializable("mOldBasedNewLujing", (Serializable) mOldBasedNewLujing);
                    Intent intent2 = new Intent(LujingActivity.this, ZxingScanActivity.class);

                    bundle2.putSerializable("requestCode", (Serializable) mRequestCodeFromMain);
                    intent2.putExtras(bundle2);

                    //运行时权限
                    if (ContextCompat.checkSelfPermission(LujingActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(LujingActivity.this,new String[]{Manifest.permission.CAMERA},1);
                    }else {
                        startActivityForResult(intent2, mRequestCodeFromMain);
                    }
                }


            }
        });

        mButtonRelateDx = (Button) findViewById(R.id.button_relate_dx);
        mButtonRelateDx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LujingActivity.this, RelatedDxActivity.class);
                Bundle bundle2 = new Bundle();
                if( mRequestCodeFromMain == Constant.REQUEST_CODE_ADD_TOTAL_NEW_LUJING ) {
                    /**
                     * 全新路径时，要输入路径名称才允许去关联电线，
                     */
                    TextInputEditText lujingNameTv = (TextInputEditText) findViewById(R.id.inputEditText_lujingName);
                    if(lujingNameTv.getText().toString().equals("")){
                        Toast.makeText(LujingActivity.this, " 关联电线前，请输入路径名称", Toast.LENGTH_LONG).show();
                        return;
                    }
                    bundle2.putSerializable("requestCode", (Serializable) mRequestCodeFromMain);
                    bundle2.putSerializable("mNewLujing", (Serializable) mNewLujing);
                } else if (mRequestCodeFromMain == Constant.REQUEST_CODE_MODIFY_LUJING ) {
                    bundle2.putSerializable("requestCode", (Serializable) mRequestCodeFromMain);
                    bundle2.putSerializable("mLujingDataToBeModified", (Serializable) mLujingDataToBeModified);
                } else if (mRequestCodeFromMain == Constant.REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST ) {
                    bundle2.putSerializable("requestCode", (Serializable) mRequestCodeFromMain);
                    bundle2.putSerializable("mOldBasedNewLujing", (Serializable) mOldBasedNewLujing);
                }
                intent.putExtras(bundle2);
                startActivityForResult(intent, REQUEST_CODE_RELATEd_DX);
            }
        });

        mButtonOk = (Button) findViewById(R.id.button_create_lj);
        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 访问服务端去加全新路径、基于旧路径加新路径、修改路径。
                 * 成功后再返回主页面。否则留在本页。
                 */
                if( mRequestCodeFromMain == Constant.REQUEST_CODE_ADD_TOTAL_NEW_LUJING ) {
                    TextInputEditText lujingNameTv = (TextInputEditText) findViewById(R.id.inputEditText_lujingName);
                    if(lujingNameTv.isEnabled() && lujingNameTv.getText().toString().equals("")){
                    Toast.makeText(LujingActivity.this, " 路径名称不能为空：", Toast.LENGTH_LONG).show();
                        Log.d("", "路径名称不能为空");
                        return;
                    }
                    //TODO 检测路径名称唯一性。。。
                    mNewLujing.setName(lujingNameTv.getText().toString());
                    mNewLujing.setCreator("当前账号");
                    mNewLujing.setCreate_time(new Date());

                    LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
                    mPostValue.put("name", new Gson().toJson(mNewLujing.getName()));
                    mNetwork.addNewLujing(addNewLujingUrl, mPostValue, new AddNewLujingHandler());
                } else if (mRequestCodeFromMain == Constant.REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST ) {
                    TextInputEditText lujingNameTv = (TextInputEditText) findViewById(R.id.inputEditText_lujingName);
                    mOldBasedNewLujing.setName(lujingNameTv.getText().toString());
                    mOldBasedNewLujing.setCreator("当前账号");
                    mOldBasedNewLujing.setCreate_time(new Date());

                    // 把路径传给服务端而不是传给主界面，返回主界面时，主界面要刷新
                    LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
                    mPostValue.put("name", new Gson().toJson(mOldBasedNewLujing.getName()));
                    String theUrl = modifyLujingUrl.replace("{id}", String.valueOf(mOldBasedNewLujing.getId()));
                    mNetwork.modifyLujingName(modifyLujingUrl, mPostValue, new ModifyLujingHandler());
                } else if (mRequestCodeFromMain == Constant.REQUEST_CODE_MODIFY_LUJING) {
                    TextInputEditText lujingNameTv = (TextInputEditText) findViewById(R.id.inputEditText_lujingName);
                    mLujingDataToBeModified.setName(lujingNameTv.getText().toString());
                    mLujingDataToBeModified.setCreator("当前账号");
                    mLujingDataToBeModified.setCreate_time(new Date());

                    LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
                    mPostValue.put("name", new Gson().toJson(mOldBasedNewLujing.getName()));
                    String theUrl = modifyLujingUrl.replace("{id}", String.valueOf(mOldBasedNewLujing.getId()));
                    mNetwork.modifyLujingName(modifyLujingUrl, mPostValue, new ModifyLujingHandler());
                }



            }
        });


    }

    // 无论是 修改路径，还是基于已有路径新建路径，都需要获该路径取原有的间距列表
    private void getGetLujingDistanceList(Intent intent) {
        //获取 路径对应的间距列表
        LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
        mPostValue.put("account", "z");
        String theUrl;
        if (mRequestCodeFromMain == Constant.REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST) {
            theUrl = getLujingDistanceListUrl.replace("lujingID", String.valueOf(mOldBasedNewLujing.getId()));
        } else if (mRequestCodeFromMain == Constant.REQUEST_CODE_MODIFY_LUJING) {
            theUrl = getLujingDistanceListUrl.replace("lujingID", String.valueOf(mLujingDataToBeModified.getId()));
        } else { //Constant.REQUEST_CODE_ADD_TOTAL_NEW_LUJING
            theUrl = getLujingDistanceListUrl.replace("lujingID", String.valueOf(mNewLujing.getId()));
        }
        mNetwork.fetchDistanceListOfLujing(theUrl, mPostValue, getLujingDistanceListHandler);
        Log.i(TAG, "刷新了间距列表");
    }
    private void showDistanceList(){
        //间距列表
        mDistanceRV = (RecyclerView) findViewById(R.id.rv_distance);
        LinearLayoutManager manager5 = new LinearLayoutManager(this);
        manager5.setOrientation(LinearLayoutManager.VERTICAL);
        mDistanceRV.setLayoutManager(manager5);
        mDistanceAdapter = new DistanceAdapter(mDistanceList, this);
        mDistanceRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mDistanceRV.setAdapter(mDistanceAdapter);


        // 设置item及item中控件的点击事件
        mDistanceAdapter.setOnItemClickListener(MyItemClickListener);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SCAN_QRCODE_START:
                if (resultCode == RESULT_OK)
                {
                    // 取出Intent里的扫码结果
                    List<DistanceData> list = (List<DistanceData>) data.getSerializableExtra("mScanResultDistanceList");
                    for(int i =0; i<list.size(); i++ ) {
                        Toast.makeText(this, " 扫码获得的结果信息1：" + list.get(i).getName(), Toast.LENGTH_LONG).show();
                        //把扫码新加的各个间距加入间距列表
                        mDistanceList.add(list.get(i));
                        mDistanceAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case REQUEST_CODE_RELATEd_DX: //关联电线
                if (resultCode == RESULT_OK)
                {
                    // 取出Intent里的 选中的 电线
                    List<DianxianQingCeData> list = (List<DianxianQingCeData>) data.getSerializableExtra("mScanResultDistanceList");
                    for(int i =0; i<list.size(); i++ ) {
                        Toast.makeText(this, " 选中的电线：" + list.get(i).getSerial_number(), Toast.LENGTH_LONG).show();

//                        mLujing.add(list.get(i));
//                        mDistanceAdapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * item＋item里的控件点击监听事件
     */
    private DistanceAdapter.OnItemClickListener MyItemClickListener = new DistanceAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, DistanceAdapter.ViewName viewName, int position) {
            //viewName可区分item及item内部控件
            switch (v.getId()) {
                case R.id.button_distance_up:
                    Toast.makeText(LujingActivity.this,"你点击了 Up 按钮"+(position+1),Toast.LENGTH_SHORT).show();
//                    gotoAddNewLujing(Constant.REQUEST_CODE_MODIFY_LUJING, mLujingList.get(position));
                    break;
                case R.id.button_distance_down:
//                    gotoAddNewLujing(Constant.REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST,null);
                    Toast.makeText(LujingActivity.this,"你点击了 Down 按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_distance_delete:
                    Toast.makeText(LujingActivity.this, "你点击了 删除 jj 按钮" + (position + 1), Toast.LENGTH_SHORT).show();
                    //TODO 警告之后再删除??
                    mDistanceList.remove(position);
                    mDistanceAdapter.notifyDataSetChanged();
                    break;
                default:
                    Toast.makeText(LujingActivity.this, "你点击了item按钮" + (position + 1), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        @Override
        public void onItemLongClick(View v) {

        }
    };


    @SuppressLint("HandlerLeak")
    class GetLujingDistanceListHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {
//            if(mLoadingProcessDialog != null && mLoadingProcessDialog.isShowing()) {
//                mLoadingProcessDialog.dismiss();
//            }

            if (msg.what == Network.OK) {
                Log.d(TAG, "GetLujingDistanceListHandler OKKK");
                mDistanceList = (ArrayList<DistanceData>) msg.obj;
                if (mDistanceList == null) {
                    Log.d(TAG, "handleMessage: " + "间距数量为0或异常"  );
                } else {
                    Log.d(TAG, "handleMessage: size: " + mDistanceList.size());
                    if (mDistanceList.size() == 0) {
                        Toast.makeText(LujingActivity.this, "该路径的间距数量为0！", Toast.LENGTH_SHORT).show();
                    } else {
                        mDistanceAdapter = new DistanceAdapter(mDistanceList, LujingActivity.this);
                        mDistanceRV.addItemDecoration(new DividerItemDecoration(LujingActivity.this, DividerItemDecoration.VERTICAL));
                        mDistanceRV.setAdapter(mDistanceAdapter);
                        mDistanceAdapter.notifyDataSetChanged();
                        // 设置item及item中控件的点击事件
                        mDistanceAdapter.setOnItemClickListener(MyItemClickListener);
                    }
                }
            } else {
                String errorMsg = (String) msg.obj;
                Log.d(TAG, "errorMsg");
                Toast.makeText(LujingActivity.this, "获取该路径的 间距列表失败！" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
    @SuppressLint("HandlerLeak")
    class AddNewLujingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Network.OK) {
                ShowMessage.showToast(LujingActivity.this,"添加路径成功！",ShowMessage.MessageDuring.SHORT);

                Intent intent =getIntent();
                intent.setClass(LujingActivity.this, Main.class);
                intent.putExtra("mNewLujing", (Serializable) mNewLujing);
                LujingActivity.this.setResult(RESULT_OK, intent);
                LujingActivity.this.finish();
            }else {
                ShowMessage.showDialog(LujingActivity.this,"添加路径出错！");
            }
        }
    }

    /**
     * 在新建全新路径，点击扫码，需要先生成路径，之后再跳到扫码界面。
     */
    @SuppressLint("HandlerLeak")
    class AddNewLujingBeforeGotoScanHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Network.OK) {
                ShowMessage.showToast(LujingActivity.this,"添加路径成功，跳转到扫码！",ShowMessage.MessageDuring.SHORT);
//                在新建全新路径，点击扫码，需要先生成路径，如何再跳到扫码界面。
                Intent intent2 = new Intent(LujingActivity.this, ZxingScanActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("requestCode", (Serializable) mRequestCodeFromMain);
//                msg.obj
//                mNewLujing.setId( Integer.parseInt( (LinkedTreeMap) msg.obj).get("id") ));
//                int id = Integer.parseInt ((LinkedTreeMap) msg.obj.).get("id").toString());

//                ((LinkedTreeMap) msg.obj).findByObject("id") /// 解析  {errorCode=0.0, id=56.0}
               String idStr = ((LinkedTreeMap) msg.obj).get("id").toString();
               int lujingID = Double.valueOf(idStr).intValue();
               mNewLujing.setId(lujingID);
                bundle2.putSerializable("mNewLujing", (Serializable) mNewLujing);
                intent2.putExtras(bundle2);

                //运行时权限
                if (ContextCompat.checkSelfPermission(LujingActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(LujingActivity.this,new String[]{Manifest.permission.CAMERA},1);
                }else {
                    startActivityForResult(intent2, mRequestCodeFromMain);
                }
            }else {
                ShowMessage.showDialog(LujingActivity.this,"添加路径出错！");
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class ModifyLujingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Network.OK) {
//                ShowMessage.showToast(LujingActivity.this," 修改路径成功！",ShowMessage.MessageDuring.SHORT);
                Intent intent =getIntent();
                intent.setClass(LujingActivity.this, Main.class);
                intent.putExtra("mLujingDataToBeModified", (Serializable) mLujingDataToBeModified);
                LujingActivity.this.setResult(RESULT_OK, intent);
                LujingActivity.this.finish();
            }else {
                ShowMessage.showDialog(LujingActivity.this,"修改路径出错！请检查网络！");
            }
        }
    }
}