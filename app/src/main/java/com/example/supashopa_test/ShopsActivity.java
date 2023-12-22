package com.example.supashopa_test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopsActivity extends AppCompatActivity implements View.OnClickListener {
    public static ArrayList<ShopDetails> shops = new ArrayList<>();
    JSONArray jsonArray;
    RecyclerView shopsRecyclerView;
    ProgressBar progressBar;
    LinearLayout networkError;
    TextView refresh;
    public static SharedPreferences mySP;
    public static SharedPreferences.Editor mySPEditor;
    SearchView searchView;
    AlertDialog.Builder alertBuilder;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    Boolean locationIsEnabled = false;
    public static LocationRequest locationRequest;
    public static Double myLatitude = null;
    public static Double myLongitude = null;
    Double distanceFromShop = null;
    TextView errorMessage;
    boolean shopsDisplayed = false;
    public static FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationIsEnabled = true;
                locationRequest = createLocationRequest();
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                getShops();
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            } else if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                String alertTitle = "Permission Request Alert";
                String alertMessage = "Supashopa requires location data to function. Please allow location access to continue.";
                requestLocationPermission(alertTitle, alertMessage);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        alertBuilder = new AlertDialog.Builder(this);
        Toolbar myToolBar = findViewById(R.id.shopsappbar);
        myToolBar.setTitle("");
        myToolBar.bringToFront();
        setSupportActionBar(myToolBar);

        searchView = findViewById(R.id.search_shops);
        searchView.setOnClickListener(this);
        shopsRecyclerView = findViewById(R.id.rv_shops);
        progressBar = findViewById(R.id.progressbar);
        errorMessage = findViewById(R.id.tv_networkerror);
        networkError = findViewById(R.id.networkerror);
        refresh = findViewById(R.id.tvrefresh);
        refresh.setOnClickListener(this);
        mySP = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        mySPEditor = mySP.edit();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String alertTitle = "Permission Request Alert";
            String alertMessage = "Supashopa will request for permission to access your location. Supashopa will use this data to determine shops close to you";
            requestLocationPermission(alertTitle, alertMessage);
        } else {
            locationIsEnabled = true;
            locationRequest = createLocationRequest();
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            if (myLatitude!=null) {
                getShops();
            }else {
               getLocationAndShops();
            }
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (locationIsEnabled) {
                    searchForShop(newText);
                    return false;
                }else {

                }
                return false;
            }
        });
    }

    public static LocationRequest createLocationRequest(){
        locationRequest = new LocationRequest.Builder(0)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setIntervalMillis(30000)// Update interval in milliseconds
                .build();
        return locationRequest;
    }

    public void requestLocationPermission(String alertTitle, String message){
        alertBuilder.setCancelable(false);
        alertBuilder.setTitle(alertTitle);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(ShopsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                ActivityCompat.requestPermissions(ShopsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            Double latitude1;
            Double longitude1;
            if(location!=null) {
                myLatitude = location.getLatitude();
                myLongitude = location.getLongitude();
                //Toast.makeText(ShopsActivity.this, "my Latitude = "+myLatitude, Toast.LENGTH_SHORT).show();
                /*checking whether the searchview is empty before fetching shops. This ensures that the getshops method is
                not called while the user is searching.
                */
                if (searchView.getQuery().toString().isEmpty()) {
                   getShops();
                }
                for (int i = 0; i < shops.size(); i++) {
                    latitude1 = shops.get(i).getLatitude();
                    longitude1 = shops.get(i).getLongitude();
                    distanceFromShop = (haversine(latitude1, longitude1, myLatitude, myLongitude));
                    shops.get(i).setDistance(distanceFromShop);
                }
                for (int x = 0; x<shops.size(); x++){
                    distanceFromShop = shops.get(x).getDistance();
                    for (int a = 0; a<shops.size(); a++){
                        Double comparison = shops.get(a).getDistance();
                        if (comparison>distanceFromShop){
                            ShopDetails temp = shops.get(x);
                            shops.set(x, shops.get(a));
                            shops.set(a, temp);
                        }
                    }
                }
            }

        }
    };

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
                        StringRequest logout = new StringRequest(Request.Method.POST, Constants.URL_LOGOUT, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("1")){
                                    Toast.makeText(ShopsActivity.this, response, Toast.LENGTH_SHORT).show();
                                    mySPEditor.clear();
                                    mySPEditor.commit();
                                    Intent intent=new Intent(ShopsActivity.this, signUpActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ShopsActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Nullable
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params = new HashMap<>();
                                params.put("user_id", mySP.getString("userId", ""));
                                return params;
                            }
                        };
                        requesthandler.getInstance(ShopsActivity.this).addTorequestQueue(logout);
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
                shops.clear();
                progressBar.setVisibility(View.GONE);
                networkError.setVisibility(View.GONE);
                try {
                    jsonArray=new JSONArray(response);

                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=new JSONObject(jsonArray.getJSONObject(i).toString());
                        String shopName=jsonObject.getString("shop name");
                        String shopDescription=jsonObject.getString("shop description");
                        String shopPicURL=Constants.IMAGESOFSHOPS_ROOT_URL+jsonObject.getString("shop image URL");
                        int shopCode=jsonObject.getInt("shop code");
                        float shopRating= (float) jsonObject.getDouble("shop rating");
                        Double shopLatitude = jsonObject.getDouble("latitude");
                        Double shopLongitude = jsonObject.getDouble("longitude");

                       distanceFromShop = haversine(myLatitude, myLongitude, shopLatitude, shopLongitude);
                        shops.add(new ShopDetails(shopCode, shopName,shopDescription, shopPicURL, shopRating, shopLatitude, shopLongitude, distanceFromShop));

                        for (int x = 0; x<shops.size(); x++){
                            distanceFromShop = shops.get(x).getDistance();
                            for (int a = 0; a<shops.size(); a++){
                                Double comparison = shops.get(a).getDistance();
                                if (comparison>distanceFromShop){
                                    ShopDetails temp = shops.get(x);
                                    shops.set(x, shops.get(a));
                                    shops.set(a, temp);
                                }
                            }
                        }

                        if (!shopsDisplayed) {
                            buildShopsRecyclerView(shops);
                            shopsDisplayed = true;
                        }else {
                        }
                    }
//                    for (int x = 0; x<shops.size(); x++){
//                        distanceFromShop = shops.get(x).getDistance();
//                        for (int a = 0; a<shops.size(); a++){
//                            Double comparison = shops.get(a).getDistance();
//                            if (comparison>distanceFromShop){
//                                ShopDetails temp = shops.get(x);
//                                shops.set(x, shops.get(a));
//                                shops.set(a, temp);
//                            }
//                        }
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(ShopsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
/*since getShops() is called everytime there is a location update, the following block checks whether the recyclerview has already been built.
* This prevents the recyclerview from being refreshed everytime there is a location update allowing the shops arraylist to be updated without disrupting the user experience*/
//                if (!shopsDisplayed) {
//                    buildShopsRecyclerView(shops);
//                    shopsDisplayed = true;
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                //Toast.makeText(ShopsActivity.this, "error: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                networkError.setVisibility(View.VISIBLE);

            }
        });
        requesthandler.getInstance(this).addTorequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 3, 1));

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
                networkError.setVisibility(View.GONE);
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
                        float shopRating= (float) jsonObject.getDouble("shop rating");
                        Double shopLatitude = jsonObject.getDouble("latitude");
                        Double shopLongitude = jsonObject.getDouble("longitude");
                        distanceFromShop = haversine(myLatitude, myLongitude, shopLatitude, shopLongitude);

                        shops.add(new ShopDetails(shopCode, shopName,shopDescription, shopPicURL ,shopRating, shopLatitude, shopLongitude, distanceFromShop));

                        for (int x = 0; x<shops.size(); x++){
                            distanceFromShop = shops.get(x).getDistance();
                            for (int a = 0; a<shops.size(); a++){
                                Double comparison = shops.get(a).getDistance();
                                if (comparison>distanceFromShop){
                                    ShopDetails temp = shops.get(x);
                                    shops.set(x, shops.get(a));
                                    shops.set(a, temp);
                                }
                            }
                        }
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 3, 1));

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

    @Override
    protected void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers

        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate differences in coordinates
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // Haversine formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance
        double distance = R * c;

        return distance;
    }

    private void getLocationAndShops(){

            new Handler().postDelayed(new Runnable() {

                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    if (myLatitude == null) {
                        getLocationAndShops();
                    } else {
                        getShops();
                    }
                }
            }, 2000);
    }

}
