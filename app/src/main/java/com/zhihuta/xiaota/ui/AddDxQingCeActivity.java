package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.zhihuta.xiaota.R;

public class AddDxQingCeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dx_qing_ce);

        //返回前页按钮
         ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initViews();//初始化控件
    }


    private void initViews() {

        Button addDianxianQingCeByHandButton = (Button) findViewById(R.id.button2);///
        addDianxianQingCeByHandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新建一个Intent(当前Activity, SecondActivity)=====显示Intent
                Intent intent = new Intent(AddDxQingCeActivity.this, AddDxQingCeByHandActivity.class);
                //启动Intent
                startActivity(intent);
            }
        });

        Button addDianxianQingCeFromFileButton = (Button) findViewById(R.id.button3);///
        addDianxianQingCeFromFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新建一个Intent(当前Activity, SecondActivity)=====显示Intent
                Intent intent = new Intent(AddDxQingCeActivity.this, AddDxQingCeFromFileActivity.class);
                //启动Intent
                startActivity(intent);
            }
        });

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