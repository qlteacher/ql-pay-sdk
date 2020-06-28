package com.qlteacher.pay.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PayPlatform {
	
	ALIPAY("alipay", "支付宝"),

	WXPAY("wxpay", "微信")
    ;
	
    private String code;

    private String name;
    
    public static PayPlatform getByName(String code) {
        for (PayPlatform bestPayTypeEnum : PayPlatform.values()) {
            if (bestPayTypeEnum.name().equalsIgnoreCase(code)) {
                return bestPayTypeEnum;
            }
        }
		return null;
    }

}
