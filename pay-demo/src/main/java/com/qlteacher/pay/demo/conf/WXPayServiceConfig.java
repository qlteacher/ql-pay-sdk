package com.qlteacher.pay.demo.conf;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.qlteacher.pay.wx.api.WxPayConfig;
import com.qlteacher.pay.wx.api.WxPayServiceImpl;

@Configuration
public class WXPayServiceConfig {

    @Bean
    public WxPayServiceImpl createWxPayServiceImpl(WxPayConfig conf) {
        WxPayServiceImpl wxPayServiceImpl = new WxPayServiceImpl(conf);
        return wxPayServiceImpl;
    }
}
