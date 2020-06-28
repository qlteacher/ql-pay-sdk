package com.qlteacher.pay.common.api;

import org.apache.commons.lang3.StringUtils;

import com.qlteacher.pay.common.enums.MsgType;
import com.qlteacher.pay.common.enums.SignType;

import lombok.Data;

@Data
public abstract class BasePayConfig implements PayConfig {

    private String appid;
    
    private String privateKey;
    
    private String publicKey;
    
    private String notifyUrl;
    
    private String returnUrl;

	/**
     * 是否为沙箱环境，默认为正式环境
     */
    private Boolean test = Boolean.FALSE;
    
    private MsgType msgType;
    
    private String charset;
    
    private String protocol;
    
    private SignType signType;
	
	private Object attach;
	
	public String getProtocol() {
	    if (StringUtils.isEmpty(protocol)) {
	        protocol = "https";
        }
        return protocol;
    }

    public String getCharset() {
        if (StringUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        return charset;
    }
    
    public void setSignType(String signType) {
        this.signType = SignType.valueOf(signType);
    }
    
    public void setMsgType(String msgType) {
        this.msgType = MsgType.valueOf(msgType);
    }

}
