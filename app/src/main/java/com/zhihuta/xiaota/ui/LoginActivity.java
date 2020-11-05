package com.zhihuta.xiaota.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;

//import com.google.gson.JsonObject;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.bean.response.LoginResponseData;
import com.zhihuta.xiaota.net.Network;
import com.zhihuta.xiaota.util.ShowMessage;
import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "nlgLoginActivity";

    private TextView mSystemVersionTv;
    private EditText mAccountText;
    private EditText mPasswordText;
    private Button mLoginButton;
    private ProgressDialog mLoadingProcessDialog;
    private Network mNetwork;
    private LoginHandler mLoginHandler;
    private XiaotaApp mApp;
    private AlertDialog mIPSettngDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mApp = (XiaotaApp) getApplication();
        mNetwork = Network.Instance(getApplication());
        mLoginHandler = new LoginHandler();

        mLoginButton = (Button) findViewById(R.id.btn_login);
        mPasswordText = (EditText) findViewById(R.id.input_password);
        mAccountText = (EditText) findViewById(R.id.input_account);

        //show the default account and password in login UI.
        if(XiaotaApp.getApp().getAccount() != null && !"".equals(XiaotaApp.getApp().getAccount())) {
            mAccountText.setText(XiaotaApp.getApp().getAccount());
        }
        if(XiaotaApp.getApp().getPassword() != null && !"".equals(XiaotaApp.getApp().getPassword())) {
            mAccountText.setText(XiaotaApp.getApp().getPassword());
        }

        mSystemVersionTv = findViewById(R.id.system_version);
        mSystemVersionTv.setText(getVersion());

        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                login();
            }
        });
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return "V" + version;
        } catch (Exception e) {
            e.printStackTrace();
            return "无版本信息";
        }
    }

    private void login() {

        mLoginButton.setEnabled(false);

        try {
            Log.d(TAG, "login: IMEI: "+XiaotaApp.getApp().getIMEI());
        }
        catch (Exception ex)
        {
            Log.d(TAG, "login: 未获取到手机IMEI");
        }

        String account  = mAccountText.getText().toString().trim();
        String password = mPasswordText.getText().toString();

        if (account.isEmpty() || password.isEmpty())
        {
            ToastUtils.showShort("用户名和密码不能为空");
            Log.d(TAG, "用户名和密码不能为空");
            mLoadingProcessDialog.dismiss();
            mLoginButton.setEnabled(true);
        }
        else {

            LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
            mPostValue.put("account", account);
            mPostValue.put("password",password );

            if(TextUtils.isEmpty(XiaotaApp.getApp().getServerIPAndPort())){

                mLoginButton.setEnabled(true);
                ToastUtils.showShort("服务端IP为空，请设置IP地址");
                Log.d(TAG, "login: 服务端IP为空，请设置IP地址");
            }
            else {

                //save the config
                XiaotaApp.getApp().setAccount(account);
                XiaotaApp.getApp().setPassword(password);

                String loginUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.USER_LOGIN;

                if( mLoadingProcessDialog == null) {
                    mLoadingProcessDialog = new ProgressDialog(com.zhihuta.xiaota.ui.LoginActivity.this);
                    mLoadingProcessDialog.setCancelable(false);
                    mLoadingProcessDialog.setCanceledOnTouchOutside(false);
                    mLoadingProcessDialog.setMessage("登录中...");
                }
                mLoadingProcessDialog.show();


                boolean bsend = mNetwork.post( loginUrl,mPostValue,mLoginHandler,
                        (handler,msg)->{
                         handler.sendMessage(msg);
                        });

                if (bsend)
                {
                    mLoadingProcessDialog.dismiss();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class LoginHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {

            if(mLoadingProcessDialog != null && mLoadingProcessDialog.isShowing()) {
                mLoadingProcessDialog.dismiss();
            }

            String errorMsg = "";

            if (msg.what == Network.OK) {
                Result result= (Result)(msg.obj);

                LoginResponseData responseData = CommonUtility.objectToJavaObject(result.getData(),LoginResponseData.class);

                if (responseData != null &&responseData.errorCode == 0)
                {
                    onLoginSuccess( responseData);
                }
                else
                {
                    errorMsg =  "登录失败:"+ result.getCode() + result.getMessage();
                }
            }
            else
            {
                errorMsg = (String) msg.obj;
            }

            if (!errorMsg.isEmpty())
            { 
			     onLoginFailed(errorMsg);
            }
        }
    }

    public void onLoginSuccess(LoginResponseData data) {
        /// test

        if (data.errorCode == 0) {

            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Main.class);

            startActivity(intent);
            finish();//销毁此Activity
        } else {

            Toast.makeText(this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
        }

    }

    public void onLoginFailed(String msg) {
        if( msg != null) {
            ShowMessage.showDialog(com.zhihuta.xiaota.ui.LoginActivity.this, msg);
        } else {
            ShowMessage.showDialog(com.zhihuta.xiaota.ui.LoginActivity.this, "登入错误，请检查网络！");
        }
        mLoginButton.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ip_settings:
                LinearLayout layout = (LinearLayout) View.inflate(LoginActivity.this, R.layout.dialog_ip_setting, null);
                final EditText editText = (EditText)layout.findViewById(R.id.ip_value);
                //读取保存的IP地址
                String temp = XiaotaApp.getApp().getServerIPAndPort();
                Log.d(TAG, "onOptionsItemSelected: "+temp);
                mIPSettngDialog = new AlertDialog.Builder(LoginActivity.this).create();
                mIPSettngDialog.setTitle("服务端IP设置");
                mIPSettngDialog.setView(layout);
                if(!TextUtils.isEmpty(temp)) {
                    editText.setText(temp);
                }
                mIPSettngDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mIPSettngDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Log.d(TAG, "onClick: 输入ip："+editText.getText().toString());
                            XiaotaApp.getApp().setServerIPAndPort(editText.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                mIPSettngDialog.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLoadingProcessDialog != null) {
            mLoadingProcessDialog.dismiss();
        }
        if(mIPSettngDialog != null) {
            mIPSettngDialog.dismiss();
        }
    }
}
