package com.iranstore.store.model.api;

import com.iranstore.store.BaseActivity;
import com.iranstore.store.exception.ExceptionMessageFactory;

import io.reactivex.CompletableObserver;


public abstract class SsCompeletableObserver implements CompletableObserver {
    private BaseActivity activity;

    public SsCompeletableObserver(BaseActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onError(Throwable e) {
        activity.showSnackBarMessage(ExceptionMessageFactory.getMessage(e));
    }
}
