package com.supashop.supashop.ui.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.supashop.supashop.Constants;
import com.supashop.supashop.MyRequestHandler;
import com.supashop.supashop.ShopDetails;
import com.supashop.supashop.ShopsActivity;
import com.supashop.supashop.login;
import com.supashop.supashop.signUpActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SplashScreenActivity extends AppCompatActivity {
    SharedPreferences mySP;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Double myLatitude = null, myLongitude = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mySP = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        if (!(mySP.contains("phone") && mySP.contains("password"))) {
            startActivity(new Intent(this, signUpActivity.class));
            finish();
            return;
        }

        loginUser();
    }

    private void loginUser() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_LOGIN,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("error") && jsonObject.getString("error").equals("false")) {
                            fetchLocationAndProceed();
                            //navigateToLogin();
                        } else {
                            navigateToLogin();
                        }
                    } catch (JSONException e) {
                        navigateToLogin();
                    }
                },
                error -> navigateToLogin()) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phone", mySP.getString("phone", ""));
                params.put("password", mySP.getString("password", ""));
                return params;
            }
        };
        MyRequestHandler.getInstance(this).addTorequestQueue(stringRequest);
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, login.class));
        finish();
    }

    private void fetchLocationAndProceed() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            myLatitude = location.getLatitude();
                            myLongitude = location.getLongitude();
                        }
                        proceedToShopsActivity();
                    })
                    .addOnFailureListener(e -> proceedToShopsActivity());
        } else {
            proceedToShopsActivity();
        }
    }

    private void proceedToShopsActivity() {
        Intent intent = new Intent(this, ShopsActivity.class);
        intent.putExtra("latitude", myLatitude);
        intent.putExtra("longitude", myLongitude);
        startActivity(intent);
        finish();
    }

    private void fetchShops() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_SHOPS,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        ArrayList<ShopDetails> shops = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            shops.add(new ShopDetails(
                                    jsonObject.getInt("shop code"),
                                    jsonObject.getString("shop name"),
                                    jsonObject.getString("shop description"),
                                    Constants.IMAGESOFSHOPS_ROOT_URL + jsonObject.getString("shop image URL"),
                                    (float) jsonObject.getDouble("shop rating"),
                                    jsonObject.getDouble("latitude"),
                                    jsonObject.getDouble("longitude"),
                                    0.0
                            ));
                        }
                        sortShopsByDistance(shops);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {});

        MyRequestHandler.getInstance(this).addTorequestQueue(stringRequest);
    }

    private void sortShopsByDistance(ArrayList<ShopDetails> shops) {
        if (myLatitude != null && myLongitude != null) {
            for (ShopDetails shop : shops) {
                shop.setDistance(haversine(shop.getLatitude(), shop.getLongitude(), myLatitude, myLongitude));
            }
            shops.sort(Comparator.comparingDouble(ShopDetails::getDistance));
        }
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of Earth in KM
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
