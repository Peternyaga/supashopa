package com.supashop.supashop.shop_management.orders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supashop.supashop.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    TextView orderId, name, total, status, additionalDetails, orderItems, date, receipt;
    CheckBox checkBox;
    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        orderId = itemView.findViewById(R.id.tv_order_id);
        name = itemView.findViewById(R.id.tv_customer_name);
        total = itemView.findViewById(R.id.tv_order_total);
        status = itemView.findViewById(R.id.tv_order_status);
        additionalDetails = itemView.findViewById(R.id.tv_additional_details);
        orderItems = itemView.findViewById(R.id.tv_order_items);
        date = itemView.findViewById(R.id.tv_order_date);
        receipt = itemView.findViewById(R.id.tv_order_receipt);
        checkBox = itemView.findViewById(R.id.order_processed);
    }
}
