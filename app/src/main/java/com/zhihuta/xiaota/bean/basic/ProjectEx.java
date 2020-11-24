package com.zhihuta.xiaota.bean.basic;

//import javax.persistence.Column;

import com.alibaba.fastjson.annotation.JSONField;

public class ProjectEx extends Project{

    @JSONField(name = "modifier_name")
    public String modifierName;

    public int pgid;

    @JSONField(name = "creat_account")
    public String creatAccount;

    @JSONField(name = "company_name")
    public String companyName;

    @JSONField(name = "department_name")
    public String departmentName;
}
