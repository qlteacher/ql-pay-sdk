package com.qlteacher.pay.common.api.request;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.qlteacher.pay.common.enums.PayPlatform;
import com.qlteacher.pay.common.enums.PayType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 支付请求
 * 
 * @author zhangpeng
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class PayRequest extends BaseRequest{
    
    /**
     * 支付方式
     */
    PayType payType;
	
	/**
     * 金额
     */
    private BigDecimal price;
    
    /**
     * 名称
     */
    private String tradeName;
    
    /**
     * 描述
     */
    private String body;
    
    /**
     * 订单附加信息，可用于预设未提供的参数，这里会覆盖以上所有的订单信息，
     */
    private Map<String, Object> attr;
    
    public Map<String, Object> getAttrs() {
        if (null == attr){
            attr = new HashMap<>();
        }
        return attr;
    }

    public Object getAttr(String key) {
        return getAttrs().get(key);
    }

    /**
     * 添加订单信息
     * @param key key
     * @param value 值
     */
    public void addAttr(String key, Object value) {
        getAttrs().put(key, value);
    }

	public PayRequest(BigDecimal price, String orderId, String tradeName, PayType payType) {
		super(orderId,payType.getPlatform());
		this.price = price;
		this.tradeName = tradeName;
		this.payType = payType;
	}

	@Builder
    public PayRequest(String orderId, PayPlatform platform, PayType payType, BigDecimal price, String tradeName,
        String body, Map<String, Object> attr) {
        super(orderId, platform);
        this.payType = payType;
        this.price = price;
        this.tradeName = tradeName;
        this.body = body;
        this.attr = attr;
    }
	
	

}
