package com.zhihuta.xiaota.bean.basic;

import java.io.Serializable;
import java.util.Date;

public class LujingData implements Serializable {
    private int id;
    private String name; // 路径名称
    private String creator; //路径的创建人
    private Date create_time; // 创建日期

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
