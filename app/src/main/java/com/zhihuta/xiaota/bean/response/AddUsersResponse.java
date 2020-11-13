package com.zhihuta.xiaota.bean.response;

import com.alibaba.fastjson.annotation.JSONField;

public class AddUsersResponse extends BaseResponse {

    @JSONField(name = "account_id")
    public int accountID;
}