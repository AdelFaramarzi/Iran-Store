package com.iranstore.store.model.api;

import com.iranstore.store.auth.TokenContainer;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request oldRequest = chain.request();

                            Request.Builder newRequestBuilder = oldRequest.newBuilder();
                            if (TokenContainer.getToken() != null) {
                                newRequestBuilder.addHeader("Authorization", "Bearer " +
                                        TokenContainer.getToken());
                            }
                            newRequestBuilder.addHeader("Accept", "application/json");
                            newRequestBuilder.method(oldRequest.method(), oldRequest.body());
                            return chain.proceed(newRequestBuilder.build());
                        }
                    })
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://expertdevelopers.ir/api/v1/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }

    private RetrofitSingleton() {

    }
}
