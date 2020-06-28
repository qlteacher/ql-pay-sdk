# pay-sdk的微信实现

本项目依赖了微信的sdk实现了pay-sdk-core的接口:
- [wxpay-sdk](https://git.qltechdev.com/lab/pay/wxpay-sdk "wxpay-sdk")



## 初始化
```java
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
```
> 由于微信sdk的问题([WXPay.java 43行](https://git.qltechdev.com/lab/pay/wxpay-sdk/-/blob/master/src/main/java/com/github/wxpay/sdk/WXPay.java "WXPay.java 43行")),正式环境的SignType必须是SHA256,沙箱环境的SignType必须是md5

## 统一下单 WXPayServiceImpl.pay
 对应微信的统一下单接口(pay/unifiedorder),这个方法根据传入的PayType不同返回也会有所不同
### 请求 PayRequest
| 参数  |  描述 |  示例值 |
| ------------ | ------------ | ------------ |
| PayType  | 支付类型 枚举类  | WXPAY_NATIVE |
| orderId  | 商户订单号,需要在商户端保证不重复  | 20150320010101001 |
| price | 订单总金额，单位为元  | 2333 |
| tradeName | 订单标题 | QQ会员充值  |
| spbill_create_ip | 终端IP | 8.8.8.8 |
| product_id | 商品ID,paytype=NATIVE时必填 | 12235413214070356458058 |
| openid | 用户标识,公共号|小程序时必填 | 12235413214070356458058 |

### 响应 PayResponse
| 参数  | 描述  | 示例值  |
| ------------ | ------------ | ------------ |
| payPlatform  |  产生交易的平台 | WXPAY  |
| body  | 平台返回的httpbody或错误信息  |   |
| outTradeNo | H5和NATIVE时返回预支付会话标识 | wx201410272009395522657a690389285100 |
| success | 是否成功 | true |

> 注意 微信接口支持的入参和出参非常的多,这里只保留了必须或必要的,如有其他需求可自行扩展

## 交易查询 WXPayServiceImpl.query
对应微信的查询接口(pay/orderquery)
> 这个接口微信返回的字段很多,这里封装后只保留了很少的属性,如有特殊需要自行扩展

### 请求 OrderQueryRequest
| 参数  |  描述 |  示例值 |
| ------------ | ------------ | ------------ |
| platform  | 产生交易的平台  | WXPAY |
| orderId  | 商户订单号,需要在商户端保证不重复  | 20150320010101001 |

### 响应 OrderQueryResponse
| 参数  | 描述  | 示例值  |
| ------------ | ------------ | ------------ |
| payPlatform  |  产生交易的平台 | WXPAY  |
| orderStatusEnum  | OrderStatus枚举类  |  SUCCESS |
| orderId  | 商户订单号  |  20150320010101001 |
| resultMsg | 业务返回码描述  |  交易已被支付 |

## 退款 WXPayServiceImpl.refund
对应微信的申请退款接口(secapi/pay/refund)(可扩展)
### 请求 RefundRequest
| 参数  |  描述 |  示例值 |
| ------------ | ------------ | ------------ |
| platform  | 产生交易的平台  | WXPAY |
| orderId  | 商户订单号,需要在商户端保证不重复  | 20150320010101001 |
| price | 退款金额 | 23 |
| totalFee | 订单金额 | 233 |
| out_refund_no | 商户退款单号 | 商户系统内部的退款单号 |

### 响应 RefundResponse
| 参数  | 描述  | 示例值  |
| ------------ | ------------ | ------------ |
| payPlatform  |  产生交易的平台 | WXPAY  |
| orderId  | 商户订单号  |  20150320010101001 |
| transaction_id | 微信订单号  |  4007752501201407033233368018 |

## 撤销交易 WXPayServiceImpl.cancel
微信不支持该方法

## 关闭交易 WXPayServiceImpl.close
对应微信的关闭订单(pay/closeorder)
### 请求 CloseRequest
| 参数  |  描述 |  示例值 |
| ------------ | ------------ | ------------ |
| platform  | 产生交易的平台  | WXPAY |
| orderId  | 商户订单号,需要在商户端保证不重复  | 20150320010101001 |

### 响应 CloseResponse
| 参数  | 描述  | 示例值  |
| ------------ | ------------ | ------------ |
| payPlatform  |  产生交易的平台 | WXPAY  |
| orderId  | 商户订单号  |  20150320010101001 |
| msg | 结果描述  |  SUCCESS |

## 对账单下载地址 WXPayServiceImpl.downloadBill
对应微信的下载交易账单接口(pay/downloadbill)
### 请求 String billDate
| 参数  |  描述 |  示例值 |
| ------------ | ------------ | ------------ |
| billDate  |  账单时间 格式为yyyyMMdd | 20140603  |

### 响应 String  downloadUrl
| 参数  | 描述  | 示例值  |
| ------------ | ------------ | ------------ |
| body  |  直接输出csv格式的文本表格 |   |

## 验签方法 WXPayServiceImpl.verify
### 请求 String billDate
| 参数  |  描述 |  示例值 |
| ------------ | ------------ | ------------ |
| paramMap  |  需要验签的参数 | 肯定包括signType,sign  |

### 响应 boolean
