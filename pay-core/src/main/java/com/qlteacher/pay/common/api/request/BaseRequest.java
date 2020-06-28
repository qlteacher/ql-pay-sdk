package com.qlteacher.pay.common.api.request;

import com.qlteacher.pay.common.enums.PayPlatform;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseRequest {

    /**
     * 订单号
     */
    String orderId;

    /**
     * 支付平台
     */
    PayPlatform platform;

}
