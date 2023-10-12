package com.example.supashopa_test;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    public TextView tvitemname, tvitemprice, tvquantity;
    public Button btnaddtocart,btnplusquantity,btnminusquantity;
    public ImageView ivitemimage;


    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        tvitemname=itemView.findViewById(R.id.tvname);
        tvitemprice=itemView.findViewById(R.id.tvprice);
        tvquantity=itemView.findViewById(R.id.tvquantity);
        btnaddtocart=itemView.findViewById(R.id.btnaddtocart);
        ivitemimage=itemView.findViewById(R.id.ivitemimage);
        btnminusquantity=itemView.findViewById(R.id.btnminus);
        btnplusquantity=itemView.findViewById(R.id.btnplus);

        btnminusquantity.setEnabled(false);


    }
}
