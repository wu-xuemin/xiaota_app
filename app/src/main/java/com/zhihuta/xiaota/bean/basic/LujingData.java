package com.zhihuta.xiaota.bean.basic;

import java.io.Serializable;
import java.util.Date;

public class LujingData implements Serializable {
    private int id;
    private String name; // 路径名称
    private String creator; //路径的创建人
    private String create_time; // 创建日期

    /**
     * 用于标记不同的场合
     * 不同的场合 显示不同的图标/按钮等
     * "InLujingTab"            -- 在路径界面  -- 要显示  修改、新建、删除
     * "InCalculateTab"         -- 在计算界面   -- 要显示 电线长度等按钮
     */
    private String flag; ///

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

//    public String getLujingCaozuo() {
//        return LujingCaozuo;
//    }
//
//    public void setLujingCaozuo(String lujingCaozuo) {
//        LujingCaozuo = lujingCaozuo;
//    }

//    private String LujingCaozuo; // 操作

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
