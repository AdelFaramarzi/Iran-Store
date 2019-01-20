
package com.iranstore.store.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class CheckoutResponse {

    @SerializedName("payable_price")
    private Long mPayablePrice;
    @SerializedName("payment_status")
    private String mPaymentStatus;
    @SerializedName("purchase_success")
    private Boolean mPurchaseSuccess;

    public Long getPayablePrice() {
        return mPayablePrice;
    }

    public void setPayablePrice(Long payablePrice) {
        mPayablePrice = payablePrice;
    }

    public String getPaymentStatus() {
        return mPaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        mPaymentStatus = paymentStatus;
    }

    public Boolean getPurchaseSuccess() {
        return mPurchaseSuccess;
    }

    public void setPurchaseSuccess(Boolean purchaseSuccess) {
        mPurchaseSuccess = purchaseSuccess;
    }

}
