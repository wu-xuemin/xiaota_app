package com.zhihuta.xiaota.ui;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;

//import com.google.gson.JsonObject;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.response.BaseResponse;
import com.zhihuta.xiaota.bean.response.UserResponse;
import com.zhihuta.xiaota.common.Constant;
//import com.zhihuta.xiaota.common.DownloadReceiver;
import com.zhihuta.xiaota.common.DownloadUtility;
import com.zhihuta.xiaota.common.NotificationClickReceiver;
import com.zhihuta.xiaota.common.RequestUrlUtility;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.bean.response.LoginResponseData;
import com.zhihuta.xiaota.net.Network;
import com.zhihuta.xiaota.util.ShowMessage;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "nlgLoginActivity";

    private TextView mRegister;
    private TextView mResetpassword;

    private TextView mSystemVersionTv;
    private EditText mAccountText;
    private EditText mPasswordText;
    private String mPassword = null;
    private String mAccount = null;
    private String mServiceIpAndPort = null;

    private Button mLoginButton;
    private ProgressDialog mLoadingProcessDialog;
    private Network mNetwork;
    private LoginHandler mLoginHandler;
    private XiaotaApp mApp;
    private AlertDialog mIPSettngDialog = null;

    //服务器上的版本号
    private String mVersionNameOnServer;
    private DownloadReceiver downloaderReceiver;
    private NotificationClickReceiver notificationClickReceiver;

    private IntentFilter intentFilter;
    private IntentFilter intentFilterDownloadRv;
    private NetworkChangeReceiver networkChangeReceiver;

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
            mPasswordText.setText(XiaotaApp.getApp().getPassword());
        }
        if(XiaotaApp.getApp().getServerIPAndPort() != null && !"".equals(XiaotaApp.getApp().getServerIPAndPort())) {
            mServiceIpAndPort = XiaotaApp.getApp().getServerIPAndPort();
        }

        mSystemVersionTv = findViewById(R.id.system_version);
        mSystemVersionTv.setText(getVersion());

        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                login();
//
//                CheckNewVersion();
            }
        });

        mRegister = (TextView) findViewById(R.id.register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                startActivityForResult(intent, 1);
            }
        });

        mResetpassword = (TextView) findViewById(R.id.reset_password);
        mResetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String account = mAccountText.getText().toString();

                if (account.trim().equals(""))
                {
                    Toast.makeText(LoginActivity.this, "用户名为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    String url = Constant.getAccountExist.replace("{account}",account );

                    mNetwork.get(url, null, new Handler(){

                                @Override
                                public void handleMessage(final Message msg) {

                                    String errorMsg = "";
                                    errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
                                    if (errorMsg != null)
                                    {
                                        Toast.makeText(LoginActivity.this, "查找用户失败：" + errorMsg, Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    Result result= (Result)(msg.obj);

                                    UserResponse responseData = CommonUtility.objectToJavaObject(result.getData(),UserResponse.class);
                                    ConfirmSendEmail(responseData.account_info.getEmail(),account);
                                }
                            },
                            (handler,msg)->{
                                handler.sendMessage(msg);
                            });
                }


            }
        });

        CheckNewVersion();
    }

    private void CheckNewVersion(){
        /**
         * 下载完成广播
         * 点击下载通知栏广播
         */
//        downloaderReceiver = new DownloadReceiver();
        notificationClickReceiver = new NotificationClickReceiver();
        getApplicationContext().registerReceiver(downloaderReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        getApplicationContext().registerReceiver(notificationClickReceiver, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));


        //test
        //注册“网络变化”的广播接收器
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);

        intentFilterDownloadRv = new IntentFilter();
        intentFilterDownloadRv.addAction("android.net.conn.ACTION_DOWNLOAD_COMPLETE");
        intentFilterDownloadRv.addAction("android.intent.action.DOWNLOAD_COMPLETE");
        downloaderReceiver = new DownloadReceiver();
        registerReceiver(downloaderReceiver, intentFilterDownloadRv);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE	);
        BroadcastReceiver receiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
