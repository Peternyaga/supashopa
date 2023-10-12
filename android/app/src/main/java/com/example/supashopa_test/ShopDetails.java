package com.example.supashopa_test;

public class ShopDetails {
    int shopCode;
    String shopname, shopdescription, shopimageurl,databaseName;
    float shoprating;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public ShopDetails(int shopCode, String shopname, String shopdescription, String shopimageurl, String databaseName, float shoprating) {
        this.shopCode= shopCode;
        this.shopname = shopname;
        this.shopdescription = shopdescription;
        this.shopimageurl = shopimageurl;
        this.databaseName=databaseName;
        this.shoprating = shoprating;
    }

    public int getShopCode() {
        return shopCode;
    }

    public void setShopCode(int shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getShopdescription() {
        return shopdescription;
    }

    public void setShopdescription(String shopdescription) {
        this.shopdescription = shopdescription;
    }

    public String getShopimageurl() {
        return shopimageurl;
    }

    public void setShopimageurl(String shopimageurl) {
        this.shopimageurl = shopimageurl;
    }

    public float getShoprating() {
        return shoprating;
    }

    public void setShoprating(float shoprating) {
        this.shoprating = shoprating;
    }
}
