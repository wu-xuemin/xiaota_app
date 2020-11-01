package com.zhihuta.xiaota.common;

import com.zhihuta.xiaota.ui.XiaotaApp;

public class Constant {
    /**
     * 用于标记不同的场合
     * 不同的场合 显示不同的图标/按钮等
     * "ToBeSelectDx"   -- 候选电线界面 （在 新增路径-关联电线） -- 要显示  checkBox
     * "RelatedDx"      -- 已选电线界面 （在 新增路径-关联电线） -- 要显示 删除按钮
     */
    public static String FLAG_TOBE_SELECT_DX = "候选电线";
    public static String FLAG_RELATED_DX = "已选电线";
    public static String FLAG_QINGCE_DX = "清册中的电线"; // 这个和 候选电线 是一样的，只是显示在清册中。

    public static final int REQUEST_CODE_ADD_TOTAL_NEW_LUJING = 1;            //路径中心，添加 全新 新路径
    public static final int REQUEST_CODE_SCAN_TO_SHAIXUAN_LUJING = 2;    //路径中心，扫码筛选路径

    public static final int REQUEST_CODE_MODIFY_LUJING = 3;                 //路径中心，修改已有路径
    public static final int REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST = 4;  //路径中心，基于已有路径， 新建路径


    public static final String getUserListUrl8004 = URL.HTTP_HEAD + "172.20.10.3:8004"+ URL.GET_USER_LIST;
    public static final String loginUrl8004 = URL.HTTP_HEAD +"172.20.10.3:8004"+ URL.USER_LOGIN;
    public static final String getDxListUrl8083 = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIP() + URL.GET_DIANXIAN_QINGCE_LIST;
    public static final String getLujingListUrl8083 = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIP() + URL.GET_LUJING_LIST;

    //获取 路径对应的间距列表 (编辑路径时，或 在基于已有路径 新建路径时)
    public static final String getLujingDistanceListUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIP() + URL.GET_LUJING_DISTANCE_LIST;
    public static final String addNewLujingUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIP() + URL.POST_ADD_NEW_LUJING;
    public static final String modifyLujingUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIP() + URL.PUT_MODIFY_LUJING_NAME;

// 后端 TODO: 同个用户下，路径名称要唯一

}
