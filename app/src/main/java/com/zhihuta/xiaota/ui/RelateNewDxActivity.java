package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;

import java.util.ArrayList;

public class RelateNewDxActivity extends AppCompatActivity {

    private ArrayList<DianxianQingCeData> mDianxianTobeSelectList;
    private Button mOkTobeSelectBt;

    private DianXianQingceAdapter mDianXianQingceAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relate_new_dx);
        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initViews();
        showTobeSelectedDxList();
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
        DianxianQingCeData mDxData1 = new DianxianQingCeData();
        mDxData1.setId(1);
        mDxData1.setDxNumber("DX22候选");
        mDxData1.setStartPoint("乌鲁木齐A");
        mDxData1.setEndPoint("北京B点");
        mDxData1.setDxModel("型号S");
        mDxData1.setDxLength("3100km");
        mDxData1.setDxXinshuJieMian("4X180");
        mDxData1.setSteelRedundancy("110/15M");
//        mDxData1.setHoseRedundancy("5M");

        mDianxianTobeSelectList = new ArrayList<>();
        mDianxianTobeSelectList.add(mDxData1);
        mDianxianTobeSelectList.add(mDxData1);
        mDianxianTobeSelectList.add(mDxData1);
        mDianxianTobeSelectList.add(mDxData1);
        mDianxianTobeSelectList.add(mDxData1);
        mDianxianTobeSelectList.add(mDxData1);
        mDianxianTobeSelectList.add(mDxData1);
        mDianxianTobeSelectList.add(mDxData1);
        mDianxianTobeSelectList.add(mDxData1);
        mDianxianTobeSelectList.add(mDxData1);
        mDianxianTobeSelectList.add(mDxData1);
        mDianxianTobeSelectList.add(mDxData1);
        mDianxianTobeSelectList.add(mDxData1);
        mDianxianTobeSelectList.add(mDxData1);

        mOkTobeSelectBt = (Button) findViewById(R.id.button_OK_to_add_dxTobeSelect22 );
        mOkTobeSelectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RelateNewDxActivity.this, RelatedDxActivity.class);
                RelateNewDxActivity.this.finish();
            }
        });
    }
    private void showTobeSelectedDxList(){
        //候选的电线列表
        RecyclerView mDxRV = (RecyclerView) findViewById(R.id.rv_dx_tobeSelect);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mDxRV.setLayoutManager(manager);
        mDianXianQingceAdapter = new DianXianQingceAdapter(mDianxianTobeSelectList);
        mDxRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mDxRV.setAdapter(mDianXianQingceAdapter);
    }
}