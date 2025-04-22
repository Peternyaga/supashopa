package com.supashop.supashop.shop_management.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.supashop.supashop.Constants;
import com.supashop.supashop.MyRequestHandler;
import com.supashop.supashop.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrdersRvAdapter extends RecyclerView.Adapter<OrderViewHolder> {
    private ArrayList<OrderModal> arrayList;
    private Context context;

    public OrdersRvAdapter(ArrayList<OrderModal> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_rv_item, parent,false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModal order = arrayList.get(position);
        holder.date.setText(order.getDate());
        holder.receipt.setText(order.getReceipt());
        holder.orderId.setText(order.getOrderId());
        holder.name.setText(order.getName());
        holder.status.setText(order.getStatus());
        holder.total.setText(order.getTotal());
        holder.additionalDetails.setText(order.getAdditionalDetails());

        StringBuilder orderItemsText = new StringBuilder();
        for (OrderItemModal item: order.getItems()) {
            orderItemsText.append(item.getName()).append("      ").append(item.getQuantity()).append("\n");
        }
        holder.orderItems.setText(orderItemsText.toString());
        holder.checkBox.setChecked(false);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.updateOrderStatusURL+"?order_id="+order.getOrderId()+"&status=2", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")){
                                arrayList.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                            }else {
                                holder.checkBox.setChecked(false);
                            }
                        } catch (JSONException e) {
                            holder.checkBox.setChecked(false);
                            throw new RuntimeException(e);

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        holder.checkBox.setChecked(false);
                    }
                });
                MyRequestHandler.getInstance(context).addTorequestQueue(stringRequest);
            } catch (Exception e) {
                holder.checkBox.setChecked(false);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
