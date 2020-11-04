package com.zhihuta.xiaota.bean.basic;

import com.alibaba.fastjson.annotation.JSONField;


//@Table(name = "role_permission")
public class RolePermission {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 权限名称
     */
    private String name;

    private String description;


    /**
     * 角色ID，-1 : 代表所有
     */
//    @Column(name = "role_id")
    @JSONField(serialize=false)
    private Integer roleId;

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
     * 获取权限名称
     *
     * @return name - 权限名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置权限名称
     *
     * @param name 权限名称
     */
    public void setName(String name) {
        this.name = name;
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
     * 获取角色ID，-1 : 代表所有
     *
     * @return role_id - 角色ID，-1 : 代表所有
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * 设置角色ID，-1 : 代表所有
     *
     * @param roleId 角色ID，-1 : 代表所有
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}