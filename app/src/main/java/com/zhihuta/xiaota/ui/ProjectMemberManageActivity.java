package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.ProjectAdapter;

public class ProjectMemberManageActivity extends AppCompatActivity {


    private static String TAG = "ProjectMemberManageActivity";

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

    }


    /**
     * 成员管理item 里的控件点击监听事件
     */
    private ProjectAdapter.OnItemClickListener MyItemClickListener = new ProjectAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, ProjectAdapter.ViewName viewName, int position) {
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