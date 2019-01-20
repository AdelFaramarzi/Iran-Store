package com.iranstore.store.main;

import com.iranstore.store.model.Banner;
import com.iranstore.store.model.Product;
import com.iranstore.store.model.api.ApiService;
import com.iranstore.store.providers.ApiServiceProvider;
import com.iranstore.store.model.CartItemCountResponse;

import java.util.List;

import io.reactivex.Single;


public class MainViewModel {
    private ApiService apiService = ApiServiceProvider.provideApiService();

    public Single<List<Product>> latestProducts() {
        return apiService.getProducts(Product.SORT_LATEST);
    }

    public Single<List<Product>> popularProducts() {
        return apiService.getProducts(Product.SORT_POPULAR);
    }

    public Single<List<Banner>> banners() {
        return apiService.getBanners();
    }

    public Single<CartItemCountResponse> getCartItemCount() {
        return apiService.getCartItemCount();
    }
}
