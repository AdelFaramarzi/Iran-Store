package com.iranstore.store.main;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iranstore.store.details.ProductDetailsActivity;
import com.iranstore.store.model.Product;
import com.iranstore.store.utils.PriceConverter;
import com.squareup.picasso.Picasso;
import com.ss.sevenstore.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products = new ArrayList<>();

    public ProductAdapter() {

    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bindProduct(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImageView;
        private TextView productTitleTextView;
        private TextView productPreviousPriceTextView;
        private TextView productPriceTextView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.iv_product_image);
            productTitleTextView = itemView.findViewById(R.id.tv_product_title);
            productPreviousPriceTextView = itemView.findViewById(R.id.tv_product_prevPrice);
            productPriceTextView = itemView.findViewById(R.id.tv_product_price);
        }

        public void bindProduct(Product product) {
            Picasso.get().load(product.getImage()).into(productImageView);
            productTitleTextView.setText(product.getTitle());
            productPreviousPriceTextView.setText(PriceConverter.convert(product.getPreviousPrice()));
            productPreviousPriceTextView.setPaintFlags(productPreviousPriceTextView.getPaintFlags() |
                    Paint.STRIKE_THRU_TEXT_FLAG);
            productPriceTextView.setText(PriceConverter.convert(product.getPrice()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
                    intent.putExtra(ProductDetailsActivity.EXTRA_KEY_PRODUCT, product);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
