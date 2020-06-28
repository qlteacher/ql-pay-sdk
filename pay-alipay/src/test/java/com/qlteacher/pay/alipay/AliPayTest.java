 package com.qlteacher.pay.alipay;

import java.math.BigDecimal;

import com.alipay.api.AlipayApiException;
import com.qlteacher.pay.alipay.api.AliPayConfig;
import com.qlteacher.pay.alipay.api.AliPayServiceImpl;
import com.qlteacher.pay.common.api.request.PayRequest;
import com.qlteacher.pay.common.api.response.PayResponse;
import com.qlteacher.pay.common.enums.PayType;

public class AliPayTest {
    
    public static void main(String[] args) throws Exception {
        AliPayConfig conf = initConfig();
        AliPayServiceImpl impl = createAliPayServiceImpl(conf);
        
        
        PayRequest request =
            PayRequest.builder().payType(PayType.ALIPAY_PC).orderId("随机生成").price(BigDecimal.valueOf(0.01)).tradeName("测试qlpaysdk").build();
        PayResponse response =  impl.pay(request);
        System.out.println(response.getBody());
    }
     
     public static AliPayConfig initConfig() {
         AliPayConfig conf = new AliPayConfig();
         conf.setAppid("2xxxxxxxxxxxx3");
         conf.setPrivateKey("xxxxxxxxxxx");
         conf.setMerchantCertPath("appCertPublicKey_xxxx.crt");
         conf.setAlipayCertPath("alipayCertPublicKey_RSA2.crt");
         conf.setAlipayRootCertPath("alipayRootCert.crt");
         conf.setNotifyUrl("http://www.qltechdev.com/paydemo/notify");
         conf.setReturnUrl("http://www.qltechdev.com/paydemo/return");
         conf.setTest(true);
         conf.setCertModel("CSR");
         conf.setSignType("RSA2");
         conf.setMsgType("json");
         return conf;
     }
     
     public static AliPayServiceImpl createAliPayServiceImpl(AliPayConfig conf) throws AlipayApiException {
         AliPayServiceImpl aliPayServiceImpl = new AliPayServiceImpl(conf);
         aliPayServiceImpl.init();
         return aliPayServiceImpl;
     }

}
