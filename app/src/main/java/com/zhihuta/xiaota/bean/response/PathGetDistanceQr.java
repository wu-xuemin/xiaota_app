package com.zhihuta.xiaota.bean.response;

import com.alibaba.fastjson.annotation.JSONField;

public class PathGetDistanceQr {
    @JSONField(name = "qr_id")
    public  int qrId;

    public  String name;

    @JSONField(name = "serial_number")
    public  String serialNumber;

    public  Double distance;

    @JSONField(name = "qr_sequence")
    public  int qrSequence;
}
