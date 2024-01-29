package com.example.supashopa_test.shop_management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supashopa_test.Item;
import com.example.supashopa_test.R;

import java.util.ArrayList;

public class InventoryRvAdapter extends RecyclerView.Adapter<InventoryItemViewHolder> {
    private ArrayList<Item> itemsArrayList;
    private Context context;

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
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }
}
