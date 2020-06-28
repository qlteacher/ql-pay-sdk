package com.qlteacher.pay.alipay.api;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.StringUtils;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.BaseClient.Config;
import com.alipay.easysdk.payment.common.models.AlipayTradeCancelResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeCloseResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeCreateResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeRefundResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.alipay.easysdk.payment.wap.models.AlipayTradeWapPayResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qlteacher.pay.alipay.constants.QlAliPayConstants;
import com.qlteacher.pay.alipay.enums.AlipayTradeStatus;
import com.qlteacher.pay.common.api.PayService;
import com.qlteacher.pay.common.api.request.CancelRequest;
import com.qlteacher.pay.common.api.request.CloseRequest;
import com.qlteacher.pay.common.api.request.OrderQueryRequest;
import com.qlteacher.pay.common.api.request.PayRequest;
import com.qlteacher.pay.common.api.request.RefundRequest;
import com.qlteacher.pay.common.api.response.CancelResponse;
import com.qlteacher.pay.common.api.response.CloseResponse;
import com.qlteacher.pay.common.api.response.OrderQueryResponse;
import com.qlteacher.pay.common.api.response.PayResponse;
import com.qlteacher.pay.common.api.response.RefundResponse;
import com.qlteacher.pay.common.enums.PayPlatform;
import com.qlteacher.pay.common.enums.SignType;
import com.qlteacher.pay.common.utils.CertReader;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
public class AliPayServiceImpl implements PayService {

    private AliPayConfig aliconfig;

    private AlipayClient alipayClient;

