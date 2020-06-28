package com.qlteacher.pay.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PayType {
	
	ALIPAY_APP("alipay_app", PayPlatform.ALIPAY, "支付宝app"),

    ALIPAY_PC("alipay_pc", PayPlatform.ALIPAY, "支付宝pc"),

    ALIPAY_WAP("alipay_wap", PayPlatform.ALIPAY, "支付宝wap"),

    ALIPAY_H5("alipay_h5", PayPlatform.ALIPAY, "支付宝统一下单(h5)"),

    WXPAY_MP("JSAPI", PayPlatform.WXPAY,"微信公众账号支付"),

    WXPAY_MWEB("MWEB", PayPlatform.WXPAY, "微信H5支付"),

    WXPAY_NATIVE("NATIVE", PayPlatform.WXPAY, "微信Native支付"),

    WXPAY_MINI("JSAPI", PayPlatform.WXPAY, "微信小程序支付"),

    WXPAY_APP("APP", PayPlatform.WXPAY, "微信APP支付"),
    ;
	
	private String code;

    private PayPlatform platform;

    private String desc;

}
