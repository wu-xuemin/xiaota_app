package com.zhihuta.xiaota.common;

public class AppUpdater {


    private static AppUpdater sInstance = new AppUpdater();

    public static AppUpdater getInstance() {
        return sInstance;
    }

    /**
     * 默认的网络访问方式：OkHttpNetManager
     */
    private static INetManager sINetManager = new OkHttpNetManager();

    public INetManager getINetManager() {
        return sINetManager;
    }

    /**
     * 指定网络访问方式
     *
     * @param netManager
     */
    public void setINetManager(INetManager netManager) {
        sINetManager = netManager;
    }
}
