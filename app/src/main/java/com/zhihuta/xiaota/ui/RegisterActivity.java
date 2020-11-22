package com.zhihuta.xiaota.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.response.AddUsersResponse;
import com.zhihuta.xiaota.common.RequestUrlUtility;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;

import java.util.HashMap;
/**
 * Created by littlecurl 2018/6/24
 */
/**
 * 此类 implements View.OnClickListener 之后，
 * 就可以把onClick事件写到onCreate()方法之外
 * 这样，onCreate()方法中的代码就不会显得很冗余
 */
public class RegisterActivity extends AppCompatActivity/* implements View.OnClickListener */{

    private String realCode;

    private Button mBtRegisteractivityRegister;
//    private RelativeLayout mRlRegisteractivityTop;
    private ImageView mIvRegisteractivityBack;
//    private LinearLayout mLlRegisteractivityBody;

    private EditText mEtUserName;
    private EditText mEtPassword1;
    private EditText mEtPassword2;
    private EditText mEtAccount;
    private EditText mEtEmail;
    private EditText mEtCompany;
    private EditText mEtDepartment;
    private EditText mEtTitle;
    private EditText mEtTel;
    private EditText mEtAdress;

//    private ImageView mIvRegisteractivityShowcode;
//    private RelativeLayout mRlRegisteractivityBottom;

    private Network mNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        //将验证码用图片的形式显示出来
//        mIvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        //realCode = Code.getInstance().getCode().toLowerCase();

