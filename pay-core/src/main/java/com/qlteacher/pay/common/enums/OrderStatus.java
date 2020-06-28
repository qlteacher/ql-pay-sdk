package com.qlteacher.pay.common.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
	
	SUCCESS("支付成功"),

    REFUND("转入退款"),

    NOTPAY("未支付"),

    CLOSED("已关闭"),

    REVOKED("已撤销（刷卡支付）"),

    USERPAYING("用户支付中"),

    PAYERROR("支付失败"),
    
    UNKNOW("未知状态"),
    ;

	/**
     * 描述 微信退款后有内容
     */
    private String desc;

    OrderStatus(String desc) {
        this.desc = desc;
    }

    public static OrderStatus findByName(String name) {
        for (OrderStatus orderStatusEnum : OrderStatus.values()) {
            if (name.toLowerCase().equals(orderStatusEnum.name().toLowerCase())) {
                return orderStatusEnum;
            }
        }
        throw new RuntimeException("错误的支付状态");
    }

}
