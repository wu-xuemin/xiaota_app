package com.zhihuta.xiaota.bean.basic;

import java.io.Serializable;

public class DianxianQingCeData implements Serializable {
    private int id;
    private String DxNumber;
    private String StartPoint;
    private String EndPoint;
    private String DxModel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDxNumber(String dxNumber) {
        DxNumber = dxNumber;
    }

    public void setStartPoint(String startPoint) {
        StartPoint = startPoint;
    }

    public void setEndPoint(String endPoint) {
        EndPoint = endPoint;
    }

    public void setDxModel(String dxModel) {
        DxModel = dxModel;
    }

    public String getDxNumber() {
        return DxNumber;
    }

    public String getStartPoint() {
        return StartPoint;
    }

    public String getEndPoint() {
        return EndPoint;
    }

    public String getDxModel() {
        return DxModel;
    }
}
