package com.qlteacher.pay.test.common;

import com.qlteacher.pay.common.enums.PayType;

public class EnumsTest {
	
	public static void main(String[] args) {
		System.out.println(PayType.ALIPAY_APP.getPlatform().getCode());
		System.out.println(PayType.ALIPAY_APP.getPlatform().getName());
	}

}
