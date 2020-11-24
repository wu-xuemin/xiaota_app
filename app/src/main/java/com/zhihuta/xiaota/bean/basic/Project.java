package com.zhihuta.xiaota.bean.basic;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

//import javax.persistence.*;

public class Project implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * project name, belong to a company
     */
    @JSONField(name = "project_name")
    private String projectName;

    /**
     * company id who owns this project
     */
    @JSONField(name = "company_id")
    private Integer companyId;

    /**
     * department id who owns this project
     */
    @JSONField(name = "department_id")
    private Integer departmentId;


    /**
     * creator who create the project
     */
    @JSONField(name = "creator_id")
    private Integer creatorId;

    /**
     * who update the project, update points may be wires,  paths
     */
    @JSONField(name = "modifer_id")
    private Integer modiferId;

    /**
     * when create the project
     */
    @JSONField(name = "create_time")
    private Date createTime;

    /**
     * when to modify the project
     */
    @JSONField(name = "modify_time")
    private Date modifyTime;

    /**
     * status of project, 0 running, 1 closed.
     */
    private Short status;

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
     * 获取project name, belong to a company
     *
     * @return project_name - project name, belong to a company
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * 设置project name, belong to a company
     *
     * @param projectName project name, belong to a company
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * 获取company id who owns this project
     *
     * @return company_id - company id who owns this project
     */
    public Integer getCompanyId() {
        return companyId;
    }

    /**
     * 设置company id who owns this project
     *
     * @param companyId company id who owns this project
     */
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    /**
     * 获取department id who owns this project
     *
     * @return department_id - company id who owns this project
     */
    public Integer getDepartmentId() {
        return departmentId;
    }

    /**
     * 设置department id who owns this project
     *
     * @param departmentId id who owns this project
     */
    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * 获取creator who create the project
     *
     * @return creator_id - creator who create the project
     */
    public Integer getCreatorId() {
        return creatorId;
    }

    /**
     * 设置creator who create the project
     *
     * @param creatorId creator who create the project
     */
    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * 获取who update the project, update points may be wires,  paths
     *
     * @return modifer_id - who update the project, update points may be wires,  paths
     */
    public Integer getModiferId() {
        return modiferId;
    }

    /**
     * 设置who update the project, update points may be wires,  paths
     *
     * @param modiferId who update the project, update points may be wires,  paths
     */
    public void setModiferId(Integer modiferId) {
        this.modiferId = modiferId;
    }

    /**
     * 获取when create the project
     *
     * @return create_time - when create the project
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置when create the project
     *
     * @param createTime when create the project
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取when to modify the project
     *
     * @return modify_time - when to modify the project
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置when to modify the project
     *
     * @param modifyTime when to modify the project
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * 获取status of project, 0 running, 1 closed.
     *
     * @return status - status of project, 0 running, 1 closed.
     */
    public Short getStatus() {
        return status;
    }

    /**
     * 设置status of project, 0 running, 1 closed.
     *
     * @param status status of project, 0 running, 1 closed.
     */
    public void setStatus(Short status) {
        this.status = status;
    }
}