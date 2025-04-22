package com.supashop.supashop.shop_management.orders;

public class OrderItemModal {
    String name, image, unitPrice, quantity, subTotal;

    public OrderItemModal(String name, String image, String unitPrice, String quantity, String subTotal) {
        this.name = name;
        this.image = image;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subTotal = subTotal;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getSubTotal() {
        return subTotal;
    }
}
