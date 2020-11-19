package com.zhihuta.xiaota.bean.basic;

import java.io.Serializable;
import java.util.Date;

//          "companyId": 1,
//          "companyName": "宝绿集团",
//          "creatAccount": "a",
//          "createTime": 1605554214000,
//          "creatorId": 23,
//          "departmentId": 1,
//          "departmentName": "工程部",
//          "id": 1,
//          "modiferId": 23,
//          "modifierName": "a修改了",
//          "modifyTime": 1605554217000,
//          "pgid": 1,
//          "projectName": "测试项目",
//          "status": 0
public class ProjectData implements Serializable {
    private int id;
    private String projectName;
    private Integer companyId;
    private Integer departmentId;
    private Integer creatorId;
    private Integer modiferId;
    private Date createTime;
    private Date modifyTime;
    private Short status;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getModiferId() {
        return modiferId;
    }

    public void setModiferId(Integer modiferId) {
        this.modiferId = modiferId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }
}
