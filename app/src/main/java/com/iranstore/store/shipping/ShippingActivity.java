package com.iranstore.store.shipping;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.iranstore.store.checkout.CheckoutActivity;
import com.iranstore.store.model.OrderSubmitResponse;
import com.iranstore.store.model.api.SsSingleObserver;
import com.iranstore.store.BaseActivity;
import com.ss.sevenstore.R;
import com.iranstore.store.cart.CartItemAdapter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ShippingActivity extends BaseActivity {

    private ShippingViewModel shippingViewModel = new ShippingViewModel();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private EditText firstNameEt;
    private EditText lastNameEt;
    private EditText postalCodeEt;
    private EditText phoneNumberEt;
    private EditText addressEt;

    private Button onlinePaymentBtn;
    private Button cashOnDeliveryBtn;
    private ProgressBar progressBar;
    private View paymentButtonContainer;

    private long totalPrice;
    private long payablePrice;
    private long shippingCost;

    public static final String EXTRA_KEY_PAYABLE = "payable";
    public static final String EXTRA_KEY_TOTAL_PRICE = "total";
    public static final String EXTRA_KEY_SHIPPING_COST = "shipping_cost";

    @Override
    public int getCoordinatorLayoutId() {
        return R.id.coordinator_shipping;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);
        payablePrice = getIntent().getLongExtra(EXTRA_KEY_PAYABLE, -1);
        shippingCost = getIntent().getLongExtra(EXTRA_KEY_SHIPPING_COST, -1);
        totalPrice = getIntent().getLongExtra(EXTRA_KEY_TOTAL_PRICE, -1);

        if (payablePrice == -1 ||
                totalPrice == -1 ||
                shippingCost == -1) {
            finish();
        }

        setupViews();
        observe();
    }

    private void setupViews() {
        firstNameEt = findViewById(R.id.et_shipping_firstName);
        lastNameEt = findViewById(R.id.et_shipping_lastName);
        postalCodeEt = findViewById(R.id.et_shipping_postalCode);
        phoneNumberEt = findViewById(R.id.et_shipping_phoneNumber);
        addressEt = findViewById(R.id.et_shipping_address);

        onlinePaymentBtn = findViewById(R.id.btn_shipping_onlinePayment);
        onlinePaymentBtn.setOnClickListener(v -> submitOrder(PaymentMethod.ONLINE_METHOD));
        cashOnDeliveryBtn = findViewById(R.id.btn_shipping_cashOnDelivery);
        cashOnDeliveryBtn.setOnClickListener(v -> submitOrder(PaymentMethod.CASH_ON_DELIVERY));
        paymentButtonContainer = findViewById(R.id.ll_shipping_paymentButtonContainer);
        progressBar = findViewById(R.id.progressBar_shipping);

        View view = findViewById(R.id.layout_shipping_purchaseDetails);
        CartItemAdapter.PurchaseDetailViewHolder purchaseDetailViewHolder = new CartItemAdapter.PurchaseDetailViewHolder(view);
        purchaseDetailViewHolder.bindDetails(totalPrice, shippingCost, payablePrice);

        View backButton = findViewById(R.id.iv_shipping_back);
        backButton.setOnClickListener(v -> finish());
    }

    private void observe() {
        compositeDisposable.add(shippingViewModel.getProgressBarVisibilitySubject()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    progressBar.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
                    paymentButtonContainer.setVisibility(aBoolean ? View.GONE : View.VISIBLE);
                }));
    }

    private void submitOrder(PaymentMethod paymentMethod) {
        // TODO: 7/6/2018 You must Validate fields

        shippingViewModel.orderSubmit(firstNameEt.getText().toString(),
                lastNameEt.getText().toString(),
                postalCodeEt.getText().toString(),
                phoneNumberEt.getText().toString(),
                addressEt.getText().toString(),
                paymentMethod).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SsSingleObserver<OrderSubmitResponse>(this) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(OrderSubmitResponse orderSubmitResponse) {
                        switch (paymentMethod) {
                            case CASH_ON_DELIVERY:
                                Intent intent = new Intent(ShippingActivity.this, CheckoutActivity.class);
                                intent.putExtra("order_id", String.valueOf(orderSubmitResponse.getOrderId()));
                                startActivity(intent);
                                break;
                            case ONLINE_METHOD:
                                Intent intentBankGateway = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                        "http://expertdevelopers.ir/payment?order_id=" + orderSubmitResponse.getOrderId()
                                ));

                                startActivity(intentBankGateway);
                                break;
                        }

                        finish();
                    }
                });
    }
}
