package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.adapter.DistanceAdapter;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;

import java.util.ArrayList;

public class RelateDxActivity extends AppCompatActivity {

    private ArrayList<DianxianQingCeData> mDianxianList;

    private DianXianQingceAdapter mDianXianQingceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relate_dx);
        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initViews();
        showDxList();

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

        TextView textViewTitle = findViewById(R.id.textView_lujingNameShow);
        textViewTitle.setText("路径ABC");

        DianxianQingCeData mDxData1 = new DianxianQingCeData();
        mDxData1.setId(1);
        mDxData1.setDxNumber("DX2211");
        mDxData1.setStartPoint("杭州A点");
        mDxData1.setEndPoint("岳阳A点");
        mDxData1.setDxModel("型号K");
        mDxData1.setDxLength("500km");
        mDxData1.setDxXinshuJieMian("3X185");
        mDxData1.setSteelRedundancy("10/5M");
//        mDxData1.setHoseRedundancy("5M");

        mDianxianList = new ArrayList<>();
        mDianxianList.add(mDxData1);
        mDianxianList.add(mDxData1);
        mDianxianList.add(mDxData1);
        mDianxianList.add(mDxData1);
        mDianxianList.add(mDxData1);
        mDianxianList.add(mDxData1);
        mDianxianList.add(mDxData1);
        mDianxianList.add(mDxData1);
        mDianxianList.add(mDxData1);
        mDianxianList.add(mDxData1);
        mDianxianList.add(mDxData1);
        mDianxianList.add(mDxData1);
        mDianxianList.add(mDxData1);
        mDianxianList.add(mDxData1);
    }
    private void showDxList(){
        //电线列表
        RecyclerView mDxRV = (RecyclerView) findViewById(R.id.rv_dx);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mDxRV.setLayoutManager(manager);
        mDianXianQingceAdapter = new DianXianQingceAdapter(mDianxianList);
        mDxRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mDxRV.setAdapter(mDianXianQingceAdapter);

    }
}