package com.siro.mall.common;

/**
 * 订单状态:0.无 1.支付宝 2.微信支付
 * @author starsea
 * @date 2021-01-22
 */
public enum PayTypeEnum {

    DEFAULT(-1, "ERROR"),
    NOT_PAY(0, "无"),
    ALI_PAY(1, "支付宝"),
    WEIXIN_PAY(2, "微信支付");

    private int payType;

    private String name;

    PayTypeEnum(int payType, String name) {
        this.payType = payType;
        this.name = name;
    }

    public static PayTypeEnum getPayTypeEnumByType(int payType) {
        for (PayTypeEnum payTypeEnum : PayTypeEnum.values()) {
            if (payTypeEnum.getPayType() == payType) {
                return payTypeEnum;
            }
        }
        return DEFAULT;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
