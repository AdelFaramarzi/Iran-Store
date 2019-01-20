package com.iranstore.store;

import android.app.Application;

import com.iranstore.store.auth.TokenContainer;
import com.iranstore.store.auth.UserInfoManager;
public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        TokenContainer.updateToken(new UserInfoManager(this).token());
    }
}
