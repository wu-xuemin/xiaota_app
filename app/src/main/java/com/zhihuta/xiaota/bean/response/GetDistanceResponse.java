package com.zhihuta.xiaota.bean.response;

import com.alibaba.fastjson.annotation.JSONField;

public class GetDistanceResponse extends BaseResponse
{
    @JSONField(name = "path_id")
    public double path_id;

    @JSONField(name = "qr_id_start")
    public double qr_id_start;

    @JSONField(name = "qr_id_end")
    public double qr_id_end;

    @JSONField(name = "length")
    public double length;
}