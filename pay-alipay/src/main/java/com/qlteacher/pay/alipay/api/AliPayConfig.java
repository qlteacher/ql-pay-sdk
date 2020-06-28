package com.qlteacher.pay.alipay.api;

import com.qlteacher.pay.common.api.BasePayConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AliPayConfig extends BasePayConfig {

    private AliCertModel certModel;

    /**
     * 公钥证书模式使用 应用公钥证书文件路径，例如：/foo/appCertPublicKey_2019051064521003.crt
     */
    private String merchantCertPath;

    /**
     * 公钥证书模式使用 支付宝公钥证书文件路径，例如：/foo/alipayCertPublicKey_RSA2.crt
     */
    private String alipayCertPath;

    /**
     * 公钥证书模式使用 支付宝根证书文件路径，例如：/foo/alipayRootCert.crt
     */
    private String alipayRootCertPath;
    
    /**
     * 默认的用户付款中途退出返回商户网站的地址 ,wap支付时使用
     */
    private String quitUrl;
    
    public void setCertModel(String certModel) {
        this.certModel = AliCertModel.valueOf(certModel);
    }

}
