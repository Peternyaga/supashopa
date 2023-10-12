package com.example.supashopa_test;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class ShopsActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<ShopDetails> shops=new ArrayList<>();
    JSONArray jsonArray;
    RecyclerView shopsRecyclerView;
    ProgressBar progressBar;
    LinearLayout networkError;
    TextView refresh;
    public static SharedPreferences mySP;
    public static SharedPreferences.Editor mySPEditor;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        Toolbar myToolBar=findViewById(R.id.shopsappbar);
        myToolBar.setTitle("");
        myToolBar.bringToFront();
        setSupportActionBar(myToolBar);

        searchView= findViewById(R.id.search_shops);
        searchView.setOnClickListener(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchForShop(newText);
                return false;
            }
        });
        //myToolBar.getMenu();
        shopsRecyclerView=findViewById(R.id.rv_shops);
        progressBar=findViewById(R.id.progressbar);
        networkError=findViewById(R.id.networkerror);
        refresh=findViewById(R.id.tvrefresh);
        refresh.setOnClickListener(this);
        mySP=getSharedPreferences("MySharedPref", MODE_PRIVATE);
        mySPEditor=mySP.edit();
        getShops();
       // Toast.makeText(this, "shops length: "+getShops().size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile:
                break;
            case R.id.logout:
                final AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("LOGOUT");
                builder.setMessage("Do you want to log out?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mySPEditor.clear();
                        mySPEditor.commit();
                        Intent intent=new Intent(ShopsActivity.this, signUpActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog=builder.create();
                dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList getShops(){
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, Constants.URL_SHOPS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
               // Toast.makeText(ShopsActivity.this, response, Toast.LENGTH_SHORT).show();
                try {
                    jsonArray=new JSONArray(response);

                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=new JSONObject(jsonArray.getJSONObject(i).toString());
                        String shopName=jsonObject.getString("shop name");
                        String shopDescription=jsonObject.getString("shop description");
                        String shopPicURL=Constants.IMAGESOFSHOPS_ROOT_URL+jsonObject.getString("shop image URL");
                        int shopCode=jsonObject.getInt("shop code");
                        String databaseName=jsonObject.getString("database name");
                        float shopRating= (float) jsonObject.getDouble("shop rating");

                        shops.add(new ShopDetails(shopCode, shopName,shopDescription, shopPicURL, databaseName ,shopRating));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                buildShopsRecyclerView(shops);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ShopsActivity.this, "error: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                networkError.setVisibility(View.VISIBLE);

            }
        });
        requesthandler.getInstance(this).addTorequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));

        shopsRecyclerView.setVisibility(View.VISIBLE);
        shopsRecyclerView.bringToFront();
        return shops;
    }
    private void searchForShop(final String query){
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constants.urlSearchForShop, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                shops.clear();
                //Toast.makeText(ShopsActivity.this, response, Toast.LENGTH_SHORT).show();
                try {
                    jsonArray=new JSONArray(response);

                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=new JSONObject(jsonArray.getJSONObject(i).toString());
                        int shopCode=jsonObject.getInt("shop code");
                        String shopName=jsonObject.getString("shop name");
                        String shopDescription=jsonObject.getString("shop description");
                        String shopPicURL=Constants.IMAGESOFSHOPS_ROOT_URL+jsonObject.getString("shop image URL");
                        String databaseName=jsonObject.getString("database name");
                        float shopRating= (float) jsonObject.getDouble("shop rating");

                        shops.add(new ShopDetails(shopCode, shopName,shopDescription, shopPicURL, databaseName ,shopRating));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                buildShopsRecyclerView(shops);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                //Toast.makeText(ShopsActivity.this, "Cannot get shops at the moment. Try again later", Toast.LENGTH_SHORT).show();
                networkError.setVisibility(View.VISIBLE);

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param=new HashMap<>();
                param.put("query", "%"+query+"%");
                return param;
            }
        };
        requesthandler.getInstance(this).addTorequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));

        shopsRecyclerView.setVisibility(View.VISIBLE);
        shopsRecyclerView.bringToFront();

    }

    private void buildShopsRecyclerView(ArrayList<ShopDetails> arrayList) {

        ShopsRecyclerviewAdapter adapter = new ShopsRecyclerviewAdapter(arrayList, ShopsActivity.this);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        shopsRecyclerView.setHasFixedSize(true);
        shopsRecyclerView.setLayoutManager(linearLayoutManager);
        shopsRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        networkError.setVisibility(View.GONE);
        getShops();
    }

    @Override
    public void onBackPressed() {
        if(!(searchView.getQuery().toString().isEmpty())){
            searchView.setQuery("",false);
            shops.clear();
            getShops();
        }else {
            super.onBackPressed();
        }
    }
}
