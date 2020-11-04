package com.zhihuta.xiaota.net;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.GsonBuilder;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.response.DistanceResponseDataWrap;
import com.zhihuta.xiaota.bean.response.LoginResponseDataWrap;
import com.zhihuta.xiaota.bean.response.DxResponseDataWrap;
import com.zhihuta.xiaota.bean.response.LujingResponseDataWrap;
import com.zhihuta.xiaota.bean.response.MsgFailResponseDataWrap;
import com.zhihuta.xiaota.bean.response.UserResponseDataWrap;
import com.zhihuta.xiaota.ui.XiaotaApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhihuta.xiaota.util.ShowMessage;

import java.util.HashMap;
import java.util.LinkedHashMap;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
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
    private static final MediaType typeJSON = MediaType.parse("application/json;charset=utf-8");
    //    private static final int CORE_THREAD_NUM = 3;
    private static final int CORE_THREAD_NUM = 1;
    public static final int OK = 1;
    private static final int NG = 0;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");


    private Network() {
    }

    public static com.zhihuta.xiaota.net.Network Instance(Application ctx) {
        if (mNetWork == null) {
            mCtx = ctx;
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

    //获取路径的间距列表
    public void fetchDistanceListOfLujing(final String url, final LinkedHashMap<String, String> values, final Handler handler) {
        final Message msg = handler.obtainMessage();
        if (!isNetworkConnected()) {
            Log.d(TAG, "fetchDistanceListOfLujing: 没网络");
            ShowMessage.showToast(mCtx, mCtx.getString(R.string.network_not_connect), ShowMessage.MessageDuring.SHORT);
            msg.what = NG;
            msg.obj = mCtx.getString(R.string.network_not_connect);
            handler.sendMessage(msg);
        } else {
            if (url != null && values != null) {
                Log.d(TAG, "fetchDistanceListOfLujing: not null");
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
//                        Request request = new Request.Builder().url(url).post(requestBody).build();
                        Request request = new Request.Builder().url(url).get().build();
                        OkHttpClient client = ((XiaotaApp) mCtx).getOKHttpClient();
                        Response response = null;
                        try {
                            //同步网络请求
                            response = client.newCall(request).execute();
                            boolean success = false;
                            if (response.isSuccessful()) {
                                Log.d(TAG, "fetchDistanceListOfLujing run: response success");
//                                Gson gson = new Gson();
                                Gson gson = new GsonBuilder()
                                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .create(); //没有指定时间格式的话 解析会出错。
                                DistanceResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken<DistanceResponseDataWrap>() {
                                }.getType());
                                if (responseData != null) {
                                    Log.d(TAG, "fetchDistanceListOfLujing run: responseData：" + responseData.getCode());
                                    if (responseData.getCode() == 200) {

                                      //  for (int k = 0; k < responseData.getData().getDistance_qrs().size(); k++) {
//                                            success = true;
                                            msg.obj = responseData.getData().getDistance_qrs();
                                        //}
                                        success = true; //可能还没有间距列表，这也是OK的
                                    } else if (responseData.getCode() == 400) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "getDistance_qrs run: error 400 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else if (responseData.getCode() == 500) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "getDistance_qrs run: error 500 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else {
                                        Log.e(TAG, "getDistance_qrs Format JSON string to object error!");
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
                            Log.d(TAG, "getDistance_qrs run: catch " + e + e.getMessage() + e.getCause());
                        } finally {
                            handler.sendMessage(msg);
                            Log.d(TAG, "getDistance_qrs run: finally");
                            if (response != null) {
                                response.close();
                            }
                        }
                    }
                });
            }
        }
    }

    public void fetchLujingListData(final String url, final LinkedHashMap<String, String> values, final Handler handler) {
        final Message msg = handler.obtainMessage();
        if (!isNetworkConnected()) {
            Log.d(TAG, "fetchLujingListData: 没网络");
            ShowMessage.showToast(mCtx, mCtx.getString(R.string.network_not_connect), ShowMessage.MessageDuring.SHORT);
            msg.what = NG;
            msg.obj = mCtx.getString(R.string.network_not_connect);
            handler.sendMessage(msg);
        } else {
            Log.d(TAG, "获取路径list: 有网络");
            if (url != null && values != null) {
                Log.d(TAG, "fetchLujingListData: not null");
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
//                        Request request = new Request.Builder().url(url).post(requestBody).build();
                        Request request = new Request.Builder().url(url).get().build();
                        OkHttpClient client = ((XiaotaApp) mCtx).getOKHttpClient();
                        Response response = null;
                        try {
                            //同步网络请求
                            response = client.newCall(request).execute();
                            boolean success = false;
                            if (response.isSuccessful()) {
                                Log.d(TAG, "fetchLujingListData run: response success");
//                                Gson gson = new Gson();
                                Gson gson = new GsonBuilder()
                                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .create(); //没有指定时间格式的话 解析会出错。
                                LujingResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken<LujingResponseDataWrap>() {
                                }.getType());
                                if (responseData != null) {
                                    Log.d(TAG, "fetchLujingListData run: responseData：" + responseData.getCode());
                                    if (responseData.getCode() == 200) {

                                        for (int k = 0; k < responseData.getData().getPaths().size(); k++) {
//                                            success = true;
                                            msg.obj = responseData.getData().getPaths();
                                        }
                                        success = true;//
                                    } else if (responseData.getCode() == 400) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "fetchLujingListData run: error 400 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else if (responseData.getCode() == 500) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "fetchLujingListData run: error 500 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else {
                                        Log.e(TAG, "fetchLujingListData Format JSON string to object error!");
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
                            Log.d(TAG, "fetchLujingListData run: catch " + e + e.getMessage() + e.getCause());
                        } finally {
                            handler.sendMessage(msg);
                            Log.d(TAG, "fetchLujingListData run: finally");
                            if (response != null) {
                                response.close();
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 这里是所有的电线
     */
    public void fetchDxListData(final String url, final LinkedHashMap<String, String> values, final Handler handler) {
        final Message msg = handler.obtainMessage();
        if (!isNetworkConnected()) {
            Log.d(TAG, "fetchDxListData: 没网络");
            ShowMessage.showToast(mCtx, mCtx.getString(R.string.network_not_connect), ShowMessage.MessageDuring.SHORT);
            msg.what = NG;
            msg.obj = mCtx.getString(R.string.network_not_connect);
            handler.sendMessage(msg);
        } else {
            Log.d(TAG, "fetchDxListData: 有网络");
            if (url != null && values != null) {
                Log.d(TAG, "fetchDxListData: not null");
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
//                        Request request = new Request.Builder().url(url).post(requestBody).build();
                        Request request = new Request.Builder().url(url).get().build();
                        OkHttpClient client = ((XiaotaApp) mCtx).getOKHttpClient();
                        Response response = null;
                        try {
                            //同步网络请求
                            response = client.newCall(request).execute();
                            boolean success = false;
                            if (response.isSuccessful()) {
                                Log.d(TAG, "fetchDxListData run: response success");
                                Gson gson = new Gson();
                                DxResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken<DxResponseDataWrap>() {
                                }.getType());
                                if (responseData != null) {
                                    Log.d(TAG, "fetchDxListData run: responseData：" + responseData.getCode());
                                    if (responseData.getCode() == 200) {

                                        for (int k = 0; k < responseData.getData().getWires().size(); k++) {
//                                            success = true;
                                            String serial_number = responseData.getData().getWires().get(k).getSerial_number();
//                                            Log.i("aaa", "serial_number is " + serial_number);
                                            msg.obj = responseData.getData().getWires();
                                        }
                                        success = true;//
                                    } else if (responseData.getCode() == 400) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "fetchDxListData run: error 400 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else if (responseData.getCode() == 500) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "fetchDxListData run: error 500 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else {
                                        Log.e(TAG, "fetchDxListData Format JSON string to object error!");
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
                            Log.d(TAG, "fetchDxListData run: catch " + e);
                        } finally {
                            handler.sendMessage(msg);
                            Log.d(TAG, "fetchDxListData run: finally");
                            if (response != null) {
                                response.close();
                            }
                        }
                    }
                });
            }
        }
    }

    public void fetchUserListData(final String url, final LinkedHashMap<String, String> values, final Handler handler) {
        final Message msg = handler.obtainMessage();
        if (!isNetworkConnected()) {
            Log.d(TAG, "fetchUserListData: 没网络");
            ShowMessage.showToast(mCtx, mCtx.getString(R.string.network_not_connect), ShowMessage.MessageDuring.SHORT);
            msg.what = NG;
            msg.obj = mCtx.getString(R.string.network_not_connect);
            handler.sendMessage(msg);
        } else {
            Log.d(TAG, "fetchUserListData: 有网络");
            if (url != null && values != null) {
                Log.d(TAG, "fetchUserListData: not null");
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
                                Log.d(TAG, "fetchUserListData run: response success");
                                Gson gson = new Gson();
                                UserResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken<UserResponseDataWrap>() {
                                }.getType());
                                if (responseData != null) {
                                    Log.d(TAG, "fetchUserListData run: responseData：" + responseData.getCode());
                                    if (responseData.getCode() == 200) {

                                        for (int k = 0; k < responseData.getData().getList().size(); k++) {
                                            success = true;
                                            String name = responseData.getData().getList().get(k).getName();
                                            Log.i("aaa", "name is " + name);
                                            msg.obj = responseData.getData();
                                        }
                                    } else if (responseData.getCode() == 400) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "fetchLoginData run: error 400 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else if (responseData.getCode() == 500) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "fetchLoginData run: error 500 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else {
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
                            Log.d(TAG, "fetchLoginData run: catch " + e);
                        } finally {
                            handler.sendMessage(msg);
                            Log.d(TAG, "fetchLoginData run: finally");
                            if (response != null) {
                                response.close();
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 获取login信息  --OK
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

                        JSONObject obj = new JSONObject();
                        try {
                            for (Object o : values.entrySet()) {
                                HashMap.Entry entry = (HashMap.Entry) o;
                                //builder.add((String) entry.getKey(), (String) entry.getValue());
                                obj.put((String) entry.getKey(), (String) entry.getValue());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        requestBody = RequestBody.create(typeJSON, obj.toString());


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
                                LoginResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken<LoginResponseDataWrap>() {
                                }.getType());
                                if (responseData != null) {
                                    Log.d(TAG, "fetchLoginData run: responseData：" + responseData.getCode());
                                    if (responseData.getCode() == 200) {

                                        success = true;
                                        msg.obj = responseData.getData();
                                    } else if (responseData.getCode() == 400) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "fetchLoginData run: error 400 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else if (responseData.getCode() == 500) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "fetchLoginData run: error 500 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else {
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
                            Log.d(TAG, "fetchLoginData run: catch " + e);
                        } finally {
                            handler.sendMessage(msg);
                            Log.d(TAG, "fetchLoginData run: finally");
                            if (response != null) {
                                response.close();
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 添加新路径， values里 name-xxx
     */
    public void addNewLujing(final String url, final LinkedHashMap<String, String> values, final Handler handler) {
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
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("name", values.get("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RequestBody RequestBody2 = RequestBody.create(typeJSON, obj.toString());

                        OkHttpClient client =((XiaotaApp) mCtx).getOKHttpClient();;
                        Request request = new Request.Builder()
                                // 指定访问的服务器地址
                                .url(url).post(RequestBody2)
                                .build();
                        Response response = null;
                        try {
                            //同步网络请求
                            response = client.newCall(request).execute();
                            boolean success = false;
                            if (response.isSuccessful()) {
                                Gson gson = new Gson();
                                MsgFailResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken< MsgFailResponseDataWrap >() {}.getType());
                                if (responseData != null) {
                                    Log.d(TAG, "addNewLujing run: " + responseData.getCode());
                                    if (responseData.getCode() == 200) {
                                        success = true;
                                        msg.obj = responseData.getData(); //成功时，后端返回路径的ID.  {errorCode=0.0, id=56.0}
//
                                    } else if (responseData.getCode() == 400) {
                                        Log.e(TAG, responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else if (responseData.getCode() == 500) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "addNewLujing run: error 500 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else {
                                        msg.obj = responseData.getMessage(); //后端添加失败时，比如二维码已经存在在该路径中。返回的message会包含这个信息
                                        Log.e(TAG, "addNewLujing Format JSON string to object error!");
                                    }
                                }

                                if (success) {
                                    msg.what = OK;
                                }

                            } else {
                                msg.what = NG;
                                msg.obj = "网络请求错误！";
                                Log.e("aaaa", "response 4 网络请求错误");
                            }
                            response.close();
                        } catch (Exception e) {
                            msg.what = NG;
                            msg.obj = "Network error!";
                            Log.d(TAG, "addNewLujing run: network error!");
                        } finally {
                            handler.sendMessage(msg);
                            if (response != null) {
                                response.close();
                            }
                        }

                    }
                });
            }
        }
    }
    // 删除路径，  ids 如 "1,2,3,4"
    public void deleteLujing(final String url, final String IDs, final Handler handler) {
        final Message msg = handler.obtainMessage();
        if (!isNetworkConnected()) {
            ShowMessage.showToast(mCtx, mCtx.getString(R.string.network_not_connect), ShowMessage.MessageDuring.SHORT);
            msg.what = NG;
            msg.obj = mCtx.getString(R.string.network_not_connect);
            handler.sendMessage(msg);
        } else {
            if (url != null && IDs != null) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {

//                        JSONObject obj = new JSONObject();
//                        try {
//                            obj.put("ids", IDs.get("ids"));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                        RequestBody RequestBody2 = RequestBody.create(typeJSON, IDs);

                        OkHttpClient client =((XiaotaApp) mCtx).getOKHttpClient();;
                        Request request = new Request.Builder()
                                .url(url).delete(RequestBody2)
                                .build();
                        Response response = null;
                        try {
                            //同步网络请求
                            response = client.newCall(request).execute();
                            boolean success = false;
                            if (response.isSuccessful()) {
                                Gson gson = new Gson();
                                MsgFailResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken< MsgFailResponseDataWrap >() {}.getType());
                                if (responseData != null) {
                                    Log.d(TAG, "deleteLujing run: " + responseData.getCode());
                                    if (responseData.getCode() == 200) {
                                        success = true;
                                        msg.obj = responseData.getData(); //成功时，后端返回路径的ID.  {errorCode=0.0, id=56.0}
//
                                    } else if (responseData.getCode() == 400) {
                                        Log.e(TAG, responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else if (responseData.getCode() == 500) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "deleteLujing run: error 500 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else {
                                        msg.obj = responseData.getMessage(); //后端添加失败时，比如二维码已经存在在该路径中。返回的message会包含这个信息
                                        Log.e(TAG, "deleteLujing Format JSON string to object error!");
                                    }
                                }

                                if (success) {
                                    msg.what = OK;
                                }

                            } else {
                                msg.what = NG;
                                msg.obj = "网络请求错误！";
                            }
                            response.close();
                        } catch (Exception e) {
                            msg.what = NG;
                            msg.obj = "Network error!";
                            Log.d(TAG, "deleteLujing run: network error!");
                        } finally {
                            handler.sendMessage(msg);
                            if (response != null) {
                                response.close();
                            }
                        }

                    }
                });
            }
        }
    }
    /**
     * 修改 路径的名称
     */
    public void modifyLujingName(final String url, final LinkedHashMap<String, String> values, final Handler handler) {
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
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("name", values.get("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RequestBody RequestBody2 = RequestBody.create(typeJSON, obj.toString());

                        OkHttpClient client = ((XiaotaApp) mCtx).getOKHttpClient();;
                        Request request = new Request.Builder()
                                // 指定访问的服务器地址
                                .url(url).put(RequestBody2)
                                .build();
                        Response response = null;
                        try {
                            //同步网络请求
                            response = client.newCall(request).execute();
                            boolean success = false;
                            if (response.isSuccessful()) {
                                msg.what = OK;
                            } else {
                                msg.what = NG;
                            }
                            response.close();
                        } catch (Exception e) {
                            msg.what = NG;
                            msg.obj = "Network error!";
                            Log.d(TAG, "modifyLujingName run: network error!");
                        } finally {
                            handler.sendMessage(msg);
                            if (response != null) {
                                response.close();
                            }
                        }

                    }
                });
            }
        }
    }
    /**
     * 添加路径的一个间距, 间距信息(只有qr_id号)在 values中。 方法是Put不是POST
     */
    public void putLujingDistance(final String url, final LinkedHashMap<String, String> values, final Handler handler) {
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
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("qr_id", values.get("qr_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RequestBody RequestBody2 = RequestBody.create(typeJSON, "" + obj);

                        OkHttpClient client = ((XiaotaApp) mCtx).getOKHttpClient();;
                        Request request = new Request.Builder()
                                // 指定访问的服务器地址
                                .url(url).put(RequestBody2)
                                .build();
                        Response response = null;
                        try {
                            //同步网络请求
                            response = client.newCall(request).execute();
                            boolean success = false;
                            if (response.isSuccessful()) {
                                Gson gson = new Gson();
//                                Result result =  gson.fromJson(response.body().string(), new TypeToken<Result>(){}.getType());
//                                LoginResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken<LoginResponseDataWrap>(){}.getType());
//                                 UserResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken< UserResponseDataWrap >() {
//                                UserResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken< UserResponseDataWrap >() {}.getType());
                                MsgFailResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken< MsgFailResponseDataWrap >() {}.getType());
                                if (responseData != null) {
                                    Log.d(TAG, "putLujingDistance run: " + responseData.getCode());
                                    if (responseData.getCode() == 200) {
                                        success = true;
//                                        msg.obj = responseData.getData().getList();
                                        msg.obj = responseData.getMessage();
                                    } else if (responseData.getCode() == 400) {
                                        Log.e(TAG, responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else if (responseData.getCode() == 500) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "putLujingDistance run: error 500 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else {
                                        msg.obj = responseData.getMessage(); //后端添加失败时，比如二维码已经存在在该路径中。返回的message会包含这个信息
                                        Log.e(TAG, "putLujingDistance Format JSON string to object error!");
                                    }
                                }

                                if (success) {
                                    msg.what = OK;
                                }

                            } else {
                                msg.what = NG;
                                msg.obj = "网络请求错误！";
                                Log.e("aaaa", "response 4 网络请求错误");
                            }
                            response.close();
                        }catch (Exception e) {
                            msg.what = NG;
                            msg.obj = "Network error!";
                            Log.d(TAG, "addNewLujing run: network error!");
                        } finally {
                            handler.sendMessage(msg);
                            if (response != null) {
                                response.close();
                            }
                        }

                    }
                });
            }
        }
    }

    /**
     * 关联电线
     */
    public void putPathWires(final String url, final LinkedHashMap<String, String> values, final Handler handler) {
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
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("wires_id", values.get("wires_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RequestBody RequestBody2 = RequestBody.create(typeJSON, "" + obj);

                        OkHttpClient client = ((XiaotaApp) mCtx).getOKHttpClient();
                        ;
                        Request request = new Request.Builder()
                                // 指定访问的服务器地址
                                .url(url).put(RequestBody2)
                                .build();
                        Response response = null;
                        try {
                            //同步网络请求
                            response = client.newCall(request).execute();
                            boolean success = false;
                            if (response.isSuccessful()) {
                                Gson gson = new Gson();
                                MsgFailResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken<MsgFailResponseDataWrap>() {
                                }.getType());
                                if (responseData != null) {
                                    Log.d(TAG, "putPathWires run: " + responseData.getCode());
                                    if (responseData.getCode() == 200) {
                                        success = true;
//                                        msg.obj = responseData.getData().getList();
                                        msg.obj = responseData.getMessage();
                                    } else if (responseData.getCode() == 400) {
                                        Log.e(TAG, responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else if (responseData.getCode() == 500) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "putPathWires run: error 500 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else {
                                        msg.obj = responseData.getMessage(); //后端添加失败时，比如二维码已经存在在该路径中。返回的message会包含这个信息
                                        Log.e(TAG, "putPathWires Format JSON string to object error!");
                                    }
                                }

                                if (success) {
                                    msg.what = OK;
                                }

                            } else {
                                msg.what = NG;
                                msg.obj = "网络请求错误！";
                                Log.e(TAG, "putPathWires 网络请求错误");
                            }
                            response.close();
                        } catch (Exception e) {
                            msg.what = NG;
                            msg.obj = "Network error!";
                            Log.d(TAG, "putPathWires run: network error!");
                        } finally {
                            handler.sendMessage(msg);
                            if (response != null) {
                                response.close();
                            }
                        }

                    }
                });
            }
        }
    }
    //获取某路径的电线列表，不是所有电线
    public void fetchDxListOfLujing(final String url, final LinkedHashMap<String, String> values, final Handler handler) {
        final Message msg = handler.obtainMessage();
        if (!isNetworkConnected()) {
            ShowMessage.showToast(mCtx, mCtx.getString(R.string.network_not_connect), ShowMessage.MessageDuring.SHORT);
            msg.what = NG;
            msg.obj = mCtx.getString(R.string.network_not_connect);
            handler.sendMessage(msg);
        } else {
            if (url != null && values != null) {
                Log.d(TAG, "fetchDxListOfLujing: not null");
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
//                        Request request = new Request.Builder().url(url).post(requestBody).build();
                        Request request = new Request.Builder().url(url).get().build();
                        OkHttpClient client = ((XiaotaApp) mCtx).getOKHttpClient();
                        Response response = null;
                        try {
                            //同步网络请求
                            response = client.newCall(request).execute();
                            boolean success = false;
                            if (response.isSuccessful()) {
                                Log.d(TAG, "fetchDxListOfLujing run: response success");
                                Gson gson = new Gson();
//                                LoginResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken<LoginResponseDataWrap>(){}.getType());
//                                UserResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken<UserResponseDataWrap>(){}.getType());
                                DxResponseDataWrap responseData = gson.fromJson(response.body().string(), new TypeToken<DxResponseDataWrap>() {
                                }.getType());
                                if (responseData != null) {
                                    Log.d(TAG, "fetchDxListOfLujing run: responseData：" + responseData.getCode());
                                    if (responseData.getCode() == 200) {

                                        for (int k = 0; k < responseData.getData().getWires().size(); k++) {
//                                            success = true;
                                            String serial_number = responseData.getData().getWires().get(k).getSerial_number();
//                                            Log.i("aaa", "serial_number is " + serial_number);
                                            msg.obj = responseData.getData().getWires();
                                        }
                                        success = true;//
                                    } else if (responseData.getCode() == 400) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "fetchDxListOfLujing run: error 400 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else if (responseData.getCode() == 500) {
                                        Log.e(TAG, responseData.getMessage());
                                        Log.d(TAG, "fetchDxListOfLujing run: error 500 :" + responseData.getMessage());
                                        msg.obj = responseData.getMessage();
                                    } else {
                                        Log.e(TAG, "fetchDxListOfLujing Format JSON string to object error!");
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
                            Log.d(TAG, "fetchDxListOfLujing run: catch " + e);
                        } finally {
                            handler.sendMessage(msg);
                            Log.d(TAG, "fetchDxListOfLujing run: finally");
                            if (response != null) {
                                response.close();
                            }
                        }
                    }
                });
            }
        }
    }
}
