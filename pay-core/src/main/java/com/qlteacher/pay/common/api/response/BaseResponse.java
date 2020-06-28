 package com.qlteacher.pay.common.api.response;

import com.qlteacher.pay.common.enums.PayPlatform;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseResponse {
     
     private PayPlatform payPlatform;

}
