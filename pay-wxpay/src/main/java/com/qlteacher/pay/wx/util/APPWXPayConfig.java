package com.qlteacher.pay.wx.util;

import com.qlteacher.pay.wx.api.WxPayConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class APPWXPayConfig extends NativeWXPayConfig {

    @Override
    public String getAppID() {
        return super.getWxPayConfig().getAppAppId();
    }

    public APPWXPayConfig(WxPayConfig wxPayConfig) {
        super(wxPayConfig);
    }

}
