package com.iranstore.store.orders;

import com.iranstore.store.model.Order;
import com.iranstore.store.model.api.ApiService;
import com.iranstore.store.providers.ApiServiceProvider;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
public class OrderHistoryViewModel {
    private ApiService apiService = ApiServiceProvider.provideApiService();
    private BehaviorSubject<Boolean> progressBarVisibilitySubject = BehaviorSubject.create();


    public Single<List<Order>> listOrders() {
        progressBarVisibilitySubject.onNext(true);
        return apiService.listOrders().doOnEvent((orders, throwable) -> progressBarVisibilitySubject.onNext(false));
    }

    public BehaviorSubject<Boolean> getProgressBarVisibilitySubject() {
        return progressBarVisibilitySubject;
    }
}
