package com.example.supashopa_test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateShop extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final int SELECT_IMAGE_REQUEST_CODE = 1000;
    Button selectImage;
    ImageView shopImage;
    Spinner shopsCategoriesSpinner, paymentMethodsSpinner;
    JSONArray shopCategories = new JSONArray();
    JSONArray paymentMethods = new JSONArray();
    ArrayList<String> categories = new ArrayList();
    ArrayList<String> methods = new ArrayList<>();
    Map params = new HashMap<>();
    ArrayAdapter<String> shopCategoriesAdapter, paymentMethodsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shop);
        Toolbar myToolBar = findViewById(R.id.create_shop_toolbar);
        setSupportActionBar(myToolBar);

        shopImage = findViewById(R.id.select_shop_image);
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

            case R.id.select_shop_category:

        }
    }

    private void fetchShopImage() {
        Intent fetchImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fetchImage.setType("image/*");
        startActivityForResult(fetchImage, SELECT_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                Bitmap selectedImageBitmap = BitmapFactory.decodeStream(inputStream);

                shopImage.setImageBitmap(selectedImageBitmap);
                shopImage.setMaxHeight(250);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "uri: " + selectedImage, Toast.LENGTH_SHORT).show();
        }else{
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
        requesthandler.getInstance(this).addTorequestQueue(request);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            JSONObject jsonObject;
            if(position > 0){
                if (parent.getId() == R.id.select_shop_category){
                    jsonObject = new JSONObject(shopCategories.get(position - 1).toString());
                    params.put("shop_category",jsonObject.get("id").toString());
                    Toast.makeText(this, "params: " +params.toString(), Toast.LENGTH_SHORT).show();                    Toast.makeText(this, "selected item: SELECT CATEGORY" , Toast.LENGTH_SHORT).show();
                } else if (parent.getId() == R.id.select_payment_method) {
                    jsonObject = new JSONObject(paymentMethods.get(position - 1).toString());
                    params.put("shop_category",jsonObject.get("id").toString());
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