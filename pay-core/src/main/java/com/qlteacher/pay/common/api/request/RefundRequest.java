package com.qlteacher.pay.common.api.request;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class RefundRequest extends BaseRequest{
	
	/**
     * 退款金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 退款说明
     */
    private String description;
    
    /**
     * 退款交易日期
     */
    private Date orderDate;

}
