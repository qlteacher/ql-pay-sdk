 package com.github.wxpay.sdk;

import java.io.InputStream;

/**
 * 把WXPayConfig的protected方法暴露出来
 * 如果微信某个版本改了下面几个方法的可见范围 那么这个类可以删除
 * @author zhangpeng
 * @date 2020/04/22
 */
public abstract class AbstractQlWXPayConfig extends WXPayConfig{
     
     /**
      * 获取 App ID
      *
      * @return App ID
      */
    public abstract String getAppID();


     /**
      * 获取 Mch ID
      *
      * @return Mch ID
      */
    public abstract String getMchID();


     /**
      * 获取 API 密钥
      *
      * @return API密钥
      */
    public abstract String getKey();


     /**
      * 获取商户证书内容
      *
      * @return 商户证书内容
      */
    public abstract InputStream getCertStream();
    
    /**
     * 获取WXPayDomain, 用于多域名容灾自动切换
     * @return
     */
    public abstract IWXPayDomain getWXPayDomain();

}
