package com.iranstore.store.auth;

import android.content.Context;
import android.content.SharedPreferences;

import io.reactivex.annotations.Nullable;


public class UserInfoManager {
    private SharedPreferences sharedPreferences;

    public UserInfoManager(Context context) {
        sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
    }

    public void saveToken(String token, String refereshToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putString("refresh_token", refereshToken);
        editor.apply();
    }

    @Nullable
    public String token() {
        return sharedPreferences.getString("token", null);
    }

    public void saveEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    public String getEmail() {
        return sharedPreferences.getString("email", "");
    }

    @Nullable
    public String refreshToken() {
        return sharedPreferences.getString("refresh_token", null);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
