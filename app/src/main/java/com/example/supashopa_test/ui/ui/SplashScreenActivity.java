package com.example.supashopa_test.ui.ui;

import static com.example.supashopa_test.ShopsActivity.haversine;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.supashopa_test.Constants;
import com.example.supashopa_test.ShopDetails;
import com.example.supashopa_test.ShopsActivity;
import com.example.supashopa_test.MyRequestHandler;
import com.example.supashopa_test.signUpActivity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreenActivity extends AppCompatActivity {
    SharedPreferences mySP;
    Intent intent;
    JSONArray jsonArray;
    Double distanceFromShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash_screen);
        mySP=getSharedPreferences("MySharedPref", MODE_PRIVATE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            /*
             * if the location access permission is granted, we get the user location.
             */

            ShopsActivity.locationRequest = ShopsActivity.createLocationRequest();
            ShopsActivity.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            ShopsActivity.fusedLocationProviderClient.requestLocationUpdates(ShopsActivity.locationRequest, locationCallback, null);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //the activity waits for 3 seconds, to allow time for location updates before the ShopsActivity is started.
                    startNextActivity();
                    /*This block checks whether the location updates have been received.
                    * If the updates have been received, this activity is finished*/
                    if (ShopsActivity.myLatitude!=null){
                        finish();
                    }
                }
            }, 3000);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startNextActivity();
                }

            }, 3000);
            finish();
        }


    }

    private void startNextActivity(){
                if(!(mySP.contains("phone")&&mySP.contains("password"))){
                    intent=new Intent(SplashScreenActivity.this, signUpActivity.class);
                }else {
                    intent = new Intent(SplashScreenActivity.this, ShopsActivity.class);
                }
                startActivity(intent);
               // finish();
    }

    public LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            Double latitude1;
            Double longitude1;
            if(location!=null) {
               ShopsActivity.myLatitude = location.getLatitude();
               ShopsActivity.myLongitude = location.getLongitude();

               if (ShopsActivity.myLatitude!=null){
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           finish();
                       }
                   }, 3000);
               }
                /*checking whether the searchview is empty before fetching shops. This ensures that the getshops method is
                not called while the user is searching.
                */
                for (int i = 0; i < ShopsActivity.shops.size(); i++) {
                    latitude1 = ShopsActivity.shops.get(i).getLatitude();
                    longitude1 = ShopsActivity.shops.get(i).getLongitude();
                    Double distanceFromShop = (haversine(latitude1, longitude1, ShopsActivity.myLatitude, ShopsActivity.myLongitude));
                    ShopsActivity.shops.get(i).setDistance(distanceFromShop);
                }
                for (int x = 0; x<ShopsActivity.shops.size(); x++){
                    distanceFromShop = ShopsActivity.shops.get(x).getDistance();
                    for (int a = 0; a<ShopsActivity.shops.size(); a++){
                        Double comparison = ShopsActivity.shops.get(a).getDistance();
                        if (comparison>distanceFromShop){
                            ShopDetails temp = ShopsActivity.shops.get(x);
                            ShopsActivity.shops.set(x, ShopsActivity.shops.get(a));
                            ShopsActivity.shops.set(a, temp);
                        }
                    }
                }
            }

        }
    };

    private void fetchShops(){
        StringRequest stringRequest=new StringRequest(Request.Method.GET, Constants.URL_SHOPS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                        distanceFromShop = 0.0000;
                       // distanceFromShop = haversine(ShopsActivity.myLatitude, ShopsActivity.myLongitude, shopLatitude, shopLongitude);
                        ShopsActivity.shops.add(new ShopDetails(shopCode, shopName,shopDescription, shopPicURL, shopRating, shopLatitude, shopLongitude, distanceFromShop));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyRequestHandler.getInstance(this).addTorequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 0, 1));
    }

}
