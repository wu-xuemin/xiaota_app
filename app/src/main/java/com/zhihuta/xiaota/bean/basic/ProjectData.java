package com.zhihuta.xiaota.bean.basic;

import java.io.Serializable;

public class ProjectData implements Serializable {
    private int id;
    private String name; // 项目名称
    private String creator; //项目的创建人
    private String create_time; // 项目创建日期


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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
