package com.qlteacher.pay.common.api.response;

import com.qlteacher.pay.common.enums.PayPlatform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 支付时的同步/异步返回
 * @author zhangpeng
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class PayResponse extends BaseResponse{
    
    /**
     * AliPay  pc网站支付返回的body体，html 可直接嵌入网页使用
     * WXPay  放了错误时的返回信息
     */
    private String body;
    
    private String orderId;

    /**
     * 第三方支付的流水号
     */
    private String outTradeNo;
    
    /**
     * 请求是否成功
     */
    private Boolean success;

    @Builder
    public PayResponse(PayPlatform payPlatform, String body, String orderId, String outTradeNo,Boolean success) {
        super(payPlatform);
        this.body = body;
        this.orderId = orderId;
        this.outTradeNo = outTradeNo;
        this.success=success;
    }

}
