package com.zhihuta.xiaota.common;

public interface INetCallback {
    /**
     * 请求成功，再此进行处理
     *
     * @param response
     */
    void onSuccess(String response);

    /**
     * 请求失败，在此进行处理
     *
     * @param throwable
     */
    void onFailed(Throwable throwable);
}