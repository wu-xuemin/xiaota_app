package com.zhihuta.xiaota.bean.basic;

import com.alibaba.fastjson.annotation.JSONField;

public class PathWiresPartsCodeSum {
    @JSONField(name = "parts_code")
    public String parts_code;

    @JSONField(name = "wickes_cross_section")
    public String wickes_cross_section;

    public double length;
}
