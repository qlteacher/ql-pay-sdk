package com.qlteacher.pay.wx.util;

import com.qlteacher.pay.wx.api.WxPayConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class MINIWXPayConfig extends NativeWXPayConfig {
    @Override
    public String getAppID() {
        return super.getWxPayConfig().getMiniAppId();
    }

    public MINIWXPayConfig(WxPayConfig wxPayConfig) {
        super(wxPayConfig);
    }
}
