package com.qlteacher.pay.wx.api;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import com.qlteacher.pay.common.enums.OrderStatus;
import com.qlteacher.pay.common.enums.PayPlatform;
import com.qlteacher.pay.common.enums.PayType;
import com.qlteacher.pay.common.enums.SignType;
import com.qlteacher.pay.wx.api.request.WXRefundRequest;
import com.qlteacher.pay.wx.constants.QLWXPayConstants;
import com.qlteacher.pay.wx.util.APPWXPayConfig;
import com.qlteacher.pay.wx.util.MINIWXPayConfig;
import com.qlteacher.pay.wx.util.MoneyUtil;
import com.qlteacher.pay.wx.util.NativeWXPayConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
public class WxPayServiceImpl implements PayService {

    private WxPayConfig wxPayConfig;

    public WxPayServiceImpl(WxPayConfig wxPayConfig) {
        super();
        this.wxPayConfig = wxPayConfig;
    }

    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation() // 不导出实体中没有用@Expose注解的属性
        .enableComplexMapKeySerialization() // 支持Map的key为复杂对象的形式
        .serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")// 时间转化为特定格式
        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)// 会把字段首字母大写,注:对于实体上使用了@SerializedName注解的不会生效.
        .setPrettyPrinting()// 对json结果格式化
        .setLenient()//开启非严格模式
        .disableHtmlEscaping()//禁止转义html标签
        .create();

    @Override
    public PayResponse pay(PayRequest request) throws Exception {
        Map<String, String> data = new HashMap<String, String>();
        WXPay wxpay = createWXPayByRequest(request.getPayType());

        data.put(QLWXPayConstants.BODY, request.getTradeName());
        data.put(QLWXPayConstants.OUT_TRADE_NO, request.getOrderId());
        data.put(QLWXPayConstants.TOTAL_FEE, String.valueOf(MoneyUtil.Yuan2Fen(request.getPrice())));
        data.put(QLWXPayConstants.SPBILL_CREATE_IP, !request.getAttrs().containsKey(QLWXPayConstants.SPBILL_CREATE_IP)
            ? QLWXPayConstants.LOCALIP : (String)request.getAttr(QLWXPayConstants.SPBILL_CREATE_IP));
        data.put(QLWXPayConstants.NOTIFY_URL, wxPayConfig.getNotifyUrl());
        data.put(QLWXPayConstants.TRADE_TYPE, request.getPayType().getCode());
        if (request.getAttrs().containsKey(QLWXPayConstants.PRODUCT_ID)) {
            data.put(QLWXPayConstants.PRODUCT_ID, (String)request.getAttr(QLWXPayConstants.PRODUCT_ID));
        }

        if (request.getAttrs().containsKey(QLWXPayConstants.OPENID)) {
            data.put(QLWXPayConstants.OPENID, (String)request.getAttr(QLWXPayConstants.OPENID));
        }

        try {
            Map<String, String> resp = wxpay.unifiedOrder(data);

            log.debug("执行wxpay.unifiedOrder -> resquest:{} response:{}", new Gson().toJson(request), gson.toJson(resp));

            if (!WXPayConstants.SUCCESS.equals(resp.get(QLWXPayConstants.RETURN_CODE))) {
                log.error("执行wxpay.unifiedOrder -> resquest:{} response:{}", gson.toJson(request), gson.toJson(resp));
                return PayResponse.builder().body(resp.get(QLWXPayConstants.RETURN_MSG)).success(false).build();
            }

            if (!WXPayConstants.SUCCESS.equals(resp.get(QLWXPayConstants.RESULT_CODE))) {
                log.error("执行wxpay.unifiedOrder -> resquest:{} response:{}", gson.toJson(request), gson.toJson(resp));
                return PayResponse.builder().body(resp.get(QLWXPayConstants.ERR_CODE_DES)).success(false).build();
            }

            switch (request.getPayType()) {
                case WXPAY_APP:
                    // TODO app的没测试
                    return PayResponse.builder().payPlatform(PayPlatform.WXPAY).orderId(request.getOrderId())
                        .outTradeNo(resp.get(QLWXPayConstants.PREPAY_ID)).success(true).build();
                case WXPAY_MINI:
                    return PayResponse.builder().payPlatform(PayPlatform.WXPAY).orderId(request.getOrderId())
                        .outTradeNo(resp.get(QLWXPayConstants.PREPAY_ID)).success(true).build();
                case WXPAY_MP:
                    return PayResponse.builder().payPlatform(PayPlatform.WXPAY).orderId(request.getOrderId())
                        .outTradeNo(resp.get(QLWXPayConstants.PREPAY_ID)).success(true).build();
                case WXPAY_MWEB:
                    return PayResponse.builder().payPlatform(PayPlatform.WXPAY).orderId(request.getOrderId())
                        .body(resp.get(QLWXPayConstants.MWEB_URL)).outTradeNo(QLWXPayConstants.PREPAY_ID).success(true)
                        .build();
                case WXPAY_NATIVE:
                    return PayResponse.builder().payPlatform(PayPlatform.WXPAY).orderId(request.getOrderId())
                        .body(resp.get(QLWXPayConstants.CODE_URL)).outTradeNo(QLWXPayConstants.PREPAY_ID).success(true)
                        .build();
                default:
                    break;

            }

        } catch (Exception e) {
            log.error("执行wxpay.unifiedOrder出现异常 -> resquest:" + gson.toJson(request), e);
        }

        return null;
    }

    private WXPay createWXPayByRequest(PayType payType) throws Exception {
        if (null == payType) {
            return new WXPay(new NativeWXPayConfig(wxPayConfig), wxPayConfig.getNotifyUrl(), true,
                wxPayConfig.getTest());
        }

        if (payType.getPlatform().equals(PayPlatform.WXPAY)) {
            // 小程序和app支付有独立的appid，公众号、h5、native都是公众号的appid
            switch (payType) {
                case WXPAY_APP:
                    return new WXPay(new APPWXPayConfig(wxPayConfig), wxPayConfig.getNotifyUrl(), true,
                        wxPayConfig.getTest());
                case WXPAY_MINI:
                    return new WXPay(new MINIWXPayConfig(wxPayConfig), wxPayConfig.getNotifyUrl(), true,
                        wxPayConfig.getTest());
                default:
                    return new WXPay(new NativeWXPayConfig(wxPayConfig), wxPayConfig.getNotifyUrl(), true,
                        wxPayConfig.getTest());
            }
        }
        return null;
    }

    @Override
    public boolean verify(Map<String, String> toBeVerifiedParamMap, SignType signType, String sign) {
        toBeVerifiedParamMap.put(WXPayConstants.FIELD_SIGN_TYPE, signType == SignType.MD5
            ? com.github.wxpay.sdk.WXPayConstants.MD5 : com.github.wxpay.sdk.WXPayConstants.HMACSHA256);
        toBeVerifiedParamMap.put(WXPayConstants.FIELD_SIGN, sign);
        return verify(toBeVerifiedParamMap);
    }

    @Override
    public boolean verify(Map<String, String> toBeVerifiedParamMap) {
        try {
            return WXPayUtil.isSignatureValid(toBeVerifiedParamMap, wxPayConfig.getPrivateKey(),
                wxPayConfig.getSignType() == SignType.MD5 ? com.github.wxpay.sdk.WXPayConstants.SignType.MD5
                    : com.github.wxpay.sdk.WXPayConstants.SignType.HMACSHA256);
        } catch (Exception e) {
            log.error("wx.verify 出错", e);
        }

        return false;
    }

    /**
     * 创建预支付参数
     * 
     * @param prepayId
     * @param appId
     * @param partnerKey
     * @param signType
     * @return
     */
    public Map<String, String> prepayIdCreateSign(String prepayId, String appId, String partnerKey, SignType signType) {
        Map<String, String> packageParams = new HashMap<String, String>(6);
        packageParams.put("appId", appId);
        packageParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        packageParams.put("nonceStr", String.valueOf(System.currentTimeMillis()));
        packageParams.put("package", "prepay_id=" + prepayId);
        if (signType == null) {
            signType = SignType.MD5;
        }
        packageParams.put("signType", signType.name());
        String packageSign;
        try {
            packageSign = WXPayUtil.generateSignature(packageParams, partnerKey,
                signType == SignType.MD5 ? com.github.wxpay.sdk.WXPayConstants.SignType.MD5
                    : com.github.wxpay.sdk.WXPayConstants.SignType.HMACSHA256);
            packageParams.put("paySign", packageSign);
            return packageParams;
        } catch (Exception e) {
            log.error("创建wx预支付请求参数出错", e);
        }
        return null;
    }

    @Override
    public RefundResponse refund(RefundRequest request) throws Exception {
        WXPay wxpay = createWXPayByRequest(null);
        WXRefundRequest wxrequest = (WXRefundRequest)request;
        Map<String, String> data = new HashMap<String, String>();
        data.put(QLWXPayConstants.OUT_TRADE_NO, wxrequest.getOrderId());
        data.put(QLWXPayConstants.REFUND_FEE, String.valueOf(MoneyUtil.Yuan2Fen(wxrequest.getRefundAmount())));
        data.put(QLWXPayConstants.TOTAL_FEE, String.valueOf(MoneyUtil.Yuan2Fen(wxrequest.getTotalFee())));
        data.put(QLWXPayConstants.OUT_REFUND_NO, String.valueOf(wxrequest.getOutRefundNo()));
        data.put(QLWXPayConstants.NOTIFY_URL, wxPayConfig.getNotifyUrl());
        Map<String, String> resp = wxpay.refund(data);

        if (!WXPayConstants.SUCCESS.equals(resp.get(QLWXPayConstants.RETURN_CODE))) {
            log.error("执行wxpay.refund -> resquest:{} response:{}", gson.toJson(wxrequest), gson.toJson(resp));
            return RefundResponse.builder().body(resp.get(QLWXPayConstants.RETURN_MSG)).success(false).build();
        }

        if (!WXPayConstants.SUCCESS.equals(resp.get(QLWXPayConstants.RESULT_CODE))) {
            log.error("执行wxpay.refund -> resquest:{} response:{}", gson.toJson(wxrequest), gson.toJson(resp));
            return RefundResponse.builder().body(resp.get(QLWXPayConstants.ERR_CODE_DES)).success(false).build();
        }

        log.debug("执行wxpay.refund -> resquest:{} response:{}", gson.toJson(wxrequest), gson.toJson(resp));
        return RefundResponse.builder().orderId(resp.get(QLWXPayConstants.OUT_TRADE_NO))
            .outTradeNo(QLWXPayConstants.TRANSACTION_ID).success(true).build();
    }

    @Override
    public OrderQueryResponse query(OrderQueryRequest request) throws Exception {
        WXPay wxpay = createWXPayByRequest(null);
        Map<String, String> data = new HashMap<String, String>();
        data.put(QLWXPayConstants.OUT_TRADE_NO, request.getOrderId());
        Map<String, String> resp = wxpay.orderQuery(data);

        if (!WXPayConstants.SUCCESS.equals(resp.get(QLWXPayConstants.RETURN_CODE))) {
            log.error("执行wxpay.query -> resquest:{} response:{}", gson.toJson(request), gson.toJson(resp));
            return OrderQueryResponse.builder().orderStatusEnum(OrderStatus.PAYERROR)
                .resultMsg(resp.get(QLWXPayConstants.RETURN_MSG)).build();
        }

        if (!WXPayConstants.SUCCESS.equals(resp.get(QLWXPayConstants.RESULT_CODE))) {
            log.error("执行wxpay.query -> resquest:{} response:{}", gson.toJson(request), gson.toJson(resp));
            return OrderQueryResponse.builder().orderStatusEnum(OrderStatus.PAYERROR)
                .resultMsg(resp.get(QLWXPayConstants.ERR_CODE_DES)).build();
        }

        log.debug("执行wxpay.query -> resquest:{} response:{}", gson.toJson(request), gson.toJson(resp));
        return OrderQueryResponse.builder().orderId(resp.get(QLWXPayConstants.OUT_TRADE_NO))
            .finishTime(resp.get(QLWXPayConstants.TIME_END)).outTradeNo(QLWXPayConstants.TRANSACTION_ID)
            .orderStatusEnum(OrderStatus.findByName(resp.get(QLWXPayConstants.TRADE_STATE))).build();
    }

    @Override
    public String downloadBill(String billDate) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(QLWXPayConstants.BILL_DATE, billDate);
        data.put(QLWXPayConstants.BILL_TYPE, QLWXPayConstants.ALL);
        try {
            WXPay wxpay = createWXPayByRequest(null);
            Map<String, String> resp = wxpay.downloadBill(data);
            log.debug("执行wxpay.downloadBill -> resquest:{} response:{}", billDate, gson.toJson(resp));
            return resp.toString();
        } catch (Exception e) {
            log.error("执行wxpay.downloadBill出错", e);
        }

        return null;
    }

    @Override
    public CloseResponse close(CloseRequest request) throws Exception {
        Map<String, String> data = new HashMap<String, String>();
        data.put(QLWXPayConstants.OUT_TRADE_NO, request.getOrderId());
        WXPay wxpay = createWXPayByRequest(null);
        Map<String, String> resp = wxpay.closeOrder(data);

        if (!WXPayConstants.SUCCESS.equals(resp.get(QLWXPayConstants.RETURN_CODE))) {
            log.error("执行wxpay.close -> resquest:{} response:{}", gson.toJson(request), gson.toJson(resp));
            return CloseResponse.builder().msg(resp.get(QLWXPayConstants.RETURN_MSG)).build();
        }

        if (!WXPayConstants.SUCCESS.equals(resp.get(QLWXPayConstants.RESULT_CODE))) {
            log.error("执行wxpay.close -> resquest:{} response:{}", gson.toJson(request), gson.toJson(resp));
            return CloseResponse.builder().msg(resp.get(QLWXPayConstants.ERR_CODE_DES)).build();
        }

        log.debug("执行wxpay.close -> resquest:{} response:{}", gson.toJson(request), gson.toJson(resp));
        return CloseResponse.builder().msg(resp.get(QLWXPayConstants.RESULT_CODE)).orderId(request.getOrderId())
            .build();
    }

    @Override
    public CancelResponse cancel(CancelRequest cancelRequest) throws Exception {
        return CancelResponse.builder().resultMsg("微信不支持取消订单").build();
    }

    public String getQrCodeUrl(String productId) {
        String appid = wxPayConfig.getAppid();
        String mch_id = wxPayConfig.getMchID();
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = WXPayUtil.generateNonceStr();

        // 先构造要签名的map
        Map<String, String> map = new HashMap<>();
        map.put("appid", appid);
        map.put("mch_id", mch_id);
        map.put("product_id", productId);
        map.put("time_stamp", timeStamp);
        map.put("nonce_str", nonceStr);

        try {
            return "weixin://wxpay/bizpayurl?" + "appid=" + appid + "&mch_id=" + mch_id + "&product_id=" + productId
                + "&time_stamp=" + timeStamp + "&nonce_str=" + nonceStr + "&sign="
                + WXPayUtil.generateSignature(map, wxPayConfig.getPrivateKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> decodeRefundInfo(String xml)
        throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
        BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, Exception {
        return com.qlteacher.pay.wx.util.WXPayUtil.wxInfoDecrypt(wxPayConfig.getPrivateKey(), xml);
    }

}
