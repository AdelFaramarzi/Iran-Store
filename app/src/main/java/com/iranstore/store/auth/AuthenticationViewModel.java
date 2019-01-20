package com.iranstore.store.auth;

import android.content.Context;

import com.iranstore.store.model.Token;
import com.iranstore.store.model.api.ApiService;
import com.iranstore.store.providers.ApiServiceProvider;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;


public class AuthenticationViewModel {
    private ApiService apiService = ApiServiceProvider.provideApiService();
    private BehaviorSubject<Boolean> isInLoginMode = BehaviorSubject.create();
    private BehaviorSubject<Boolean> progressBarVisiblitySubject = BehaviorSubject.create();

    public Completable authenticate(Context context, String email, String password) {
        progressBarVisiblitySubject.onNext(true);
        Single<Token> singleToken;
        if (isInLoginMode.getValue() == null || isInLoginMode.getValue()) {
            singleToken = apiService.getToken("password", 2, "BUw3JdAUclVOFBvBVldechJrovignWG2Jjo0DPG7",
                    email, password);
        } else {
            singleToken = apiService.registerUser(email, password)
                    .flatMap(successResponse -> apiService.getToken("password",
                            2, "BUw3JdAUclVOFBvBVldechJrovignWG2Jjo0DPG7",
                            email, password));
        }
        final UserInfoManager userInfoManager = new UserInfoManager(context);
        return singleToken.doOnSuccess(token -> {
            userInfoManager.saveToken(token.getAccessToken(), token.getRefreshToken());
            userInfoManager.saveEmail(email);
            TokenContainer.updateToken(token.getAccessToken());
        })
                .doOnEvent((token, throwable) -> progressBarVisiblitySubject.onNext(false))
                .toCompletable();
    }

    public void onChangeAuthenticationModeButtonClick() {
        isInLoginMode.onNext(isInLoginMode.getValue() != null && !isInLoginMode.getValue());
    }

    public BehaviorSubject<Boolean> getIsInLoginMode() {
        return isInLoginMode;
    }

    public BehaviorSubject<Boolean> getProgressBarVisiblitySubject() {
        return progressBarVisiblitySubject;
    }
}