    public AliPayServiceImpl(AliPayConfig config) throws AlipayApiException {
        super();
        this.aliconfig = config;
        init();
    }
    
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation() // 不导出实体中没有用@Expose注解的属性
        .enableComplexMapKeySerialization() // 支持Map的key为复杂对象的形式
        .serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")// 时间转化为特定格式
        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)// 会把字段首字母大写,注:对于实体上使用了@SerializedName注解的不会生效.
        .setPrettyPrinting()// 对json结果格式化
        .setLenient()//开启非严格模式
        .disableHtmlEscaping()//禁止转义html标签
        .create();

    public void init() throws AlipayApiException {
        Factory.setOptions(getOptions());

        try {
            switch (aliconfig.getCertModel()) {
                case CSR:
                    alipayClient = new DefaultAlipayClient(getClientParams());
                    break;
                case RSA:
                    alipayClient = new DefaultAlipayClient(
                        aliconfig.getProtocol() + "://"
                            + (aliconfig.getTest() ? QlAliPayConstants.ALIPAY_GATEWAY_OPEN_DEV
                                : QlAliPayConstants.ALIPAY_GATEWAY_OPEN),
                        aliconfig.getAppid(), aliconfig.getPrivateKey(), aliconfig.getMsgType().name(),
                        aliconfig.getCharset(), aliconfig.getPublicKey(), aliconfig.getSignType().name());
                    break;
            }
        } catch (AlipayApiException e) {
            log.error("初始化alipayClient出错 配置:" + gson.toJson(aliconfig), e);
            throw e;
        }
    }

    private CertAlipayRequest getClientParams() {
        CertAlipayRequest certParams = new CertAlipayRequest();
        certParams.setServerUrl(aliconfig.getProtocol() + "://"
            + (aliconfig.getTest() ? QlAliPayConstants.ALIPAY_GATEWAY_OPEN_DEV : QlAliPayConstants.ALIPAY_GATEWAY_OPEN)
            + "/gateway.do");
        // 请更换为您的AppId
        certParams.setAppId(aliconfig.getAppid());
        // 请更换为您的PKCS8格式的应用私钥
        certParams.setPrivateKey(aliconfig.getPrivateKey());
        // 请更换为您使用的字符集编码，推荐采用utf-8
        certParams.setCharset(aliconfig.getCharset());
        certParams.setFormat(aliconfig.getMsgType().name());
        certParams.setSignType(aliconfig.getSignType().name());

        certParams.setCertPath(CertReader.readCertpath(aliconfig.getMerchantCertPath()));
        certParams.setAlipayPublicCertPath(CertReader.readCertpath(aliconfig.getAlipayCertPath()));
        certParams.setRootCertPath(CertReader.readCertpath(aliconfig.getAlipayRootCertPath()));

        return certParams;
    }

    private Config getOptions() {
        Config config = new Config();
        config.protocol = aliconfig.getProtocol();// "https";
        config.gatewayHost =
            aliconfig.getTest() ? QlAliPayConstants.ALIPAY_GATEWAY_OPEN_DEV : QlAliPayConstants.ALIPAY_GATEWAY_OPEN;
        config.signType = aliconfig.getSignType().name();

        config.appId = aliconfig.getAppid();

        config.merchantPrivateKey = aliconfig.getPrivateKey();

        switch (aliconfig.getCertModel()) {
            case CSR:
                config.merchantCertPath = aliconfig.getMerchantCertPath();
                config.alipayCertPath = aliconfig.getAlipayCertPath();
                config.alipayRootCertPath = aliconfig.getAlipayRootCertPath();
                break;
            case RSA:
                config.alipayPublicKey = aliconfig.getPublicKey();
                break;
        }
        return config;
    }

    @Override
    public PayResponse pay(PayRequest request) throws Exception {
        PayResponse response = null;
        try {

            if (request.getPayType().getPlatform().equals(PayPlatform.ALIPAY)) {
                switch (request.getPayType()) {
                    case ALIPAY_H5:
                        AlipayTradeCreateResponse aliresponse =
                            Factory.Payment.Common().create(request.getTradeName(), request.getOrderId(),
                                String.valueOf(request.getPrice()), (String)request.getAttr("buyerId"));
                        if (StringUtils.isEmpty(aliresponse.subCode)) {
                            response = PayResponse.builder().payPlatform(PayPlatform.ALIPAY).body(aliresponse.httpBody)
                                .orderId(aliresponse.tradeNo).outTradeNo(aliresponse.outTradeNo).success(true).build();
                        } else {
                            response = PayResponse.builder().payPlatform(PayPlatform.ALIPAY).body(aliresponse.subMsg)
                                .success(false).build();
                        }
                        break;
                    case ALIPAY_WAP:
                        // 20200506 alisdk升级修改
                        // response = aliWapPay(request);
                        AlipayTradeWapPayResponse aliWapPayResponse =
                            Factory.Payment.Wap().pay(request.getTradeName(), request.getOrderId(),
                                String.valueOf(request.getPrice()), request.getAttr().containsKey("quit_url")
                                    ? (String)request.getAttr("quit_url") : aliconfig.getQuitUrl());
                        response = PayResponse.builder().payPlatform(PayPlatform.ALIPAY).success(true)
                            .body(aliWapPayResponse.body).orderId(request.getOrderId()).build();
                        break;
                    case ALIPAY_PC:
                        // 20200506 alisdk升级修改
                        // response = aliPcPay(request);
                        AlipayTradePagePayResponse aliPagePayResponse = Factory.Payment.Page()
                            .pay(request.getTradeName(), request.getOrderId(), String.valueOf(request.getPrice()));
                        response = PayResponse.builder().payPlatform(PayPlatform.ALIPAY).success(true)
                            .body(aliPagePayResponse.body).orderId(request.getOrderId()).build();
                        break;
                    case ALIPAY_APP:
                        // 20200506 alisdk升级修改
                        // response = aliAppPay(request);
                        com.alipay.easysdk.payment.app.models.AlipayTradeAppPayResponse aliAppPayResponse =
                            Factory.Payment.App().pay(request.getTradeName(), request.getOrderId(),
                                String.valueOf(request.getPrice()));
                        response = PayResponse.builder().payPlatform(PayPlatform.ALIPAY).success(true)
                            .body(aliAppPayResponse.body).orderId(request.getOrderId()).build();
                        break;
                }
            } else {
                throw new RuntimeException("不支持的请求");
            }

        } catch (Exception e) {
            log.error("执行alipay.trade.pay出现异常 -> resquest:" + gson.toJson(request), e);
            throw e;
        }
        return response;
    }

    @Override
    public boolean verify(Map<String, String> toBeVerifiedParamMap) {
        try {
            if (AliCertModel.CSR == aliconfig.getCertModel()) {
                return AlipaySignature.certVerifyV1(toBeVerifiedParamMap,
                    CertReader.readCertpath(aliconfig.getAlipayCertPath()), aliconfig.getCharset(),
                    aliconfig.getSignType().name());
            } else if (AliCertModel.RSA == aliconfig.getCertModel()) {
                return AlipaySignature.verifyV1(toBeVerifiedParamMap, aliconfig.getPublicKey(), aliconfig.getCharset(),
                    aliconfig.getSignType().name());
            }
        } catch (Exception e) {
            log.error("执行alipay.verify出现异常 -> toBeVerifiedParamMap:" + toBeVerifiedParamMap, e);
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean verify(Map<String, String> toBeVerifiedParamMap, SignType signType, String sign) {
        toBeVerifiedParamMap.put("sign", sign);
        try {
            if (AliCertModel.CSR == aliconfig.getCertModel()) {
                return AlipaySignature.certVerifyV1(toBeVerifiedParamMap,
                    CertReader.readCertpath(aliconfig.getAlipayCertPath()), aliconfig.getCharset(), signType.name());
            } else if (AliCertModel.RSA == aliconfig.getCertModel()) {
                return AlipaySignature.verifyV1(toBeVerifiedParamMap, aliconfig.getPublicKey(), aliconfig.getCharset(),
                    signType.name());
            }
        } catch (Exception e) {
            log.error("执行alipay.verify出现异常 -> toBeVerifiedParamMap:" + toBeVerifiedParamMap, e);
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public RefundResponse refund(RefundRequest request) throws Exception {
        AlipayTradeRefundResponse refundResponse =
            Factory.Payment.Common().refund(request.getOrderId(), String.valueOf(request.getRefundAmount()));
        log.debug("执行alipay.trade.refund -> resquest:{} response:{}", gson.toJson(request),
            gson.toJson(refundResponse));
        RefundResponse response ;
        if (QlAliPayConstants.RESPONSE_CODE_SUCCESS.equals(refundResponse.code)) {
            response =
                RefundResponse.builder().orderId(refundResponse.outTradeNo).outTradeNo(refundResponse.tradeNo)
                    .refundTime((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(refundResponse.gmtRefundPay))
                    .orderAmount(new BigDecimal(refundResponse.refundFee)).success(true).build();
        }else {
            response =
                RefundResponse.builder().orderId(refundResponse.outTradeNo).body(refundResponse.msg).success(false).build();
        }
        return response;
    }

    @Override
    public OrderQueryResponse query(OrderQueryRequest request) throws Exception {
        AlipayTradeQueryResponse queryResponse = Factory.Payment.Common().query(request.getOrderId());
        log.debug("执行alipay.trade.query -> resquest:{} response:{}", gson.toJson(request), gson.toJson(queryResponse));
        if (QlAliPayConstants.RESPONSE_CODE_SUCCESS.equals(queryResponse.code)) {
            return OrderQueryResponse.builder().orderId(queryResponse.outTradeNo).outTradeNo(queryResponse.tradeNo)
                .orderStatusEnum(AlipayTradeStatus.findByName(queryResponse.tradeStatus).getOrderStatus())
                .resultMsg(queryResponse.msg).build();
        } else {
            return OrderQueryResponse.builder().orderId(request.getOrderId()).resultMsg(queryResponse.subMsg).build();
        }

    }

    @Override
    public String downloadBill(String billDate) {
        try {
            com.alipay.easysdk.payment.common.models.AlipayDataDataserviceBillDownloadurlQueryResponse downloadurlQueryResponse = Factory.Payment.Common().downloadBill("trade", billDate);
            if(StringUtils.isEmpty(downloadurlQueryResponse.subCode)) {
                log.error("执行alipay.data.dataservice.bill.downloadurl.query 失败 -> billDate:" + billDate + " response:"
                    + gson.toJson(downloadurlQueryResponse));
                return gson.toJson(downloadurlQueryResponse);
            }
            return downloadurlQueryResponse.billDownloadUrl;
        } catch (Exception e) {
            log.error("执行alipay.data.dataservice.bill.downloadurl.query 出错 -> billDate:" + billDate, e);
        }
        return null;
        
    }

    @Override
    public CloseResponse close(CloseRequest request) throws Exception {
        AlipayTradeCloseResponse closeResponse = Factory.Payment.Common().close(request.getOrderId());
        log.debug("执行alipay.trade.close -> resquest:{} response:{}", gson.toJson(request), gson.toJson(closeResponse));
        if (StringUtils.isEmpty(closeResponse.subCode)) {
            return CloseResponse.builder().msg(closeResponse.msg).orderId(closeResponse.outTradeNo)
                .outTradeNo(closeResponse.tradeNo).build();
        } else {
            return CloseResponse.builder().msg(closeResponse.subMsg).build();
        }

    }

    @Override
    public CancelResponse cancel(CancelRequest cancelRequest) throws Exception {
        AlipayTradeCancelResponse cancelResponse = Factory.Payment.Common().cancel(cancelRequest.getOrderId());
        log.debug("执行alipay.trade.cancel -> resquest:{} response:{}", gson.toJson(cancelRequest),
            gson.toJson(cancelResponse));
        if (StringUtils.isEmpty(cancelResponse.subCode)) {
            return CancelResponse.builder().resultMsg(cancelResponse.action).orderId(cancelResponse.outTradeNo)
                .outTradeNo(cancelResponse.tradeNo).build();
        } else {
            return CancelResponse.builder().orderId(cancelResponse.outTradeNo).outTradeNo(cancelResponse.tradeNo)
                .resultMsg(cancelResponse.subMsg).build();
        }

    }

}
