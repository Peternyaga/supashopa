package com.supashop.supashop;

public class CartItems {
    private int productid;
    private String productname;
    private String productpic_url;
    private String price;
    private String category;
    private int quantity;

    public CartItems(int productid, String productname, String productpic_url, String price, String category, int quantity) {
        this.productid = productid;
        this.productname = productname;
        this.productpic_url = productpic_url;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
    }

    public int getProductid() {
        return productid;
    }

    public void setProductid(int productid) {
        this.productid = productid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductpic_url() {
        return productpic_url;
    }

    public void setProductpic_url(String productpic_url) {
        this.productpic_url = productpic_url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
