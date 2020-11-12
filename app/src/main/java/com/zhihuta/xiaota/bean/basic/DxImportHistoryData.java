package com.zhihuta.xiaota.bean.basic;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DxImportHistoryData implements Serializable {
    private int id;
    private String fileName;
    private String operate_time;
    private String operator;
    private String account;
    private String name;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

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

    public String getOperate_time() {

        return operate_time;
    }

    public void setOperate_time(String operate_time) {
        this.operate_time = operate_time;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
