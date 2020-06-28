package com.qlteacher.pay.demo.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alipay.api.AlipayApiException;
import com.qlteacher.pay.alipay.api.AliPayConfig;
import com.qlteacher.pay.alipay.api.AliPayServiceImpl;

@Configuration
public class AliPayServiceConfig {
    
    @Bean
    public AliPayServiceImpl createAliPayServiceImpl(AliPayConfig conf) throws AlipayApiException {
        AliPayServiceImpl aliPayServiceImpl = new AliPayServiceImpl(conf);
        aliPayServiceImpl.init();
        return aliPayServiceImpl;
    }

}
