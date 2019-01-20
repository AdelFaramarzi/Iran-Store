package com.iranstore.store.main;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iranstore.store.BaseActivity;
import com.iranstore.store.auth.AuthenticationActivity;
import com.iranstore.store.auth.OnUserAuthenticate;
import com.iranstore.store.auth.TokenContainer;
import com.iranstore.store.auth.UserInfoManager;
import com.iranstore.store.cart.CartActivity;
import com.iranstore.store.cart.CartItemCountContainer;
import com.iranstore.store.cart.OnCartItemCountChanged;
import com.iranstore.store.model.Banner;
import com.iranstore.store.model.Product;
import com.iranstore.store.model.api.SsSingleObserver;
import com.iranstore.store.orders.OrderHistoryActivity;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.ss.sevenstore.R;
import com.iranstore.store.list.ProductListActivity;
import com.iranstore.store.model.CartItemCountResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final int ID_CART = 1;
    private static final int ID_ORDER_HISTORY = 2;
    private static final int ID_AUTH = 3;

    private MainViewModel viewModel;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ProductAdapter latestProductsAdapter;
    private ProductAdapter popularProductsAdapter;
    private BannerAdapter bannerAdapter;
    private Drawer drawer;
    private UserInfoManager userInfoManager;
    private PrimaryDrawerItem authDrawerItem;
    private ProfileDrawerItem profileDrawerItem;
    private AccountHeader accountHeader;
    private Typeface drawerFont;
    private TextView cartItemCountBadge;
    private PrimaryDrawerItem cartDrawerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerFont = ResourcesCompat.getFont(this, R.font.primary_regular);
        userInfoManager = new UserInfoManager(this);
        viewModel = new MainViewModel();
        setupViews();
        setupDrawer();
        observe();

    }

    private void setupDrawer() {
        updateProfileDrawerItem();
        accountHeader = new AccountHeaderBuilder()
                .addProfiles(
                        profileDrawerItem
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        if (TokenContainer.getToken() == null)
                            startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
                        return false;
                    }
                })
                .withActivity(this)
                .withTypeface(drawerFont)
                .withHeaderBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary))
                .build();

        cartDrawerItem = new PrimaryDrawerItem().withName("سبد خرید")
                .withIdentifier(ID_CART)
                .withTypeface(drawerFont)
                .withBadge("0")
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (TokenContainer.getToken() == null) {
                            Toast.makeText(MainActivity.this, "ابتدا وارد حساب کاربری شوید", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(MainActivity.this, CartActivity.class));
                        }
                        return false;
                    }
                })
                .withBadgeStyle(new BadgeStyle()
                        .withBadgeBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.shape_badge))
                        .withTextColor(ContextCompat.getColor(MainActivity.this, R.color.white)))
                .withSelectable(false);

        PrimaryDrawerItem orderHistoryDrawerItem = new PrimaryDrawerItem()
                .withName("سوابق سفارش")
                .withSelectable(false)
                .withTypeface(drawerFont)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (TokenContainer.getToken() == null) {
                            Toast.makeText(MainActivity.this, "ابتدا وارد حساب کاربری خود شوید",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
                        }
                        return false;
                    }
                })
                .withIdentifier(ID_ORDER_HISTORY);

        updateAuthDrawerItem();
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withSelectedItem(-1)
                .withAccountHeader(accountHeader)
                .addDrawerItems(cartDrawerItem, orderHistoryDrawerItem, authDrawerItem)
                .withDrawerGravity(Gravity.RIGHT)
                .build();
    }

    private void updateAuthDrawerItem() {
        String authTitle = TokenContainer.getToken() != null ? "خروج از حساب کاربری" : "ورود به حساب کاربری";

        if (drawer == null) {
            authDrawerItem = new PrimaryDrawerItem()
                    .withName(authTitle)
                    .withIdentifier(ID_AUTH)
                    .withTypeface(drawerFont)
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            if (TokenContainer.getToken() != null) {
                                userInfoManager.clear();
                                TokenContainer.updateToken(null);
                                updateAuthDrawerItem();
                                updateProfileDrawerItem();
                                CartItemCountContainer.updateCount(0);
                                EventBus.getDefault().post(new OnCartItemCountChanged(0));
                            } else {
                                startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
                            }
                            return false;
                        }
                    });
        } else {
            authDrawerItem.withName(authTitle);
            drawer.updateItem(authDrawerItem);
        }
    }

    private void updateProfileDrawerItem() {
        String name = TokenContainer.getToken() != null ? "" : "کاربر مهمان";
        String email = TokenContainer.getToken() != null ? userInfoManager.getEmail() : "ورود به حساب کاربری یا ثبت نام";

        if (drawer == null) {
            profileDrawerItem = new ProfileDrawerItem()
                    .withName(name)
                    .withEmail(email)
                    .withIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.profile_img));
        } else {
            profileDrawerItem.withName(name)
                    .withEmail(email);
            accountHeader.updateProfile(profileDrawerItem);
        }
    }

    private void observe() {
        viewModel.latestProducts().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SsSingleObserver<List<Product>>(this) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<Product> products) {
                        latestProductsAdapter.setProducts(products);
                    }
                });

        viewModel.popularProducts().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SsSingleObserver<List<Product>>(this) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<Product> products) {
                        popularProductsAdapter.setProducts(products);
                    }
                });

        viewModel.banners().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SsSingleObserver<List<Banner>>(this) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<Banner> banners) {
                        bannerAdapter.setBanners(banners);
                    }
                });

        getUserCartItemCount();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAuthDrawerItem();
        updateProfileDrawerItem();
        updateCartItemCountBadge(CartItemCountContainer.getCount());
    }

    private void setupViews() {
        cartItemCountBadge = findViewById(R.id.tv_main_cartItemCountBadge);
        RecyclerView latestProductsRv = findViewById(R.id.rv_main_latestProducts);
        latestProductsRv.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, true
        ));
        latestProductsAdapter = new ProductAdapter();
        latestProductsRv.setAdapter(latestProductsAdapter);

        RecyclerView popularProductRv = findViewById(R.id.rv_main_popularProducts);
        popularProductsAdapter = new ProductAdapter();
        popularProductRv.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, true
        ));
        popularProductRv.setAdapter(popularProductsAdapter);

        RecyclerView sliderRv = findViewById(R.id.rv_main_slider);
        sliderRv.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, true
        ));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(sliderRv);
        bannerAdapter = new BannerAdapter();
        sliderRv.setAdapter(bannerAdapter);

        View cartItemIcon = findViewById(R.id.iv_main_cart);
        cartItemIcon.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public int getCoordinatorLayoutId() {
        return R.id.coordinator_main;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ProductListActivity.class);

        switch (v.getId()) {
            case R.id.tv_main_viewAllLatestProducts:
                intent.putExtra(ProductListActivity.EXTRA_KEY_SORT, Product.SORT_LATEST);
                break;
            case R.id.tv_main_viewAllPopularProducts:
                intent.putExtra(ProductListActivity.EXTRA_KEY_SORT, Product.SORT_POPULAR);
        }

        startActivity(intent);
    }

    private void getUserCartItemCount() {
        viewModel.getCartItemCount().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SsSingleObserver<CartItemCountResponse>(this) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(CartItemCountResponse cartItemCountResponse) {
                        CartItemCountContainer.updateCount(cartItemCountResponse.getCount());
                        EventBus.getDefault().post(new OnCartItemCountChanged(cartItemCountResponse.getCount()));
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCartItemCountChanged(OnCartItemCountChanged onCartItemCountChanged) {
        updateCartItemCountBadge(onCartItemCountChanged.getCount());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserAuthenticate(OnUserAuthenticate onUserAuthenticate) {
        getUserCartItemCount();
    }

    private void updateCartItemCountBadge(int count) {
        if (count > 0) {
            cartItemCountBadge.setVisibility(View.VISIBLE);
            cartItemCountBadge.setText(String.valueOf(count));
        } else {
            cartItemCountBadge.setVisibility(View.GONE);
        }

        cartDrawerItem.withBadge(String.valueOf(count));
        drawer.updateItem(cartDrawerItem);
    }


}
