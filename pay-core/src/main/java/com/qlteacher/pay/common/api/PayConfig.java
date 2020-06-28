package com.qlteacher.pay.common.api;

import com.qlteacher.pay.common.enums.MsgType;

/**
 * 支付客户端配置
 * 
 * @author zhangpeng
 *
 */
public interface PayConfig {
	
	/**
     *  应用id
     *  @return 应用id
     */
     String getAppid();
     
     /**
      * 应用私钥
     * @return
     */
    String getPrivateKey();
    
    /**
     * 应用公钥
     * @return
     */
    String getPublicKey();
    
    /**
     * 异步通知地址
     * @return
     */
    String getNotifyUrl();
    
    /**
     * 同步通知地址
     * @return
     */
    String getReturnUrl();
    
    /**
     * 是否是测试
     * @return
     */
    Boolean getTest();
    
    /**
     * 消息类型
     * @return
     */
    MsgType getMsgType();
    
    
    /**
     * 编码
     * @return
     */
    String getCharset();
    
    /**
     * 附加支付配置
     * @return 附加信息
     */
     Object getAttach();
    

}
