package com.zhihuta.xiaota.bean.response;
import com.alibaba.fastjson.annotation.JSONField;

public class PathGetObject {

    public Integer id;
    public String name;
    public String creator;
    @JSONField(name = "create_time")
    public String createTime;
}
