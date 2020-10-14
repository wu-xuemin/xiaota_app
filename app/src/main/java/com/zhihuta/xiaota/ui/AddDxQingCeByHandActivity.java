package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.zhihuta.xiaota.R;

public class AddDxQingCeByHandActivity extends AppCompatActivity {

    private static final String[] mXinshuXuanXiang = {"单芯","2芯","3芯","4芯","5芯","6芯","其他"};
    private Spinner spinnerXinshu;
    private ArrayAdapter<String> adapterXinShu;

    private static final String[] mJieMianXuanxXiang = {"直径1mm","直径2mm","直径3mm","直径4mm","直径5mm","直径6mm","其他"};
    private Spinner spinnerJieMian;
    private ArrayAdapter<String> adapterJieMian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dx_qing_ce_by_hand);
        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /// 芯数 下拉
        spinnerXinshu = (Spinner) findViewById(R.id.spinner_xinshu);
        //将可选内容与ArrayAdapter连接起来
        adapterXinShu = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mXinshuXuanXiang);

        //设置下拉列表的风格
        adapterXinShu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinnerXinshu.setAdapter(adapterXinShu);

        //添加事件Spinner事件监听
        spinnerXinshu.setOnItemSelectedListener(new SpinnerSelectedListenerXinShu());

        //设置默认值
        spinnerXinshu.setVisibility(View.VISIBLE);

        /// 截面 下拉
        spinnerJieMian = (Spinner) findViewById(R.id.spinner_jiemian);
        //将可选内容与ArrayAdapter连接起来
        adapterJieMian = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mJieMianXuanxXiang);

        //设置下拉列表的风格
        adapterJieMian.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinnerJieMian.setAdapter(adapterJieMian);

        //添加事件Spinner事件监听
        spinnerJieMian.setOnItemSelectedListener(new SpinnerSelectedListenerJieMian());

        //设置默认值
        spinnerJieMian.setVisibility(View.VISIBLE);

    }
    //使用数组形式操作
    class SpinnerSelectedListenerXinShu implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
//            view.setText("你的选择是："+m[arg2]);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }


    class SpinnerSelectedListenerJieMian implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
//            view.setText("你的选择是："+m[arg2]);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
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