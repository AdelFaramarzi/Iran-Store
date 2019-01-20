package com.iranstore.store.list;

import com.iranstore.store.model.Product;
import com.iranstore.store.model.api.ApiService;
import com.iranstore.store.providers.ApiServiceProvider;

import java.util.List;

import io.reactivex.Single;
public class ProductListViewModel {
    private ApiService apiService = ApiServiceProvider.provideApiService();

    public Single<List<Product>> products(Integer sortType) {
        return apiService.getProducts(sortType);
    }
}
