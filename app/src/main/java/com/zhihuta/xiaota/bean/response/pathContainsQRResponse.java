package com.zhihuta.xiaota.bean.response;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class pathContainsQRResponse extends BaseResponse {

    @JSONField(name = "not_in_path")
    public List<Integer> not_in_path;
}