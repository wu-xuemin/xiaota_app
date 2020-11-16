package com.zhihuta.xiaota.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.blankj.utilcode.util.CacheUtils;
import com.blankj.utilcode.util.Utils;
import com.zhihuta.xiaota.common.AuthenticateInterceptor;
import com.zhihuta.xiaota.common.MyActivityManager;
import com.zhihuta.xiaota.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;


public class XiaotaApp extends Application {

    private static final boolean DEBUG_LOG = true;
    private static final String TAG = "XiaotaApp";

    /**
     * Shared preferences file name, where we store persistent values.
     */
    static final String SHARED_PREFS_FILENAME = "路由宝";

    public static final int LOGIN_REQUEST_CODE = 20000;
    public static final int LOGIN_RESULT_SUCCESS_CODE = 20001;
    public static final int LOGIN_FOR_ADMIN = 2;
    public static final int LOGIN_FOR_STAFF = 11;

    public static final String FROM_NOTIFICATION = "1";



    private boolean isLogined = false; // 是否已登录
    private String account;//用户账号
    private String fullname; //用户姓名
    private String password; //用户密码
    private String roles[]; //用户角色
    private String ip="47.114.157.108";
    private String port="8083";
    private int appUserId;
    private int groupId;
    private String groupName;
    private String groupType;

    private String mIMEI = null;
    private static XiaotaApp mApp;


    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    /**
     * 缓存
     */
    private CacheUtils mCache;

    /**
     * Shared preferences editor for writing program settings.
     */
    private SharedPreferences.Editor mPrefEditor = null;
    private SharedPreferences mSharedPrefs;

    /**
     * Http Client
     */
    private OkHttpClient mOKHttpClient;


    /**
     * Persistent value types.
     */
    public enum PersistentValueType {
        ACCOUNT,    //账号
        PASSWORD,   //密码
        FULL_NAME,  //名字
        IS_LOGIN,   //是否登录
        ROLE,        //角色
        SERVICE_IP,  //服务器地址
        SERVICE_PORT,  //服务器端口
        USER_ID,     //用户id
        GROUP_ID,
        GROUP_NAME,
        GROUP_TYPE
    }

    public enum TabPageIndex {
        ACCOUNT,    //账号
        PASSWORD,   //密码
        FULL_NAME,  //名字
        IS_LOGIN,   //是否登录
        ROLE,        //角色
        SERVICE_IP,  //服务器地址
        SERVICE_PORT,  //服务器端口
        USER_ID,     //用户id
        GROUP_ID,
        GROUP_NAME,
        GROUP_TYPE
    }




    public static XiaotaApp getApp() {
        return mApp;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate() {
        super.onCreate();

        mIMEI = null;

        mApp = this;
        //start log
        LogUtils.logInit(true);

        Utils.init(this);
        mCache = CacheUtils.getInstance(this.getCacheDir());

        /**
         * okhttp3带cookie请求
         * 从返回的response里得到新的Cookie，你可能得想办法把Cookie保存起来。
         * 但是OkHttp可以不用我们管理Cookie，自动携带，保存和更新Cookie。
         * 方法是在创建OkHttpClient设置管理Cookie的CookieJar
         */
        mOKHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        cookieStore.put(httpUrl.host(), list);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                }).addInterceptor(new AuthenticateInterceptor())
                .build();

        /*
		 * Get shared preferences and editor so we can read/write program settings.
		 */
        mSharedPrefs = getSharedPreferences( SHARED_PREFS_FILENAME, 0 );
        mPrefEditor = mSharedPrefs.edit();


        this.isLogined  = Boolean.valueOf(readValue(PersistentValueType.IS_LOGIN, "0"));
        this.account = readValue(PersistentValueType.ACCOUNT, "");
        this.password = readValue(PersistentValueType.PASSWORD, "");
        this.fullname = readValue(PersistentValueType.FULL_NAME, "");
        String strRoles = readValue(PersistentValueType.ROLE, "customer_worker").toLowerCase();
        //roles = "customer_worker,other else... split by ',' "
         if (!strRoles.isEmpty())
        {
            this.roles = strRoles.split(",");
        }
        else
        {
            this.roles = new String[1];
            this.roles[0] = "customer_worker";
        }
        //}

