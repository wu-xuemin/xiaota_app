package com.zhihuta.xiaota.common;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.response.BaseResponse;
import com.zhihuta.xiaota.net.Network;
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

    public static String getResponseErrMsg(final Message msg)
    {
        if (msg.what == Network.OK) {
            Result result= (Result)(msg.obj);
            if (result.getCode() == 200)
            {//then can get the data
                return null;
            }
            else
            {//no data returned.

                return result.getMessage();
            }
        }
        else
        {
            return (String) msg.obj;
        }
    }
}
