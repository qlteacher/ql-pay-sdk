package com.qlteacher.pay.demo.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.qlteacher.pay.wx.api.WxPayConfig;

@Component
@ConfigurationProperties(prefix = "wechat")
public class WXPayAccountConfig extends WxPayConfig{


}
