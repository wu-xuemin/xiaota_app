package com.zhihuta.xiaota.common;

public class URL {

    public static final String HTTP_HEAD = "http://";
    public static final String TCP_HEAD = "tcp://";
    public static final String USER_LOGIN = "/login";


    public static final String GET_DIANXIAN_QINGCE_LIST = "/wires";
    public static final String GET_ACCOUNT_LIST = "/accounts";
    public static final String GET_USER_LIST = "/user/list";
    public static final String GET_LUJING_LIST = "/paths";
    public static final String GET_LUJING_DISTANCE_LIST = "/paths/lujingID/distance_qrs";   //E.g: paths/1/distance_qrs
    public static final String POST_ADD_NEW_LUJING = "/paths";
    public static final String PUT_MODIFY_LUJING_NAME = "/paths/{id}/name"; //  修改路径的名称， "name": "南京上海"
    public static final String PUT_LUJING_DISTANCE = "/paths/lujingID/distance_qrs";   // E.g: paths/1/distance_qrs "qr_id":1, 数据库里 间距二维码的id

    public static final String GET_DX_OF_LUJING = "/paths/{lujingId}/wires?serial_number={dxSN}&parts_code={dxPartsCode}"; //serial_number:电线编号 parts_code:型号



}
