package com.zhihuta.xiaota.bean.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhihuta.xiaota.bean.basic.DistanceQrEx;

public class GetDistanceQrInfoResponse extends BaseResponse {
    //@JSONField(name = "qr_infor")
    public DistanceQrEx qr_infor;
}
