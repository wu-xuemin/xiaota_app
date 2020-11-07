package com.zhihuta.xiaota.common;

import com.zhihuta.xiaota.ui.XiaotaApp;

public class Constant {
    /**
     * 用于标记不同的场合
     * 不同的场合 显示不同的图标/按钮等
     *  -- 候选电线界面 （在 新增路径-关联电线） -- 要显示  checkBox
     *  -- 已选电线界面 （在 新增路径-关联电线） -- 要显示 删除按钮
     *  -- 导出界面                           -- 只显示 型号、芯数截面、长度
     */
    public static String FLAG_TOBE_SELECT_DX = "候选电线";
    public static String FLAG_RELATED_DX = "已选电线";
    public static String FLAG_QINGCE_DX = "清册中的电线"; // 这个和 候选电线 是一样的，只是显示在清册中。
    public static String FLAG_EXPORT_DX = "按型号导出的电线";

    public static String FLAG_LUJING_IN_LUJING = "路径模型中的路径";
    public static String FLAG_LUJING_IN_CALCULATE = "计算中心中的路径";

    public static String FLAG_DISTANCE_IN_LUJING = "路径模型中的间距";
    public static String FLAG_DISTANCE_IN_CALCULATE = "计算中心中的间距";

    public static final int REQUEST_CODE_ADD_TOTAL_NEW_LUJING = 1;            //路径中心，添加 全新 新路径
    public static final int REQUEST_CODE_SCAN_TO_FILTER_LUJING = 2;    //路径中心，扫码筛选路径

    public static final int REQUEST_CODE_MODIFY_LUJING = 3;                 //路径中心，修改已有路径
    public static final int REQUEST_CODE_ADD_NEW_LUJING_BASE_ON_EXIST = 4;  //路径中心，基于已有路径， 新建路径
    public static final int REQUEST_CODE_CALCULATE_WIRES = 5;  //计算中心，电线清单


//    public static final String getUserListUrl8004 = URL.HTTP_HEAD + "172.20.10.3:8004"+ URL.GET_USER_LIST;
//    public static final String loginUrl8004 = URL.HTTP_HEAD +"172.20.10.3:8004"+ URL.USER_LOGIN;
    public static final String getDxListUrl8083 = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.GET_DIANXIAN_QINGCE_LIST;
    public static final String getLujingListUrl8083 = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.GET_LUJING_LIST;
    public static final String getFilterLujingListByQrUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.GET_FILTER_LUJING_BY_QR;
    public static final String getFilterLujingListByNameUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.GET_FILTER_LUJING_BY_NAME;

    //获取 路径对应的间距列表 (编辑路径时，或 在基于已有路径 新建路径时)
    public static final String getLujingDistanceListUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.GET_LUJING_DISTANCE_LIST;
    public static final String addNewLujingUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.POST_ADD_NEW_LUJING;
    public static final String modifyLujingUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.PUT_MODIFY_LUJING_NAME;
    public static final String putLujingDistanceUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.PUT_LUJING_DISTANCE;
    public static final String deleteLujingUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.DELETE_DELETE_LUJING;
    public static final String putDxOfLujingUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.PUT_DX_OF_LUJING;
    public static final String getDxOfLujingUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.GET_CAL_WIRES_OF_LUJING;

    public static final String getDxListOfLujingUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.GET_DX_OF_LUJING;
    public static final String getExportWiresOfLujingUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.GET_EXPORT_WIRES_OF_LUJING;
    public static final String getDistanceListByTwoDistanceUrl = URL.HTTP_HEAD + XiaotaApp.getApp().getServerIPAndPort() + URL.GET_DISTANCE_LIST_BY_TWO_DISTANCE;
// 后端 TODO: 同个用户下，路径名称要唯一

}
