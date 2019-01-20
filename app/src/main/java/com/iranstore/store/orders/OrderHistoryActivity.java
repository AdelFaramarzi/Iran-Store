package com.iranstore.store.orders;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.iranstore.store.model.Order;
import com.iranstore.store.model.api.SsSingleObserver;
import com.iranstore.store.BaseActivity;
import com.ss.sevenstore.R;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class OrderHistoryActivity extends BaseActivity {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private OrderHistoryViewModel viewModel = new OrderHistoryViewModel();
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private OrderAdapter adapter;

    @Override
    public int getCoordinatorLayoutId() {
        return R.id.coordinator_orderHistory;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        setupViews();
        observe();
    }

    private void observe() {
        Disposable disposable = viewModel.getProgressBarVisibilitySubject().subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> progressBar.setVisibility(aBoolean ? View.VISIBLE : View.GONE));

        compositeDisposable.add(disposable);

        viewModel.listOrders().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SsSingleObserver<List<Order>>(this) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<Order> orders) {
                        adapter = new OrderAdapter(orders);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }


    private void setupViews() {
        progressBar = findViewById(R.id.progressBar_orderHistory);
        recyclerView = findViewById(R.id.rv_orderHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}
