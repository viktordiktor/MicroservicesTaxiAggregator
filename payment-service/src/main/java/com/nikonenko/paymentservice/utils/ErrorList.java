package com.nikonenko.paymentservice.utils;

import lombok.AllArgsConstructor;
import java.util.ResourceBundle;

@AllArgsConstructor
public enum ErrorList {
    CUSTOMER_ALREADY_EXISTS("customer-already-exists"),
    INSUFFICIENT_FUNDS("insufficient-funds"),
    CUSTOMER_NOT_FOUND("customer-not-found"),
    EXPIRED_COUPON("expired-coupon"),
    CONFIRM_INTENT_FAILED("confirm.intent.failed"),
    CREATE_CHARGE_FAILED("create.charge.failed"),
    CREATE_CUSTOMER_FAILED("create.customer.failed"),
    CREATE_INTENT_FAILED("create.intent.failed"),
    CREATE_PAYMENT_FAILED("create.payment.failed"),
    CREATE_TOKEN_FAILED("create.token.failed"),
    CREATE_COUPON_FAILED("create.coupon.failed"),
    RETRIEVE_BALANCE_FAILED("retrieve.balance.failed"),
    RETRIEVE_CUSTOMER_FAILED("retrieve.customer.failed"),
    RETRIEVE_CHARGE_FAILED("retrieve.charge.failed"),
    RETRIEVE_COUPON_FAILED("retrieve.coupon.failed"),
    RETRIEVE_INTENT_FAILED("retrieve.intent.failed"),
    UPDATE_CUSTOMER_FAILED("update.customer.failed");

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("exceptions");
    private final String key;

    public String getValue() {
        return resourceBundle.getString(this.key);
    }
}
