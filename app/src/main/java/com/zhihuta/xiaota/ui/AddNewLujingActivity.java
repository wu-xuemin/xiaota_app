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
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DistanceAdapter;
import com.zhihuta.xiaota.adapter.LujingAdapter;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.bean.basic.DistanceData;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class AddNewLujingActivity extends AppCompatActivity {

    private static String TAG = "AddNewLujingActivity";
    private DistanceAdapter mDistanceAdapter;
    private ArrayList<DistanceData> mDistanceList = new ArrayList<>();
    private RecyclerView mDistanceRV;

    private Network mNetwork;
    //获取 路径对应的间距列表 (编辑路径时，或 在基于已有路径 新建路径时)
    String getLujingDistanceListUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIP() + URL.GET_LUJING_DISTANCE_LIST;

    private GetLujingDistanceListHandler getLujingDistanceListHandler;

    private Button mButtonScanToAddXianduan; // 扫码去添加线段
    private Button mButtonRelateDx; // 去关联电缆电线
    private Button mButtonOkToCreateLujing; // 去关联电缆电线

    private static final int REQUEST_CODE_SCAN_QRCODE_START = 1;
    private static final int REQUEST_CODE_RELATEd_DX =2;

    private LujingData mNewLujing = new LujingData(); //新建的路径
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
    private void initViews() {

        //获取传递过来的信息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(intent.getExtras().getSerializable("requestCode").equals(Constant.REQUEST_CODE_MODIFY_LUJING)){
            // 如果是修改路径，那路径名称不变, 标题设为 编辑路径
            LujingData lujingDataToBeModified = (LujingData) intent.getExtras().getSerializable("tobeModifiedOrBasedLujing");
            TextInputEditText lujingNameTv = (TextInputEditText) findViewById(R.id.inputEditText_lujingName);

            lujingNameTv.setText( lujingDataToBeModified.getName());
            lujingNameTv.setEnabled(false);
            this.setTitle("编辑路径");
            getGetLujingDistanceList(intent);
        }
        // 在基于已有路径 新建路径
        if(intent.getExtras().getSerializable("requestCode").equals(Constant.REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST) ){

            getGetLujingDistanceList(intent);
        }

        showDistanceList();
        mButtonScanToAddXianduan = (Button) findViewById(R.id.button_scan_to_add_xianduan);
        mButtonScanToAddXianduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewLujingActivity.this, ZxingScanActivity.class);
//                startActivity(intent);

//运行时权限
                if (ContextCompat.checkSelfPermission(AddNewLujingActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AddNewLujingActivity.this,new String[]{Manifest.permission.CAMERA},1);
                }else {
                    startActivityForResult(intent, REQUEST_CODE_SCAN_QRCODE_START);
                }

            }
        });

        mButtonRelateDx = (Button) findViewById(R.id.button_relate_dx);
        mButtonRelateDx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewLujingActivity.this, RelatedDxActivity.class);
                startActivityForResult(intent, REQUEST_CODE_RELATEd_DX);
            }
        });

        mButtonOkToCreateLujing = (Button) findViewById(R.id.button_create_lj);
        mButtonOkToCreateLujing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText lujingNameTv = (TextInputEditText) findViewById(R.id.inputEditText_lujingName);
                if(lujingNameTv.isEnabled() && lujingNameTv.getText().toString().equals("")){
//                    Toast.makeText(AddNewLujingActivity.this, " 路径名称不能为空：", Toast.LENGTH_LONG).show();
                    Log.d("", "路径名称不能为空");
                    return;
                }
                //TODO 检测路径名称唯一性。。。
                mNewLujing.setName(lujingNameTv.getText().toString());
                mNewLujing.setCreator("当前账号");
                mNewLujing.setCreate_time(new Date());

//                Intent intent = getIntent();
//                intent.setClass(ZxingScanActivity.this, AddNewLujingActivity.class);
//                intent.putExtra("mScanResultDistanceList", (Serializable) mScanResultDistanceList);
//                ZxingScanActivity.this.setResult(RESULT_OK, intent);
                Intent intent =getIntent();
                intent.setClass(AddNewLujingActivity.this, Main.class);
                intent.putExtra("mNewLujing", (Serializable) mNewLujing); // putSerializable和区别 ？？？？？？？

                AddNewLujingActivity.this.setResult(RESULT_OK, intent);

                Log.d("", "新路径  传回去");
                AddNewLujingActivity.this.finish();
            }
        });


    }

    private void getGetLujingDistanceList(Intent intent){
        //获取 路径对应的间距列表
        LujingData oldBasedlujingData = (LujingData) intent.getExtras().getSerializable("tobeModifiedOrBasedLujing");
        LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
        mPostValue.put("account","z");
        String theUrl = getLujingDistanceListUrl.replace("lujingID", String.valueOf(oldBasedlujingData.getId()));
        mNetwork.fetchDistanceListOfLujing(theUrl, mPostValue, getLujingDistanceListHandler);//ok
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
//                    List<DistanceData> list = (List<DistanceData>) getIntent().getSerializableExtra("mScanResultDistanceList");
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
                    Toast.makeText(AddNewLujingActivity.this,"你点击了 Up 按钮"+(position+1),Toast.LENGTH_SHORT).show();
//                    gotoAddNewLujing(Constant.REQUEST_CODE_MODIFY_LUJING, mLujingList.get(position));
                    break;
                case R.id.button_distance_down:
//                    gotoAddNewLujing(Constant.REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST,null);
                    Toast.makeText(AddNewLujingActivity.this,"你点击了 Down 按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_distance_delete:
                    Toast.makeText(AddNewLujingActivity.this, "你点击了 删除 jj 按钮" + (position + 1), Toast.LENGTH_SHORT).show();
                    //TODO 警告之后再删除??
                    mDistanceList.remove(position);
                    mDistanceAdapter.notifyDataSetChanged();
                    break;
                default:
                    Toast.makeText(AddNewLujingActivity.this, "你点击了item按钮" + (position + 1), Toast.LENGTH_SHORT).show();
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
                Log.d(TAG , "GetLujingDistanceListHandler OKKK");
                mDistanceList = (ArrayList<DistanceData>)msg.obj;
                Log.d(TAG, "handleMessage: size: " + mDistanceList.size());
                if (mDistanceList.size()==0){
                    Toast.makeText(AddNewLujingActivity.this, "该路径的间距数量为0！", Toast.LENGTH_SHORT).show();
                } else {
                    mDistanceAdapter = new DistanceAdapter(mDistanceList,AddNewLujingActivity.this);
                    mDistanceRV.addItemDecoration(new DividerItemDecoration(AddNewLujingActivity.this,DividerItemDecoration.VERTICAL));
                    mDistanceRV.setAdapter(mDistanceAdapter);
                    mDistanceAdapter.notifyDataSetChanged();
                    // 设置item及item中控件的点击事件
                    mDistanceAdapter.setOnItemClickListener(MyItemClickListener);
                }
            } else {
                String errorMsg = (String)msg.obj;
                Log.d(TAG, "errorMsg");
                Toast.makeText(AddNewLujingActivity.this, "获取该路径的 间距列表失败！" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}