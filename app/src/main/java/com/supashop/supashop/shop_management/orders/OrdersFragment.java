package com.supashop.supashop.shop_management.orders;

import static android.content.Context.MODE_PRIVATE;
import static com.supashop.supashop.Constants.FETCH_ORDERS;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.supashop.supashop.Constants;
import com.supashop.supashop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrdersFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<OrderModal> orders = new ArrayList<>();
    SharedPreferences mySp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_orders, container, false);
        recyclerView = view.findViewById(R.id.my_orders_rv);
        mySp = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        fetchOrders();
        return view;
    }
    private void fetchOrders() {
        String url = FETCH_ORDERS + "?shop_id=" + Constants.myShopId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String orderId = jsonObject.getString("order Id");
                            String name = jsonObject.getString("customer");
                            String total = jsonObject.getString("Total");
                            String date = jsonObject.getString("date");
                            String receipt = jsonObject.getString("payout receipt id");
                            String status = jsonObject.getString("payment status");
                            String note = jsonObject.getString("notes");

                            JSONArray orderItems = jsonObject.getJSONArray("items");
                            ArrayList<OrderItemModal> items = new ArrayList<>();
                            for (int x = 0; x < orderItems.length(); x++) {
                                JSONObject item = orderItems.getJSONObject(x);
                                items.add(new OrderItemModal(
                                        item.getString("name"),
                                        item.getString("product_picture"),
                                        item.getString("unit_price"),
                                        item.getString("quantity"),
                                        item.getString("subtotal")
                                ));
                            }

                            orders.add(new OrderModal(orderId, name, total, status, note, date, receipt, items));
                        }
                        buildRecyclerView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    // Show a toast/snackbar or log error
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1));
        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }

    public void buildRecyclerView(){
        RecyclerView.Adapter<OrderViewHolder> adapter = new OrdersRvAdapter(orders, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

}
