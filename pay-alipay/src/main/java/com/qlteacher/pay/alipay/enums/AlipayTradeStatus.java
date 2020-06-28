package com.qlteacher.pay.alipay.enums;

import static com.qlteacher.pay.common.enums.OrderStatus.CLOSED;
import static com.qlteacher.pay.common.enums.OrderStatus.NOTPAY;
import static com.qlteacher.pay.common.enums.OrderStatus.SUCCESS;

import com.qlteacher.pay.common.enums.OrderStatus;

import lombok.Getter;


@Getter
public enum AlipayTradeStatus {

    /** 交易创建，等待买家付款。 */
    WAIT_BUYER_PAY(NOTPAY),

    /**
     * <pre>
     * 在指定时间段内未支付时关闭的交易；
     * 在交易完成全额退款成功时关闭的交易。
     * </pre>
     */
    TRADE_CLOSED(CLOSED),

    /** 交易成功，且可对该交易做操作，如：多级分润、退款等。 */
    TRADE_SUCCESS(SUCCESS),

    /** 等待卖家收款（买家付款后，如果卖家账号被冻结）。 */
    TRADE_PENDING(NOTPAY),

    /** 交易成功且结束，即不可再做任何操作。 */
    TRADE_FINISHED(SUCCESS),

    ;

    private OrderStatus orderStatus;

    AlipayTradeStatus(OrderStatus orderStatusEnum) {
        this.orderStatus = orderStatusEnum;
    }

    public static AlipayTradeStatus findByName(String name) {
        for (AlipayTradeStatus statusEnum : AlipayTradeStatus.values()) {
            if (statusEnum.name().equalsIgnoreCase(name)) {
                return statusEnum;
            }
        }
        throw new RuntimeException("错误的支付宝支付状态");
    }
}