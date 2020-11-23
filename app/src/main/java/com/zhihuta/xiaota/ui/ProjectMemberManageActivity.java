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
import com.zhihuta.xiaota.adapter.MemberAdapter;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.MemberData;
import com.zhihuta.xiaota.bean.basic.ProjectData;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.response.ProjectMembersResponse;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.common.RequestUrlUtility;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ProjectMemberManageActivity extends AppCompatActivity {

    private static String TAG = "ProjectMemberManageActivity";
    private Button mCreateNewMemberBt;
    private Button mInviteAllCompanyMemberBt;
    private Button mRemoveAllCompanyMemberBt;

    private ProjectData mProject = null;
    private ArrayList<MemberData> mMemberList;
    private MemberAdapter mMemberAdapter;
    private RecyclerView mMemberRV;

    private Network mNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_member_manage);
        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mNetwork =  Network.Instance(getApplication());
        Intent intent = getIntent();
        mProject = (ProjectData) intent.getExtras().getSerializable("mProject");
        if(mProject == null){
            Toast.makeText(ProjectMemberManageActivity.this, "异常：没有获取到项目"  , Toast.LENGTH_SHORT).show();
        }
        initViews();
        showMemberList();
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

        mMemberList = new ArrayList<>();
        mCreateNewMemberBt = (Button)findViewById(R.id.createNewMemberBt);
        mCreateNewMemberBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(ProjectMemberManageActivity.this);
                android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectMemberManageActivity.this);
                alertDialogBuilder.setTitle("输入成员账号：")
                        .setView(et)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LinkedHashMap<String, String> newProjectParameters = new LinkedHashMap<>();
                                String strNewMember = et.getText().toString();
                                if (strNewMember == null || strNewMember.isEmpty()) { //不允许名称为空
                                    Toast.makeText(ProjectMemberManageActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
                                } else {
//                                    {
//                                        member_ids:[1,2,10,15,]//账号id列表,
//                                        member_accounts:[ a, test,myaccount]//账号列表，账号列表和账号ID列表任意一个都可以实现添加功能，主要是为了调用使用，用id列表效率会更高点。后台会处理两个列表，如果账号和账号id指向同一个人，那么也只会添加一次。
//                                    }
                                    LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
//"member_accounts":["ft"]
                                    mPostValue.put("member_accounts", strNewMember );
                                    String url = Constant.putProjectMemberUrl.replace("{id}", String.valueOf(mProject.getId()));
                                    mNetwork.put( url, mPostValue, new PutProjectMemberHandler(),(handler, msg)->{
                                        handler.sendMessage(msg);
                                    });
                                }
                            }
                        })
                        .show();
            }
        });

        mInviteAllCompanyMemberBt = (Button)findViewById(R.id.inviteAllCompanyMemberBt);
        mInviteAllCompanyMemberBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectMemberManageActivity.this);
                alertDialogBuilder.setTitle("确定要邀请所有公司人员?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String url = RequestUrlUtility.build(URL.PUT_PROJECT_MEMBERS_COMPANY.replace("{id}",String.valueOf(mProject.getId())));
                                mNetwork.put( url, null, new PutProjectMemberHandler(),(handler, msg)->{
                                    handler.sendMessage(msg);
                                });
                            }
                        })
                        .show();
            }
        });

        mRemoveAllCompanyMemberBt = (Button)findViewById(R.id.removeAllCompanyMemberBt);
        mRemoveAllCompanyMemberBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = RequestUrlUtility.build(URL.DELETE_PROJECT_MEMBERS_COMPANY.replace("{id}",String.valueOf(mProject.getId())));
                mNetwork.delete( url, null, new DeleteProjectMemberListHandler(),(handler, msg)->{
                    handler.sendMessage(msg);
                });
            }
        });


        String  url = Constant.getProjectMemberListUrl.replace("{id}", String.valueOf(mProject.getId()));
        mNetwork.get(url, null, new GetProjectMemberListHandler(),(handler, msg)->{
            handler.sendMessage(msg);
        });
    }

    @SuppressLint("HandlerLeak")
    class GetProjectMemberListHandler extends Handler {

        private boolean bIsGetting = false;
        public boolean getIsGetting()
        {
            return bIsGetting;
        }
        public void setIsGetting(boolean getting) {
            bIsGetting = getting;
        }

        @Override
        public void handleMessage(final Message msg) {
            String errorMsg = "";

            try {

                errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
                if (errorMsg != null)
                {
                    Log.d("项目成员获取 NG:", errorMsg);
                    Toast.makeText(ProjectMemberManageActivity.this, "项目成员获取失败！" + errorMsg, Toast.LENGTH_SHORT).show();

                    return;
                }

                Result result= (Result)(msg.obj);

                ProjectMembersResponse responseData = CommonUtility.objectToJavaObject(( result.getData()), ProjectMembersResponse.class);// CommonUtility.objectToJavaObject(result.getData()).get("accounts"), ProjectMembersResponse.class);

                mMemberList = new ArrayList<>();

                for (MemberData memberData : responseData.accounts) {

                    MemberData memberData1 = new MemberData();
                    memberData1.setId(memberData.getId());
                    memberData1.setAccount((memberData.getAccount()));
                    memberData1.setAddress(memberData.getAddress());
                    memberData1.setCompany(memberData.getCompany());
                    memberData1.setDeleted(memberData.getDeleted());
                    memberData1.setDepartment((memberData.getDepartment()));
                    memberData1.setEmail(memberData.getEmail());
                    memberData1.setName(memberData.getName());
                    memberData1.setPhone(memberData.getPhone());

                    mMemberList.add(memberData1);
                }

                Log.d(TAG, "成员数量: size: " + mMemberList.size());

                if (mMemberList.size() == 0) {
                    Toast.makeText(ProjectMemberManageActivity.this, "项目成员数量为0！", Toast.LENGTH_SHORT).show();
                }
                mMemberAdapter = null;
                mMemberAdapter = new MemberAdapter(mMemberList, ProjectMemberManageActivity.this, null);
                if (mMemberRV.getItemDecorationCount() == 0)
                {
                    mMemberRV.addItemDecoration(new DividerItemDecoration(ProjectMemberManageActivity.this, DividerItemDecoration.VERTICAL));
                }
                mMemberRV.setAdapter(mMemberAdapter);
                mMemberAdapter.setOnItemClickListener(MyItemClickListener);

                mMemberAdapter.notifyDataSetChanged();
            }
            catch (Exception ex)
            {
                Log.d("项目成员获取 NG:", ex.getMessage());
            }
            finally {
                setIsGetting(false);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class PutProjectMemberHandler extends Handler {

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

                errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
                if (errorMsg != null)
                {
                    Log.d("添加成员 NG:", errorMsg);
                    Toast.makeText(ProjectMemberManageActivity.this, "添加成员失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                    return;
                }

                Result result= (Result)(msg.obj);
                Toast.makeText(ProjectMemberManageActivity.this, "添加成员成功", Toast.LENGTH_SHORT).show();
                //成员添加成功，再刷新一次
                String  url = Constant.getProjectMemberListUrl.replace("{id}", String.valueOf(mProject.getId()));
                mNetwork.get(url, null, new GetProjectMemberListHandler(),(handler, msgGetMember)->{
                    handler.sendMessage(msgGetMember);
                });

            } catch (Exception ex) {
                Log.d("添加成员 NG:", ex.getMessage());
            }
            finally {
                setIsGetting(false);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class DeleteProjectMemberListHandler extends Handler {

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
                errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
                if (errorMsg != null)
                {
                    Log.d("删除成员 NG:", errorMsg);
                    Toast.makeText(ProjectMemberManageActivity.this, "删除成员失败！" + errorMsg, Toast.LENGTH_SHORT).show();

                    return;
                }

                Toast.makeText(ProjectMemberManageActivity.this, "删除成员成功", Toast.LENGTH_SHORT).show();
                //成员删除成功，再刷新一次
                String  url = Constant.getProjectMemberListUrl.replace("{id}", String.valueOf(mProject.getId()));
                mNetwork.get(url, null, new GetProjectMemberListHandler(),(handler, msgGetMember)->{
                    handler.sendMessage(msgGetMember);
                });

            } catch (Exception ex) {
                Log.d("删除成员 NG:", ex.getMessage());
            }
            finally {
                setIsGetting(false);
            }
        }
    }
    private void showMemberList(){
        //成员列表
        mMemberRV = (RecyclerView) findViewById(R.id.rv_project_member);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mMemberRV.setLayoutManager(manager);
        mMemberAdapter = new MemberAdapter(mMemberList,this, null);
        if (mMemberRV.getItemDecorationCount() == 0)
        {
            mMemberRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        }
        mMemberRV.setAdapter(mMemberAdapter);

        // 设置item及item中控件的点击事件
        mMemberAdapter.setOnItemClickListener(MyItemClickListener);

    }

    /**
     * 成员管理item 里的控件点击监听事件
     */
    private MemberAdapter.OnItemClickListener MyItemClickListener = new MemberAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, MemberAdapter.ViewName viewName, int position) {
            //viewName可区分item及item内部控件
            switch (v.getId()) {

                case R.id.projectMemberAccountTextView:
                    Toast.makeText(ProjectMemberManageActivity.this, "你点击了账号名称" + (position + 1), Toast.LENGTH_SHORT).show();

                    break;
                case R.id.projectMemberAccountDisableBt:
//                    Toast.makeText(ProjectMemberManageActivity.this, "你点击了 禁用" + (position + 1), Toast.LENGTH_SHORT).show();
                    LinkedHashMap<String, String> deleteProjectMemberParameters = new LinkedHashMap<>();
                    deleteProjectMemberParameters.put("member_accounts",mMemberList.get(position).getAccount());
                    String  url = Constant.deleteProjectMemberUrl.replace("{id}", String.valueOf(mProject.getId()));
                    mNetwork.delete(url, deleteProjectMemberParameters, new DeleteProjectMemberListHandler(),(handler, msgGetMember)->{
                        handler.sendMessage(msgGetMember);
                    });
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