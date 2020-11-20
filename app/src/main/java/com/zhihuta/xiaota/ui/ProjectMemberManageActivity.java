package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.MemberAdapter;
import com.zhihuta.xiaota.adapter.ProjectAdapter;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.bean.basic.MemberData;
import com.zhihuta.xiaota.bean.basic.ProjectData;
import com.zhihuta.xiaota.net.Network;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ProjectMemberManageActivity extends AppCompatActivity {

    private static String TAG = "ProjectMemberManageActivity";
    private Button mCreateNewMemberBt;
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
                                String strNewPathName = et.getText().toString();
                                if (strNewPathName == null || strNewPathName.isEmpty()) { //不允许名称为空
                                    Toast.makeText(ProjectMemberManageActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
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
    }
    private void showMemberList(){
        //成员列表
        mMemberRV = (RecyclerView) findViewById(R.id.rv_project_member);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mMemberRV.setLayoutManager(manager);
        mMemberAdapter = new MemberAdapter(mMemberList,this, null);
        mMemberRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
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
                case R.id.projectMemberAccountDisableTextView:
                    Toast.makeText(ProjectMemberManageActivity.this, "你点击了 禁用" + (position + 1), Toast.LENGTH_SHORT).show();

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