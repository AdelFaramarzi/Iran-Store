
package com.iranstore.store.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class AddToCartResponse {

    @SerializedName("count")
    private Long mCount;
    @SerializedName("id")
    private Long mId;
    @SerializedName("product_id")
    private Long mProductId;

    public Long getCount() {
        return mCount;
    }

    public void setCount(Long count) {
        mCount = count;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public Long getProductId() {
        return mProductId;
    }

    public void setProductId(Long productId) {
        mProductId = productId;
    }

}
