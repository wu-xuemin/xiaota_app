package com.zhihuta.xiaota.common;

import android.app.Activity;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.response.BaseResponse;
import com.zhihuta.xiaota.bean.response.GetWiresResponse;
import com.zhihuta.xiaota.ui.LoginActivity;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

public class AuthenticateInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // try the request
        Response originalResponse = chain.proceed(request);

        /**通过如下的办法曲线取到请求完成的数据
         *
         * 原本想通过  originalResponse.body().string()
         * 去取到请求完成的数据,但是一直报错,不知道是okhttp的bug还是操作不当
         *
         * 然后去看了okhttp的源码,找到了这个曲线方法,取到请求完成的数据后,根据特定的判断条件去判断token过期
         */
        ResponseBody responseBody = originalResponse.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }

        if (contentType.toString().toLowerCase().equals("application/json;charset=UTF-8".toLowerCase()))
        {
            String bodyString = buffer.clone().readString(charset);

            Result result = JSONObject.parseObject(bodyString, Result.class);

            if ( result.getCode() == 401)
            {//time out or not login

                Activity currentActivity =  MyActivityManager.getInstance().getCurrentActivity();
                Intent intent = new Intent(currentActivity, LoginActivity.class);

                //String strResponseData = JSON.toJSONString(loginResponseData);
                //intent.putExtra("loginResponseData", (Serializable)strResponseData);
                startActivity(intent);
                currentActivity.finish();//销毁此Activity
            }
        }

//        LogUtil.debug("body---------->" + bodyString);

        /***************************************/

//        if (response shows expired token){//根据和服务端的约定判断token过期
//
//            //取出本地的refreshToken
//            String refreshToken = "sssgr122222222";
//
//            // 通过一个特定的接口获取新的token，此处要用到同步的retrofit请求
//            ApiService service = ServiceManager.getService(ApiService.class);
//            Call<String> call = service.refreshToken(refreshToken);
//
//            //要用retrofit的同步方式
//            String newToken = call.execute().body();
//
//
//            // create a new request and modify it accordingly using the new token
//            Request newRequest = request.newBuilder().header("token", newToken)
//                    .build();
//
//            // retry the request
//
//            originalResponse.body().close();
//            return chain.proceed(newRequest);
//        }

        // otherwise just pass the original response on
        return originalResponse;
    }
}