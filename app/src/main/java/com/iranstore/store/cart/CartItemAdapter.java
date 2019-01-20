package com.iranstore.store.cart;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iranstore.store.model.Product;
import com.iranstore.store.utils.PriceConverter;
import com.squareup.picasso.Picasso;
import com.ss.sevenstore.R;
import com.iranstore.store.model.CartItem;
import com.iranstore.store.model.CartModel;
import com.iranstore.store.view.CartItemCountChanger;

public class CartItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CART_ITEM = 0;
    private static final int TYPE_PURCHASE_DETAIL = 1;
    private CartModel cartModel;
    private CartItemEventListener eventListener;

    public CartItemAdapter(CartModel cartModel, CartItemEventListener eventListener) {

        this.cartModel = cartModel;
        this.eventListener = eventListener;
    }

    public void updateCartModel(CartModel cartModel) {
        this.cartModel = cartModel;
        notifyDataSetChanged();
    }

    public void notifyItemChanged(CartItem cartItem) {
        int index = cartModel.getCartItems().indexOf(cartItem);
        notifyItemChanged(index);
    }

    public void removeCartItem(CartItem cartItem) {
        int index = cartModel.getCartItems().indexOf(cartItem);
        cartModel.getCartItems().remove(index);
        notifyItemRemoved(index);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CART_ITEM) {
            return new CartItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_cart, parent, false
            ));
        } else {
            return new PurchaseDetailViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_purchase_details, parent, false
            ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CartItemViewHolder) {
            ((CartItemViewHolder) holder).bindCartItem(cartModel.getCartItems().get(position));
        } else if (holder instanceof PurchaseDetailViewHolder) {
            ((PurchaseDetailViewHolder) holder).bindDetails(cartModel.getTotalPrice(),
                    cartModel.getShippingCost(),
                    cartModel.getPayablePrice());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < cartModel.getCartItems().size()) {
            return TYPE_CART_ITEM;
        }

        return TYPE_PURCHASE_DETAIL;
    }

    @Override
    public int getItemCount() {
        return cartModel.getCartItems().size() + 1;
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv;
        private ImageView productImage;
        private CartItemCountChanger cartItemCountChanger;
        private TextView removeBtn;
        private ProgressBar removeProgressBar;
        private ProgressBar changeCountProgressBar;
        private TextView prevPriceTv;
        private TextView priceTv;

        public CartItemViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.tv_cartItem_title);
            productImage = itemView.findViewById(R.id.iv_cartItem_productImage);
            cartItemCountChanger = itemView.findViewById(R.id.cartItemCountChanger_cartItem);
            removeBtn = itemView.findViewById(R.id.iv_cartItem_remove);
            removeProgressBar = itemView.findViewById(R.id.progressBar_cartItem_remove);
            changeCountProgressBar = itemView.findViewById(R.id.progressBar_cartItem_changeCartItemCount);
            priceTv = itemView.findViewById(R.id.tv_cartItem_price);
            prevPriceTv = itemView.findViewById(R.id.tv_cartItem_prevPrice);
        }

        public void bindCartItem(CartItem cartItem) {
            titleTv.setText(cartItem.getProduct().getTitle());
            Picasso.get().load(cartItem.getProduct().getImage()).into(productImage);
            priceTv.setText(PriceConverter.convert(cartItem.getProduct().getPrice()));
            prevPriceTv.setText(PriceConverter.convert(cartItem.getProduct().getPreviousPrice()));
            prevPriceTv.setPaintFlags(prevPriceTv.getPaintFlags() |
                    Paint.STRIKE_THRU_TEXT_FLAG);
            cartItemCountChanger.setCount(cartItem.getCount());

            if (cartItem.isRemoving()) {
                removeBtn.setVisibility(View.INVISIBLE);
                removeProgressBar.setVisibility(View.VISIBLE);
            } else {
                removeBtn.setVisibility(View.VISIBLE);
                removeProgressBar.setVisibility(View.GONE);
            }

            if (cartItem.isChangingCount()) {
                cartItemCountChanger.setVisibility(View.INVISIBLE);
                changeCountProgressBar.setVisibility(View.VISIBLE);
            } else {
                cartItemCountChanger.setVisibility(View.VISIBLE);
                changeCountProgressBar.setVisibility(View.GONE);
            }

            removeBtn.setOnClickListener(v -> {
                cartItem.setRemoving(true);
                eventListener.onRemoveButtonClick(cartItem);
                notifyItemChanged(getAdapterPosition());
            });

            cartItemCountChanger.setCartItemCountChangeListener(count -> {
                eventListener.onCartItemCountChange(cartItem, count);
                cartItem.setChangingCount(true);
                notifyItemChanged(getAdapterPosition());
            });

            productImage.setOnClickListener(v -> eventListener.onProductClick(cartItem.getProduct()));
        }

    }

    public static class PurchaseDetailViewHolder extends RecyclerView.ViewHolder {
        private TextView totalPriceTv;
        private TextView payablePriceTv;
        private TextView shippingCostTv;

        public PurchaseDetailViewHolder(View itemView) {
            super(itemView);
            totalPriceTv = itemView.findViewById(R.id.tv_purchaseDetail_totalPrice);
            payablePriceTv = itemView.findViewById(R.id.tv_purchaseDetail_payable);
            shippingCostTv = itemView.findViewById(R.id.tv_purchaseDetail_shippingCost);

        }

        public void bindDetails(long totalPrice, long shippingCost, long payablePrice) {
            totalPriceTv.setText(PriceConverter.convert(totalPrice));
            if (shippingCost>0)
            shippingCostTv.setText(PriceConverter.convert(shippingCost));
            else
                shippingCostTv.setText("رایگان");
            payablePriceTv.setText(PriceConverter.convert(payablePrice));
        }
    }

    public interface CartItemEventListener {
        void onProductClick(Product product);

        void onRemoveButtonClick(CartItem cartItem);

        void onCartItemCountChange(CartItem cartItem, int requestedCount);
    }
}
