package com.example.supashopa_test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShopsRecyclerviewAdapter extends RecyclerView.Adapter<ShopViewHolder> {
   private ArrayList<ShopDetails> shopDetails;
   Context context;


    public ShopsRecyclerviewAdapter(ArrayList<ShopDetails> shopDetails, Context context) {
        this.shopDetails = shopDetails;
        this.context=context;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_rv_item,parent,false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShopViewHolder holder, final int position) {
        final ShopDetails shop=shopDetails.get(position);
        holder.btnshopname.setText(shop.getShopname());
        holder.btnshopname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, shoppingActivity.class);
                Constants.shopDbName =shop.getDatabaseName();
                ShopsActivity.mySPEditor.putInt("shop_code", shop.getShopCode());
                ShopsActivity.mySPEditor.putString("shop_db_name", shop.getDatabaseName());
                ShopsActivity.mySPEditor.putString("shop_name", shop.getShopname());
                ShopsActivity.mySPEditor.commit();
                Constants.shopName=shop.getShopname();
                context.startActivity(intent);
                ((Activity) context).finish();

            }
        });
        holder.tvshopdescription.setText(shop.getShopdescription());
        holder.ratingBar.setRating(shop.getShoprating());
        try {

            Picasso.get().load(shop.getShopimageurl()).fit().into(holder.ivshop);
        } catch (Exception e) {
            e.printStackTrace();
           Toast.makeText(context, ""+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return shopDetails.size();
    }
}