//                if(reference == myreference){
//                    //对下载的文件进行一些操作
//                }
                Log.i("DownloadReceiver0000", "DownloadReceiver===========");
            }

        };
        registerReceiver(receiver, filter);


        //todo 从服务器获取真实版本
        mVersionNameOnServer = "0.01";
        String currentVersionName = getAppVersionName(this);
        if(Double.valueOf(mVersionNameOnServer) > Double.valueOf(currentVersionName)){
            Log.w(TAG,  "有版本要更新： 当前版本为" + currentVersionName + "，服务器版本为" + mVersionNameOnServer);
            AlertDialog alertDialogAppUpgrade;
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(LoginActivity.this);
            alertDialogBuilder.setTitle("有新版本APP可更新")
                    .setNegativeButton("以后再说", null)
                    .setPositiveButton("下载新版本", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            downloadApk();
//                            downloadFile();
                            downloadFile2();
                        }
                    })
                    .show();
        }
    }

    private class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("DownloadReceiver", "DownloadReceiver===========");
            Log.i(TAG, intent.getAction());
            if(intent.getAction().equals("android.intent.action.DOWNLOAD_COMPLETE")){
                Log.i(TAG, "下载完成");
            }
        }
    }

    /**
     * “网络变化”的广播接收器
     */
    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=manager.getActiveNetworkInfo();
            if(networkInfo!=null&&networkInfo.isAvailable()){
                Toast.makeText(context,"网络可用",Toast.LENGTH_SHORT).show();
                Log.e("AAA","网络可用");
            }else{
                Log.e("AAA","网络不可用");
                Toast.makeText(context,"网络不可用",Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 为防止下载文件NG，不如直接下载,不再检查本地是否已经有下载过。
     */
    private void downloadApk(){
        DownloadUtility downloadUtility = new DownloadUtility();
        //todo: 这里更新为正式的地址
//        String urlDownload = "http://47.114.157.108//release/v3.3.apk";
        String urlDownload = "http://15.231.6.43:88/release/test.txt";

        ///data/data/com.my.app/files
        String saveDir = this.getFilesDir().getPath();
        downloadUtility.download(urlDownload, saveDir, new DownloadUtility.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                Log.i(TAG, "下载成功" + urlDownload);
                //todo 下载成功，下载次数加一
            }

            @Override
            public void onDownloading(int progress) {
                Log.i(TAG, "下载进行中" + urlDownload);
            }

            @Override
            public void onDownloadFailed() {

                Log.i(TAG, "下载失败" + urlDownload);
            }

        });

    }

    private void downloadFile() {
        //下载路径，如果路径无效了，可换成你的下载路径
//            final String url = "http://c.qijingonline.com/test.mkv";
        String url = "http://15.231.6.43:88/release/test.txt";
        final long startTime = System.currentTimeMillis();
        Log.i("DOWNLOAD", "startTime=" + startTime);

        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Log.i("DOWNLOAD", "download failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    String mSDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File dest = new File(mSDCardPath, url.substring(url.lastIndexOf("/") + 1));
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());

                    bufferedSink.close();
                    Log.i("DOWNLOAD", "download success");
                    Log.i("DOWNLOAD", "totalTime=" + (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("DOWNLOAD", "download failed");
                } finally {
                    if (bufferedSink != null) {
                        bufferedSink.close();
                    }

                }
            }
        });
    }

    private void downloadFile2(){
        //下载路径，如果路径无效了，可换成你的下载路径
//        String url = "http://c.qijingonline.com/test.mkv";
        String url = "http://15.231.6.43:88/release/v3.3.apk";
        //创建下载任务,downloadUrl就是下载链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //指定下载路径和下载文件名
        request.setDestinationInExternalPublicDir("", url.substring(url.lastIndexOf("/") + 1));
        //获取下载管理器
        DownloadManager downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载任务加入下载队列，否则不会进行下载
        downloadManager.enqueue(request);
    }
    /**
     * 获取当前app version code
     */
    private static long getAppVersionCode(Context context) {
        long appVersionCode = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appVersionCode = packageInfo.getLongVersionCode();
            } else {
                appVersionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", e.getMessage());
        }
        return appVersionCode;
    }

    /**
     * 获取当前app version name
     */
    private static String getAppVersionName(Context context) {
        String appVersionName = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", e.getMessage());
        }
        return appVersionName;
    }

    private void ConfirmSendEmail(String userEmail,final String account )
    {
        final  String registeredEmail = userEmail;

        if (userEmail == null)
        {
            userEmail = "";
        }

        int  pos = userEmail.lastIndexOf("@");
        if ( pos!= -1)
        {
            userEmail = userEmail.charAt(0) + "***" + userEmail.substring(pos);
        }



        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder
                (LoginActivity.this);

        final EditText etEmail = new EditText(LoginActivity.this);
        alertDialogBuilder.setTitle("请确认注册的邮箱，新密码将被发送到 "+ userEmail +" 邮箱")
                .setView(etEmail)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String inputEmail = etEmail.getText().toString();
                        if (!inputEmail.equals(registeredEmail))
                        {
                            Toast.makeText(LoginActivity.this, "邮箱不匹配！", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ///accounts/{account}/password/reset
                        String url = Constant.putPasswordReset.replace("{account}",account );

                        mNetwork.put(url, null, new Handler() {
                                    @Override
                                    public void handleMessage(final Message msg) {

                                        String errorMsg = "";

                                        if (msg.what == Network.OK) {
                                            Result result = (Result) (msg.obj);

                                            BaseResponse responseData = CommonUtility.objectToJavaObject(result.getData(), BaseResponse.class);

                                            if (responseData != null && responseData.errorCode == 0) {
                                                Toast.makeText(LoginActivity.this, "密码重置成功，请稍后检查邮箱", Toast.LENGTH_SHORT).show();
                                            } else {
                                                errorMsg = "密码重置失败:" + result.getCode() + result.getMessage();
                                            }
                                        } else {
                                            errorMsg = "密码重置失败：" + (String) msg.obj;
                                        }

                                        if (!errorMsg.isEmpty()) {
                                            Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },
                                (handler,msg)->{
                                    handler.sendMessage(msg);
                                });
                        //
                    }
                })
                .show();
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

        mAccount  = mAccountText.getText().toString().trim();
        mPassword = mPasswordText.getText().toString();

        if (mAccount.isEmpty() || mPassword.isEmpty())
        {
            ToastUtils.setMsgColor(0x77000000);
            ToastUtils.setBgColor(0xAAFFFFFF);
            ToastUtils.showShort("用户名和密码不能为空");
            Log.d(TAG, "用户名和密码不能为空");
            if( mLoadingProcessDialog != null) {
                mLoadingProcessDialog.dismiss();
            }
            mLoginButton.setEnabled(true);
        }
        else {
            Log.d(TAG, "account: " + mAccount);
            Log.d(TAG, "password: " + mPassword);
            HashMap<String, String> mPostValue = new LinkedHashMap<>();
            mPostValue.put("account", mAccount);
            mPostValue.put("password",mPassword );

            if(TextUtils.isEmpty(XiaotaApp.getApp().getServerIPAndPort())){

                mLoginButton.setEnabled(true);
                ToastUtils.showShort("服务端IP为空，请设置IP地址");
                Log.d(TAG, "login: 服务端IP为空，请设置IP地址");
            }
            else {

                //save the config
                XiaotaApp.getApp().setAccount(mAccount);
                XiaotaApp.getApp().setPassword(mPassword);

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
            errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
            if (errorMsg != null)
            {
                onLoginFailed(errorMsg);
                return;
            }

            Result result= (Result)(msg.obj);

            LoginResponseData responseData = CommonUtility.objectToJavaObject(result.getData(),LoginResponseData.class);
            onLoginSuccess( responseData);
        }
    }

    public void onLoginSuccess(LoginResponseData loginResponseData) {

        if( loginResponseData != null) {
            //Store to memory and preference
            mApp.setIsLogined(true,
                    mAccount,
                    null,
                    mPassword,
                    mServiceIpAndPort
            );
            if (loginResponseData.errorCode == 0) {

                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();

                Intent intentProjects = new Intent(this, ProjectsCenterActivity.class);

                String strResponseData = JSON.toJSONString(loginResponseData);
                intentProjects.putExtra("loginResponseData", (Serializable) strResponseData);
                intentProjects.putExtra("requestCode", "initialSelectEntry".hashCode());
                startActivity(intentProjects);

//                Intent intent = new Intent(this, Main.class);
//
//                String strResponseData = JSON.toJSONString(loginResponseData);
//                intent.putExtra("loginResponseData", (Serializable) strResponseData);
//                startActivity(intent);
                finish();//销毁此Activity
                
            } else {

                Toast.makeText(this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
            }
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
                            mServiceIpAndPort = editText.getText().toString();
                            XiaotaApp.getApp().setServerIPAndPort(mServiceIpAndPort);
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
        unregisterReceiver(downloaderReceiver);
//        unregisterReceiver(notificationClickReceiver);

    }
}
