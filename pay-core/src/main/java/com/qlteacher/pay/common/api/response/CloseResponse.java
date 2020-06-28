package com.qlteacher.pay.common.api.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CloseResponse {

    /**
     * 订单号.
     */
    private String orderId;

    /**
     * 第三方支付流水号.
     */
    private String outTradeNo;
    
    private String msg;
}