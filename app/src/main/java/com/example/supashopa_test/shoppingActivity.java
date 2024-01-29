package com.example.supashopa_test;

import static android.view.View.GONE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class shoppingActivity extends AppCompatActivity implements OnItemClickInterface, View.OnClickListener {
    RecyclerView recyclerView, searchRecyclerview;
    ProgressBar progressBar;
    Button cart, logout,goBack;
    TextView textView,refresh;
    LinearLayout error;
    SearchView searchView;
    Toolbar toolbar;

    JSONArray jsonArray;
    private RecyclerviewAdapter adapter;
    private ArrayList<Item> itemsArrayList;
    ArrayList<Item> itemsArrayList1 =null;
    public static ArrayList<String> cartitems;
    private int[] deactivateditems =new int[0];//used to deactivate items already in cart
    private String shop;
    JSONArray shopname;
    SharedPreferences mySP;
    public static SharedPreferences.Editor mySPEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mySP=getSharedPreferences("MySharedPref", MODE_PRIVATE);
        mySPEditor=mySP.edit();

        //checking whether a user has logged in.
        if (!(mySP.contains("phone")&& mySP.contains("password"))){
            Intent intent=new Intent(this, signUpActivity.class);
            startActivity(intent);
            finish();
        }//method to check whether the user is logged in

        setContentView(R.layout.activity_shopping);

        androidx.appcompat.widget.Toolbar myToolbar= findViewById(R.id.appbar);
        myToolbar.setTitle("");
        myToolbar.bringToFront();
        setSupportActionBar(myToolbar);
        error=findViewById(R.id.error);
        refresh=findViewById(R.id.tvrefresh);
        refresh.setOnClickListener(this);
        searchView=findViewById(R.id.search_items);
        searchView.setOnClickListener(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               itemsSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemsArrayList1=new ArrayList<>();
                progressBar.setVisibility(View.VISIBLE);
                progressBar.bringToFront();
                for (int i=0; i<itemsArrayList.size(); i++){
                    if (itemsArrayList.get(i).getProductname().contains(newText)){
                        itemsArrayList1.add(itemsArrayList.get(i));
                    }
                }
                if (itemsArrayList1.size()==0){
                    itemsSearch(newText);
                }else {
                    buildRecyclerView(itemsArrayList1, deactivateditems, searchRecyclerview);
                    progressBar.setVisibility(GONE);
                }
                return false;
            }
        });
       recyclerView=findViewById(R.id.rvshoppingitems);
       searchRecyclerview=findViewById(R.id.rvshoppingitems);
       progressBar=findViewById(R.id.progressbar);
       textView=findViewById(R.id.tvitems);
       cart =findViewById(R.id.cart);
       cart.setOnClickListener(this);
       goBack=findViewById(R.id.goback);
       goBack.setOnClickListener(this);
      // logout=findViewById(R.id.logout);
      // logout.setOnClickListener(this);
       itemsArrayList=new ArrayList<>();
       cartitems= new ArrayList<>();
      cart.setText(String.valueOf(cartitems.size()));

        productsrequest();//method to get items for display is called inside onCreate method.
    }

    public void itemsSearch(final String query){
        itemsArrayList1 =new ArrayList<>();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constants.URL_SEARCHITEMS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        if (!response.equals("[]")) {
                            itemsArrayList1.clear();
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int itemid = jsonObject.getInt("product_id");
                                    String itemname = jsonObject.getString("name");
                                    String itemimage = Constants.IMAGESOFITEMS_ROOT_URL + Constants.shopName + "/" + jsonObject.getString("product_picture");
                                    String price = jsonObject.getString("unit_price");
                                    String category = jsonObject.getString("category");

                                    itemsArrayList1.add(new Item(itemid, itemname, itemimage, price, category));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(shoppingActivity.this, "Query not found!", Toast.LENGTH_LONG).show();
                        }
                        buildRecyclerView(itemsArrayList1, deactivateditems,searchRecyclerview);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                params.put("shop_id", ""+mySP.getInt("shop_code",0));
                params.put("query", "%"+query+"%");
                params.put("shopDbName", Constants.shopDbName);
                return params;
            }
        };
        requesthandler.getInstance(this).addTorequestQueue(stringRequest);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.profile:
                Toast.makeText(this, item.getTitle()+" selected", Toast.LENGTH_LONG).show();
                return true;

            case R.id.logout:
                AlertDialog.Builder builder=new AlertDialog.Builder(shoppingActivity.this);
                builder.setMessage("Do you want to logout?");
                builder.setTitle("logout");
                builder.setCancelable(false);
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mySPEditor.remove("phone");
                        mySPEditor.remove("password");
                        mySPEditor.remove("user_id");
                        mySPEditor.commit();
                        if (!(mySP.contains("phone")&&mySP.contains("password")&&mySP.contains("user_id"))){
                            Toast.makeText(getApplicationContext(), "logged out!", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(getApplicationContext(), login.class);
                            startActivity(intent);
                            finish();
                        }

                    }

                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert=builder.create();
                alert.show();
                if (!(mySP.contains("phone")&&mySP.contains("password"))) {
                    Toast.makeText(getApplicationContext(),"logged out",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(this,signUpActivity.class);
                    startActivity(intent);
                }
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    private void productsrequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.urlFetchItems,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(GONE);
                recyclerView.setVisibility(View.VISIBLE);
                try {
                    jsonArray=new JSONArray(response);

                    for (int i=0;i<jsonArray.length();i++){

                        JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                        int itemid = jsonObject.getInt("product_id");
                        String itemname = jsonObject.getString("name");
                        String itemimage = Constants.IMAGESOFITEMS_ROOT_URL+Constants.shopName+"/"+jsonObject.getString("product_picture");
                        String price = jsonObject.getString("unit_price");
                        String category = jsonObject.getString("category");

                        itemsArrayList.add(new Item(itemid, itemname, itemimage, price, category));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                buildRecyclerView(itemsArrayList,deactivateditems,recyclerView);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(GONE);
                shoppingActivity.this.error.setVisibility(View.VISIBLE);
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                params.put("shop_id", ""+mySP.getInt("shop_code",0));
                return params;
            }
        };
        requesthandler.getInstance(this).addTorequestQueue(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(7000, 2, 2));
    }//method to get items from remote source.

    private void buildRecyclerView(ArrayList<Item> rvItems, int[] deactivateItems, RecyclerView recyclerView){
        adapter=new RecyclerviewAdapter(rvItems, shoppingActivity.this,this,deactivateItems);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }//method to build the items display recyclerview

    @Override
    public void onClickAddItemtoCart(int productId,int numberofitems) {

        try {
           for (int i=0; i<jsonArray.length(); i++){
                if(jsonArray.getJSONObject(i).getInt("product_id")==productId){
                    cartitems.add((jsonArray.getJSONObject(i).put("quantity", numberofitems)).toString());
                    break;
                }
            }
           // cartitems.add((jsonArray.getJSONObject(productId).put("quantity",numberofitems)).toString());
            cart.setText(String.valueOf(cartitems.size()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }//method to handle adding an item to the cart

    @Override
    public void changeCartItems(int position, int quantity) {
        try {
            JSONObject jsonObject=new JSONObject(cartitems.get(position));
            cartitems.remove(position);
            jsonObject.put("quantity", quantity);
            cartitems.add(position,jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }//method to handle changes in quantity of cart items

    @Override
    public void removecartitem(int index) {

    }//method to remove an item from cart. not functional for this activity

    @Override
    public void setTotal(ArrayList arrayList) {

    }//not functional for this class

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cart:
        Intent intent=new Intent(getApplicationContext(),cartActivity.class);
        intent.putStringArrayListExtra("cart items",cartitems);
        intent.putExtra("shop", shop);
        startActivityForResult(intent,2);
        break;
            case R.id.logout:
                mySPEditor.remove("phone");
                mySPEditor.remove("password");
                mySPEditor.commit();

                if (!(mySP.contains("phone")&&mySP.contains("password"))) {
                    Toast.makeText(getApplicationContext(),"logged out",Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.goback:
                if (!(itemsArrayList1 ==null)) {
                    buildRecyclerView(itemsArrayList, deactivateditems, recyclerView);
                    searchView.clearFocus();
                    itemsArrayList1.clear();
                    itemsArrayList1 =null;
                }else {
                    intent=new Intent(this, ShopsActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.tvrefresh:
                productsrequest();
                error.setVisibility(GONE);
                progressBar.setVisibility(View.VISIBLE);
        }
    }//handling button clicks

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==2){
           cartitems.clear();
            ArrayList<String> arrayList=data.getStringArrayListExtra("cart items");
            cartitems=arrayList;
            cart.setText("0");
            deactivateditems=new int[arrayList.size()];
            for (int i=0; i<arrayList.size(); i++){
                try {
                    JSONObject jsonObject=new JSONObject(arrayList.get(i));
                    for (int x=0; x<itemsArrayList.size();x++){
                        if (jsonObject.getInt("product_id")==itemsArrayList.get(x).getProductid()){
                            deactivateditems[i]=x;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            buildRecyclerView(itemsArrayList,deactivateditems, recyclerView);
        }else if (requestCode==3){
            shop=data.getStringExtra("shopDbName");
            Toast.makeText(getApplicationContext(), shop, Toast.LENGTH_SHORT).show();
        }
    }//method to sychronize shopping activity with the cart

    @Override
    protected void onResume() {
        super.onResume();
        cart.setText(String.valueOf(cartitems.size()));
        if (itemsArrayList.size()<1){
            //productsrequest();
        }
        Constants.shopDbName=mySP.getString("shop_db_name", "");
        Constants.shopName=mySP.getString("shop_name", "");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!(itemsArrayList1 == null)) {
            buildRecyclerView(itemsArrayList, deactivateditems, recyclerView);
            searchView.clearFocus();
            itemsArrayList1.clear();
            itemsArrayList1 = null;
        } else {
            Intent intent = new Intent(this, ShopsActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
