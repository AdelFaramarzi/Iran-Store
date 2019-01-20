
package com.iranstore.store.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class CartItemCountResponse {

    @SerializedName("count")
    private int mCount;

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

}