        this.ip   = readValue(PersistentValueType.SERVICE_IP, "47.114.157.108");
        if( this.ip.contains(":") )
        {
        	this.ip =  this.ip.split(":")[0];
            this.port = this.ip.split(":")[1];
        }
        else
        {
            this.port = readValue(PersistentValueType.SERVICE_PORT, "8083");
        }
        if (false)
        {//for debug purpose
            this.ip = "192.168.31.133";
            this.port = "8083";
//        this.ip = "10.0.2.2:8080";
//模拟器
//        this.ip = "10.0.2.2:8004";
        this.ip = "172.20.10.3";
///        this.ip = "47.114.157.108:8083";
        }
        String appUserIdStr = readValue(PersistentValueType.USER_ID, "0");
        if ("".equals(appUserIdStr)){
            this.appUserId =0;
        }else {
            this.appUserId = Integer.valueOf(readValue(PersistentValueType.USER_ID, "0"));
        }
        String groupIdStr = readValue(PersistentValueType.GROUP_ID, "0");
        if ("".equals(groupIdStr)){
            this.groupId =0;
        }else {
            this.groupId = Integer.valueOf(readValue(PersistentValueType.GROUP_ID, "0"));
        }
        this.groupName = readValue(PersistentValueType.GROUP_NAME, "");
        this.groupType = readValue(PersistentValueType.GROUP_TYPE, "");

