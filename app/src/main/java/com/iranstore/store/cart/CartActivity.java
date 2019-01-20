package com.iranstore.store.cart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.iranstore.store.BaseActivity;
import com.iranstore.store.details.ProductDetailsActivity;
import com.iranstore.store.model.AddToCartResponse;
import com.iranstore.store.model.Product;
import com.iranstore.store.model.api.SsSingleObserver;
import com.iranstore.store.shipping.ShippingActivity;
import com.iranstore.store.utils.PriceConverter;
import com.ss.sevenstore.R;
import com.iranstore.store.model.CartItem;
import com.iranstore.store.model.CartModel;
import com.iranstore.store.model.SuccessResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CartActivity extends BaseActivity implements CartItemAdapter.CartItemEventListener {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private CartViewModel viewModel = new CartViewModel();
    private RecyclerView recyclerView;
    private CartItemAdapter cartItemAdapter;
    private FrameLayout emptyState;
    private FrameLayout progressBar;
    private TextView payablePriceTv;
    private TextView goToPurchaseDetailsLink;
    private TextView cartItemCountBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cartItemCountBadge = findViewById(R.id.tv_cart_cartItemCountBadge);
        recyclerView = findViewById(R.id.rv_cart);
        emptyState = findViewById(R.id.frame_cart_emptyState);
        progressBar = findViewById(R.id.frame_cart_progressBar);
        payablePriceTv = findViewById(R.id.tv_cart_payablePrice);
        goToPurchaseDetailsLink = findViewById(R.id.tv_cart_viewPurchaseDetail);
        goToPurchaseDetailsLink.setOnClickListener(v -> recyclerView.smoothScrollToPosition(cartItemAdapter.getItemCount()));
        getCartItems(true);


        compositeDisposable.add(viewModel.getProgressBarVisibilitySubject().subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> progressBar.setVisibility(aBoolean ? View.VISIBLE : View.GONE)));
    }

    private void getCartItems(boolean mustShowProgressBar) {
        viewModel.cart(mustShowProgressBar).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SsSingleObserver<CartModel>(this) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(CartModel cartModel) {
                        CartItemCountContainer.updateCount(cartModel.getCartItems().size());
                        EventBus.getDefault().post(new OnCartItemCountChanged(cartModel.getCartItems().size()));

                        if (cartModel.getCartItems().isEmpty())
                            emptyState.setVisibility(View.VISIBLE);
                        else {
                            Button submit = findViewById(R.id.btn_cart_submit);
                            submit.setOnClickListener(v -> {
                                Intent intent = new Intent(CartActivity.this, ShippingActivity.class);
                                intent.putExtra(ShippingActivity.EXTRA_KEY_TOTAL_PRICE, cartModel.getTotalPrice());
                                intent.putExtra(ShippingActivity.EXTRA_KEY_SHIPPING_COST, cartModel.getShippingCost());
                                intent.putExtra(ShippingActivity.EXTRA_KEY_PAYABLE, cartModel.getPayablePrice());
                                startActivity(intent);
                                finish();

                            });
                            emptyState.setVisibility(View.GONE);
                            payablePriceTv.setText(PriceConverter.convert(cartModel.getPayablePrice()));
                        }


                        if (cartItemAdapter == null) {
                            cartItemAdapter = new CartItemAdapter(cartModel, CartActivity.this);
                            recyclerView.setLayoutManager(new LinearLayoutManager(
                                    CartActivity.this, LinearLayoutManager.VERTICAL, false
                            ));
                            recyclerView.setAdapter(cartItemAdapter);
                        }

                        cartItemAdapter.updateCartModel(cartModel);
                    }
                });
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra(ProductDetailsActivity.EXTRA_KEY_PRODUCT, product);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartItemCountBadge(CartItemCountContainer.getCount());
    }

    @Override
    public void onRemoveButtonClick(CartItem cartItem) {
        viewModel.removeCartItem(cartItem)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SsSingleObserver<SuccessResponse>(this) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(SuccessResponse successResponse) {
                        cartItemAdapter.removeCartItem(cartItem);
                        getCartItems(false);
                        int count = CartItemCountContainer.getCount();
                        count -= 1;
                        CartItemCountContainer.updateCount(count);
                        EventBus.getDefault().post(new OnCartItemCountChanged(count));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        cartItem.setRemoving(false);
                        cartItemAdapter.notifyItemChanged(cartItem);
                    }
                });
    }

    @Override
    public void onCartItemCountChange(CartItem cartItem, int requestedCount) {
        viewModel.changeCount(cartItem, requestedCount)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SsSingleObserver<AddToCartResponse>(this) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(AddToCartResponse addToCartResponse) {
                        cartItem.setCount(requestedCount);
                        cartItem.setChangingCount(false);
                        cartItemAdapter.notifyItemChanged(cartItem);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        cartItem.setChangingCount(false);
                        cartItemAdapter.notifyItemChanged(cartItem);
                    }
                });
    }

    @Override
    public int getCoordinatorLayoutId() {
        return R.id.coordinator_cart;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCartItemCountChanged(OnCartItemCountChanged onCartItemCountChanged) {
        updateCartItemCountBadge(onCartItemCountChanged.getCount());
    }

    private void updateCartItemCountBadge(int count) {
        if (count > 0) {
            cartItemCountBadge.setVisibility(View.VISIBLE);
            cartItemCountBadge.setText(String.valueOf(count));
        } else {
            cartItemCountBadge.setVisibility(View.GONE);
        }
    }
}
