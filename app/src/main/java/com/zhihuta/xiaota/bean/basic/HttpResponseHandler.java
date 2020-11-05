package com.zhihuta.xiaota.bean.basic;

import android.os.Handler;
import android.os.Message;

import com.zhihuta.xiaota.bean.response.BaseResponse;


@FunctionalInterface
public interface HttpResponseHandler {

    void processResponse(Handler osHandler, Message msg);
}
