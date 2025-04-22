package com.supashop.supashop;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartItemViewHolder extends RecyclerView.ViewHolder {
    public TextView tvitemname, tvitemprice, tvquantity, tvsubtotal;
    public Button btnremovefromcart,btnplusquantity,btnminusquantity;
    public ImageView ivitemimage;

    public CartItemViewHolder(@NonNull View itemView) {
        super(itemView);

        tvitemname=itemView.findViewById(R.id.tvcartitemname);
        tvitemprice=itemView.findViewById(R.id.tvcartitemprice);
        tvquantity=itemView.findViewById(R.id.tvcartquantity);
        tvsubtotal=itemView.findViewById(R.id.tvcartsubtotal);
        btnremovefromcart=itemView.findViewById(R.id.btnremoveitem);
        btnplusquantity=itemView.findViewById(R.id.btncartplus);
        btnminusquantity=itemView.findViewById(R.id.btncartminus);
        ivitemimage=itemView.findViewById(R.id.ivcartitemimage);
    }
}
