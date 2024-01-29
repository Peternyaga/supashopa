package com.example.supashopa_test.shop_management;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supashopa_test.R;

public class InventoryItemViewHolder extends RecyclerView.ViewHolder {
    TextView itemName;
    Button edit, outOfStock, removeItem;
    public InventoryItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemName = itemView.findViewById(R.id.inventory_item);
        edit  = itemView.findViewById(R.id.edit_inventory_item);
        outOfStock = itemView.findViewById(R.id.out_of_stock);
        removeItem = itemView.findViewById(R.id.remove_item);
    }
}
