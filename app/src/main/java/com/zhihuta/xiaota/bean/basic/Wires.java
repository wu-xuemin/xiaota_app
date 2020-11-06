package com.zhihuta.xiaota.bean.basic;


import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
//import javax.persistence.*;

public class Wires {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 电线编号
     */
//    @Column(name = "serial_number")
    @JSONField(name = "serial_number")
    private String serialNumber;

    /**
     * 起点
     */
//    @Column(name = "start_point")
    @JSONField(name = "start_point")
    private String startPoint;

    /**
     * 终点
     */
//    @Column(name = "end_point")
    @JSONField(name = "end_point")
    private String endPoint;

    /**
     * 型号
     */
//    @Column(name = "parts_code")
    @JSONField(name = "parts_code")
    private String partsCode;

    /**
     * 截面
     */
//    @Column(name = "wickes_cross_section")
    @JSONField(name = "wickes_cross_section")
    private String wickesCrossSection;

    /**
     * 长度
     */
    private String length;

    /**
     * 钢冗余
     */
//    @Column(name = "steel_redundancy")
    @JSONField(name = "steel_redundancy")
    private Double steelRedundancy;

    /**
     * 软冗余
     */
//    @Column(name = "hose_redundancy")
    @JSONField(name = "hose_redundancy")
    private Double hoseRedundancy;

    /**
     * 导入文件记录id
     */
//    @Column(name = "import_id")
    @JSONField(name = "import_id")
    private Integer importId;

    /**
     * 添加人
     */
    private Integer operator;

    /**
     * 添加方法，0手工，1导入
     */
//    @Column(name = "operate_method")
    @JSONField(name = "operate_method")
    private Integer operateMethod;

    /**
     * 添加时间
     */
//    @Column(name = "operate_time")
    @JSONField(name = "operate_time")
    private Date operateTime;

    private Boolean deleted;


//    @Transient
    String  groupKeyByPratsCodeAndWickesCrossSection;
    /**
     * @return PratsCode + WickesCrossSection
     */
    public String getGroupKeyByPratsCodeAndWickesCrossSection() {

        return partsCode + "^" + wickesCrossSection;
    }

    public void setGroupKeyByPratsCodeAndWickesCrossSection( String ss) {

        ;//groupKeyByPratsCodeAndWickesCrossSection =  partsCode + "^" + wickesCrossSection;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取电线编号
     *
     * @return serial_number - 电线编号
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * 设置电线编号
     *
     * @param serialNumber 电线编号
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * 获取起点
     *
     * @return start_point - 起点
     */
    public String getStartPoint() {
        return startPoint;
    }

    /**
     * 设置起点
     *
     * @param startPoint 起点
     */
    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    /**
     * 获取终点
     *
     * @return end_point - 终点
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * 设置终点
     *
     * @param endPoint 终点
     */
    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    /**
     * 获取型号
     *
     * @return parts_code - 型号
     */
    public String getPartsCode() {
        return partsCode;
    }

    /**
     * 设置型号
     *
     * @param partsCode 型号
     */
    public void setPartsCode(String partsCode) {
        this.partsCode = partsCode;
    }

    /**
     * 获取截面
     *
     * @return wickes_cross section - 截面
     */
    public String getWickesCrossSection() {
        return wickesCrossSection;
    }

    /**
     * 设置截面
     *
     * @param wickesCrossSection 截面
     */
    public void setWickesCrossSection(String wickesCrossSection) {
        this.wickesCrossSection = wickesCrossSection;
    }

    /**
     * 获取长度
     *
     * @return Length - 长度
     */
    public String getLength() {
        return length;
    }

    /**
     * 设置长度
     *
     * @param length 长度
     */
    public void setLength(String length) {
        this.length = length;
    }

    /**
     * 获取钢冗余
     *
     * @return steel_redundancy - 钢冗余
     */
    public Double getSteelRedundancy() {
        return steelRedundancy;
    }

    /**
     * 设置钢冗余
     *
     * @param steelRedundancy 钢冗余
     */
    public void setSteelRedundancy(Double steelRedundancy) {
        this.steelRedundancy = steelRedundancy;
    }

    /**
     * 获取软冗余
     *
     * @return hose_redundancy - 软冗余
     */
    public Double getHoseRedundancy() {
        return hoseRedundancy;
    }

    /**
     * 设置软冗余
     *
     * @param hoseRedundancy 软冗余
     */
    public void setHoseRedundancy(Double hoseRedundancy) {
        this.hoseRedundancy = hoseRedundancy;
    }

    /**
     * 获取导入文件记录id
     *
     * @return import_id - 导入文件记录id
     */
    public Integer getImportId() {
        return importId;
    }

    /**
     * 设置导入文件记录id
     *
     * @param importId 导入文件记录id
     */
    public void setImportId(Integer importId) {
        this.importId = importId;
    }

    /**
     * 获取添加人
     *
     * @return operator - 添加人
     */
    public Integer getOperator() {
        return operator;
    }

    /**
     * 设置添加人
     *
     * @param operator 添加人
     */
    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    /**
     * 获取添加方法，0手工，1导入
     *
     * @return operate_method - 添加方法，0手工，1导入
     */
    public Integer getOperateMethod() {
        return operateMethod;
    }

    /**
     * 设置添加方法，0手工，1导入
     *
     * @param operateMethod 添加方法，0手工，1导入
     */
    public void setOperateMethod(Integer operateMethod) {
        this.operateMethod = operateMethod;
    }

    /**
     * 获取添加时间
     *
     * @return operate_time - 添加时间
     */
    public Date getOperateTime() {
        return operateTime;
    }

    /**
     * 设置添加时间
     *
     * @param operateTime 添加时间
     */
    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    /**
     * @return deleted
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * @param deleted
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}