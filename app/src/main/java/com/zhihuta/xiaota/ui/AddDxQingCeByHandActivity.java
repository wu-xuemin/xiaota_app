package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.basic.Wires;
import com.zhihuta.xiaota.bean.response.GetWiresResponse;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.net.Network;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.zhihuta.xiaota.common.Constant.getDxListUrl8083;

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
//        spinnerXinshu = (Spinner) findViewById(R.id.spinner_xinshu);
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
        //spinnerJieMian = (Spinner) findViewById(R.id.spinner_jiemian);
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

        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //add new data to server
                Network network = Network.Instance(getApplication());

                LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();

               String strTemp = ((TextInputEditText)(findViewById(R.id.txtinput_serialnumer))).getText().toString();
               if (strTemp.isEmpty())
               {
                   Toast.makeText(AddDxQingCeByHandActivity.this, "电线编号不能为空" , Toast.LENGTH_SHORT).show();

                   return;
               }
                mPostValue.put("serial_number", strTemp);

                strTemp = ((TextInputEditText)(findViewById(R.id.txtinput_startpoint))).getText().toString();
                if (strTemp.isEmpty())
                {
                    Toast.makeText(AddDxQingCeByHandActivity.this, "起点不能为空" , Toast.LENGTH_SHORT).show();

                    return;
                }
                mPostValue.put("start_point", strTemp);


                strTemp = ((TextInputEditText)(findViewById(R.id.txtinput_endpoint))).getText().toString();
                if (strTemp.isEmpty())
                {
                    Toast.makeText(AddDxQingCeByHandActivity.this, "终点不能为空" , Toast.LENGTH_SHORT).show();

                    return;
                }
                mPostValue.put("end_point", strTemp);


                strTemp = ((TextInputEditText)(findViewById(R.id.txtinput_partscode))).getText().toString();
                if (strTemp.isEmpty())
                {
                    Toast.makeText(AddDxQingCeByHandActivity.this, "类型不能为空" , Toast.LENGTH_SHORT).show();

                    return;
                }
                mPostValue.put("parts_code", strTemp);

                strTemp = ((TextInputEditText)(findViewById(R.id.txtinput_wickes_cross_section))).getText().toString();
                if (strTemp.isEmpty())
                {
                    Toast.makeText(AddDxQingCeByHandActivity.this, "芯数与界面不能为空" , Toast.LENGTH_SHORT).show();

                    return;
                }
                mPostValue.put("wickes_cross_section",strTemp);

                strTemp = ((TextInputEditText)(findViewById(R.id.txtinput_length))).getText().toString();
                if (strTemp.isEmpty())
                {
                    Toast.makeText(AddDxQingCeByHandActivity.this, "长度不能为空" , Toast.LENGTH_SHORT).show();

                    return;
                }

                mPostValue.put("length", strTemp);


                strTemp = ((TextInputEditText)(findViewById(R.id.txtinput_steel_redundancy))).getText().toString();
                if (strTemp.isEmpty())
                {
//                    Toast.makeText(AddDxQingCeByHandActivity.this, "钢冗余不能为空" , Toast.LENGTH_SHORT).show();

 //                   return;
                }
                mPostValue.put("steel_redundancy", strTemp);

                strTemp = ((TextInputEditText)(findViewById(R.id.txtinput_hose_redundancy))).getText().toString();
                if (strTemp.isEmpty())
                {
//                    Toast.makeText(AddDxQingCeByHandActivity.this, "起点不能为空" , Toast.LENGTH_SHORT).show();

//                    return;
                }
                mPostValue.put("hose_redundancy", strTemp);


                network.post(getDxListUrl8083,mPostValue,new AddDxQinceHandler(), (handler, msg)->{

                    handler.sendMessage(msg);

                } );


                //
            }
        });

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

    @SuppressLint("HandlerLeak")
    class AddDxQinceHandler extends Handler {

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
//            if(mLoadingProcessDialog != null && mLoadingProcessDialog.isShowing()) {
//                mLoadingProcessDialog.dismiss();
//            }

            //////////////////////
            String errorMsg = "";

            try {

                if (msg.what == Network.OK) {
                    Result result= (Result)(msg.obj);

                    if (result.getCode() != 200)
                    {
                        errorMsg = result.getMessage();
                    }
                    else
                    {
                        AddDxQingCeByHandActivity.this.finish();

                        return;
                    }
                 }
                else
                {
                    errorMsg = (String) msg.obj;
                }

                if (!errorMsg.isEmpty())
                {
                    Log.d("添加电线失败:", errorMsg);
                    Toast.makeText(AddDxQingCeByHandActivity.this, "添加电线失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                Log.d("添加电线失败:", ex.getMessage());
            }
            finally {
                setIsGetting(false);
            }
        }//handle message

    }
}