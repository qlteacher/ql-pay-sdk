package com.qlteacher.pay.wx;

import java.math.BigDecimal;

import com.qlteacher.pay.common.api.request.PayRequest;
import com.qlteacher.pay.common.api.response.PayResponse;
import com.qlteacher.pay.common.enums.PayType;
import com.qlteacher.pay.wx.api.WxPayConfig;
import com.qlteacher.pay.wx.api.WxPayServiceImpl;

public class WXPayTest {
    
    public static void main(String[] args) throws Exception {
        WxPayServiceImpl impl = createWxPayServiceImpl(initConfig());
        
        PayRequest request =
            PayRequest.builder().payType(PayType.WXPAY_NATIVE).orderId("123456789").price(BigDecimal.valueOf(0.01)).tradeName("测试qlpaysdk").build();
        PayResponse response =  impl.pay(request);
        System.out.println(response.getBody());
    }

    public static WxPayConfig initConfig() {
        WxPayConfig conf = new WxPayConfig();
        conf.setMiniAppId("wxxxxxxxxxxx6bf9b");
        conf.setMiniAppSecret("xxbc6xxxxxxxxxxxxxx9c49d");
        conf.setAppAppId("wxxxxxxxxxxxx43b0");
        conf.setCertPath("cert/weixin/apiclient_cert.p12");
        conf.setMchID("1580507371");
        conf.setAppid("wxbd5f876e38982fc1");
        conf.setNotifyUrl("http://www.qltechdev.com/paydemo/notify");
        conf.setPrivateKey("AAAAB3NzaC1yc2EAAAADAQABAAACAQC0");
        conf.setSignType("HMACSHA256");//正式环境必须是SHA256
        conf.setTest(false);
        return conf;
    }

    public static WxPayServiceImpl createWxPayServiceImpl(WxPayConfig conf) {
        WxPayServiceImpl wxPayServiceImpl = new WxPayServiceImpl(conf);
        return wxPayServiceImpl;
    }

}
