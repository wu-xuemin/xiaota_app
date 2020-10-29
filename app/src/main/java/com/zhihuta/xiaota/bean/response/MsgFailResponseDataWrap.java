package com.zhihuta.xiaota.bean.response;

//用于解析服务端返回的
//{
//        "code":262145,
//        "data":null,
//        "message":"PATH_QRCODE_EXIST"
//        }
// 比如 比如二维码已经存在在该路径中。后端添加失败，返回的message会包含这个信息
public class MsgFailResponseDataWrap extends ResponseData {
    private Object data;

    public Object getData() {
        return data;
    }
}
