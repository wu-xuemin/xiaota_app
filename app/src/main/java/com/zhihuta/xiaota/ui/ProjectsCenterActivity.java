package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.common.Constant;

import java.util.LinkedHashMap;

public class ProjectsCenterActivity extends AppCompatActivity {

    private Button mCreateNewProjectBt;

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
    }
}