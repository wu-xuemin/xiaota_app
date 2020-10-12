package com.zhihuta.xiaota.bean.basic;

import java.io.Serializable;
import java.util.Date;

public class OrderData implements Serializable {
    private int id;
    private String OrderNumber; //订单号
    private Date CreatedDate; // 创建日期
    private String OrderCreater; //提交人
    private String OrderStatus; // 状态

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public Date getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(Date createdDate) {
        CreatedDate = createdDate;
    }

    public String getOrderCreater() {
        return OrderCreater;
    }

    public void setOrderCreater(String orderCreater) {
        OrderCreater = orderCreater;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }
}
