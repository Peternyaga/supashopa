package com.example.supashopa_test.shop_management;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.supashopa_test.Constants;
import com.example.supashopa_test.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InventoryRvAdapter extends RecyclerView.Adapter<InventoryItemViewHolder> {
    private ArrayList<Item> itemsArrayList;
    private Context context;
    int setStatus;

    public InventoryRvAdapter(ArrayList<Item> itemsArrayList, Context context) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public InventoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_item,parent,false);
        return new InventoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryItemViewHolder holder, int position) {
        final Item item= itemsArrayList.get(position);
        holder.itemName.setText(item.getProductname());
        int itemStatus = item.getProductStatus();
        holder.outOfStock.setEnabled(true);
        holder.itemImage.setVisibility(View.VISIBLE);
        holder.unitPrice.setText("Unit Price: "+item.getPrice());
        try {
        Picasso.get().load(item.getProductpic_url()).into(holder.itemImage);
        }catch (Exception e){
        }

        Map params = new HashMap<>();
        if(itemStatus == 4){
            holder.status.setText("Out of stock");
            holder.status.setTextColor(Color.MAGENTA);
            setStatus = 3;
            holder.outOfStock.setText("out of stock");
        } else if (itemStatus == 3) {
            setStatus = 4;
            holder.status.setText("In Stock");
            holder.outOfStock.setText("in stock");
            holder.status.setTextColor(Color.GREEN);
        } else {
            setStatus = 0;
        }
        holder.outOfStock.setOnClickListener(v -> {
            holder.outOfStock.setEnabled(false);
            params.put("status", ""+setStatus);
            params.put("item_id", ""+item.getProductid());
            stringRequest(Constants.changeInventoryItemStatus, params, position);
        });
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    private void stringRequest(String url, Map params, int position){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Toast.makeText(context, "response: "+ response, Toast.LENGTH_SHORT).show();
            if (response.trim().equals("1")) {
                itemsArrayList.get(position).setProductStatus(setStatus);
                this.notifyItemChanged(position);
            }
        }, error -> {
            Toast.makeText(context, "error: "+error, Toast.LENGTH_SHORT).show();
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
    }
}
