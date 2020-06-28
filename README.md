# pay-sdk

仅仅作为后台接口 SDK 封装三方支付渠道api(没有对三方API进行任何修改,如有后续升级只需关注三方升级的注意事项即可)

demo地址:
http://www.qltechdev.com/paydemo/

### 支持的方法
| PayService方法  | 描述  |
| ------------ | ------------ |
| pay  | 统一下单  |
| verify | 验证签名  |
| refund | 退款  |
| query | 查询  |
| downloadBill  | 下载对账单  |
| close  | 关闭订单 |
| cancel | 取消订单  |

### 注意
- 证书路径可以是绝对路径也可以是相对路径但是不能被打包在程序内
- 微信不支持cancel方法
- 微信沙箱环境问题多多不要用
- [wxpay-sdk](https://git.qltechdev.com/lab/pay/wxpay-sdk "wxpay-sdk") 上传到了maven私服中
- 支付宝依赖了2个sdk 同时都在使用 ([alipay-sdk-java-all](https://github.com/alipay/alipay-sdk-java-all "alipay-sdk-java-all") 和 [alipay-easysdk](https://github.com/alipay/alipay-easysdk "alipay-easysdk"))

## 支付宝支付
### 初始化
```java
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
```
### 调用
```java
        AliPayServiceImpl impl = createAliPayServiceImpl(conf);
        PayRequest request = PayRequest.builder().payType(PayType.ALIPAY_PC).
            orderId("123456").price(BigDecimal.valueOf(0.01)).tradeName("测试qlpaysdk").build();
        PayResponse response =  impl.pay(request);
        System.out.println(response.getBody());
```

## 微信支付
### 初始化
```java
    public static WxPayConfig initConfig() {
        WxPayConfig conf = new WxPayConfig();
        conf.setMiniAppId("wxxxxxxxxxxx6bf9b");
        conf.setMiniAppSecret("xxbc6xxxxxxxxxxxxxx9c49d");
        conf.setAppAppId("wxxxxxxxxxxxx43b0");
        conf.setCertPath("cert/weixin/apiclient_cert.p12");
        conf.setMchID("xxxxxxx");
        conf.setAppid("xxxxxxxxxxxxxx");
        conf.setNotifyUrl("http://www.qltechdev.com/paydemo/notify");
        conf.setPrivateKey("xxxxxxxxxxxxxxxxxxx");
        conf.setSignType("HMACSHA256");//正式环境必须是SHA256
        conf.setTest(false);
        return conf;
    }
	
	public static WxPayServiceImpl createWxPayServiceImpl(WxPayConfig conf) {
        WxPayServiceImpl wxPayServiceImpl = new WxPayServiceImpl(conf);
        return wxPayServiceImpl;
    }
```
### 调用
```java
    WxPayServiceImpl impl = createWxPayServiceImpl(initConfig());
        
    PayRequest request = PayRequest.builder().payType(PayType.WXPAY_NATIVE).orderId("123456789")
       .price(BigDecimal.valueOf(0.01)).tradeName("测试qlpaysdk").build();
    PayResponse response =  impl.pay(request);
    System.out.println(response.getBody());
```
