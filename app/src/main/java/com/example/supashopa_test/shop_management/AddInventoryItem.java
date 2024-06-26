package com.example.supashopa_test.shop_management;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.supashopa_test.Constants;
import com.example.supashopa_test.MultipartRequest;
import com.example.supashopa_test.MyRequestHandler;
import com.example.supashopa_test.R;

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

public class AddInventoryItem extends AppCompatActivity implements View.OnClickListener {
    EditText itemName, itemDescription, itemPrice;
    Spinner itemCategory;
    ImageView itemImage;
    Button pickImage, addItem;
    final int SELECT_IMAGE_REQUEST_CODE = 102;
    ActivityResultLauncher<Intent> fetchImageLauncher;
    Bitmap selectedImage = null;
    ArrayList<String> itemCategories = new ArrayList<>();
    Map<String, String> params = new HashMap<>();
    Map<String, String> headers = new HashMap<>();

    JSONArray categories = new JSONArray();
    ArrayAdapter<String> itemCategoriesAdapter;
    boolean categoryIsSelected = false;
    boolean imageIsSelected = false;
    SharedPreferences mySp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory_item);
        mySp = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        fetchItemCategories();
        itemCategories.add("Select item category");
        itemName = findViewById(R.id.addInventoryItemName);
        itemDescription = findViewById(R.id.addInventoryItemDescription);
        itemPrice = findViewById(R.id.addInventoryItemPrice);
        itemCategory = findViewById(R.id.addInventoryItemCategory);
        itemCategoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,itemCategories);
        itemCategory.setAdapter(itemCategoriesAdapter);
        itemCategory.setSelection(0,false);
        itemCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                   return;
                }
                try {
                    JSONObject selectedCategory = new JSONObject(categories.getString(position-1));
                    params.put("category", selectedCategory.getString("id"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                categoryIsSelected = true;
                itemCategory.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        itemImage = findViewById(R.id.addInventoryItemImage);
        pickImage = findViewById(R.id.pickInventoryItemImage);
        pickImage.setOnClickListener(this);
        addItem = findViewById(R.id.btnAddInventoryItem);
        addItem.setOnClickListener(this);

        fetchImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            File file = new File(getCacheDir(), "compressed_image.jpg");
                            try (FileOutputStream out = new FileOutputStream(file)) {
                                BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG,50, out);
                                selectedImage = new Compressor(this).compressToBitmap(file);
                                //selectedImage = BitmapFactory.decodeFile(file.getAbsolutePath());
                                //selectedImage.
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                           // itemImage.setImageURI(file.);
                            itemImage.setImageBitmap(selectedImage);
                            imageIsSelected = true;
                        } catch (FileNotFoundException e) {
                            Toast.makeText(this, "file not found", Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
    }

    private void fetchItemCategories(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.fetchInventoryItemCategories, response -> {
            JSONArray jsonArray;
            JSONObject jsonObject;
            try {
                jsonArray = new JSONArray(response);
                    categories = jsonArray;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        itemCategories.add(jsonObject.getString("category"));
                        itemCategoriesAdapter.notifyDataSetChanged();
                    }
                }catch(JSONException e){

                }
        }, error -> {

        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 3, 2));
        MyRequestHandler.getInstance(this).addTorequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pickInventoryItemImage:
                Intent fetchImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                fetchImage.setType("image/*");
                fetchImageLauncher.launch(fetchImage);
                break;
            case R.id.btnAddInventoryItem:
                Toast.makeText(this, "**********************", Toast.LENGTH_SHORT).show();
                String name = itemName.getText().toString().trim();
                String description = itemDescription.getText().toString().trim();
                int price = Integer.parseInt(itemPrice.getText().toString().trim());
                if (imageIsSelected && categoryIsSelected && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(description) && price>0){
                   params.put("shop_id", Constants.currentShopId);
                   params.put("item_name", name);
                   params.put("unit_price", String.valueOf(price));
                   params.put("status", "4");
                   params.put("shop_name", mySp.getString("shop_name", ""));
                   headers.put("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary123");
                }
               MultipartRequest multipartRequest = new MultipartRequest(Constants.addInventoryItem, null, params, "image/png", selectedImage, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers)));
                            Toast.makeText(AddInventoryItem.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            if (!jsonObject.getBoolean("error")){

                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        Toast.makeText(AddInventoryItem.this, "******"+response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        itemDescription.setText(error.getMessage());
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 1));
                requestQueue.add(multipartRequest);
        }
    }
}