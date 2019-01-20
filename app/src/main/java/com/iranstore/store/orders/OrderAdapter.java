package com.iranstore.store.orders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iranstore.store.model.Order;
import com.iranstore.store.utils.PriceConverter;
import com.ss.sevenstore.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;

    public OrderAdapter(List<Order> orders) {

        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_order, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bindOrder(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView orderCodeTv;
        private TextView recipientTv;
        private TextView payableTv;
        private TextView paymentStatusTv;
        private TextView dateTv;

        public OrderViewHolder(View itemView) {
            super(itemView);

            orderCodeTv = itemView.findViewById(R.id.tv_order_code);
            recipientTv = itemView.findViewById(R.id.tv_order_recipient);
            payableTv = itemView.findViewById(R.id.tv_order_payable);
            paymentStatusTv = itemView.findViewById(R.id.tv_order_paymentStatus);
            dateTv = itemView.findViewById(R.id.tv_order_date);
        }

        public void bindOrder(Order order) {
            orderCodeTv.setText(String.valueOf(order.getId()));
            recipientTv.setText(order.getFirstName() + " " + order.getLastName());
            payableTv.setText(PriceConverter.convert(order.getPayable()));
            paymentStatusTv.setText(order.getPaymentStatus());
            dateTv.setText(order.getDate());
        }
    }
}
