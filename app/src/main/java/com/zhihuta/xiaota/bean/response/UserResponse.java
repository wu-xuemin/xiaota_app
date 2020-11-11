package com.zhihuta.xiaota.bean.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhihuta.xiaota.bean.basic.Users;

public class UserResponse extends BaseResponse {

    @JSONField(name = "account_info")
    public Users account_info;
}