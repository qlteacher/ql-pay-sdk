package com.qlteacher.pay.common.api.response;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Builder
public class CancelResponse extends BaseResponse {

    /**
     * 商户订单号
     */
    private String orderId;

    /**
     * 平台交易号
     */
    private String outTradeNo;
    
    /**
     * 结果 
     * ali:
     *   close：交易未支付，触发关闭交易动作，无退款；
     *   refund：交易已支付，触发交易退款动作；
     *   未返回：未查询到交易，或接口调用失败；
     */
    private String resultMsg;

}
