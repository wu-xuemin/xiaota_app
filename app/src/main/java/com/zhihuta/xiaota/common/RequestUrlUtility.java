package com.zhihuta.xiaota.common;

import com.zhihuta.xiaota.ui.XiaotaApp;

public class RequestUrlUtility {

    static public String build(String urlRequest)
    {
        String strURL = "";

        strURL = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + urlRequest;

        return strURL;
    }
}
