package com.qlteacher.pay.wx.api;

import com.github.wxpay.sdk.AbstractQlWXPayConfig;
import com.qlteacher.pay.common.api.BasePayConfig;
import com.qlteacher.pay.common.enums.PayType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 由于Java无法多继承
 * 
 * @author zhangpeng
 * @date 2020/04/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WxPayConfig extends BasePayConfig  {

    /**
     * 小程序appId 获取地址 https://mp.weixin.qq.com
     */
    private String miniAppId;

    /**
     * 小程序appSecret
     */
    private String miniAppSecret;

    /**
     * 商户号 获取地址 https://pay.weixin.qq.com
     */
    private String mchID;

    /**
     * app应用appid 获取地址 https://open.weixin.qq.com
     */
    private String appAppId;

    /**
     * 商户证书路径
     */
    private String certPath;
    
}
