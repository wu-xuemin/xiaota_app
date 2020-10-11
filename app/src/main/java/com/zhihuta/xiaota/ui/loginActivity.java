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

import com.blankj.utilcode.util.ToastUtils;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.bean.response.LoginResponseData;
import com.zhihuta.xiaota.net.Network;
import com.zhihuta.xiaota.util.ShowMessage;

import java.io.Serializable;
import java.util.ArrayList;
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
    private String mPassword=null;

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
        if(XiaotaApp.getApp().getAccount() != null && !"".equals(XiaotaApp.getApp().getAccount())) {
            mAccountText.setText(XiaotaApp.getApp().getAccount());
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

        if( mLoadingProcessDialog == null) {
            mLoadingProcessDialog = new ProgressDialog(com.zhihuta.xiaota.ui.LoginActivity.this);
            mLoadingProcessDialog.setCancelable(false);
            mLoadingProcessDialog.setCanceledOnTouchOutside(false);
            mLoadingProcessDialog.setMessage("登录中...");
        }
        mLoadingProcessDialog.show();
        LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
        mPassword=mPasswordText.getText().toString();
        mPostValue.put("account", mAccountText.getText().toString());
        mPostValue.put("password", mPassword);
        mPostValue.put("meid", XiaotaApp.getApp().getIMEI());
        Log.d(TAG, "login: IMEI: "+XiaotaApp.getApp().getIMEI());
        if(TextUtils.isEmpty(XiaotaApp.getApp().getServerIP())){
            if(mLoadingProcessDialog.isShowing()) {
                mLoadingProcessDialog.dismiss();
            }
            mLoginButton.setEnabled(true);
            ToastUtils.showShort("服务端IP为空，请设置IP地址");
            Log.d(TAG, "login: 服务端IP为空，请设置IP地址");
        } else if (XiaotaApp.getApp().getIMEI()==null){
            if(mLoadingProcessDialog.isShowing()) {
                mLoadingProcessDialog.dismiss();
            }
            mLoginButton.setEnabled(true);
            ToastUtils.showShort("未获取到手机识别码,请重启软件");
            Log.d(TAG, "login: 未获取到手机IMEI");
        }
        else {
            String loginUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIP() + URL.USER_LOGIN;
            ///test
//            mNetwork.fetchLoginData(loginUrl, mPostValue, mLoginHandler);
            onLoginSuccess(null);
        }

    }

    @SuppressLint("HandlerLeak")
    class LoginHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {

            if(mLoadingProcessDialog != null && mLoadingProcessDialog.isShowing()) {
                mLoadingProcessDialog.dismiss();
            }

            if (msg.what == Network.OK) {
                onLoginSuccess((LoginResponseData)msg.obj);
            } else {
                String errorMsg = (String)msg.obj;
                onLoginFailed(errorMsg);
            }
        }
    }

    public void onLoginSuccess(LoginResponseData data) {
        /// test
        String name = "a";
        String password = "b";
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {
//            ArrayList<User> data = mDBOpenHelper.getAllData();
            boolean match = false;
//            for (int i = 0; i < data.size(); i++) {
//                User user = data.get(i);
//                if (name.equals(user.getName()) && password.equals(user.getPassword())) {
//                    match = true;
//                    break;
//                } else {
//                    match = false;
//                }
//            }

            /// test
            match = true;
            if (match) {
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(this, MainActivity.class);
                Intent intent = new Intent(this, DianxianQingCe.class);

                ArrayList<DianxianQingCeData> mDianxianQingCeList;
                DianxianQingCeData dianxianQingCeData1 = new DianxianQingCeData();
                dianxianQingCeData1.setId(1);
                dianxianQingCeData1.setDxNumber("dx_111");
                dianxianQingCeData1.setDxModel("型号112");
                dianxianQingCeData1.setStartPoint("杭州A22");
                dianxianQingCeData1.setEndPoint("上海B66");
                mDianxianQingCeList = new ArrayList<>();
                mDianxianQingCeList.add(dianxianQingCeData1);
                Bundle bundle = new Bundle();
                bundle.putSerializable("mDianxianQingCeList", (Serializable) mDianxianQingCeList);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();//销毁此Activity
            } else {
                Toast.makeText(this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "请输入你的用户名或密码", Toast.LENGTH_SHORT).show();
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
                String temp = XiaotaApp.getApp().getServerIP();
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
                            XiaotaApp.getApp().setServerIP(editText.getText().toString());
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
