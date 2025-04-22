package com.supashop.supashop;

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
import android.widget.TextView;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class CreateShop extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final int SELECT_IMAGE_REQUEST_CODE = 1000;
    ScrollView scrollView;
    EditText shopName;
    private TextView tvShortcode, tvAccountRef;
    private EditText enterShortcode, enterAccountRef;
    static  EditText shopDescription;
    Button selectImage, createShop;
    ImageView shopImage;
    Spinner shopsCategoriesSpinner, paymentMethodsSpinner;
    Boolean categorySelected = false;
    Boolean paymentMethodSelected = false;
    Boolean shopImageSelected = false;
    JSONArray shopCategories = new JSONArray();
    JSONArray paymentMethods = new JSONArray();
    ArrayList<String> categories = new ArrayList<>();
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
        tvShortcode = findViewById(R.id.tv_shortcode);
        tvAccountRef = findViewById(R.id.tv_accountRef);
        enterShortcode = findViewById(R.id.enter_shortcode);
        enterAccountRef = findViewById(R.id.enter_account_ref);
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
        selectImage.setOnClickListener(this);

        stringGetRequest(Constants.URL_FETCHSHOPCATEGORIES, 1);
        stringGetRequest(Constants.URL_FETCHPAYMENTMETHODS, 2);

        setPaymentFieldsVisibility(View.GONE, View.GONE);

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
                if (!shopName.getText().toString().trim().isEmpty() &&
                        !shopDescription.getText().toString().trim().isEmpty() &&
                        !enterShortcode.getText().toString().trim().isEmpty() &&
                        categorySelected && paymentMethodSelected && shopImageSelected){

                    // Get payment method ID
                    String paymentMethodId = params.get("payment");

                    // Basic validation
                    if (paymentMethodId.equals("2") && enterAccountRef.getText().toString().trim().isEmpty()) {
                        Toast.makeText(this, getString(R.string.account_number_required), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (ShopsActivity.myLatitude == null || ShopsActivity.myLongitude == null){
                        Toast.makeText(this, getString(R.string.location_access_failed), Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Add parameters based on payment method
                    params.put("shop_name", shopName.getText().toString().trim());
                    params.put("shop_description", shopDescription.getText().toString().trim());

                    if (paymentMethodId.equals("1")) {
                        // Till Number
                        params.put("mpesa_till", enterShortcode.getText().toString().trim());
                    } else if (paymentMethodId.equals("2")) {
                        // Paybill Number + Account Number
                        params.put("mpesa_paybill", enterShortcode.getText().toString().trim());
                        params.put("account_number", enterAccountRef.getText().toString().trim());
                    }

                    params.put("latitude", ShopsActivity.myLatitude.toString());
                    params.put("longitude", ShopsActivity.myLongitude.toString());
                    headers.put("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary123");

                    // Continue with your existing MultipartRequest...
                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    MultipartRequest multipartRequest = new MultipartRequest(
                            Constants.urlCreateShop,
                            null,
                            params,
                            "image/png",
                            selectedImageBitmap,
                            new Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers)));
                                        if (!jsonObject.getBoolean("error")){
                                            scrollView.removeAllViews();
                                            getSupportFragmentManager().beginTransaction()
                                                    .add(R.id.create_shop_scroller,  CreateShopAdminFragment.newInstance(jsonObject.getString("shop_id"), params.get("shop_name")))
                                                    .commit();
                                            Toast.makeText(CreateShop.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                            shopDescription.setText(jsonObject.toString());
                                        }else{
                                            Snackbar.make(findViewById(android.R.id.content), jsonObject.getString("message"), Snackbar.LENGTH_LONG).show();
                                        }
                                    } catch (UnsupportedEncodingException | JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(CreateShop.this, getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );

                    // Rest of your code for request handling
                    multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 1));
                    requestQueue.add(multipartRequest);
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main_layout), params.toString(), Snackbar.LENGTH_LONG);
                    snackbar.show();

                    Toast.makeText(this, params.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.all_fields_required), Toast.LENGTH_LONG).show();
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
                //selectedImageBitmap = BitmapFactory.decodeStream(inputStream);
                File file = new File(getCacheDir(), "compressed_image.jpg");
                try (FileOutputStream out = new FileOutputStream(file)) {
                        BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG,100, out);
                    while (file.length()/1024 > 500){
                        file = new Compressor(this).compressToFile(file);
                    }
                    selectedImageBitmap = new Compressor(this).compressToBitmap(file);
                    shopImage.setImageBitmap(selectedImageBitmap);
                    shopImageSelected = true;
                    shopImage.setMaxHeight(250);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Image not selected!", Toast.LENGTH_SHORT).show();
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
                } else if (parent.getId() == R.id.select_payment_method) {
                    jsonObject = new JSONObject(paymentMethods.get(position - 1).toString());
                    String paymentId = jsonObject.get("id").toString();
                    params.put("payment", paymentId);
                    paymentMethodSelected = true;

                    // Handle payment method selection
                    if (paymentId.equals("1")) {
                        // For payment method 1 (Till Number)
                        tvShortcode.setText(R.string.enter_till_number);
                        enterShortcode.setHint(R.string.enter_till_number_hint);
                        setPaymentFieldsVisibility(View.VISIBLE, View.GONE);
                    } else if (paymentId.equals("2")) {
                        // For payment method 2 (Paybill Number and Account Number)
                        tvShortcode.setText(R.string.enter_paybill_number);
                        enterShortcode.setHint(R.string.enter_paybill_number_hint);
                        tvAccountRef.setText(R.string.enter_account_number);
                        enterAccountRef.setHint(R.string.enter_account_number_hint);
                        setPaymentFieldsVisibility(View.VISIBLE, View.VISIBLE);
                    } else {
                        // No valid selection or other payment methods
                        setPaymentFieldsVisibility(View.GONE, View.GONE);
                    }

                    Toast.makeText(this, "params: " + params.toString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Reset visibility when "select your payment method" is chosen
                if (parent.getId() == R.id.select_payment_method) {
                    setPaymentFieldsVisibility(View.GONE, View.GONE);
                    paymentMethodSelected = false;
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Helper method to set visibility of payment fields
    private void setPaymentFieldsVisibility(int shortcodeVisibility, int accountRefVisibility) {
        tvShortcode.setVisibility(shortcodeVisibility);
        enterShortcode.setVisibility(shortcodeVisibility);
        tvAccountRef.setVisibility(accountRefVisibility);
        enterAccountRef.setVisibility(accountRefVisibility);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}