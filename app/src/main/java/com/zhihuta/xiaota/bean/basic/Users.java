package com.zhihuta.xiaota.bean.basic;


import com.alibaba.fastjson.annotation.JSONField;

//import javax.persistence.Column;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
import java.util.Date;

public class Users {
    //    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String account;

    //    @Column(name = "name")
    @JSONField(name = "name")
    private String name;

    private String title;

    @JSONField(serialize = false)
    private String password;

    private String email;

    private String description;

    /**
     * 组合需要的角色，比如管理员的 role = 1,业务员=2,现场操作员=3，订单人=4,订单及app操作=3,4
     */
    private String roles;

    private String department;

    private String company;

    //    @Column(name = "register_time")
    @JSONField(name = "register_time")
    private Date registerTime;

    private String phone;

    private String address;

    /**
     * 0=not deleted,1 = deleted
     */
    private Boolean deleted;

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
     * @return account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 组合需要的角色，比如管理员的 role = 1,业务员=2,现场操作员=3，订单人=4,订单及app操作=3,4
     *
     * @return roles - 组合需要的角色，比如管理员的 role = 1,业务员=2,现场操作员=3，订单人=4,订单及app操作=3,4
     */
    public String getRoles() {
        return roles;
    }

    /**
     * 组合需要的角色，比如管理员的 role = 1,业务员=2,现场操作员=3，订单人=4,订单及app操作=3,4
     *
     * @param roles 组合需要的角色，比如管理员的 role = 1,业务员=2,现场操作员=3，订单人=4,订单及app操作=3,4
     */
    public void setRoles(String roles) {
        this.roles = roles;
    }

    /**
     * @return department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * @param department
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * @return company
     */
    public String getCompany() {
        return company;
    }

    /**
     * @param company
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * @return register_time
     */
    public Date getRegisterTime() {
        return registerTime;
    }

    /**
     * @param registerTime
     */
    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    /**
     * @return phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取0=not deleted,1 = deleted
     *
     * @return deleted - 0=not deleted,1 = deleted
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * 设置0=not deleted,1 = deleted
     *
     * @param deleted 0=not deleted,1 = deleted
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
