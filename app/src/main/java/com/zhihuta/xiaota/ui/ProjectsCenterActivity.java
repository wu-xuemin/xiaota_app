package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.ProjectAdapter;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.ProjectData;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.response.GetProjectsResponse;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.net.Network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ProjectsCenterActivity extends AppCompatActivity {

    private static String TAG = "ProjectsCenterActivity";
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
        mNetwork = Network.Instance(getApplication());
        initViews();
        showProjectList();

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

        mNetwork.get(Constant.getProjectListOfCompanyUrl, null, new GetProjectListOfCompanyHandler(),(handler, msg)->{
            handler.sendMessage(msg);
        });

    }
    private void showProjectList(){
        //电线列表
        mProjectRV = (RecyclerView) findViewById(R.id.rv_project);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mProjectRV.setLayoutManager(manager);
        mProjectAdapter = new ProjectAdapter(mProjectList,this, null);
        mProjectRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mProjectRV.setAdapter(mProjectAdapter);

        // 设置item及item中控件的点击事件
        mProjectAdapter.setOnItemClickListener(MyItemClickListener);

    }

    class AddNewProjectHandler extends Handler {

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

            try {
                if (msg.what == Network.OK) {
                    Result result= (Result)(msg.obj);
                    if(result.getMessage().equals("SUCCESS")){
                        Toast.makeText(ProjectsCenterActivity.this, "添加项目成功", Toast.LENGTH_SHORT).show();
                        //项目添加成功，再刷新一次
                        mNetwork.get(Constant.getProjectListOfCompanyUrl, null, new GetProjectListOfCompanyHandler(),(handler, msgGetProject)->{
                            handler.sendMessage(msgGetProject);
                        });
                    } else {
                        Toast.makeText(ProjectsCenterActivity.this, "添加项目异常", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    errorMsg = (String) msg.obj;
                }

                if (!errorMsg.isEmpty()) {
                    Log.d("添加项目 NG:", errorMsg);
                    Toast.makeText(ProjectsCenterActivity.this, "添加项目失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Log.d("添加项目 NG:", ex.getMessage());
            }
            finally {
                setIsGetting(false);
            }
        }
    }

    class DeleteProjectHandler extends Handler {

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

            try {
                if (msg.what == Network.OK) {
                    Result result= (Result)(msg.obj);
                    if(result.getMessage().equals("SUCCESS")){
                        Toast.makeText(ProjectsCenterActivity.this, "删除项目成功", Toast.LENGTH_SHORT).show();
                        //删除添加成功，再刷新一次
                        mNetwork.get(Constant.getProjectListOfCompanyUrl, null, new GetProjectListOfCompanyHandler(),(handler, msgGetProject)->{
                            handler.sendMessage(msgGetProject);
                        });
                    } else {
                        Toast.makeText(ProjectsCenterActivity.this, "删除项目异常", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    errorMsg = (String) msg.obj;
                }

                if (!errorMsg.isEmpty()) {
                    Log.d("删除项目 NG:", errorMsg);
                    Toast.makeText(ProjectsCenterActivity.this, "删除项目失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Log.d("删除项目 NG:", ex.getMessage());
            }
            finally {
                setIsGetting(false);
            }
        }
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

            try {

                if (msg.what == Network.OK) {
                    Result result= (Result)(msg.obj);

                    GetProjectsResponse responseData = CommonUtility.objectToJavaObject(result.getData(), GetProjectsResponse.class);

                    if (responseData != null &&responseData.errorCode == 0) {
                        mProjectList = new ArrayList<>();

                        for (ProjectData projectData : responseData.project_list) {

                            ProjectData projectData1 = new ProjectData();
                            projectData1.setId(projectData.getId());
                            projectData1.setCompanyId(projectData.getCompanyId());
                            projectData1.setCreateTime((projectData.getCreateTime()));
                            projectData1.setCreatorId(projectData.getCreatorId());
                            projectData1.setDepartmentId(projectData.getDepartmentId());
                            projectData1.setModiferId(projectData.getModiferId());
                            projectData1.setModifyTime(projectData.getModifyTime());
                            projectData1.setProjectName((projectData.getProjectName()));
                            projectData1.setStatus(projectData.getStatus());

                            mProjectList.add(projectData1);
                        }

                        Log.d(TAG, "项目数量: size: " + mProjectList.size());

                        if (mProjectList.size() == 0) {
                            Toast.makeText(ProjectsCenterActivity.this, "项目数量为0！", Toast.LENGTH_SHORT).show();
                        }
                        mProjectAdapter = null;
                        mProjectAdapter = new ProjectAdapter(mProjectList, ProjectsCenterActivity.this, null);
                        mProjectRV.addItemDecoration(new DividerItemDecoration(ProjectsCenterActivity.this, DividerItemDecoration.VERTICAL));
                        mProjectRV.setAdapter(mProjectAdapter);
                        mProjectAdapter.setOnItemClickListener(MyItemClickListener);

                        mProjectAdapter.notifyDataSetChanged();
//                        mProjectAdapter.updateDataSoruce(mProjectList);
                    }
                    else
                    {
                        errorMsg =  "项目获取异常:"+ result.getCode() + result.getMessage();
                        Log.d(TAG, errorMsg );
                    }
                }
                else
                {
                    errorMsg = (String) msg.obj;
                }

                if (!errorMsg.isEmpty())
                {
                    Log.d("项目获取 NG:", errorMsg);
                    Toast.makeText(ProjectsCenterActivity.this, "项目获取失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                Log.d("项目获取 NG:", ex.getMessage());
            }
            finally {
                setIsGetting(false);
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
                    Toast.makeText(ProjectsCenterActivity.this, "你点击了 项目 成员管理" + (position + 1), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProjectsCenterActivity.this, ProjectMemberManageActivity.class);
                    intent.putExtra("mProject", (Serializable) mProjectList.get(position));
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onItemLongClick(View v) {

        }
    };
}