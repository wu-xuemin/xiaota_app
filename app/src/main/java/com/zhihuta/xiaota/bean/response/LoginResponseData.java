package com.zhihuta.xiaota.bean.response;

import com.zhihuta.xiaota.bean.basic.RolePermissionDesc;
import java.util.List;

public class LoginResponseData extends BaseResponse {

    public int id;
    public String account;
    public List<RolePermissionDesc> roles;

    public int getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }
}

