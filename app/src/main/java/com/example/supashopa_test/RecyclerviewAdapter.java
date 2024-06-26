package com.example.supashopa_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supashopa_test.shop_management.Item;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecyclerviewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private ArrayList<Item> itemsArrayList;
    private Context context;
    private final OnItemClickInterface onItemClickInterface;
    private int[] deactivateItems;


    public RecyclerviewAdapter(ArrayList<Item> itemsArrayList, Context context, OnItemClickInterface onItemClickInterface, int[] deactivateItems) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
        this.onItemClickInterface=onItemClickInterface;
        this.deactivateItems = deactivateItems;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_rv_item,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position){
        final Item item=itemsArrayList.get(position);
        int quantity;
        holder.tvitemprice.setText("KSh \n"+item.getPrice());
        holder.tvitemname.setText(item.getProductname().toUpperCase());
        holder.tvquantity.setText("1");
        holder.btnaddtocart.setText("ADD TO CART");
        holder.btnaddtocart.setEnabled(true);
        holder.btnplusquantity.setEnabled(true);
        //holder.btnminusquantity.setEnabled(true);
        try {
            Picasso.get().load(item.getProductpic_url()).into(holder.ivitemimage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.btnplusquantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence itm=holder.tvquantity.getText();
               int numberOfItems=Integer.parseInt(itm.toString());
                numberOfItems++;
                holder.tvquantity.setText(String.valueOf(numberOfItems));
                holder.btnminusquantity.setEnabled(true);
            }
        });

        holder.btnminusquantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence itm=holder.tvquantity.getText();
                int numberofitems=Integer.parseInt(itm.toString());
                numberofitems--;
                holder.tvquantity.setText(String.valueOf(numberofitems));
                if (numberofitems<2){
                    holder.btnminusquantity.setEnabled(false);
                }
            }
        });
        holder.btnaddtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickInterface!=null){
                    int position=holder.getAdapterPosition();

                    if (position!=RecyclerView.NO_POSITION){
                        holder.btnplusquantity.setEnabled(false);
                        holder.btnminusquantity.setEnabled(false);
                        int quantity=Integer.parseInt(holder.tvquantity.getText().toString());
                        int itemid=item.getProductid();
                        Constants.cartItems.add(new JSONObject());
                        onItemClickInterface.onClickAddItemtoCart(itemid, quantity);
                        //onItemClickInterface.onClickAddItemtoCart(position,quantity);
                        if (deactivateItem(item.getProductid())>0){
                            holder.btnaddtocart.setText("ADDED TO CART");
                            holder.btnaddtocart.setEnabled(false);
                        }
                    }
                }
            }
        });

        /*This statement is executed during binding of every view to check whether the
         item at the particular adapter position exists in the cart
         *if an the item at the particular adapter position exists in the cart, then the view is modified in the following way:
         *The plus, minus and add to cart buttons are disabbled- this prevents a customer from adding an item twice in the cart
         *The text of the quantity textview is set to the quantity of that particular item as it is in the cart*/
        if (deactivateItem(item.getProductid())>0){
            holder.btnaddtocart.setText("ADDED TO CART");
            holder.btnaddtocart.setEnabled(false);
            holder.btnplusquantity.setEnabled(false);
            holder.btnminusquantity.setEnabled(false);
            holder.tvquantity.setText(""+deactivateItem(item.getProductid()));
        }

        //loop for deactivating items that are already added to the cart
       /* if (deactivateItems.length>0) {
            for (int i = 0; i < deactivateItems.length; i++) {
                if (holder.getLayoutPosition() == deactivateItems[i]) {
                    holder.btnaddtocart.setEnabled(false);
                    holder.btnaddtocart.setText("added to cart");
                    holder.btnminusquantity.setEnabled(false);
                    holder.btnplusquantity.setEnabled(false);
                }
            }
        }*/

    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }
/*This method is used to check whether the item at a particular adapter position has been added to the cart.
* @return true if the product id if the particular item is found in the cart arraylist*/
    private int deactivateItem(int itemId){
        JSONObject cartItem;
        int quantity=0;
        for (int i=0; i<shoppingActivity.cartitems.size(); i++){
            try {
                cartItem=new JSONObject(shoppingActivity.cartitems.get(i));
                if (cartItem.getInt("product_id")==itemId){
                    quantity=cartItem.getInt("quantity");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return quantity;
    }

    private void increaseNumberOfItems(){

    }
}
