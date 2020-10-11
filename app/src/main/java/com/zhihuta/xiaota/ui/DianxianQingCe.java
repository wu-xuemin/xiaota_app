package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.QingceAdapter;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;

import java.util.ArrayList;

public class DianxianQingCe extends AppCompatActivity {

//    private InstallActualAdapter mInstallActualAdapter;
//private ArrayList<InstallPlanData> mInstallPlanActualList = new ArrayList<>();
    private QingceAdapter mQingceAdapter;
    private ArrayList<DianxianQingCeData> mDianxianQingCeList = new ArrayList<>();
    private ArrayList<DianxianQingCeData> mDxQingceList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dianxian_qing_ce);

        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initViews();//初始化控件


    }

    private void initViews() {
        //获取传递过来的信息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mDianxianQingCeList = (ArrayList<DianxianQingCeData>) bundle.getSerializable("mDianxianQingCeList");
        //列表
        RecyclerView mQingceRV = (RecyclerView) findViewById(R.id.recycle_view_qingce);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mQingceRV.setLayoutManager(manager);
        mQingceAdapter = new QingceAdapter(mDianxianQingCeList);
        mQingceRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mQingceRV.setAdapter(mQingceAdapter);

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
}