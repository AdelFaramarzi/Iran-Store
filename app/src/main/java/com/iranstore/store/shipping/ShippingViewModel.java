package com.iranstore.store.shipping;

import com.google.gson.JsonObject;
import com.iranstore.store.model.OrderSubmitResponse;
import com.iranstore.store.model.api.ApiService;
import com.iranstore.store.providers.ApiServiceProvider;

import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
public class ShippingViewModel {
    private ApiService apiService = ApiServiceProvider.provideApiService();
    private BehaviorSubject<Boolean> progressBarVisibilitySubject = BehaviorSubject.create();

    public BehaviorSubject<Boolean> getProgressBarVisibilitySubject() {
        return progressBarVisibilitySubject;
    }

    public Single<OrderSubmitResponse> orderSubmit(String firstName,
                                                   String lastName,
                                                   String postalCode,
                                                   String phoneNumber,
                                                   String address,
                                                   PaymentMethod paymentMethod
    ) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("first_name", firstName);
        jsonObject.addProperty("last_name", lastName);
        jsonObject.addProperty("postal_code", postalCode);
        jsonObject.addProperty("mobile", phoneNumber);
        jsonObject.addProperty("address", address);
        jsonObject.addProperty("payment_method", paymentMethod.getValue());
        progressBarVisibilitySubject.onNext(true);
        return apiService.orderSubmit(jsonObject).doOnEvent((orderSubmitResponse, throwable) ->
                progressBarVisibilitySubject.onNext(false));
    }
}