        //Android自 API 14开始引入了一个方法，即Application的registerActivityLifecycleCallbacks方法，用来监听所有Activity的生命周期回调，
        // 比如onActivityCreated,onActivityResumed等。
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                MyActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public String getIMEI() {

        if (mIMEI!=null)
        {
            return mIMEI;
        }

        try {

            mIMEI = "";

            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                mIMEI = telephonyManager.getDeviceId();
//模拟器
                if(false)
                {
                    mIMEI = "AVDAVD7890AVDAV";
                }
            } else {
                Log.d(TAG, "getIMEI: have some error");
            }
        }
        catch (Exception ex)
        {
            Log.d(TAG, "getIMEI: have some error exception");

        }
        return mIMEI;
    }
    /**
     * 由片段调用，设置登录信息
     *
     * @param isLogined
     * @param account
     * @param fullname
     */
    public void setIsLogined(boolean isLogined,
                             String account,
                             String fullname,
                             String password,
                             String ServiceIpAndPort) {
        writePreferenceValue(PersistentValueType.IS_LOGIN, String.valueOf(isLogined));
        writePreferenceValue(PersistentValueType.ACCOUNT, account);
        writePreferenceValue(PersistentValueType.FULL_NAME, fullname);
        writePreferenceValue(PersistentValueType.PASSWORD, password);
        writePreferenceValue(PersistentValueType.SERVICE_IP, ServiceIpAndPort.split(":")[0]);
        writePreferenceValue(PersistentValueType.SERVICE_PORT, ServiceIpAndPort.split(":")[1]);

        try {
            commitValues();
            this.isLogined = isLogined;
            this.account = account;
            this.fullname = fullname;
            this.password = password;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLogOut() {

        writePreferenceValue(PersistentValueType.IS_LOGIN, "");
        //writePreferenceValue(PersistentValueType.ACCOUNT, "");
        writePreferenceValue(PersistentValueType.FULL_NAME, "");
        writePreferenceValue(PersistentValueType.PASSWORD, "");
        try {
            commitValues();
            this.isLogined = false;
            //this.account = "";
            this.fullname = "";
            this.password = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the specified string persistent value.
     *
     * @param valueType - The persistent value type to read.
     * @param sDefault - The default value to return if the requested value does not exist.
     * @return The requested value.
     */
    public String readValue( PersistentValueType valueType, String sDefault )
    {
        if ( DEBUG_LOG ) {
            Log.i(TAG, String.format("[readValue] ==> value [%s] sDefault [%s]",
                    valueType.toString(), sDefault));
        }

        String sValue = mSharedPrefs.getString(valueType.toString(), sDefault);

        if ( DEBUG_LOG ) {
            Log.i(TAG, "[readValue] <== nValue: " + sValue);
        }
        return sValue;
    }

    /**
     * Writes the specified string persistent value.
     *
     * @param valueType - The persistent value type to read.
     * @param sValue - The value to write.
     */
    public void writePreferenceValue( PersistentValueType valueType, String sValue )
    {
        if ( DEBUG_LOG ) {
            Log.i(TAG, String.format("[writeValue] ==> sValuName [%s] nValue [%s]",
                    valueType.toString(), sValue));
        }

        mPrefEditor.putString(valueType.toString(), sValue );

        if ( DEBUG_LOG ) {
            Log.i(TAG, "[writeValue] <==");
        }
    }

    /**
     * Commits persistent values that were previously written.
     *
     * @throws Exception When the commit operation fails.
     */
    public void commitValues() throws Exception
    {
        if ( DEBUG_LOG ) {
            Log.i(TAG, "[commitValues] ==>");
        }

        boolean bSuccess = mPrefEditor.commit();
        if ( ! bSuccess ){
            throw new Exception( "commit() failed" );
        }
        if ( DEBUG_LOG ) {
            Log.i(TAG, "[commitValues] <==");
        }
    }

    /**
     * Delete the specified string persistent value.
     *
     * @param valueType - The persistent value type to Delete.
     */
    public void deleteValue( PersistentValueType valueType)
    {
        if ( DEBUG_LOG ) {
            Log.i(TAG, String.format("[deleteValue] ==> sValuName [%s] ",
                    valueType.toString()));
        }
        mPrefEditor.remove( valueType.toString() );

        if ( DEBUG_LOG ) {
            Log.i(TAG, "[deleteValue] <==");
        }
    }

    public OkHttpClient getOKHttpClient() {
        return mOKHttpClient;
    }

    public boolean isLogined() {
        return isLogined;
    }

    public String getAccount() {

        this.account = readValue(PersistentValueType.ACCOUNT, "a");
        return account;
    }

    public void setAccount(String account) {
         this.account = account;

        writePreferenceValue(PersistentValueType.ACCOUNT, account);
    }

    public String getFullName() {
        return fullname;
    }

    public String getPassword() {

        this.password = readValue(PersistentValueType.PASSWORD, "a");
        return password;
    }

    public void setPassword(String password) {
       this.password = password;
       writePreferenceValue(PersistentValueType.PASSWORD, password);
    }
    public String[] getRoles() {
        return roles;
    }

    public int getAppUserId() {
        return appUserId;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    private String getServerIP() {
        return ip;
    }

    private void setServerIP(String ipStr) {
        writePreferenceValue(PersistentValueType.SERVICE_IP, ipStr);
        try {
            commitValues();
            this.ip = ipStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //return string like 127.0.0.1:8083
    public String getServerIPAndPort() {
        return ip+":"+port;
    }

    //string like 127.0.0.1:8083 or 127.0.0.1 or domain likely,it will save to ip and port variables
    public void setServerIPAndPort(String ipAndPortStr) {

        try {

            if (ipAndPortStr.isEmpty())
            {
                return;
            }
            if (ipAndPortStr.contains(":"))
            {
                this.ip = ipAndPortStr.split(":")[0];
                this.port = ipAndPortStr.split(":")[1];
            }
            else
            {
                this.ip = ipAndPortStr;
                this.port = "8083";//default
            }

            writePreferenceValue(PersistentValueType.SERVICE_IP, this.ip);
            writePreferenceValue(PersistentValueType.SERVICE_IP, this.port);
            commitValues();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public CacheUtils getCache() {
        return mCache;
    }
}
