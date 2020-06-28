package com.qlteacher.pay.wx.api.request;

import java.math.BigDecimal;

import com.qlteacher.pay.common.api.request.RefundRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WXRefundRequest extends RefundRequest {

    /**
     * 商户退款单号
     */
    private String outRefundNo;

    /**
     * 退款金额
     */
    private BigDecimal totalFee;

}
