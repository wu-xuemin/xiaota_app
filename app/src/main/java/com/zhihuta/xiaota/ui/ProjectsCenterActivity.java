package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.ProjectAdapter;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.ProjectEx;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.response.GetProjectsResponse;
import com.zhihuta.xiaota.bean.response.LoginResponseData;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.common.RequestUrlUtility;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ProjectsCenterActivity extends AppCompatActivity {

    private static String TAG = "ProjectsCenterActivity";
    private Button mCreateNewProjectBt;

    private List<ProjectEx> mProjectList;
    private ProjectAdapter mProjectAdapter;
    private RecyclerView mProjectRV;

    private Network mNetwork;

    LoginResponseData loginResponseData;
    int  mRequestCodeFroPrev = 0;

    SwipeRefreshLayout mSwipeRefreshLayout;
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
        mNetwork = Network.Instance(getApplication());

        //get login infor from Loginactivity
        Intent intent = getIntent();
        String strLoginResponseJson = (String) intent.getExtras().getSerializable("loginResponseData");
        loginResponseData = JSON.parseObject(strLoginResponseJson, LoginResponseData.class);
        //
        mRequestCodeFroPrev = intent.getIntExtra("requestCode", 0);

        initViews();
        showProjectList();
        refreshLayout();
    }

    void refreshLayout()
    {
        mSwipeRefreshLayout.setRefreshing(true);

        String url = RequestUrlUtility.build(URL.GET_PROJECT_LIST_OF_COMPANY);
        mNetwork.get(url, null, new GetProjectListOfCompanyHandler(),(handler, msgGetProject)->{
            handler.sendMessage(msgGetProject);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (mRequestCodeFroPrev == "initialSelectEntry".hashCode()) {
                    confirmExitOrNot();
                }
                else
                {
                    this.finish(); // back button
                }

                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {

        mProjectList = new ArrayList<>();
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
                                String strNewProjectName = et.getText().toString();
                                if (strNewProjectName == null || strNewProjectName.isEmpty()) { //不允许名称为空
                                    Toast.makeText(ProjectsCenterActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                                } else {
                                    newProjectParameters.put("project_name",  strNewProjectName);
                                    //新建项目
                                    mNetwork.post(Constant.postAddProjectUrl,newProjectParameters,new AddNewProjectHandler(),
                                            (handler, msg)->{
                                                handler.sendMessage(msg);
                                            } );

                                }
                            }
                        })
                        .show();
            }
        });
    }
    private void showProjectList(){
        //电线列表
        mProjectRV = (RecyclerView) findViewById(R.id.rv_project);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mProjectRV.setLayoutManager(manager);
        mProjectAdapter = new ProjectAdapter(mProjectList,this, null);
        if (mProjectRV.getItemDecorationCount() == 0)
        {
            mProjectRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        }
        mProjectRV.setAdapter(mProjectAdapter);

        // 设置item及item中控件的点击事件
        mProjectAdapter.setOnItemClickListener(MyItemClickListener);

        mSwipeRefreshLayout = findViewById(R.id.project_swipeRefresh);
//        mSwipeRefreshLayout.setProgressViewOffset(false,100);
//        mSwipeRefreshLayout.setProgressViewEndTarget();
        //mSwipeRefreshLayout.setDistanceToTriggerSync(500);//default 64

        CommonUtility.setDistanceToTriggerSync(mSwipeRefreshLayout,this,0.6f, 400);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    class AddNewProjectHandler extends Handler {

        @Override
        public void handleMessage(final Message msg) {
            String errorMsg = "";

            try {

                 errorMsg = RequestUrlUtility.getResponseErrMsg(msg);

                 if (errorMsg!= null)
                 {
                     Log.d("添加项目 NG:", errorMsg);
                     Toast.makeText(ProjectsCenterActivity.this, "添加项目失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                     return;
                 }

                Toast.makeText(ProjectsCenterActivity.this, "添加项目成功", Toast.LENGTH_SHORT).show();
                //项目添加成功，再刷新一次
                refreshLayout();

            } catch (Exception ex) {
                Log.d("添加项目 NG:", ex.getMessage());
            }
            finally {

            }
        }
    }

    @SuppressLint("HandlerLeak")
    class DeleteProjectHandler extends Handler {

        @Override
        public void handleMessage(final Message msg) {
            String errorMsg = "";

            try {

                errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
                if (errorMsg != null)
                {
                    Toast.makeText(ProjectsCenterActivity.this, "删除项目异常" + errorMsg, Toast.LENGTH_SHORT).show();

                    return;
                }

                Result result= (Result)(msg.obj);

                Toast.makeText(ProjectsCenterActivity.this, "删除项目成功", Toast.LENGTH_SHORT).show();
                //删除添加成功，再刷新一次

                refreshLayout();

            } catch (Exception ex) {
                Log.d("删除项目 NG:", ex.getMessage());
            }
            finally {

            }
        }
    }
    @SuppressLint("HandlerLeak")
    class GetProjectListOfCompanyHandler extends Handler {

        @Override
        public void handleMessage(final Message msg) {
            String errorMsg = "";

            try {

                errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
                if (errorMsg!= null)
                {
                    Log.d("项目获取 NG:", errorMsg);
                    Toast.makeText(ProjectsCenterActivity.this, "项目获取失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                    return;
                }

                Result result= (Result)(msg.obj);

                GetProjectsResponse responseData = CommonUtility.objectToJavaObject(result.getData(), GetProjectsResponse.class);

                mProjectList = responseData.project_list;
                if (mProjectList == null)
                {
                    mProjectList = new ArrayList<>();
                }
                Log.d(TAG, "项目数量: size: " + mProjectList.size());

                if (mProjectList.size() == 0) {
                    Toast.makeText(ProjectsCenterActivity.this, "项目数量为0！", Toast.LENGTH_SHORT).show();
                }
                mProjectAdapter = null;
                mProjectAdapter = new ProjectAdapter(mProjectList, ProjectsCenterActivity.this, null);
                if ( mProjectRV.getItemDecorationCount() == 0)
                {
                    mProjectRV.addItemDecoration(new DividerItemDecoration(ProjectsCenterActivity.this, DividerItemDecoration.VERTICAL));
                }

                mProjectRV.setAdapter(mProjectAdapter);
                mProjectAdapter.setOnItemClickListener(MyItemClickListener);

                mProjectAdapter.notifyDataSetChanged();
            }
            catch (Exception ex)
            {
                Log.d("项目获取 NG:", ex.getMessage());
            }
            finally {

                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    /**
     * 项目中心item 里的控件点击监听事件
     */
    private ProjectAdapter.OnItemClickListener MyItemClickListener = new ProjectAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, ProjectAdapter.ViewName viewName, int position) {
            //viewName可区分item及item内部控件
            switch (v.getId()) {

                case R.id.projectMingChenTextView:
                    Toast.makeText(ProjectsCenterActivity.this, "你点击了 项目名称" + (position + 1), Toast.LENGTH_SHORT).show();
                    //

                    break;
                case R.id.button_delete_project:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectsCenterActivity.this);
                    alertDialogBuilder.setTitle("确认删除项目 " + mProjectList.get(position).getProjectName() + "吗？" )
//                            .setView(et)
                            .setNegativeButton("取消", null)
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    String theUrl = Constant.deleteProjectUrl.replace("{id}", String.valueOf(mProjectList.get(position).getId()));
                                    mNetwork.delete(theUrl, null, new DeleteProjectHandler(),(handler, msg)->{
                                        handler.sendMessage(msg);
                                    });
                                }
                            })
                            .show();
                    break;
                case R.id.button_member_manager:
                    //Toast.makeText(ProjectsCenterActivity.this, "你点击了 项目 成员管理" + (position + 1), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProjectsCenterActivity.this, ProjectMemberManageActivity.class);
                    intent.putExtra("mProject", (Serializable) mProjectList.get(position));
                    startActivity(intent);
                    break;

                case R.id.button_open_project:


                    alertDialogBuilder = new AlertDialog.Builder(ProjectsCenterActivity.this);
                    alertDialogBuilder.setTitle("确认打开项目 " + mProjectList.get(position).getProjectName() + "吗？" )
//                            .setView(et)
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intentMain = new Intent(ProjectsCenterActivity.this, Main.class);
                                    String strResponseData = JSON.toJSONString(loginResponseData);
                                    intentMain.putExtra("loginResponseData", (Serializable) strResponseData);
                                    intentMain.putExtra("project_id", (Serializable) String.valueOf(mProjectList.get(position).getId()));

                                    startActivity(intentMain);
                                    finish();
                                }
                            })
                            .show();

                    break;
                default:
                    break;
            }
        }

        @Override
        public void onItemLongClick(View v) {

        }
    };

    private void confirmExitOrNot()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectsCenterActivity.this);
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

        return ;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //if (mConfirmedExit == false)
        {
            if(keyCode== KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN) {
                if (mRequestCodeFroPrev == "initialSelectEntry".hashCode()) {

                    confirmExitOrNot();

                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}