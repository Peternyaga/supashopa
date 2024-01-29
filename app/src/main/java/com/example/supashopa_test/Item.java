package com.example.supashopa_test;

public class Item {
    private int productid;
    private String productname;
    private String productpic_url;
    private String price;
    private String category;

    public Item(int id, String name, String pic_url, String price, String category){
        this.productid=id;
        this.productname=name;
        this.productpic_url=pic_url;
        this.price=price;
        this.category=category;
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
}
