package com.zhihuta.xiaota.bean.basic;

import com.alibaba.fastjson.annotation.JSONField;

public class DistanceQrEx {

    @JSONField(name = "qr_id")
    public int qrId;

    @JSONField(name = "qr_name")
    public String qrName;

    @JSONField(serialize = false,name = "preset_name")
    public String presetName;

    public int type;

    public double distance;

    @JSONField(name = "serial_number")
    public String serialNumber;
}