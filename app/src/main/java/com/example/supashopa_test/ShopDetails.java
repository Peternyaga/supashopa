package com.example.supashopa_test;

public class ShopDetails {
    int shopCode;
    String shopname, shopdescription, shopimageurl;
    float shoprating;
    Double latitude, longitude, distance;

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public ShopDetails(int shopCode, String shopname, String shopdescription, String shopimageurl,  float shoprating, Double latitude, Double longitude, Double distance) {
        this.shopCode= shopCode;
        this.shopname = shopname;
        this.shopdescription = shopdescription;
        this.shopimageurl = shopimageurl;
        this.shoprating = shoprating;
        this.latitude=latitude;
        this.longitude = longitude;
        this.distance = distance;
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
