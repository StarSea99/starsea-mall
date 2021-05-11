package com.siro.mall.vo;

/**
 * @author starsea
 * @date 2021-01-28
 */
public class PayVO {
    private String orderNo;//商品订单号
    private String subject;//订单名称
    private String totalAmount;//付款金额
    private String body;//订单描述

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
