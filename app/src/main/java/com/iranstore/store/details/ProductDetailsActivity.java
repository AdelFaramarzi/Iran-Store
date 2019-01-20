package com.iranstore.store.details;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iranstore.store.details.addcomment.AddCommentDialog;
import com.iranstore.store.model.AddToCartResponse;
import com.iranstore.store.model.Comment;
import com.iranstore.store.model.Product;
import com.iranstore.store.model.api.SsSingleObserver;
import com.iranstore.store.utils.PriceConverter;
import com.squareup.picasso.Picasso;
import com.iranstore.store.BaseActivity;
import com.ss.sevenstore.R;
import com.iranstore.store.cart.CartItemCountContainer;
import com.iranstore.store.cart.OnCartItemCountChanged;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProductDetailsActivity extends BaseActivity {
    public static final String EXTRA_KEY_PRODUCT = "product";
    private ImageView productIv;
    private TextView productTitleTv;
    private TextView productPrevPriceTv;
    private TextView productPriceTv;
    private Button addToCartBtn;
    private RecyclerView commentsRv;
    private TextView addCommentBtn;
    private TextView activityTitleTv;
    private ImageView backBtn;

    private Product product;

    private ProductDetailsViewModel viewModel = new ProductDetailsViewModel();
    private ProgressBar progressBar;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ProgressBar addToCartProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product = getIntent().getParcelableExtra(EXTRA_KEY_PRODUCT);
        if (product == null) {
            finish();
        }
        setContentView(R.layout.activity_product_details);
        setupViews();

        viewModel.comments(product.getId()).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SsSingleObserver<List<Comment>>(this) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<Comment> comments) {
                        commentsRv.setAdapter(new CommentAdapter(comments));
                    }
                });

        Disposable disposable = viewModel.progressBarVisibility().subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    progressBar.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
                });
        compositeDisposable.add(disposable);

        addToCartBtn = findViewById(R.id.button_details_addToCart);
        addToCartBtn.setOnClickListener(v -> {
            viewModel.addToCart(product.getId())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SsSingleObserver<AddToCartResponse>(ProductDetailsActivity.this) {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onSuccess(AddToCartResponse addToCartResponse) {
                            Toast.makeText(ProductDetailsActivity.this, "با موفقیت به سبد خرید شما اضافه شد", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            v.setVisibility(View.VISIBLE);
                            int count = CartItemCountContainer.getCount();
                            count += 1;
                            CartItemCountContainer.updateCount(count);
                            EventBus.getDefault().post(new OnCartItemCountChanged(count));
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            progressBar.setVisibility(View.GONE);
                            v.setVisibility(View.VISIBLE);
                        }
                    });
            addToCartProgressBar = findViewById(R.id.progressBar_details_addToCart);
            addToCartProgressBar.setVisibility(View.VISIBLE);
            v.setVisibility(View.INVISIBLE);
        });
    }

    private void setupViews() {
        productIv = findViewById(R.id.iv_details_productImage);
        Picasso.get().load(product.getImage()).into(productIv);

        productTitleTv = findViewById(R.id.tv_details_productTitle);
        productTitleTv.setText(product.getTitle());

        productPrevPriceTv = findViewById(R.id.tv_details_prevPrice);
        productPrevPriceTv.setPaintFlags(productPrevPriceTv.getPaintFlags() |
                Paint.STRIKE_THRU_TEXT_FLAG);
        productPrevPriceTv.setText(PriceConverter.convert(product.getPreviousPrice()));

        productPriceTv = findViewById(R.id.tv_details_price);
        productPriceTv.setText(PriceConverter.convert(product.getPrice()));

        backBtn = findViewById(R.id.iv_details_back);
        backBtn.setOnClickListener(v -> finish());

        activityTitleTv = findViewById(R.id.tv_details_title);
        activityTitleTv.setText(product.getTitle());

        commentsRv = findViewById(R.id.rv_details_comments);
        commentsRv.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        ));

        progressBar = findViewById(R.id.progressBar_details);

        addCommentBtn = findViewById(R.id.tv_details_addComment);
        addCommentBtn.setOnClickListener(v -> {
            AddCommentDialog dialog = AddCommentDialog.newInstance(product.getId());
            dialog.show(getSupportFragmentManager(), null);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    public int getCoordinatorLayoutId() {
        return R.id.coordinator_details;
    }
}
