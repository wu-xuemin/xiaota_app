package com.zhihuta.xiaota.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.zhihuta.xiaota.Code;
import com.zhihuta.xiaota.DBOpenHelper;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.response.BaseResponse;
import com.zhihuta.xiaota.bean.response.LoginResponseData;
import com.zhihuta.xiaota.bean.response.UserResponse;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;

import java.util.HashMap;
import java.util.LinkedHashMap;
/**
 * Created by littlecurl 2018/6/24
 */

/**
 * 此类 implements View.OnClickListener 之后，
 * 就可以把onClick事件写到onCreate()方法之外
 * 这样，onCreate()方法中的代码就不会显得很冗余
 */
public class PersonalInfoActivity extends AppCompatActivity{

    private EditText mEtAccount;
    private EditText mEtUserName;
    private EditText mEtEmail;
    private EditText mEdTitle;
    private EditText mEtCompany;
    private EditText mEtDepartment;
    private EditText mEtTel;
    private EditText mEtAdress;

    private Button mBtModify;
    private Button mBtConfirm;

    private LoginResponseData loginResponseData;

    private Network mNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        //get login infor from Loginactivity
        Intent intent = getIntent();
        String strLoginResponseJson = (String) intent.getExtras().getSerializable("loginResponseData");
        loginResponseData = JSON.parseObject(strLoginResponseJson, LoginResponseData.class);
        //////////

        mNetwork = Network.Instance(getApplication());

        initView();

