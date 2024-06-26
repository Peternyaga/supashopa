package com.example.supashopa_test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateShop extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final int SELECT_IMAGE_REQUEST_CODE = 1000;
    ScrollView scrollView;
    EditText shopName, shortCode;
    static  EditText shopDescription;
    Button selectImage, createShop;
    ImageView shopImage;
    Spinner shopsCategoriesSpinner, paymentMethodsSpinner;
    Boolean categorySelected = false;
    Boolean paymentMethodSelected = false;
    Boolean shopImageSelected = false;
    JSONArray shopCategories = new JSONArray();
    JSONArray paymentMethods = new JSONArray();
    ArrayList<String> categories = new ArrayList();
    ArrayList<String> methods = new ArrayList<>();
    Map<String, String> params = new HashMap<>();
    Map<String, String> headers = new HashMap<>();
    Bitmap selectedImageBitmap = null;
    ArrayAdapter<String> shopCategoriesAdapter, paymentMethodsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shop);
        Toolbar myToolBar = findViewById(R.id.create_shop_toolbar);
        setSupportActionBar(myToolBar);

        scrollView = findViewById(R.id.create_shop_scroller);
        shopName = findViewById(R.id.enter_shop_name);
        shopDescription = findViewById(R.id.enter_shop_description);
        shortCode = findViewById(R.id.enter_shortcode);
        shopImage = findViewById(R.id.select_shop_image);
        createShop = findViewById(R.id.btn_create);
        createShop.setOnClickListener(this);
        categories.add("Select your shop category");
        methods.add("select your a payment method");
        shopsCategoriesSpinner = findViewById(R.id.select_shop_category);
        shopCategoriesAdapter = new ArrayAdapter<>(CreateShop.this, android.R.layout.simple_spinner_dropdown_item, categories);
        shopCategoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shopsCategoriesSpinner.setAdapter(shopCategoriesAdapter);
        shopsCategoriesSpinner.setOnItemSelectedListener(this);
        shopsCategoriesSpinner.setSelection(0, false);

        paymentMethodsSpinner = findViewById(R.id.select_payment_method);
        paymentMethodsAdapter = new ArrayAdapter<>(CreateShop.this, android.R.layout.simple_spinner_dropdown_item, methods);
        paymentMethodsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodsSpinner.setAdapter(paymentMethodsAdapter);
        paymentMethodsSpinner.setOnItemSelectedListener(this);
        paymentMethodsSpinner.setSelection(0, false);

        selectImage=findViewById(R.id.btn_select_shop_image);
        selectImage.setOnClickListener(this::onClick);

        stringGetRequest(Constants.URL_FETCHSHOPCATEGORIES, 1);
        stringGetRequest(Constants.URL_FETCHPAYMENTMETHODS, 2);

    }

    void  createShop(String shopName, String shopDescription, int Category, String shortcode, double latitude, double longitude){

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_select_shop_image:
                fetchShopImage();
                break;
            case R.id.btn_create:
                if (shopName.getText().toString().trim().length() > 0 && shopDescription.getText().toString().trim().length() > 0 && shortCode.getText().toString().trim().length() > 0 &&
                categorySelected && paymentMethodSelected && shopImageSelected){
                    if (ShopsActivity.myLatitude == null || ShopsActivity.myLongitude == null){
                        Toast.makeText(this, "location access failed. Please go back and try again", Toast.LENGTH_LONG).show();
                    }else {
                        params.put("shop_name", shopName.getText().toString().trim());
                        params.put("shop_description", shopDescription.getText().toString().trim());
                        params.put("mpesa_till", shortCode.getText().toString().trim());
                        params.put("latitude", ShopsActivity.myLatitude.toString());
                        params.put("longitude", ShopsActivity.myLongitude.toString());
                        headers.put("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary123");
                    }

                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    MultipartRequest multipartRequest = new MultipartRequest(Constants.urlCreateShop, null, params, "image/png", selectedImageBitmap,
                            new Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers)));
                                        if (jsonObject.getBoolean("code")){
                                            scrollView.removeAllViews();
                                            getSupportFragmentManager().beginTransaction()
                                                    .add(R.id.create_shop_scroller,  CreateShopAdminFragment.newInstance(jsonObject.getString("shop_id"), params.get("shop_name")))
                                                    .commit();
                                            Toast.makeText(CreateShop.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                            shopDescription.setText(jsonObject.toString());
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        throw new RuntimeException(e);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(CreateShop.this, "An error occurred. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                    multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 1));
                    requestQueue.add(multipartRequest);
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main_layout), params.toString(), Snackbar.LENGTH_LONG);
                    snackbar.show();

                    Toast.makeText(this, params.toString(), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void fetchShopImage() {
        Intent fetchImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fetchImage.setType("image/*");
        startActivityForResult(fetchImage, SELECT_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream);
                shopImage.setImageBitmap(selectedImageBitmap);
                shopImageSelected = true;
                shopImage.setMaxHeight(250);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "uri: " + selectedImage, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Result code: " + resultCode, Toast.LENGTH_SHORT).show();
        }
    }

    void stringGetRequest(String url, int requestCode){
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray;
                JSONObject jsonObject;
                try {
                    jsonArray = new JSONArray(response);
                    if (requestCode == 1){
                        shopCategories = jsonArray;
                        for (int i = 0; i <jsonArray.length(); i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            categories.add(jsonObject.getString("category"));
                        }
                    }else if(requestCode == 2){
                        paymentMethods = jsonArray;
                            for (int i = 0; i <jsonArray.length(); i++){
                                jsonObject = jsonArray.getJSONObject(i);
                                methods.add(jsonObject.getString("payment method"));
                            }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> {

        });
        MyRequestHandler.getInstance(this).addTorequestQueue(request);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            JSONObject jsonObject;
            if(position > 0){
                if (parent.getId() == R.id.select_shop_category){
                    jsonObject = new JSONObject(shopCategories.get(position - 1).toString());
                    params.put("category",jsonObject.get("id").toString());
                    categorySelected = true;
                    //Toast.makeText(this, "params: " +params.toString(), Toast.LENGTH_SHORT).show();
                } else if (parent.getId() == R.id.select_payment_method) {
                    jsonObject = new JSONObject(paymentMethods.get(position - 1).toString());
                    params.put("payment",jsonObject.get("id").toString());
                    paymentMethodSelected = true;
                    Toast.makeText(this, "params: " +params.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}