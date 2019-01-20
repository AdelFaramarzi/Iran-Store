package com.iranstore.store.details;

import com.google.gson.JsonObject;
import com.iranstore.store.model.AddToCartResponse;
import com.iranstore.store.model.Comment;
import com.iranstore.store.model.api.ApiService;
import com.iranstore.store.providers.ApiServiceProvider;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;


public class ProductDetailsViewModel {
    private ApiService apiService = ApiServiceProvider.provideApiService();
    private BehaviorSubject<Boolean> progressBarVisibilitySubject = BehaviorSubject.create();


    public Single<List<Comment>> comments(int productId) {
        progressBarVisibilitySubject.onNext(true);
        return apiService.getComments(productId).doOnEvent((comments, throwable) -> progressBarVisibilitySubject.onNext(false));
    }

    public Single<AddToCartResponse> addToCart(int productId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("product_id", productId);
        return apiService.addToCart(jsonObject);
    }

    public BehaviorSubject<Boolean> progressBarVisibility() {
        return progressBarVisibilitySubject;
    }
}
