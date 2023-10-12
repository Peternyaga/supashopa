package com.example.supashopa_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartRecyclerviewAdapter extends RecyclerView.Adapter<CartItemViewHolder> {
   private ArrayList<CartItems> cartItems;
   private Context context;

   private int quantity;
   private float subtotal,total;
   private OnItemClickInterface onItemClickInterface;
   private DecimalFormat decimalFormat=new DecimalFormat("####.00");


    public CartRecyclerviewAdapter(ArrayList<CartItems> cartItems, Context context, OnItemClickInterface onItemClickInterface) {
        this.cartItems = cartItems;
        this.context = context;
        this.onItemClickInterface = onItemClickInterface;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_rv_item,parent,false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartItemViewHolder holder, final int position) {

        final int index=holder.getAdapterPosition();

        final CartItems item=cartItems.get(position);
        holder.tvitemname.setText(item.getProductname());
        holder.tvitemprice.setText("Unit price \n KSh "+item.getPrice());
        holder.tvquantity.setText(String.valueOf(item.getQuantity()));
        try {
            Picasso.get().load(item.getProductpic_url()).into(holder.ivitemimage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tvsubtotal.setText("subtotal \nKSh. "+decimalFormat.format(Double.parseDouble(item.getPrice())*item.getQuantity()));

        if (item.getQuantity()<2){
            holder.btnminusquantity.setEnabled(false);
        }

        holder.btnminusquantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int qnty=cartItems.get(index).getQuantity();
                qnty--;
                cartItems.get(index).setQuantity(qnty);
                holder.tvquantity.setText(String.valueOf(item.getQuantity()));
                holder.tvsubtotal.setText("subtotal \nKSh. "+decimalFormat.format(qnty*Double.parseDouble(item.getPrice())));
                onItemClickInterface.setTotal(cartItems);
                if (item.getQuantity()<2){
                    holder.btnminusquantity.setEnabled(false);
                }
            }
        });


        holder.btnplusquantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int qnty=cartItems.get(index).getQuantity();
                qnty++;
                cartItems.get(index).setQuantity(qnty);
                holder.tvquantity.setText(String.valueOf(item.getQuantity()));
                holder.tvsubtotal.setText("subtotal\n "+"KSh. "+decimalFormat.format(qnty*Double.parseDouble(item.getPrice())));
                onItemClickInterface.setTotal(cartItems);

                if (item.getQuantity()>1){
                    holder.btnminusquantity.setEnabled(true);
                }
            }
        });

        holder.btnremovefromcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cartItems.remove(position);
                onItemClickInterface.removecartitem(position);
                //onItemClickInterface.setTotal(cartItems);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }


}