        mNetwork = Network.Instance(getApplication());
    }

    private void initView(){
        mBtRegisteractivityRegister = findViewById(R.id.bt_registeractivity_register);
//        mRlRegisteractivityTop = findViewById(R.id.rl_registeractivity_top);
        mIvRegisteractivityBack = findViewById(R.id.iv_registeractivity_back);
//        mLlRegisteractivityBody = findViewById(R.id.ll_registeractivity_body);


        mEtAccount = findViewById(R.id.et_registeractivity_account);
        mEtUserName = findViewById(R.id.et_registeractivity_username);
        mEtPassword1 = findViewById(R.id.et_registeractivity_password1);
        mEtPassword2 = findViewById(R.id.et_registeractivity_password2);
        mEtEmail = findViewById(R.id.et_registeractivity_email);
        mEtCompany = findViewById(R.id.et_registeractivity_company);
        mEtDepartment = findViewById(R.id.et_registeractivity_department);
        mEtTitle = findViewById(R.id.et_registeractivity_title);
        mEtTel = findViewById(R.id.et_registeractivity_tel);
        mEtAdress= findViewById(R.id.et_registeractivity_address);


        /**
         * 注册页面能点击的就三个地方
         * top处返回箭头、刷新验证码图片、注册按钮
         */
        mIvRegisteractivityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        mIvRegisteractivityShowcode.setOnClickListener(null);

        mBtRegisteractivityRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( checkValid() )
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( RegisterActivity.this);
                    alertDialogBuilder.setTitle("确定要提交注册信息？")
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
                                    newUserInfoParameters.put("account",  mEtAccount.getText().toString());
                                    newUserInfoParameters.put("name",  mEtUserName.getText().toString());
                                    newUserInfoParameters.put("password",  mEtPassword1.getText().toString());
                                    newUserInfoParameters.put("email",  mEtEmail.getText().toString());
                                    newUserInfoParameters.put("title",  mEtTitle.getText().toString());
                                    newUserInfoParameters.put("phone",  mEtTel.getText().toString());
                                    newUserInfoParameters.put("company",  mEtCompany.getText().toString());
                                    newUserInfoParameters.put("department",  mEtDepartment.getText().toString());
                                    newUserInfoParameters.put("address",  mEtAdress.getText().toString());
                                    //newUserInfoParameters.put("roles",  strNewPathName);//[2,4]..

                                    String registerUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.USER_REGISTER.replace("{type}",
                                            "3");

                                    mNetwork.post(registerUrl,newUserInfoParameters, new Handler(){

                                        @Override
                                        public void handleMessage(final Message msg) {
                                            /*"{
                                              ""account"":""zhanglin01"",//账号名，不可以重复
                                              ""name"": ""maosan"",
                                              ""password"": ""abc123""
                                              ""email"": ""san9.mao@xxx.com"",
                                              ""title"":""总经理"",
                                              ""phone"": ""13588476857"",
                                              ""company"":""xxx公司"",
                                              ""department"":""设计部"",
                                              ""address"":""xxx路xx号""
                                            }"
                                            */

                                            String errorMsg = "";

                                            errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
                                            if (errorMsg != null)
                                            {
                                                Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();

                                                return;
                                            }

                                            Result result= (Result)(msg.obj);

                                            AddUsersResponse addUsersResponse = CommonUtility.objectToJavaObject(result.getData(), AddUsersResponse.class);
                                            //
                                            Toast.makeText(RegisterActivity.this, "注册成功!", Toast.LENGTH_LONG).show();

                                            finish();

                                        }
                                    },(handler, msg)->{
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
        if( mEtAccount.getText().toString().trim().equals("") )
        {
            Toast.makeText( this, "账号不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if( mEtUserName.getText().toString().trim().equals("") )
        {
            Toast.makeText( this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        String email = mEtEmail.getText().toString().trim();
        if( !CommonUtility.isEmail(email) )
        {
            Toast.makeText(this, "email 无效格式", Toast.LENGTH_SHORT).show();
            return false;
        }

        String  password1 = mEtPassword1.getText().toString().trim();
        if (password1.equals(""))
        {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!mEtPassword2.getText().toString().trim().equals( password1))
        {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }

        if( mEtTitle.getText().toString().trim().equals("") )
        {
            //Toast.makeText(PersonalInfoActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            //return false;
            ;
        }
        if( mEtCompany.getText().toString().trim().equals("") )
        {
            Toast.makeText(this, "公司名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if( mEtTel.getText().toString().trim().equals("") )
        {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if( mEtAdress.getText().toString().trim().equals("") )
        {
            //Toast.makeText(PersonalInfoActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            //return false;
            ;
        }
        if( mEtDepartment.getText().toString().trim().equals("") )
        {
            Toast.makeText(this, "部门不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

//    public void onClick(View view) {
////        switch (view.getId()) {
////            case R.id.iv_registeractivity_back: //返回登录页面
////                Intent intent1 = new Intent(this, loginActivity.class);
////                startActivity(intent1);
////                finish();
////                break;
////            case R.id.iv_registeractivity_showCode:    //改变随机验证码的生成
////                mIvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
////                realCode = Code.getInstance().getCode().toLowerCase();
////                break;
////            case R.id.bt_registeractivity_register:    //注册按钮
////                //获取用户输入的用户名、密码、验证码
////                String username = mEtRegisteractivityUsername.getText().toString().trim();
////                String password = mEtRegisteractivityPassword2.getText().toString().trim();
////                String phoneCode = mEtRegisteractivityPhonecodes.getText().toString().toLowerCase();
////                //注册验证
////                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(phoneCode) ) {
////                    if (phoneCode.equals(realCode)) {
////                        //将用户名和密码加入到数据库中
////                        mDBOpenHelper.add(username, password);
////                        Intent intent2 = new Intent(this, MainActivity.class);
////                        startActivity(intent2);
////                        finish();
////                        Toast.makeText(this,  "验证通过，注册成功", Toast.LENGTH_SHORT).show();
////                    } else {
////                        Toast.makeText(this, "验证码错误,注册失败", Toast.LENGTH_SHORT).show();
////                    }
////                }else {
////                    Toast.makeText(this, "未完善信息，注册失败", Toast.LENGTH_SHORT).show();
////                }
////                break;
////        }
//    }
}

