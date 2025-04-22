package com.supashop.supashop.shop_management.orders;

import java.util.ArrayList;

public class OrderModal {
    String orderId, name, total,status, additionalDetails, date, receipt;
    ArrayList<OrderItemModal> items;

    public OrderModal(String orderId, String name, String total, String status, String additionalDetails, String date, String receipt, ArrayList<OrderItemModal> items) {
        this.orderId = orderId;
        this.name = name;
        this.total = total;
        this.status = status;
        this.additionalDetails = additionalDetails;
        this.date = date;
        this.receipt = receipt;
        this.items = items;
    }

    public String getDate() {
        return date;
    }

    public String getReceipt() {
        return receipt;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getName() {
        return name;
    }

    public String getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    public ArrayList<OrderItemModal> getItems() {
        return items;
    }
}
