package com.iranstore.store.model.api;

import com.iranstore.store.BaseActivity;
import com.iranstore.store.exception.ExceptionMessageFactory;

import io.reactivex.SingleObserver;

public abstract class SsSingleObserver<T> implements SingleObserver<T> {
    private BaseActivity activity;

    public SsSingleObserver(BaseActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onError(Throwable e) {
        activity.showSnackBarMessage(ExceptionMessageFactory.getMessage(e));

    }
}
