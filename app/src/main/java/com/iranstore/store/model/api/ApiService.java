package com.iranstore.store.model.api;

import com.google.gson.JsonObject;
import com.iranstore.store.model.AddToCartResponse;
import com.iranstore.store.model.Banner;
import com.iranstore.store.model.Comment;
import com.iranstore.store.model.Order;
import com.iranstore.store.model.OrderSubmitResponse;
import com.iranstore.store.model.Product;
import com.iranstore.store.model.Token;
import com.iranstore.store.model.CartItemCountResponse;
import com.iranstore.store.model.CartModel;
import com.iranstore.store.model.CheckoutResponse;
import com.iranstore.store.model.SuccessResponse;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Saeed Shahini on 6/5/2018.
 */
public interface ApiService {
    @GET("product/list")
    Single<List<Product>> getProducts(
            @Query("sort") Integer sort
    );

    @GET("banner/slider")
    Single<List<Banner>> getBanners();

    @FormUrlEncoded
    @POST("auth/token")
    Single<Token> getToken(@Field("grant_type") String grantType,
                           @Field("client_id") Integer clientId,
                           @Field("client_secret") String clientSecret,
                           @Field("username") String email,
                           @Field("password") String password);

    @FormUrlEncoded
    @POST("user/register")
    Single<SuccessResponse> registerUser(@Field("email") String email,
                                         @Field("password") String password);

    @GET("comment/list")
    Single<List<Comment>> getComments(@Query("product_id") int productId);

    @POST("comment/add")
    Single<Comment> addComment(@Body JsonObject jsonObject);

    @GET("cart/list")
    Single<CartModel> getCart();

    @POST("cart/add")
    Single<AddToCartResponse> addToCart(@Body JsonObject jsonObject);

    @POST("cart/changeCount")
    Single<AddToCartResponse> changeCartItemCount(@Body JsonObject jsonObject);

    @POST("cart/remove")
    Single<SuccessResponse> removeCartItem(@Body JsonObject jsonObject);

    @GET("cart/count")
    Single<CartItemCountResponse> getCartItemCount();

    @POST("order/submit")
    Single<OrderSubmitResponse> orderSubmit(@Body JsonObject jsonObject);

    @GET("order/checkout")
    Single<CheckoutResponse> checkout(@Query("order_id") String orderId);

    @GET("order/list")
    Single<List<Order>> listOrders();
}
