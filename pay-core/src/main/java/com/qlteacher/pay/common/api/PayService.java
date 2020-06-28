package com.qlteacher.pay.common.api;

import java.util.Date;
import java.util.Map;

import com.qlteacher.pay.common.api.request.CancelRequest;
import com.qlteacher.pay.common.api.request.CloseRequest;
import com.qlteacher.pay.common.api.request.OrderQueryRequest;
import com.qlteacher.pay.common.api.request.PayRequest;
import com.qlteacher.pay.common.api.request.RefundRequest;
import com.qlteacher.pay.common.api.response.CancelResponse;
import com.qlteacher.pay.common.api.response.CloseResponse;
import com.qlteacher.pay.common.api.response.OrderQueryResponse;
import com.qlteacher.pay.common.api.response.PayResponse;
import com.qlteacher.pay.common.api.response.RefundResponse;
import com.qlteacher.pay.common.enums.SignType;

/**
 * 支付服务
 * @author zhangpeng
 *
 */
public interface PayService {
	
	/**
     * 发起支付.
	 * @throws Exception 
     */
    public PayResponse pay(PayRequest request) throws Exception;
    
    /**
     * 验证支付结果. 包括同步和异步.
     *
     * @param toBeVerifiedParamMap 待验证的支付结果参数.
     * @param signType             签名方式.
     * @param sign                 签名.
     * @return 验证结果.
     */
    boolean verify(Map<String, String> toBeVerifiedParamMap, SignType signType, String sign);
    
    
    boolean verify(Map<String, String> toBeVerifiedParamMap);
    
    /**
     * 退款
     * @param request
     * @return
     * @throws Exception 
     */
    RefundResponse refund(RefundRequest request) throws Exception;
    
    /**
     * 查询订单
     * @param request
     * @return
     * @throws Exception 
     */
    OrderQueryResponse query(OrderQueryRequest request) throws Exception;
    
    /**
     * 下载对账单
     * @param request
     * @return
     */
    String downloadBill(String billDate);
    
    /**
     * 关闭订单
     * @param request
     * @return
     * @throws Exception 
     */
    CloseResponse close(CloseRequest request) throws Exception;
    
    /**
     * 订单交易撤销
     * @param cancelRequest
     * @return
     * @throws Exception 
     */
    CancelResponse cancel(CancelRequest cancelRequest) throws Exception;


}
