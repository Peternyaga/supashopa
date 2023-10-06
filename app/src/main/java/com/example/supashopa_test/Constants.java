package com.example.supashopa_test;

import java.util.ArrayList;

public class Constants {
    //      private static final String ROOT_URL ="http://192.168.137.1/";
    private static final String ROOT_URL="https://4293-197-156-130-11.ngrok-free.app/";
    private static final String API_URL=ROOT_URL.trim()+ "android/v1/";
    public static String IMAGESOFSHOPS_ROOT_URL=ROOT_URL.trim()+"shop images/";
    public static final String IMAGESOFITEMS_ROOT_URL=ROOT_URL.trim()+"item images/";
    public static final String URL_REGISTER= API_URL +"registeruser.php";
    public static final String URL_LOGIN= API_URL +"login.php";
    public static final String URL_FETCHITEMS= API_URL +"fetch_shop_items.php";
    public static final String URL_CHECKOUT= API_URL +"checkout.php";
    public static  final String URL_SEARCHITEMS= API_URL +"item_search.php";
    public static  final String URL_SHOPS= API_URL +"fetch_Shops.php";
    public static  final String urlSearchForShop= API_URL+ "search_For_Shop.php";
    public static final String CHECK_ORDER_STATUS= API_URL+"check_order_status.php";
    public static String shopDbName;//name of the shop the user has entered;
    public static String shopName;
    public static int recursion=0;
    public static ArrayList cartItems=new ArrayList();
}






