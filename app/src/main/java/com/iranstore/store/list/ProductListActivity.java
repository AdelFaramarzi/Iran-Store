package com.iranstore.store.list;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.iranstore.store.BaseActivity;
import com.iranstore.store.model.Product;
import com.iranstore.store.model.api.SsSingleObserver;
import com.ss.sevenstore.R;
import com.iranstore.store.main.ProductAdapter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProductListActivity extends BaseActivity {
    public static final String EXTRA_KEY_SORT = "sort";
    private ProductListViewModel viewModel = new ProductListViewModel();
    private int sortType;
    private ProductAdapter productAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private SsSingleObserver<List<Product>> productObserver = new SsSingleObserver<List<Product>>(this) {
        @Override
        public void onSubscribe(Disposable d) {
            compositeDisposable.add(d);
        }

        @Override
        public void onSuccess(List<Product> products) {
            productAdapter.setProducts(products);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        sortType = getIntent().getIntExtra(EXTRA_KEY_SORT, Product.SORT_LATEST);
        setupViews();
        observe();
    }

    private void observe() {
        viewModel.products(sortType).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(productObserver);
    }

    private void setupViews() {
        RecyclerView productsRv = findViewById(R.id.rv_list_products);
        productsRv.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter();
        productsRv.setAdapter(productAdapter);

        RecyclerView sortChipsRv = findViewById(R.id.rv_list_sort);
        sortChipsRv.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, true
        ));
        sortChipsRv.setAdapter(new SortAdapter(this, sortType, new SortAdapter.OnSortClickListener() {
            @Override
            public void onClick(int sortType) {
                ProductListActivity.this.sortType = sortType;
                observe();
            }
        }));

        ImageView backButton = findViewById(R.id.iv_list_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public int getCoordinatorLayoutId() {
        return R.id.coordinator_list;
    }
}
