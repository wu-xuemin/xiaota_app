package com.zhihuta.xiaota.bean.basic;

import java.io.Serializable;
import java.util.Date;

public class LujingData implements Serializable {
    private int id;
    private String LujingName; // 路径名称
    private String LujingCreater; //路径的创建人
    private Date LujingCreatedDate; // 创建日期

    public String getLujingCaozuo() {
        return LujingCaozuo;
    }

    public void setLujingCaozuo(String lujingCaozuo) {
        LujingCaozuo = lujingCaozuo;
    }

    private String LujingCaozuo; // 操作

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLujingName() {
        return LujingName;
    }

    public void setLujingName(String lujingName) {
        LujingName = lujingName;
    }

    public String getLujingCreater() {
        return LujingCreater;
    }

    public void setLujingCreater(String lujingCreater) {
        LujingCreater = lujingCreater;
    }

    public Date getLujingCreatedDate() {
        return LujingCreatedDate;
    }

    public void setLujingCreatedDate(Date lujingCreatedDate) {
        LujingCreatedDate = lujingCreatedDate;
    }
}
