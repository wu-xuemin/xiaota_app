package com.zhihuta.xiaota.bean.basic;

import java.io.Serializable;

public class DianxianQingCeData implements Serializable {
    private int id;
    private String serial_number;
    private String start_point;
    private String end_point;
    private String parts_code; // 型号
    private String wickes_cross_section; // 芯数X截面
    private String length; //电线长度
//    private String calculatedLength; //电线长度
    private String steel_redundancy;  //钢冗
    private String hose_redundancy;  //皮冗
    private String wires_sequence;
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

    public String getWickes_cross_section() {
        return wickes_cross_section;
    }

    public void setWickes_cross_section(String wickes_cross_section) {
        this.wickes_cross_section = wickes_cross_section;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getSteel_redundancy() {
        return steel_redundancy;
    }

    public void setSteel_redundancy(String steel_redundancy) {
        this.steel_redundancy = steel_redundancy;
    }

    public String getHose_redundancy() {
        return hose_redundancy;
    }

    public void setHose_redundancy(String hose_redundancy) {
        this.hose_redundancy = hose_redundancy;
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

    public void setStart_point(String start_point) {
        this.start_point = start_point;
    }

    public void setEnd_point(String end_point) {
        this.end_point = end_point;
    }

    public void setParts_code(String parts_code) {
        this.parts_code = parts_code;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public String getStart_point() {
        return start_point;
    }

    public String getEnd_point() {
        return end_point;
    }

    public String getParts_code() {
        return parts_code;
    }
    public String getWires_sequence() {
        return wires_sequence;
    }
    public void setWires_sequence(String wires_sequence) {
        this.wires_sequence = wires_sequence;
    }
}