        getUserInfo(loginResponseData);

        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }
    /**
     * 复写：左侧按钮点击动作
     * android.R.id.home
     * v7 actionbar back event
     * 注意：如果复写了onOptionsItemSelected方法，则onSupportNavigateUp无用
     * */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void initView(){

        mEtAccount= findViewById(R.id.personal_account_update);
        mEtUserName = findViewById(R.id.personal_username_update);
        mEtEmail = findViewById(R.id.personal_email_update);
        mEdTitle = findViewById(R.id.personal_title_update);
        mEtCompany = findViewById(R.id.personal_company_update);
        mEtDepartment = findViewById(R.id.personal_department_update);
        mEtTel = findViewById(R.id.personal_tel_update);
        mEtAdress = findViewById(R.id.personal_address_update);

        mBtModify = findViewById(R.id.bt_personal_modify);
        mBtConfirm = findViewById(R.id.bt_personal_confirm);
        mBtConfirm.setEnabled(false);

        mBtModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mEtAccount.setText(userResponse.account_info.getAccount());
                mEtUserName.setEnabled(true);
                mEtEmail.setEnabled(true);
                mEdTitle.setEnabled(true);
                mEtCompany.setEnabled(true);
                mEtTel.setEnabled(true);
                mEtAdress.setEnabled(true);
                mEtDepartment.setEnabled(true);

                mBtConfirm.setEnabled(true);
            }
        });

        mBtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( checkValid() )
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonalInfoActivity.this);
                    alertDialogBuilder.setTitle("确定要提交修改信息？")
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //mEtUserName.setEnabled(false);
                                    //mEtEmail.setEnabled(false);
                                    //mEdTitle.setEnabled(false);
                                    //mEtCompany.setEnabled(false);
                                    //mEtTel.setEnabled(false);
                                    //mEtAdress.setEnabled(false);
                                    //mEtDepartment.setEnabled(false);

                                    //mBtConfirm.setEnabled(false);
                                }
                            })
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                        HashMap<String, String> newUserInfoParameters = new HashMap<>();
                                        newUserInfoParameters.put("account",  mEtUserName.getText().toString());
                                        newUserInfoParameters.put("name",  mEtUserName.getText().toString());
                                        //newUserInfoParameters.put("password",  strNewPathName);
                                        newUserInfoParameters.put("mail",  mEtEmail.getText().toString());
                                        newUserInfoParameters.put("title",  mEdTitle.getText().toString());
                                        newUserInfoParameters.put("phone",  mEtTel.getText().toString());
                                        newUserInfoParameters.put("company",  mEtCompany.getText().toString());
                                        newUserInfoParameters.put("department",  mEtDepartment.getText().toString());
                                        newUserInfoParameters.put("address",  mEtAdress.getText().toString());
                                        //newUserInfoParameters.put("roles",  strNewPathName);//[2,4]..

                                        String loginUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.USER_INFO.replace("{account_id}",
                                                Integer.toString( loginResponseData.getId() ) );

                                        mNetwork.put(loginUrl,newUserInfoParameters, new modifyUserInfoHandler(),(handler,msg)->{
                                            handler.sendMessage(msg);
                                        } );
                                }

                            })
                            .show();
                }

            }
        });
    }

    private boolean checkValid()
    {
        if( mEtUserName.getText().toString().trim().equals("") )
        {
            Toast.makeText(PersonalInfoActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if( mEtEmail.getText().toString().trim().equals("") )
        {
            Toast.makeText(PersonalInfoActivity.this, "email 不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if( mEdTitle.getText().toString().trim().equals("") )
        {
            //Toast.makeText(PersonalInfoActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            //return false;
            ;
        }
        if( mEtCompany.getText().toString().trim().equals("") )
        {
            Toast.makeText(PersonalInfoActivity.this, "公司名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if( mEtTel.getText().toString().trim().equals("") )
        {
            //Toast.makeText(PersonalInfoActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            //return false;
            ;
        }
        if( mEtAdress.getText().toString().trim().equals("") )
        {
            //Toast.makeText(PersonalInfoActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            //return false;
            ;
        }
        if( mEtDepartment.getText().toString().trim().equals("") )
        {
            Toast.makeText(PersonalInfoActivity.this, "部门不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateDataToUI(UserResponse userResponse)
    {
        mEtAccount.setText(userResponse.account_info.getAccount());
        mEtUserName.setText(userResponse.account_info.getName());
        mEtEmail.setText(userResponse.account_info.getEmail());
        mEdTitle.setText(userResponse.account_info.getTitle());
        mEtCompany.setText(userResponse.account_info.getCompany());
        mEtTel.setText(userResponse.account_info.getPhone());
        mEtAdress.setText(userResponse.account_info.getAddress());
        mEtDepartment.setText(userResponse.account_info.getDepartment());
    }

    private void getUserInfo(LoginResponseData loginResponseData)
    {
        String loginUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.USER_INFO.replace("{account_id}",
                Integer.toString( loginResponseData.getId() ) );

        mNetwork.get(loginUrl, null, new GetUserInfoHandler(),(handler, msg)->{
            handler.sendMessage(msg);
        });
    }

    @SuppressLint("HandlerLeak")
    class GetUserInfoHandler extends Handler {

        @Override
        public void handleMessage(final Message msg) {

            String errorMsg = "";

            if (msg.what == Network.OK) {
                Result result= (Result)(msg.obj);

                UserResponse userResponse = CommonUtility.objectToJavaObject(result.getData(),UserResponse.class);

                if (userResponse != null &&userResponse.errorCode == 0)
                {
                     //
                    updateDataToUI(userResponse);
                }
                else
                {
                    errorMsg =  "获取个人信息失败:"+ result.getCode() + result.getMessage();
                }
            }
            else
            {
                errorMsg = (String) msg.obj;
            }

            if (!errorMsg.isEmpty())
            {
                Toast.makeText(PersonalInfoActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class modifyUserInfoHandler extends Handler {

        @Override
        public void handleMessage(final Message msg) {

            String errorMsg = "";

            if (msg.what == Network.OK) {
                Result result= (Result)(msg.obj);

                BaseResponse baseResponse = CommonUtility.objectToJavaObject(result.getData(),BaseResponse.class);

                if (baseResponse != null &&baseResponse.errorCode == 0)
                {
                    //
                    Toast.makeText(PersonalInfoActivity.this, "更新个人信息成功!", Toast.LENGTH_SHORT).show();

                    mEtUserName.setEnabled(false);
                    mEtEmail.setEnabled(false);
                    mEdTitle.setEnabled(false);
                    mEtCompany.setEnabled(false);
                    mEtTel.setEnabled(false);
                    mEtAdress.setEnabled(false);
                    mEtDepartment.setEnabled(false);

                    mBtConfirm.setEnabled(false);
                }
                else
                {
                    errorMsg =  "更新个人信息失败:"+ result.getCode() + result.getMessage();
                }
            }
            else
            {
                errorMsg = (String) msg.obj;
            }

            if (!errorMsg.isEmpty())
            {
                Toast.makeText(PersonalInfoActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

