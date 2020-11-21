package com.zhihuta.xiaota.common;

public class URL {

    public static final String HTTP_HEAD = "http://";
    public static final String TCP_HEAD = "tcp://";
    public static final String USER_LOGIN = "/login";
    public static final String USER_LOGOUT= "/logout/{account_id}";
    public static final String USER_PASSWORD_RESET= "/accounts/{account}/password/reset";
    public static final String USER_INFO = "/accounts/{account_id}";
    public static final String USER_REGISTER = "/accounts/customer/{type}";
    public static final String USER_ACCOUNT_EXIST= "/accounts/exist/{account}";
    public static final String GET_ACCOUNT_LIST = "/accounts";
    public static final String GET_USER_LIST = "/user/list";

    //wires
    public static final String GET_DIANXIAN_QINGCE_LIST = "/wires";
    public static final String ADD_DIANXIAN_QINGCE_LIST = "/wires/project/{project_id}";
    public static final String DEL_DIANXIAN_QINGCE_LIST = "/wires/project/{project_id}";
    public static final String MOD_DIANXIAN_QINGCE_LIST = "/wires/project/{project_id}/{id}";

    public static final String PUT_DIANXIAN_QINGCE_IMPORT = "/wires/import/{project_id}";
    public static final String GET_DX_IMPORT_HISTORY = "/wires/import/files";  //电线导入历史

    //paths
    public static final String GET_LUJING_LIST = "/paths";
    public static final String DELETE_DELETE_LUJING = "/paths";
    public static final String GET_LUJING_DISTANCE_LIST = "/paths/lujingID/distance_qrs";   //E.g: paths/1/distance_qrs
    public static final String POST_ADD_NEW_LUJING = "/paths/{project_id}";
    public static final String POST_ADD_NEW_LUJING_BASE_ON_OLD = "/paths/{id}/new";// 追加模式 新建路径
    public static final String POST_ADD_NEW_LUJING_BRANCH_ON_OLD = "/paths/{id}/branch";// 分叉模式新建路径
    public static final String POST_ADD_NEW_LUJING_SUB_ON_OLD = "/paths/{id}/sub";// 分叉模式新建路径

    public static final String PUT_MODIFY_LUJING_NAME = "/paths/{id}/name"; //  修改路径的名称， "name": "南京上海"
    public static final String PUT_LUJING_DISTANCE = "/paths/lujingID/distance_qrs";   // E.g: paths/1/distance_qrs "qr_id":1, 数据库里 间距二维码的id

    public static final String GET_LUJING_DISTANCE_EXIST = "/paths/{lujingID}/exsit";   // E.g:  /paths/1/exsit?qr_ids={1,2} "qr_id":1, 数据库里 间距二维码的id

    public static final String GET_DX_OF_LUJING = "/paths/{lujingId}/wires?serial_number={dxSN}&parts_code={dxPartsCode}"; //serial_number:电线编号 parts_code:型号
    public static final String PUT_DX_OF_LUJING = "/paths/{lujingId}/wires"; // 参数为电线ID列表  "wires_id":[ 1,2,3]
    public static final String GET_FILTER_LUJING_BY_QR ="/paths?qr_ids=qrIDs"; // 二维码筛选路径 /paths?name={}&qr_ids={1,2,3}&order_by={}&try_scope={}&offset={}&limit={}
    //public static final String GET_FILTER_LUJING_BY_NAME ="/paths?name={LujingName}"; // 名称筛选路径

    public static final String GET_CAL_WIRES_OF_LUJING ="/caculate/{path_id}/wires";        //计算中心 获取路径的电线
    public static final String GET_EXPORT_WIRES_OF_LUJING ="/caculate/{path_id}/wires/parts_code";    //计算中心 按型号导出电线
    public static final String GET_DISTANCE_LIST_BY_TWO_DISTANCE = "/caculate/distance?qr_id=qrId1,qrId2"; //扫2个码获取期间间距


    public static final String PUT_QR_DISTANCE = "/distance/qr/{qr_id}/{distance}/changeDistance";//设置二维码距离

    //project
    public static final String GET_ALL_PROJIECT_LIST = "/project/order_by=create_time&offset=0&limit=0";//获取所有加入过的项目列表
    public static final String GET_PROJECT_LIST_OF_COMPANY = "/project/?order_by=create_time&offset=0&limit=0";//获取公司自己的项目列表
    public static final String POST_ADD_NEW_PROJECT = "/project";//新建项目
    public static final String DELETE_REMOVE_PROJECT =  "/project/{id}";//移除所有和项目相关的资源， 项目组，电线，路径
    public static final String GET_PROJECT_MEMBERS = "/project/{id}/member";//
    public static final String PUT_PROJECT_MEMBERS = "/project/{id}/invitemember";//邀请项目组新成员
    public static final String DELETE_PROJECT_MEMBERS = "/project/{id}/removemember";//
}
