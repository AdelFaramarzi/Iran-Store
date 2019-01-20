package com.iranstore.store.cart;

public class CartItemCountContainer {
    private static int count;

    public static void updateCount(int count) {
        CartItemCountContainer.count = count;
    }

    public static int getCount() {
        return count;
    }
}
