package com.qlteacher.pay.common.api.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class CloseRequest extends BaseRequest{

    /**
     * 第三方支付流水号.
     */
    private String outOrderId;

    /**
     * 卖家端自定义的的操作员 ID
     */
    private String operatorId;

}
