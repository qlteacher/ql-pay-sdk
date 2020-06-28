package com.qlteacher.pay.demo.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.wxpay.sdk.WXPayUtil;
import com.qlteacher.pay.alipay.api.AliPayConfig;
import com.qlteacher.pay.alipay.api.AliPayServiceImpl;
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
import com.qlteacher.pay.common.enums.PayType;
import com.qlteacher.pay.utils.CurrentIP;
import com.qlteacher.pay.wx.api.WxPayConfig;
import com.qlteacher.pay.wx.api.WxPayServiceImpl;
import com.qlteacher.pay.wx.api.request.WXRefundRequest;
import com.qlteacher.pay.wx.constants.QLWXPayConstants;

import lombok.extern.log4j.Log4j2;

/**
 * 支付相关
 */
@Controller
@Log4j2
@SuppressWarnings("unused")
public class PayController {

    @Autowired
    private AliPayServiceImpl aliPayServiceImpl;

    @Autowired
    private WxPayServiceImpl wxPayServiceImpl;

    @Autowired
    private AliPayConfig aliconfig;
    
    @Autowired
    private WxPayConfig wxconfig;

    /**
     * 发起支付
     * 
     * @throws Exception
     */
    @PostMapping(value = "/pay")
    @ResponseBody
    public PayResponse pay(@RequestParam(value = "openid", required = false) String openid, // 微信openid, 仅微信公众号/小程序支付时需要
        @RequestParam PayType payType, @RequestParam String orderId, @RequestParam BigDecimal price,
        @RequestParam(required = false) String buyerLogonId, @RequestParam(required = false) String buyerId,HttpServletRequest httprequest)
        throws Exception {
        // 支付请求参数
        PayRequest request =
            PayRequest.builder().payType(payType).orderId(orderId).price(price).tradeName("测试qlpaysdk").build();
        request.addAttr("openid", openid);

        if (payType == PayType.ALIPAY_H5) {
            request.addAttr("buyerLogonId", buyerLogonId);
            request.addAttr("buyerId", buyerId);
        }

        if (payType.getPlatform().equals(PayPlatform.ALIPAY)) {
            PayResponse payResponse = aliPayServiceImpl.pay(request);
            return payResponse;
        } else if (payType.getPlatform().equals(PayPlatform.WXPAY)) {
            request.addAttr(QLWXPayConstants.SPBILL_CREATE_IP, CurrentIP.getIpAddr(httprequest));
            PayResponse payResponse = wxPayServiceImpl.pay(request);
            return payResponse;
        }
        return null;
    }

