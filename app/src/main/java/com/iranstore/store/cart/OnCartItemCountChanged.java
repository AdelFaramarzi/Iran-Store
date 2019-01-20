package com.iranstore.store.cart;


public class OnCartItemCountChanged {
    private int count;

    public OnCartItemCountChanged(int count){

        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
