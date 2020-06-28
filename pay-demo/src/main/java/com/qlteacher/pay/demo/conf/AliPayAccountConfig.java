package com.qlteacher.pay.demo.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.qlteacher.pay.alipay.api.AliPayConfig;

@ConfigurationProperties(prefix = "alipay")
@Component
public class AliPayAccountConfig extends AliPayConfig{

}
