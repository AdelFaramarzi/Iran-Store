package com.iranstore.store.shipping;
public enum PaymentMethod {
    ONLINE_METHOD("online"), CASH_ON_DELIVERY("cash_on_delivery");

    private String value;

    PaymentMethod(String value) {

        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
