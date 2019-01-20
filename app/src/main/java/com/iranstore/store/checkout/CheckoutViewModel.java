package com.iranstore.store.checkout;

import com.iranstore.store.model.api.ApiService;
import com.iranstore.store.model.CheckoutResponse;
import com.iranstore.store.providers.ApiServiceProvider;

import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;


public class CheckoutViewModel {
    private ApiService apiService = ApiServiceProvider.provideApiService();
    private BehaviorSubject<Boolean> progressBarVisibilitySubject = BehaviorSubject.create();

    public Single<CheckoutResponse> checkout(String orderId) {
        progressBarVisibilitySubject.onNext(true);
        return apiService.checkout(orderId).doOnEvent((checkoutResponse, throwable) -> progressBarVisibilitySubject.onNext(false));
    }

    public BehaviorSubject<Boolean> getProgressBarVisibilitySubject() {
        return progressBarVisibilitySubject;
    }
}
