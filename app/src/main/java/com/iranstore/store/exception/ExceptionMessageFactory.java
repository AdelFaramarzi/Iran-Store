package com.iranstore.store.exception;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import retrofit2.HttpException;

public class ExceptionMessageFactory {
    public static String getMessage(Throwable throwable) {
        if (throwable instanceof HttpException) {
            switch (((HttpException) throwable).code()) {
                case 401:
                case 403:
                    EventBus.getDefault().post(new UnAuthorizedException());
                case 400:
                case 422:
                    Gson gson = new Gson();
                    try {
                        SevenStoreHttpException exception = gson.fromJson(((HttpException) throwable).response().errorBody().string(), SevenStoreHttpException.class);
                        return exception.getMessage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    return "اختلال در دریافت اطلاعات";
            }
        }

        return "خطای نامشخص";
    }

    private ExceptionMessageFactory() {

    }
}
