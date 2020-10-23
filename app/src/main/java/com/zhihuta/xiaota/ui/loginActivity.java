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
import com.zhihuta.xiaota.bean.basic.DistanceData;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.bean.basic.OrderData;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.bean.response.LoginResponseData;
import com.zhihuta.xiaota.net.Network;
import com.zhihuta.xiaota.util.ShowMessage;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
                Intent intent = new Intent(this, Main.class);

//                SimpleDateFormat sf3=new SimpleDateFormat("yy/MM/dd");
                ArrayList<DianxianQingCeData> mDianxianQingCeList;
                ArrayList<OrderData> mOrderList;
                ArrayList<LujingData> mLujingList;
                ArrayList<LujingData> mLujingShaixuanList;


                mDianxianQingCeList = new ArrayList<>();
                for(int k=1;k<16; k++) {
                    DianxianQingCeData dianxianQingCeData1 = new DianxianQingCeData();
                    dianxianQingCeData1.setId(1);
                    dianxianQingCeData1.setDxNumber("dx_0000" + k);
                    dianxianQingCeData1.setDxModel("型号00" + k);
                    dianxianQingCeData1.setStartPoint("杭州A00" + k);
                    dianxianQingCeData1.setEndPoint("上海B001"  + k);
                    dianxianQingCeData1.setSteelRedundancy("20M");
                    dianxianQingCeData1.setHoseRedundancy("6M");
                    dianxianQingCeData1.setFlag(Constant.FLAG_QINGCE_DX);

                    mDianxianQingCeList.add(dianxianQingCeData1);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("mDianxianQingCeList", (Serializable) mDianxianQingCeList);
                intent.putExtras(bundle);

                OrderData mOrderData1 = new OrderData();
                mOrderData1.setId(1);
                mOrderData1.setOrderNumber("订单_001");
                mOrderData1.setCreatedDate(new Date());
                mOrderData1.setOrderCreater("魏武");
                mOrderData1.setOrderStatus("正常");
                OrderData mOrderData2 = new OrderData();
                mOrderData2.setId(2);
                mOrderData2.setOrderNumber("订单_002");
                mOrderData2.setCreatedDate(new Date());
                mOrderData2.setOrderCreater("小王");
                mOrderData2.setOrderStatus("正常");

                mOrderList = new ArrayList<>();
                mOrderList.add(mOrderData1);
                mOrderList.add(mOrderData2);
                bundle.putSerializable("mOrderList", (Serializable) mOrderList);

                LujingData mLujingData1 = new LujingData();
                mLujingData1.setId(1);
                mLujingData1.setLujingName("路径_abc1");
//                String sDate = sf3.format(new Date());
//                try {
                    mLujingData1.setLujingCreatedDate(new Date());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
                mLujingData1.setLujingCreater("路小凡");
                mLujingData1.setLujingCaozuo("cccc");

                LujingData mLujingData2 = new LujingData();
                mLujingData2.setId(2);
                mLujingData2.setLujingName("路径_bbb2");
                mLujingData2.setLujingCreatedDate(new Date());
                mLujingData2.setLujingCreater("张三");

                LujingData mLujingData3 = new LujingData();
                mLujingData3.setId(3);
                mLujingData3.setLujingName("上海南京1");
                mLujingData3.setLujingCreatedDate(new Date());
                mLujingData3.setLujingCreater("张三丰");
                LujingData mLujingData4 = new LujingData();
                mLujingData4.setId(4);
                mLujingData4.setLujingName("北京南京12");
                mLujingData4.setLujingCreatedDate(new Date());
                mLujingData4.setLujingCreater("小王");
                LujingData mLujingData5 = new LujingData();
                mLujingData5.setId(5);
                mLujingData5.setLujingName("杭州太原88");
                mLujingData5.setLujingCreatedDate(new Date());
                mLujingData5.setLujingCreater("张小明");
                LujingData mLujingData6 = new LujingData();
                mLujingData6.setId(5);
                mLujingData6.setLujingName("沿海888A");
                mLujingData6.setLujingCreatedDate(new Date());
                mLujingData6.setLujingCreater("杨晓阳");
                LujingData mLujingData7 = new LujingData();
                mLujingData7.setId(5);
                mLujingData7.setLujingName("秦岭淮河790");
                mLujingData7.setLujingCreatedDate(new Date());
                mLujingData7.setLujingCreater("秦始皇");

                mLujingList = new ArrayList<>();
                mLujingList.add(mLujingData1);
                mLujingList.add(mLujingData2);
                mLujingList.add(mLujingData3);
                mLujingList.add(mLujingData4);
                mLujingList.add(mLujingData5);
                mLujingList.add(mLujingData5);
                mLujingList.add(mLujingData6);
                mLujingList.add(mLujingData7);
                bundle.putSerializable("mLujingList", (Serializable) mLujingList);
                intent.putExtras(bundle);

                mLujingShaixuanList = new ArrayList<>();
                mLujingShaixuanList.add(mLujingData1);
                mLujingShaixuanList.add(mLujingData2);
                bundle.putSerializable("mLujingShaixuanList", (Serializable) mLujingShaixuanList);
                intent.putExtras(bundle);



                mLujingShaixuanList = new ArrayList<>();
                mLujingShaixuanList.add(mLujingData1);
                mLujingShaixuanList.add(mLujingData2);
                bundle.putSerializable("mLujingShaixuanList", (Serializable) mLujingShaixuanList);
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
