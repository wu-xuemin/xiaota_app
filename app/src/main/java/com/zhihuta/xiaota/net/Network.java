package com.zhihuta.xiaota.net;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.response.LoginResponseDataWrap;
import com.zhihuta.xiaota.ui.XiaotaApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhihuta.xiaota.util.ShowMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network {
    private static String TAG = "nlgNetwork";
    @SuppressLint("StaticFieldLeak")
    private static com.zhihuta.xiaota.net.Network mNetWork;
    @SuppressLint("StaticFieldLeak")
    private static Application mCtx;
    private static ThreadPoolExecutor executor;
    private static final int CORE_THREAD_NUM = 3;
    public static final int OK = 1;
    private static final int NG = 0;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");


    private Network() {
    }

    public static com.zhihuta.xiaota.net.Network Instance(Application ctx){
        if( mNetWork == null ) {
            mCtx= ctx;
            mNetWork = new com.zhihuta.xiaota.net.Network();
            executor = new ThreadPoolExecutor(CORE_THREAD_NUM, 20, 500, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(5), new ThreadPoolExecutor.DiscardPolicy());
        }
        return mNetWork;
    }
    /**
     * 判断是否有网络连接
     */
    public boolean isNetworkConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) (mCtx.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connectivity != null) {
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取login信息
     */
    public void fetchLoginData(final String url, final LinkedHashMap<String, String> values, final Handler handler) {
        final Message msg = handler.obtainMessage();
        if (!isNetworkConnected()) {
            Log.d(TAG, "fetchLoginData: 没网络");
            ShowMessage.showToast(mCtx, mCtx.getString(R.string.network_not_connect), ShowMessage.MessageDuring.SHORT);
            msg.what = NG;
            msg.obj = mCtx.getString(R.string.network_not_connect);
            handler.sendMessage(msg);
        } else {
            Log.d(TAG, "fetchLoginData: 有网络");
            if (url != null && values != null) {
                Log.d(TAG, "fetchLoginData: not null");
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        RequestBody requestBody;
                        FormBody.Builder builder = new FormBody.Builder();
                        for (Object o : values.entrySet()) {
                            HashMap.Entry entry = (HashMap.Entry) o;
                            builder.add((String) entry.getKey(), (String) entry.getValue());
                        }
                        requestBody = builder.build();
                        //Post method
                        Request request = new Request.Builder().url(url).post(requestBody).build();
                        OkHttpClient client = ((XiaotaApp) mCtx).getOKHttpClient();
                        Response response = null;
                        try {
                            //同步网络请求
                            response = client.newCall(request).execute();
                            boolean success = false;
                            if (response.isSuccessful()) {
                                Log.d(TAG, "fetchLoginData run: response success");
                                Gson gson = new Gson();
                                LoginResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken<LoginResponseDataWrap>(){}.getType());
                                if (responseData != null) {
                                    Log.d(TAG, "fetchLoginData run: responseData："+responseData.getCode());
                                    if (responseData.getCode() == 200) {
                                        if(responseData.getData().getValid()!=1){
                                            Log.e(TAG, "用户已离职");
                                            msg.obj="用户已离职";
                                        }else {
                                            success = true;
                                            msg.obj = responseData.getData();
                                        }
                                    } else if (responseData.getCode() == 400) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "fetchLoginData run: error 400 :"+responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else if (responseData.getCode() == 500) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "fetchLoginData run: error 500 :"+responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    }else {
                                        Log.e(TAG, "fetchLoginData Format JSON string to object error!");
                                    }
                                }
                                if (success) {
                                    msg.what = OK;
                                }
                            } else {
                                msg.what = NG;
                            }
                            response.close();
                        } catch (Exception e) {
                            msg.what = NG;
                            msg.obj = "Network error!";
                            Log.d(TAG, "fetchLoginData run: catch "+e);
                        } finally {
                            handler.sendMessage(msg);
                            Log.d(TAG, "fetchLoginData run: finally");
                            if(response != null) {
                                response.close();
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 获取单个machineByNameplate
     */
    public void fetchMachineByNameplate(final String url, final LinkedHashMap<String, String> values, final Handler handler) {
        final Message msg = handler.obtainMessage();
        if (!isNetworkConnected()) {
            ShowMessage.showToast(mCtx, mCtx.getString(R.string.network_not_connect), ShowMessage.MessageDuring.SHORT);
            msg.what = NG;
            msg.obj = mCtx.getString(R.string.network_not_connect);
            handler.sendMessage(msg);
        } else {
            if (url != null && values != null) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        RequestBody requestBody;
                        FormBody.Builder builder = new FormBody.Builder();
                        for (Object o : values.entrySet()) {
                            HashMap.Entry entry = (HashMap.Entry) o;
                            builder.add((String) entry.getKey(), (String) entry.getValue());
                        }
                        requestBody = builder.build();
                        //Post method
                        Request request = new Request.Builder().url(url).post(requestBody).build();
                        OkHttpClient client = ((XiaotaApp) mCtx).getOKHttpClient();
                        Response response = null;
                        try {
                            //同步网络请求
                            response = client.newCall(request).execute();
                            boolean success = false;
                            if (response.isSuccessful()) {
                                Gson gson = new Gson();
//                                MachineResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken<MachineResponseDataWrap>(){}.getType());
//                                if (responseData != null) {
//                                    Log.d(TAG, "fetchMachineByNameplate run: "+responseData.getData());
//                                    if (responseData.getCode() == 200) {
//                                        if (responseData.getData()!=null) {
//                                            success = true;
//                                            msg.obj = responseData.getData();
//                                        } else {
//                                            msg.what = NG;
//                                            msg.obj = "没有这个钢印号的机器信息！";
//                                        }
//                                    } else if (responseData.getCode() == 400) {
//                                        Log.e(TAG, responseData.getMessage());
//                                        msg.obj = responseData.getMessage();
//                                    } else if (responseData.getCode() == 500) {
//                                        Log.e(TAG, responseData.getMessage());
//                                        Log.d(TAG, "fetchMachineByNameplate run: error 500 :"+responseData.getMessage());
//                                        msg.obj = responseData.getMessage();
//                                    } else {
//                                        Log.e(TAG, "fetchMachineByNameplate Format JSON string to object error!");
//                                    }
//                                }
//                                if (success) {
//                                    msg.what = OK;
//                                }
                            } else {
                                msg.what = NG;
                                msg.obj = "网络请求错误！";
                            }
                            response.close();
                        } catch (Exception e) {
                            msg.what = NG;
                            msg.obj = "网络请求错误！";
                        } finally {
                            handler.sendMessage(msg);
                            if(response != null) {
                                response.close();
                            }
                        }
                    }
                });
            }
        }
    }
}
