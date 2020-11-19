package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.adapter.ProjectAdapter;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.bean.basic.ProjectData;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.basic.Wires;
import com.zhihuta.xiaota.bean.response.GetWiresResponse;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.net.Network;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ProjectsCenterActivity extends AppCompatActivity {

    private Button mCreateNewProjectBt;

    private ArrayList<ProjectData> mProjectList;
    private ProjectAdapter mProjectAdapter;
    private RecyclerView mProjectRV;

    private Network mNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects_center);
        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

    private void initViews() {
        mCreateNewProjectBt = (Button)findViewById(R.id.createNewProjectBt);
        mCreateNewProjectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(ProjectsCenterActivity.this);
                android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectsCenterActivity.this);
                alertDialogBuilder.setTitle("输入项目名称：")
                        .setView(et)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LinkedHashMap<String, String> newProjectParameters = new LinkedHashMap<>();
                                String strNewPathName = et.getText().toString();
                                if (strNewPathName == null || strNewPathName.isEmpty()) { //不允许名称为空
                                    Toast.makeText(ProjectsCenterActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                                } else {
                                    newProjectParameters.put("name",  strNewPathName);
                                    //TODO
//                                    mNetwork.addNewLujing(Constant.addNewLujingUrl, newPathParameters, new Main.NewLujingHandler(strNewPathName));
                                }
                            }
                        })
                        .show();
            }
        });

        LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
        /// mPostValue 在后续会用到，比如不同用户，获取各自公司的电线
        mPostValue.put("company","2");
        mNetwork.get(Constant.getProjectListOfCompanyUrl, mPostValue, new GetProjectListOfCompanyHandler(),(handler, msg)->{
            handler.sendMessage(msg);
        });

    }
    @SuppressLint("HandlerLeak")
    class GetProjectListOfCompanyHandler extends Handler {

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
            String errorMsg = "";

//            try {
//
//                if (msg.what == Network.OK) {
//                    Result result= (Result)(msg.obj);
//
//                    GetWiresResponse responseData = CommonUtility.objectToJavaObject(result.getData(), GetWiresResponse.class);
//
//                    if (responseData != null &&responseData.errorCode == 0)
//                    {
//                        mDianxianTobeSelectList = new ArrayList<>();
//
//                        for (Wires wire : responseData.wires) {
//
//                            DianxianQingCeData dianxianQingCeData = new DianxianQingCeData();
//                            dianxianQingCeData.setId(wire.getId());
//                            dianxianQingCeData.setEnd_point(wire.getEndPoint());
//                            dianxianQingCeData.setHose_redundancy(Double.toString(wire.getHoseRedundancy()));
//                            dianxianQingCeData.setLength(wire.getLength());
//                            dianxianQingCeData.setParts_code(wire.getPartsCode());
//                            dianxianQingCeData.setSerial_number(wire.getSerialNumber());
//                            dianxianQingCeData.setStart_point(wire.getStartPoint());
//                            dianxianQingCeData.setSteel_redundancy(Double.toString(wire.getSteelRedundancy()));
//                            dianxianQingCeData.setWickes_cross_section(wire.getWickesCrossSection());
//
//                            mDianxianTobeSelectList.add( dianxianQingCeData);
//                        }
//
//                        Log.d(TAG, "电线数量: size: " + mDianxianTobeSelectList.size());
//
//                        if (mDianxianTobeSelectList.size() == 0) {
//                            Toast.makeText(RelateNewDxActivity.this, "电线数量为0！", Toast.LENGTH_SHORT).show();
//                        }
//
//                        for (int k = 0; k < mDianxianTobeSelectList.size(); k++) {
//                            mDianxianTobeSelectList.get(k).setFlag(Constant.FLAG_TOBE_SELECT_DX);
//                            checkedList.add(false); //初始时都是未选中。
//                        }
//
//                        if (mDianXianToBeSelectedAdapter == null)
//                        {
//                            mDianXianToBeSelectedAdapter = new DianXianQingceAdapter(mDianxianTobeSelectList, RelateNewDxActivity.this);
//                            mDxRV.addItemDecoration(new DividerItemDecoration(RelateNewDxActivity.this, DividerItemDecoration.VERTICAL));
//                            mDxRV.setAdapter(mDianXianToBeSelectedAdapter);
//                            mDianXianToBeSelectedAdapter.setOnItemClickListener(MyItemClickListener);
//                        }
//                        mDianXianToBeSelectedAdapter.updateDataSoruce(mDianxianTobeSelectList);
//                    }
//                    else
//                    {
//                        errorMsg =  "电线获取异常:"+ result.getCode() + result.getMessage();
//                        Log.d(TAG, errorMsg );
//                    }
//                }
//                else
//                {
//                    errorMsg = (String) msg.obj;
//                }
//
//                if (!errorMsg.isEmpty())
//                {
//                    Log.d("项目列表获取 NG:", errorMsg);
//                    Toast.makeText(ProjectsCenterActivity.this, "项目列表获取失败！" + errorMsg, Toast.LENGTH_SHORT).show();
//                }
//            }
//            catch (Exception ex)
//            {
//                Log.d("项目列表获取 NG:", ex.getMessage());
//            }
//            finally {
//                setIsGetting(false);
//            }
        }
    }
}