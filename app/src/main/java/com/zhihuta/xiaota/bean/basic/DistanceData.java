package com.zhihuta.xiaota.bean.basic;

import java.io.Serializable;

public class DistanceData implements Serializable {
    private int qr_id;
    private String name; // 间距名称
    private String serial_number; //间距 编号
    private String distance; // 距离值
    private Integer qr_sequence; ///二维码的顺序  path distance qr表
    private String flag; ///
    private String type; // 二维码类型,0固定码，1通用码
    private String preset_name; // 间距名称

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Integer getQr_sequence() {
        return qr_sequence;
    }

    public void setQr_sequence(Integer qr_sequence) {
        this.qr_sequence = qr_sequence;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getQr_id() {
        return qr_id;
    }

    public void setQr_id(int qr_id) {
        this.qr_id = qr_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getPreset_name() {
        return preset_name;
    }

    public void setPreset_name(String preset_name) {
        this.preset_name = preset_name;
    }

}
