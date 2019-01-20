package com.iranstore.store.providers;

import com.iranstore.store.model.api.ApiService;
import com.iranstore.store.model.api.RetrofitSingleton;
public class ApiServiceProvider {
    private static ApiService apiService;

    public static ApiService provideApiService() {
        if (apiService == null) {
            apiService = RetrofitSingleton.getInstance().create(ApiService.class);
        }
        return apiService;
    }
}
