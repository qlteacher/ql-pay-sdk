package com.qlteacher.pay.common.api.response;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper=true)
public class RefundResponse extends BaseResponse{
	
	/**
     * 订单号.
     */
    private String orderId;

    /**
     * 订单金额.
     */
    private BigDecimal orderAmount;

    /**
     * 第三方支付流水号.
     */
    private String outTradeNo;
    
    /**
     * 退款支付时间 alipay
     */
    private Date refundTime;
    
    private String body;
    
    private Boolean success;

}
