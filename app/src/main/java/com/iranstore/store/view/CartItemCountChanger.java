package com.iranstore.store.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ss.sevenstore.R;
public class CartItemCountChanger extends CardView {
    private View rootView;
    private View add;
    private View minus;
    private TextView countTv;
    private int count;
    private OnCartItemCountChangeListener cartItemCountChangeListener;

    public CartItemCountChanger(@NonNull Context context) {
        super(context);
        init();
    }

    public CartItemCountChanger(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CartItemCountChanger(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        rootView = LayoutInflater.from(getContext()).inflate(
                R.layout.view_cart_item_count_changer, this, true
        );
        add = findViewById(R.id.tv_cartItemCountChanger_add);
        add.setOnClickListener(v -> {
            count += 1;
            onCountChange();
        });
        minus = findViewById(R.id.tv_cartItemCountChanger_minus);
        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                count -= 1;
                onCountChange();
            }
        });
        countTv = rootView.findViewById(R.id.tv_cartItemCountChanger_count);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        countTv.setText(String.valueOf(count));
    }

    private void onCountChange() {
        if (count > 0) {
            countTv.setText(String.valueOf(count));
            if (cartItemCountChangeListener != null)
                cartItemCountChangeListener.onChange(count);
        } else {
            count = 1;
        }
    }

    public void setCartItemCountChangeListener(OnCartItemCountChangeListener cartItemCountChangeListener) {
        this.cartItemCountChangeListener = cartItemCountChangeListener;
    }

    public interface OnCartItemCountChangeListener {
        void onChange(int count);
    }
}
