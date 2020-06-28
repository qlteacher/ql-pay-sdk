package com.qlteacher.pay.alipay.constants;

public interface QlAliPayConstants {

    /**
     * 沙箱测试
     */
    String ALIPAY_GATEWAY_OPEN_DEV = "openapi.alipaydev.com";

    /**
     * 支付宝网关. 开放平台 见https://b.alipay.com/order/pidAndKey.htm
     */
    String ALIPAY_GATEWAY_OPEN = "openapi.alipay.com";

    String ALIPAY_TRADE_WAP_PAY = "alipay.trade.wap.pay";

    /**
     * 销售产品码，与支付宝签约的产品码名称。 注：目前仅支持FAST_INSTANT_TRADE_PAY
     */
    String FAST_INSTANT_TRADE_PAY = "FAST_INSTANT_TRADE_PAY";

    /**
     * 手机Wap支付
     */
    String QUICK_WAP_PAY = "QUICK_WAP_PAY";

    String QUICK_MSECURITY_PAY = "QUICK_MSECURITY_PAY";

    /**
     * 交易创建，等待买家付款
     */
    String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";

    /**
     * 未付款交易超时关闭，或支付完成后全额退款
     */
    String TRADE_CLOSED = "TRADE_CLOSED";

    /**
     * 交易支付成功
     */
    String TRADE_SUCCESS = "TRADE_SUCCESS";

    /**
     * 交易结束，不可退款
     */
    String TRADE_FINISHED = "TRADE_FINISHED";
    
    /** 支付宝返回码 - 成功. */
    String RESPONSE_CODE_SUCCESS = "10000";

}
