package com.example.supashopa_test;

import java.util.ArrayList;

public interface OnItemClickInterface {
    void onClickAddItemtoCart(int position,int numberofitems);
    void changeCartItems(int position, int quantity);
    void setTotal(ArrayList<CartItems> arrayList);
    void removecartitem(int index);
}
