package com.iranstore.store.checkout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iranstore.store.model.api.SsSingleObserver;
import com.iranstore.store.orders.OrderHistoryActivity;
import com.iranstore.store.utils.PriceConverter;
import com.iranstore.store.BaseActivity;
import com.ss.sevenstore.R;
import com.iranstore.store.model.CheckoutResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CheckoutActivity extends BaseActivity {
    private CheckoutViewModel viewModel = new CheckoutViewModel();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ProgressBar progressBar;

    @Override
    public int getCoordinatorLayoutId() {
        return R.id.coordinator_checkout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        setupViews();
        Uri uri = getIntent().getData();
        String orderId;
        if (uri != null) {
            orderId = uri.getQueryParameter("order_id");
        } else {
            orderId = getIntent().getStringExtra("order_id");
        }

        viewModel.checkout(orderId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SsSingleObserver<CheckoutResponse>(this) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(CheckoutResponse checkoutResponse) {
                        showData(checkoutResponse);
                    }
                });

        Disposable disposable = viewModel.getProgressBarVisibilitySubject()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> progressBar.setVisibility(aBoolean ? View.VISIBLE : View.GONE));
        compositeDisposable.add(disposable);
    }

    private void setupViews() {
        progressBar = findViewById(R.id.progressBar_checkout);
    }

    private void showData(CheckoutResponse checkoutResponse) {
        Button goHome = findViewById(R.id.btn_checkout_goHome);
        Button goOrderHistory = findViewById(R.id.btn_checkout_orderHistory);
        goOrderHistory.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, OrderHistoryActivity.class));
        });

        TextView purchaseStatusTv = findViewById(R.id.tv_checkout_message);
        purchaseStatusTv.setText(checkoutResponse.getPurchaseSuccess() ? "خرید با موفقیت انجام شد" : "خرید ناموفق");

        TextView paymentStatusTv = findViewById(R.id.tv_checkout_paymentStatus);
        paymentStatusTv.setText(checkoutResponse.getPaymentStatus());

        TextView payablePriceTv = findViewById(R.id.tv_checkout_payable);
        payablePriceTv.setText(PriceConverter.convert(checkoutResponse.getPayablePrice()));

        goHome.setOnClickListener(v -> finish());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
