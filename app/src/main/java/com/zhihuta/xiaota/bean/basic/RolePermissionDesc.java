package com.zhihuta.xiaota.bean.basic;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class RolePermissionDesc {

    public int   id;
    public String name;
    @JSONField(name = "role_permissions_ids")
    public List<RolePermission> rolePermissionsIds;
}
