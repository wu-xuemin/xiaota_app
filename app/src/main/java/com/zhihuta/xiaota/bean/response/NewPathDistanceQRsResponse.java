package com.zhihuta.xiaota.bean.response;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class NewPathDistanceQRsResponse extends BaseResponse {

    public int id;

    @JSONField(name = "distance_qrs")
    public List<PathGetDistanceQr> distance_qrs;
}