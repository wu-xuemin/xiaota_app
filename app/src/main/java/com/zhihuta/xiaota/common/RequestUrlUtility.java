package com.zhihuta.xiaota.common;

import android.util.Log;

import com.zhihuta.xiaota.ui.XiaotaApp;

public class RequestUrlUtility {

    static String TAG = "RequestURL";
    static public String build(String urlRequest)
    {
        String strURL = "";

        strURL = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + urlRequest;

        Log.d(TAG, strURL);
        return strURL;
    }
}
