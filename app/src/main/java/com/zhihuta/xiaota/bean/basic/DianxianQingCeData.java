package com.zhihuta.xiaota.bean.basic;

import java.io.Serializable;

public class DianxianQingCeData implements Serializable {
    private int id;
    private String serial_number;
    private String StartPoint;
    private String EndPoint;
    private String DxModel; // 型号
    private String DxXinshuJieMian; // 芯数X截面
    private String DxLength; //电线长度
    private String SteelRedundancy;  //钢冗
    private String HoseRedundancy;  //皮冗

    /**
     * 用于标记不同的场合
     * 不同的场合 显示不同的图标/按钮等
     * "ToBeSelectDx"   -- 候选电线界面 （在 新增路径-关联电线） -- 要显示  checkBox
     * "RelatedDx"      -- 已选电线界面 （在 新增路径-关联电线） -- 要显示 删除按钮
     */
    private String flag; ///
    public String getFlag() {
        return flag;
    }

    private String isChecked;  //是否勾选
    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }


    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getDxXinshuJieMian() {
        return DxXinshuJieMian;
    }

    public void setDxXinshuJieMian(String dxXinshuJieMian) {
        DxXinshuJieMian = dxXinshuJieMian;
    }

    public String getDxLength() {
        return DxLength;
    }

    public void setDxLength(String dxLength) {
        DxLength = dxLength;
    }

    public String getSteelRedundancy() {
        return SteelRedundancy;
    }

    public void setSteelRedundancy(String steelRedundancy) {
        SteelRedundancy = steelRedundancy;
    }

    public String getHoseRedundancy() {
        return HoseRedundancy;
    }

    public void setHoseRedundancy(String hoseRedundancy) {
        HoseRedundancy = hoseRedundancy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public void setStartPoint(String startPoint) {
        StartPoint = startPoint;
    }

    public void setEndPoint(String endPoint) {
        EndPoint = endPoint;
    }

    public void setDxModel(String dxModel) {
        DxModel = dxModel;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public String getStartPoint() {
        return StartPoint;
    }

    public String getEndPoint() {
        return EndPoint;
    }

    public String getDxModel() {
        return DxModel;
    }
}
