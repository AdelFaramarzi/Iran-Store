package com.iranstore.store;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.iranstore.store.exception.UnAuthorizedException;
import com.iranstore.store.auth.AuthenticationActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
public abstract class BaseActivity extends AppCompatActivity {
    public abstract int getCoordinatorLayoutId();

    public void showSnackBarMessage(String message) {
        Snackbar.make(findViewById(getCoordinatorLayoutId()),
                message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void handleUnAuthorizedException(UnAuthorizedException unAuthorizedException) {
        startActivity(new Intent(this, AuthenticationActivity.class));
    }
}
