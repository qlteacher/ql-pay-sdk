# pay-sdk的支付宝实现

本项目依赖了2个alipay的sdk实现了pay-sdk-core的接口:
- [alipay-sdk-java](https://github.com/alipay/alipay-sdk-java-all "alipay-sdk-java")
- [alipay-easysdk](https://github.com/alipay/alipay-easysdk "alipay-easysdk")


## 初始化
> 支付宝支持两种验证方式,普通公钥方式和公钥证书方式,因为普通公钥方式会被逐渐弃用,那么本项目中所有的例子都是使用的公钥证书方式,如需要普通公钥方式只需要setSignType("RSA"),然后只设置privateKey和publicKey即可

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
AliPayServiceImpl中初始化了AlipayClient和Factory,他们分别来自上面引入的2个sdk,如需要PayService中没有支持的功能可以直接aliPayServiceImpl.getAlipayClient或Factory对象即可(Factory方法全是静态的且只需要初始化一次就行)

## 统一下单 AliPayServiceImpl.pay
 对应支付宝的[alipay.trade.create](https://opendocs.alipay.com/apis/api_1 "对应阿里api")接口,这个方法根据传入的PayType不同返回也会有所不同
### 请求 PayRequest
| 参数  |  描述 |  示例值 |
| ------------ | ------------ | ------------ |
| PayType  | 支付类型 枚举类  | alipay_pc |
| orderId  | 商户订单号,需要在商户端保证不重复  | 20150320010101001 |
| price | 订单总金额，单位为元  | 2333 |
| buyerLogonId  | 当PayType等于ALIPAY_H5需要传  | 186xxxxxxxxxx |
| buyerId  | 同上  | 2088202954065786 |
| tradeName | 订单标题 | QQ会员充值  |

### 响应 PayResponse
| 参数  | 描述  | 示例值  |
| ------------ | ------------ | ------------ |
| payPlatform  |  产生交易的平台 | alipay  |
| body  | 平台返回的httpbody或错误信息  |   |
| orderId  | 支付宝交易号  |  2013112011001004330000121536 |
| outTradeNo | 商户订单号  |  20150320010101001 |
| success | 是否成功 | true |

> 注意 支付宝接口支持的入参和出参非常的多,这里只保留了必须或必要的,如有其他需求可自行扩展

## 交易查询 AliPayServiceImpl.query
对应支付宝的[alipay.trade.query](https://opendocs.alipay.com/apis/api_1/alipay.trade.query "alipay.trade.query")接口 
> 这个接口支付宝返回的字段很多,这里封装后只保留了很少的属性,如有特殊需要自行扩展

### 请求 OrderQueryRequest
| 参数  |  描述 |  示例值 |
| ------------ | ------------ | ------------ |
| platform  | 产生交易的平台  | alipay |
| orderId  | 商户订单号,需要在商户端保证不重复  | 20150320010101001 |

### 响应 OrderQueryResponse
| 参数  | 描述  | 示例值  |
| ------------ | ------------ | ------------ |
| payPlatform  |  产生交易的平台 | alipay  |
| orderStatusEnum  | OrderStatus枚举类  |  SUCCESS |
| orderId  | 商户订单号  |  20150320010101001 |
| resultMsg | 业务返回码描述  |  交易已被支付 |

## 退款 AliPayServiceImpl.refund
对应支付宝的[alipay.trade.refund](https://opendocs.alipay.com/apis/api_1/alipay.trade.refund)接口,这里目前不支持同笔交易多次退款(可扩展)
### 请求 RefundRequest
| 参数  |  描述 |  示例值 |
| ------------ | ------------ | ------------ |
| platform  | 产生交易的平台  | alipay |
| orderId  | 商户订单号,需要在商户端保证不重复  | 20150320010101001 |
| price | 退款金额 | 233 |

### 响应 RefundResponse
| 参数  | 描述  | 示例值  |
| ------------ | ------------ | ------------ |
| payPlatform  |  产生交易的平台 | alipay  |
| orderId  | 商户订单号  |  20150320010101001 |
| outTradeNo | 支付宝交易号  |  2013112011001004330000121536 |
| parse | 退款支付时间 | 2014-11-27 15:45:57 |

## 撤销交易 AliPayServiceImpl.cancel
对应支付宝的[alipay.trade.cancel](https://opendocs.alipay.com/apis/api_1/alipay.trade.cancel)接口,调用这个方法前应该先进行订单状态的查询,如果是失败或者超时才用这个方法,否则用refund
### 请求 CancelRequest
| 参数  |  描述 |  示例值 |
| ------------ | ------------ | ------------ |
| platform  | 产生交易的平台  | alipay |
| orderId  | 商户订单号,需要在商户端保证不重复  | 20150320010101001 |

### 响应 CancelResponse
| 参数  | 描述  | 示例值  |
| ------------ | ------------ | ------------ |
| payPlatform  |  产生交易的平台 | alipay  |
| orderId  | 商户订单号  |  20150320010101001 |
| msg | 交易动作  |  close |
| outTradeNo | 支付宝交易号 | 2013112011001004330000121536 |

## 关闭交易 AliPayServiceImpl.close
[alipay.trade.close](https://opendocs.alipay.com/apis/api_1/alipay.trade.close)
### 请求 CloseRequest
| 参数  |  描述 |  示例值 |
| ------------ | ------------ | ------------ |
| platform  | 产生交易的平台  | alipay |
| orderId  | 商户订单号,需要在商户端保证不重复  | 20150320010101001 |

### 响应 CloseResponse
| 参数  | 描述  | 示例值  |
| ------------ | ------------ | ------------ |
| payPlatform  |  产生交易的平台 | alipay  |
| orderId  | 商户订单号  |  20150320010101001 |
| msg | 业务返回码描述  |  交易已被支付 |
| outTradeNo | 支付宝交易号 | 2013112011001004330000121536 |

## 对账单下载地址 AliPayServiceImpl.downloadBill
[alipay.data.dataservice.bill.downloadurl.query](https://opendocs.alipay.com/apis/api_15/alipay.data.dataservice.bill.downloadurl.query)
### 请求 String billDate
| 参数  |  描述 |  示例值 |
| ------------ | ------------ | ------------ |
| billDate  |  账单时间 日账单格式为yyyy-MM-dd,月账单格式为yyyy-MM | 2016-04-05  |

### 响应 String  downloadUrl
| 参数  | 描述  | 示例值  |
| ------------ | ------------ | ------------ |
| url  |  账单下载地址链接,30秒后失效 | http://dwbillcenter.alipay.com/xxxx  |

## 验签方法 AliPayServiceImpl.verify
### 请求 String billDate
| 参数  |  描述 |  示例值 |
| ------------ | ------------ | ------------ |
| paramMap  |  需要验签的参数 | 肯定包括signType,sign  |

### 响应 boolean
