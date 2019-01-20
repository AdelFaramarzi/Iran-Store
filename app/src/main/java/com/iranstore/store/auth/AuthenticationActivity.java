package com.iranstore.store.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iranstore.store.BaseActivity;
import com.iranstore.store.model.api.SsCompeletableObserver;
import com.iranstore.store.utils.KeyboardUtil;
import com.ss.sevenstore.R;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AuthenticationActivity extends BaseActivity {
    private EditText emailET;
    private EditText passwordEt;
    private Button authenticationButton;
    private TextView changeAuthenticationMode;
    private AuthenticationViewModel viewModel;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        viewModel = new AuthenticationViewModel();
        setupViews();
        observe();
    }

    private void observe() {
        Disposable isInLoginModeDisposable = viewModel.getIsInLoginMode().subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isInLoginMode -> {
                    if (isInLoginMode) {
                        changeAuthenticationMode.setText("ثبت نام");
                        authenticationButton.setText("ورود به حساب کاربری");
                    } else {
                        changeAuthenticationMode.setText("ورود به حساب کاربری");
                        authenticationButton.setText("ثبت نام");
                    }
                });

        Disposable progressBarDisposable = viewModel.getProgressBarVisiblitySubject().subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shouldShowProgressBar -> progressBar.setVisibility(shouldShowProgressBar ? View.VISIBLE : View.GONE));

        compositeDisposable.add(isInLoginModeDisposable);
        compositeDisposable.add(progressBarDisposable);


    }

    private void setupViews() {
        emailET = findViewById(R.id.et_auth_email);
        passwordEt = findViewById(R.id.et_auth_password);
        progressBar = findViewById(R.id.ll_auth_progressBar);
        changeAuthenticationMode = findViewById(R.id.tv_auth_changeAuthenticationMode);
        changeAuthenticationMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.onChangeAuthenticationModeButtonClick();
            }
        });

        authenticationButton = findViewById(R.id.button_auth_authentication);
        authenticationButton.setOnClickListener(view -> {
            KeyboardUtil.closeKeyboard(getCurrentFocus());
            viewModel.authenticate(AuthenticationActivity.this, emailET.getText().toString(),
                    passwordEt.getText().toString()).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SsCompeletableObserver(this) {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onComplete() {
                            EventBus.getDefault().post(new OnUserAuthenticate());
                            Toast.makeText(AuthenticationActivity.this, "خوش آمدید", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public int getCoordinatorLayoutId() {
        return R.id.relativeLayout_auth_root;
    }
}
