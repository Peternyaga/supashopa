package com.example.supashopa_test;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShopViewHolder extends RecyclerView.ViewHolder {

    ImageView ivshop;
    Button btnshopname;
    TextView tvshopdescription;
    RatingBar ratingBar;
    public ShopViewHolder(@NonNull View itemView) {
        super(itemView);
        ivshop=itemView.findViewById(R.id.ivshoppic);
        btnshopname =itemView.findViewById(R.id.tvshopname);
        tvshopdescription=itemView.findViewById(R.id.tvshopdescription);
        ratingBar=itemView.findViewById(R.id.ratingbar);
    }
}
