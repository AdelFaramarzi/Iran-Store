package com.iranstore.store.cart;

import com.google.gson.JsonObject;
import com.iranstore.store.model.AddToCartResponse;
import com.iranstore.store.model.api.ApiService;
import com.iranstore.store.providers.ApiServiceProvider;
import com.iranstore.store.model.CartItem;
import com.iranstore.store.model.CartModel;
import com.iranstore.store.model.SuccessResponse;

import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;

public class CartViewModel {
    private ApiService apiService = ApiServiceProvider.provideApiService();
    private BehaviorSubject<Boolean> progressBarVisibilitySubject = BehaviorSubject.create();

    public Single<CartModel> cart(boolean mustShowProgressBar) {
        if (mustShowProgressBar)
            progressBarVisibilitySubject.onNext(true);
        return apiService.getCart().doOnEvent((cartModel, throwable) -> progressBarVisibilitySubject.onNext(false));
    }

    public Single<SuccessResponse> removeCartItem(CartItem cartItem) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cart_item_id", cartItem.getCartItemId());
        return apiService.removeCartItem(jsonObject);
    }

    public Single<AddToCartResponse> changeCount(CartItem cartItem, int newCount) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cart_item_id", cartItem.getCartItemId());
        jsonObject.addProperty("count", newCount);
        return apiService.changeCartItemCount(jsonObject);
    }

    public BehaviorSubject<Boolean> getProgressBarVisibilitySubject() {
        return progressBarVisibilitySubject;
    }
}
