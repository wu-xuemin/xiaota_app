package com.zhihuta.xiaota.bean.basic;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DxImportHistoryData implements Serializable {
    private int id;
    private String fileName;
    private Date operate_time;
    private String operator;
    private SimpleDateFormat sf3 = new SimpleDateFormat("yy/MM/dd");
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getOperate_time() {

        return operate_time;
    }

    public void setOperate_time(Date operate_time) {
        this.operate_time = operate_time;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
