
package com.iranstore.store.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class CartItem {

    @SerializedName("cart_item_id")
    private Long cartItemId;
    @Expose
    private int count;
    @Expose
    private Product product;
    private boolean isRemoving;
    private boolean isChangingCount;

    public Long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isRemoving() {
        return isRemoving;
    }

    public void setRemoving(boolean removing) {
        isRemoving = removing;
    }

    public boolean isChangingCount() {
        return isChangingCount;
    }

    public void setChangingCount(boolean changingCount) {
        isChangingCount = changingCount;
    }
}
