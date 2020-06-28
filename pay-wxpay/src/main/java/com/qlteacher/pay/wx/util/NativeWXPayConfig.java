 package com.qlteacher.pay.wx.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.github.wxpay.sdk.AbstractQlWXPayConfig;
import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPay;
import com.qlteacher.pay.common.utils.CertReader;
import com.qlteacher.pay.wx.api.WxPayConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class NativeWXPayConfig extends AbstractQlWXPayConfig {
    
    private WxPayConfig wxPayConfig;

    @Override
    public IWXPayDomain getWXPayDomain() {
         return WXPayDomainSimpleImpl.instance();
    }

    @Override
    public String getAppID() {
         return wxPayConfig.getAppid();
    }

    @Override
    public String getMchID() {
         return wxPayConfig.getMchID();
    }

    @Override
    public String getKey() {
         return wxPayConfig.getPrivateKey();
    }

    @Override
    public InputStream getCertStream() {
         return new ByteArrayInputStream(CertReader.readCertFileByte(wxPayConfig.getCertPath()));
    }

    public NativeWXPayConfig(WxPayConfig wxPayConfig) {
        super();
        this.wxPayConfig = wxPayConfig;
        if(wxPayConfig.getTest()) {
            wxPayConfig.setPrivateKey(getSandboxSignKey());
        }
    }
    
    public String getSandboxSignKey() {
        WXPay wxPay = null;
        try {
            wxPay = new WXPay(this);
        } catch (Exception e1) {
             e1.printStackTrace();
        }
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("mch_id", this.getMchID());
            params.put("nonce_str", WXPayUtil.generateNonceStr());
            params.put("sign", WXPayUtil.generateSignature(params, this.getKey()));
            String strXML = wxPay.requestWithoutCert("/sandboxnew/pay/getsignkey",
                    params, this.getHttpConnectTimeoutMs(), this.getHttpReadTimeoutMs());
 
            Map<String, String> result = WXPayUtil.xmlToMap(strXML);
            if ("SUCCESS".equals(result.get("return_code"))) {
                return result.get("sandbox_signkey");
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取sandbox_signkey异常" + e.getMessage());
            return null;
        }
    }


}
