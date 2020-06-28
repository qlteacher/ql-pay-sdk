package com.qlteacher.pay.demo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qlteacher.pay.alipay.api.AliPayConfig;
import com.qlteacher.pay.wx.api.WxPayConfig;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class IndexController {

    @Autowired
    private AliPayConfig aliconf;

    @Autowired
    private WxPayConfig wxiconf;

    @RequestMapping(value = "/")
    public ModelAndView index(HttpServletResponse response) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("aliconf", aliconf);
        params.put("wxiconf", wxiconf);
        return new ModelAndView("index", params);
    }

    @GetMapping("/paydemo")
    public ModelAndView pay(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("aliconf", aliconf);
        params.put("wxiconf", wxiconf);
        return new ModelAndView("index", params);
    }

    // 发送请求，根据code获取openId
    public String getOpenId(HttpServletRequest request, HttpServletResponse response, String code) {

        String content = "";
        String openId = "";
        String unionId = "";
        // 封装获取openId的微信API
        StringBuffer url = new StringBuffer();
        url.append("https://api.weixin.qq.com/sns/oauth2/access_token?appid=").append(wxiconf.getAppid())
            .append("&secret=").append(wxiconf.getPrivateKey()).append("&code=").append(code)
            .append("&grant_type=authorization_code");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(url.toString());
            content = EntityUtils.toString(httpClient.execute(get).getEntity(), "UTF-8");
            Map map = objectMapper.readValue(content, Map.class);
            openId = String.valueOf(map.get("openid"));
            unionId = String.valueOf(map.get("unionid"));

            log.info("openId=" + openId);
            log.info("unionId=" + unionId);

        } catch (Exception e) {
            log.error("json解析失败：", e);
        }
        return openId;
    }

}
