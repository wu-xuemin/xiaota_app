package com.zhihuta.xiaota.bean.basic;

import java.io.Serializable;
import java.util.Date;

public class DistanceData implements Serializable {
    private int id;
    private String DistanceName; // 间距名称
    private String DistanceNumber; //间距 编号
    private String DistanceValue; // 距离值

    public String getDistanceValue() {
        return DistanceValue;
    }

    public void setDistanceValue(String distanceValue) {
        DistanceValue = distanceValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDistanceName() {
        return DistanceName;
    }

    public void setDistanceName(String distanceName) {
        DistanceName = distanceName;
    }

    public String getDistanceNumber() {
        return DistanceNumber;
    }

    public void setDistanceNumber(String distanceNumber) {
        DistanceNumber = distanceNumber;
    }
}
