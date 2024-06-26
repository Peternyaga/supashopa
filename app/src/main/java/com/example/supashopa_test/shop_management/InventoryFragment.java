package com.example.supashopa_test.shop_management;

import static android.content.Context.MODE_PRIVATE;
import static com.example.supashopa_test.Constants.fetchInventory;

import android.content.Intent;
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
import com.example.supashopa_test.Constants;
import com.example.supashopa_test.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InventoryFragment extends Fragment {
RecyclerView recyclerView;
FloatingActionButton addNewItem;
ArrayList<Item> items = new ArrayList<>();
SharedPreferences mySp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inventory_fragment, container, false);
        addNewItem = view.findViewById(R.id.addInventoryItem);
        mySp = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddInventoryItem.class);
                startActivity(intent);
            }
        });
        recyclerView = view.findViewById(R.id.inventory_rv);
        fetchInventory();
        return view;
    }
private void fetchInventory(){
    StringRequest stringRequest = new StringRequest(Request.Method.POST, fetchInventory, response -> {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i< jsonArray.length(); i++){
                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                int itemid = jsonObject.getInt("product_id");
                String itemname = jsonObject.getString("name");
                String itemimage = Constants.IMAGESOFITEMS_ROOT_URL+Constants.shopName+"/"+jsonObject.getString("product_picture");
                String price = jsonObject.getString("unit_price");
                String category = jsonObject.getString("category");
                int status = jsonObject.getInt("status");

                items.add(new Item(itemid, itemname, itemimage, price, category, status));
            }
            buildRecyclerView();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }, error -> {

    }){
        @Nullable
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String,String> params = new HashMap();
            params.put("shop_id",""+mySp.getInt("shop_code",0));
            return params;
        }
    };
    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
    stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1));
    requestQueue.add(stringRequest);
}

public void buildRecyclerView(){
    RecyclerView.Adapter adapter = new InventoryRvAdapter(items, getContext());
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(layoutManager);
}
}
