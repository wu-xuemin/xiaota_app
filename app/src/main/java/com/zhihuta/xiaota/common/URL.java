package com.zhihuta.xiaota.common;

public class URL {

    public static final String HTTP_HEAD = "http://";
    public static final String TCP_HEAD = "tcp://";
    public static final String USER_LOGIN = "/login";


    public static final String GET_DIANXIAN_QINGCE_LIST = "/wires";
    public static final String POST_DIANXIAN_QINGCE_IMPORT = "/wires/import";


    public static final String GET_ACCOUNT_LIST = "/accounts";
    public static final String GET_USER_LIST = "/user/list";
    public static final String GET_LUJING_LIST = "/paths";
    public static final String GET_LUJING_DISTANCE_LIST = "/paths/lujingID/distance_qrs";   //E.g: paths/1/distance_qrs
    public static final String POST_ADD_NEW_LUJING = "/paths";
    public static final String DELETE_DELETE_LUJING = "/paths";
    public static final String PUT_MODIFY_LUJING_NAME = "/paths/{id}/name"; //  修改路径的名称， "name": "南京上海"
    public static final String PUT_LUJING_DISTANCE = "/paths/lujingID/distance_qrs";   // E.g: paths/1/distance_qrs "qr_id":1, 数据库里 间距二维码的id

    public static final String GET_DX_OF_LUJING = "/paths/{lujingId}/wires?serial_number={dxSN}&parts_code={dxPartsCode}"; //serial_number:电线编号 parts_code:型号
    public static final String PUT_DX_OF_LUJING = "/paths/{lujingId}/wires"; // 参数为电线ID列表  "wires_id":[ 1,2,3]
    public static final String GET_FILTER_LUJING_BY_QR ="/paths?qr_ids=qrIDs"; // 二维码筛选路径 /paths?name={}&qr_ids={1,2,3}&order_by={}&try_scope={}&offset={}&limit={}
    public static final String GET_FILTER_LUJING_BY_NAME ="/paths?name={LujingName}"; // 名称筛选路径

    public static final String GET_CAL_WIRES_OF_LUJING ="/caculate/{path_id}/wires";        //计算中心 获取路径的电线
    public static final String GET_EXPORT_WIRES_OF_LUJING ="/caculate/{path_id}/wires/parts_code";    //计算中心 按型号导出电线
    public static final String GET_DISTANCE_LIST_BY_TWO_DISTANCE = "/caculate/distance?qr_id=qrId1,qrId2"; //扫2个码获取期间间距
}