    @GetMapping("/query")
    @ResponseBody
    public OrderQueryResponse query(@RequestParam String orderId, @RequestParam("platform") PayPlatform platform) {
        OrderQueryRequest orderQueryRequest = new OrderQueryRequest();
        orderQueryRequest.setOrderId(orderId);
        orderQueryRequest.setPlatform(platform);

        if (platform.equals(PayPlatform.ALIPAY)) {
            OrderQueryResponse payResponse = null;
            try {
                payResponse = aliPayServiceImpl.query(orderQueryRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return payResponse;
        } else if (platform.equals(PayPlatform.WXPAY)) {
            try {
                return wxPayServiceImpl.query(orderQueryRequest);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                 e.printStackTrace();
            }
        }

        return null;
    }

    @GetMapping("/refund")
    @ResponseBody
    public RefundResponse refund(@RequestParam String orderId, @RequestParam BigDecimal price,
        @RequestParam("platform") PayPlatform platform,
        @RequestParam(required = false) BigDecimal totalFee,@RequestParam(required = false) String outRefundNo) {
        
        
        if (platform.equals(PayPlatform.ALIPAY)) {
            try {
                RefundRequest request = new RefundRequest();
                request.setOrderId(orderId);
                request.setPlatform(platform);
                request.setRefundAmount(price);
                return aliPayServiceImpl.refund(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (platform.equals(PayPlatform.WXPAY)) {
            try {
                WXRefundRequest request = new WXRefundRequest();
                request.setOrderId(orderId);
                request.setPlatform(platform);
                request.setRefundAmount(price);
                request.setTotalFee(totalFee);
                request.setOutRefundNo(outRefundNo);
                return wxPayServiceImpl.refund(request);
            } catch (Exception e) {
                 e.printStackTrace();
            }
        }

        return null;
    }

    @GetMapping("/cancel")
    @ResponseBody
    public CancelResponse cancel(@RequestParam String orderId, @RequestParam BigDecimal price,
        @RequestParam("platform") PayPlatform platform) {
        CancelRequest request = new CancelRequest();
        request.setOrderId(orderId);
        request.setPlatform(platform);

        if (platform.equals(PayPlatform.ALIPAY)) {
            CancelResponse payResponse = null;
            try {
                payResponse = aliPayServiceImpl.cancel(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return payResponse;
        } else if (platform.equals(PayPlatform.WXPAY)) {
            try {
                return wxPayServiceImpl.cancel(request);
            } catch (Exception e) {
                 e.printStackTrace();
            }
        }

        return null;
    }

    @GetMapping("/close")
    @ResponseBody
    public CloseResponse close(@RequestParam String orderId, @RequestParam("platform") PayPlatform platform) {
        CloseRequest request = new CloseRequest();
        request.setOrderId(orderId);
        request.setPlatform(platform);

        if (platform.equals(PayPlatform.ALIPAY)) {
            CloseResponse payResponse = null;
            try {
                payResponse = aliPayServiceImpl.close(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return payResponse;
        } else if (platform.equals(PayPlatform.WXPAY)) {
            try {
                return wxPayServiceImpl.close(request);
            } catch (Exception e) {
                 e.printStackTrace();
            }
        }

        return null;
    }

    @GetMapping("/downloadBill")
    @ResponseBody
    public String downloadBill(@RequestParam String billDate, @RequestParam("platform") PayPlatform platform) {

        if (platform.equals(PayPlatform.ALIPAY)) {
            String downloadurl = null;
            try {
                downloadurl = aliPayServiceImpl.downloadBill(billDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return downloadurl;
        } else if (platform.equals(PayPlatform.WXPAY)) {
            return wxPayServiceImpl.downloadBill(billDate.replace("-", ""));
        }

        return null;
    }

    @RequestMapping(value = "/notify")
    @ResponseBody
    public String certNotifyUrl(@RequestBody String notifyData) throws Exception {
        log.info("【异步通知】支付平台的数据request={}", notifyData);
        boolean verifyResult = false;
        Map<String, String> params;

        // <xml>开头的是微信通知
        if (notifyData.startsWith("<xml>")) {
            // 获取微信POST过来反馈信息
            params = WXPayUtil.xmlToMap(notifyData); 
            verifyResult = wxPayServiceImpl.verify(params);
        } else {
            // 获取支付宝POST过来反馈信息
            params = form2Map(notifyData);
            verifyResult = aliPayServiceImpl.verify(params);
        }
        try {

            for (Map.Entry<String, String> entry : params.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }

            if (verifyResult) {
                // TODO 请在这里加上商户的业务逻辑程序代码 异步通知可能出现订单重复通知 需要做去重处理
                System.out.println("certNotifyUrl 验证成功succcess");
                return "success";
            } else {
                System.out.println("certNotifyUrl 验证失败");
                // TODO
                return "failure";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
    }

    @RequestMapping(value = "/return")
    @ResponseBody
    public String certReturnUrl(@RequestBody String notifyData) {
        try {
            // 获取支付宝POST过来反馈信息
            Map<String, String> params = form2Map(notifyData);

            for (Map.Entry<String, String> entry : params.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }

            boolean verifyResult = aliPayServiceImpl.verify(params);

            if (verifyResult) {
                // TODO 请在这里加上商户的业务逻辑程序代码 异步通知可能出现订单重复通知 需要做去重处理
                System.out.println("certreturnUrl 验证成功succcess");
                return "success";
            } else {
                System.out.println("certreturnUrl 验证失败");
                // TODO
                return "failure";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
    }
    
    /**
     * 微信h5支付，要求referer是白名单的地址，这里做个重定向
     * @param prepayId
     * @param packAge
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @GetMapping("/wxpay_mweb_redirect")
    public ModelAndView wxpayMweb(@RequestParam("prepay_id") String prepayId,
                                  @RequestParam("package") String packAge,
                                  Map map) {
        String url = String.format("https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=%s&package=%s", prepayId, packAge);
        map.put("url", url);
        return new ModelAndView("pay/wxpayMwebRedirect");
    }
    
    @RequestMapping(value = "/wxQrCode")
    @ResponseBody
    public String getQrCodeUrl(@RequestParam String orderId, @RequestParam("platform") PayPlatform platform) {
        return wxPayServiceImpl.getQrCodeUrl(orderId);
    }

    /**
     * 表单字符串转化成 hashMap
     * 
     * @param orderinfo
     * @return
     */
    public static HashMap<String, String> form2Map(String orderinfo) {
        String listinfo[];
        HashMap<String, String> map = new HashMap<String, String>();
        listinfo = orderinfo.split("&");
        for (String s : listinfo) {
            String list[] = s.split("=");
            if (list.length > 1) {
                map.put(list[0], list[1]);
            }
        }
        return map;
    }
    
    @RequestMapping(value = "/prepayIdCreateSign")
    @ResponseBody
    public Map<String, String> prepayIdCreateSign(@RequestParam String prepayId) {
       return wxPayServiceImpl.prepayIdCreateSign(prepayId, wxconfig.getAppid(), wxconfig.getPrivateKey(), wxconfig.getSignType());
    }


}
