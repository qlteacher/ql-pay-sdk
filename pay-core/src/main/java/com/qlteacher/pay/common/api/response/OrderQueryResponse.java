package com.qlteacher.pay.common.api.response;

import com.qlteacher.pay.common.enums.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderQueryResponse {
	
    /**
     * 订单状态
     */
    private OrderStatus orderStatusEnum;

    /**
     *第三方支付的流水号
     */
    private String outTradeNo;

    /**
     * 附加内容，发起支付时传入
     */
    private String attach;

    /**
     * 错误原因
     */
    private String resultMsg;

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 支付完成时间
     */
    private String finishTime;

}
