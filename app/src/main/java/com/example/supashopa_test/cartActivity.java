package com.example.supashopa_test;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class cartActivity extends AppCompatActivity implements OnItemClickInterface,View.OnClickListener{
    RecyclerView recyclerView;
    ProgressBar progressBar;
    EditText confirmPhoneNumber;
    TextView tvtotal, tvProgressbarMsg, tvCheckoutTotal, tvCheckoutShop, tvServiceCost, tvGrandTotal;
    Button btncheckout, confirmCheckoutDetails;
    LinearLayoutCompat linearLayout, checkoutDetails;
    ArrayList<String> cartItems;
    ArrayList<CartItems> arrayList;
    JSONArray finalCheckoutList;
    double total;
    String shop; //name of shop database passed from shopping activity
    private CartRecyclerviewAdapter adapter;
    SharedPreferences mySP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mySP=getSharedPreferences("MySharedPref", MODE_PRIVATE);
        recyclerView=findViewById(R.id.rvcartitems);
        progressBar=findViewById(R.id.cartprogressbar);
        linearLayout=findViewById(R.id.linearLayout);
        linearLayout.bringToFront();
        tvtotal=findViewById(R.id.tvtotal);
        tvProgressbarMsg =findViewById(R.id.tvprogressbarmsg);
        tvProgressbarMsg.setVisibility(View.GONE);
        btncheckout=findViewById(R.id.btncheckout);
        btncheckout.setOnClickListener(this);
        checkoutDetails=findViewById(R.id.mpesaPaymentDetails);
        confirmPhoneNumber=findViewById(R.id.et_paymentMpesaPhoneNumber);
        tvCheckoutTotal =findViewById(R.id.tv_paymentMpesaTotal);
        tvServiceCost =findViewById(R.id.tv_servicecost);
        tvGrandTotal=findViewById(R.id.tv_grandtotal);
        tvCheckoutShop =findViewById(R.id.tv_paymentShopname);
        confirmCheckoutDetails=findViewById(R.id.btn_paymentDetailsOk);
        confirmCheckoutDetails.setOnClickListener(this);

        Intent intent=getIntent();
        cartItems=intent.getStringArrayListExtra("cart items");
        shop=intent.getStringExtra("shop");
        arrayList=new ArrayList<>();

        for (int i=0; i<cartItems.size(); i++){
            try {
                JSONObject jsonObject=new JSONObject(cartItems.get(i));
                int itemid = jsonObject.getInt("product_id");
                String itemname = jsonObject.getString("name");
                String itemImage;
                if (jsonObject.getString("product_picture").contains(Constants.IMAGESOFITEMS_ROOT_URL)){
                    itemImage=jsonObject.getString("product_picture");
                }else {
                    itemImage = Constants.IMAGESOFITEMS_ROOT_URL + Constants.shopName + "/" + jsonObject.getString("product_picture");
                }
                String price = jsonObject.getString("unit_price");
                String category = jsonObject.getString("category");
                int quantity=jsonObject.getInt("quantity");

                arrayList.add(new CartItems(itemid,itemname, itemImage, price, category, quantity));
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            } catch (JSONException e) {
                e.printStackTrace();
                tvtotal.setText(e.getMessage());
            }
        }
        buildRecyclerView();
        setTotal(arrayList);
    }

    public void cartIsEmpty(){
        if(arrayList.size()==0){
            Toast.makeText(getApplicationContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
            btncheckout.setEnabled(false);
            btncheckout.bringToFront();
            progressBar.setVisibility(View.GONE);
            tvProgressbarMsg.setVisibility(View.VISIBLE);
            tvProgressbarMsg.setText("your cart is empty!");
            tvProgressbarMsg.bringToFront();
        }
    }

    private void checkOut(){
        /*creating a list of checkout items ids with their corresponding quantity. The arraylist is then converted and assigned to a string that will be passed as a parameter
        * while sending the request*/
        try {
        final String userId=mySP.getString("userId", null);
        int id,quantity;
        JSONObject idqnty;
        finalCheckoutList=new JSONArray();
        for (int i=0; i<arrayList.size(); i++){
            idqnty=new JSONObject();
            id=arrayList.get(i).getProductid();
            quantity=arrayList.get(i).getQuantity();
                idqnty.put("item id",id);
                idqnty.put("quantity", quantity);
                finalCheckoutList.put(idqnty);
        }
        final String finalItems=finalCheckoutList.toString();
        //Double total;
            /*sending the checkout request*/
        final StringRequest submitOrder=new StringRequest(Request.Method.POST, Constants.URL_CHECKOUT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                tvProgressbarMsg.setText("Please wait.");
                Toast.makeText(cartActivity.this, "response: "+ response, Toast.LENGTH_LONG).show();
                try {
                    final JSONObject jsonObject=new JSONObject(response);
                    int responseCode=jsonObject.getInt("responsecode");
                    if(responseCode==0){
                        final String sessionId=jsonObject.getString("sessionid");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                StringRequest checkOrderStatus=new StringRequest(Request.Method.POST, Constants.CHECK_ORDER_STATUS, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressBar.setVisibility(View.GONE);
                                        tvProgressbarMsg.setVisibility(View.GONE);
                                        //tvProgressbarMsg.setText(response);
                                        Toast.makeText(cartActivity.this, "checkOrderStatus response: "+response, Toast.LENGTH_SHORT).show();
                                        String alertMessage="";
                                        int alertCode = 3;
                                        try {
                                            if (!response.equals("null")) {
                                                JSONObject orderStatus=new JSONObject(response);
                                                if (orderStatus.getInt("payment status") == 1) {
                                                    alertCode=0;
                                                    alertMessage = "your order has been received!";
                                                } else {
                                                    alertCode=1;
                                                    alertMessage = "Transaction was unsuccessful.\nTry checking your Mpesa details and try again.";
                                                }
                                            }else {
                                                alertCode=2;
                                                alertMessage="Transaction unsuccessful.\nTry checking your Mpesa details and try again.";
                                            }
                                            final AlertDialog.Builder builder=new AlertDialog.Builder(cartActivity.this);
                                            builder.setTitle("CHECK OUT");
                                            builder.setMessage(alertMessage);
                                            builder.setCancelable(false);
                                            final String finalAlertMessage = alertMessage;
                                            final int finalAlertCode= alertCode;
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (finalAlertCode==0) {
                                                        Intent intent = new Intent(getApplicationContext(), shoppingActivity.class);
                                                        intent.putExtra("shopDbName", Constants.shopDbName);
                                                        startActivity(intent);
                                                        finish();
                                                    }else{
                                                        confirmCheckoutDetails.setEnabled(true);
                                                    }
                                                }

                                            });
                                            if (finalAlertCode>0){
                                                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        builder.setTitle("ALERT");
                                                        builder.setMessage("Your order will be discarded. \nDo you wish to abandon your order?");
                                                        builder.setCancelable(false);
                                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(getApplicationContext(), shoppingActivity.class);
                                                                intent.putExtra("shopDbName", Constants.shopDbName);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                confirmCheckoutDetails.setEnabled(true);
                                                            }
                                                        });
                                                        AlertDialog alert=builder.create();
                                                        alert.show();
                                                    }
                                                });
                                            }

                                            AlertDialog alert=builder.create();
                                            alert.show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            //run();
                                            Toast.makeText(cartActivity.this, "check order status: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }){
                                    @Nullable
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params=new HashMap();
                                        params.put("session_id", sessionId);
                                        params.put("shop", Constants.shopDbName);
                                        return params;
                                    }
                                };
                                requesthandler.getInstance(cartActivity.this).addTorequestQueue(checkOrderStatus);
                            }
                        }, 35000);
                        //arrayList.clear();
                        //Snackbar.make(btncheckout,jsonObject.getString("message")+ ". You will be notified once your package is ready. \n THANK YOU FOR SHOPPING WITH US!!", Snackbar.LENGTH_LONG).show();


                    }else {
                        progressBar.setVisibility(View.GONE);
                        tvProgressbarMsg.setVisibility(View.GONE);
                        confirmCheckoutDetails.setEnabled(true);
                        AlertDialog.Builder builder=new AlertDialog.Builder(cartActivity.this);
                        builder.setTitle("ERROR!");
                        builder.setMessage(jsonObject.getString("message"));
                        builder.setCancelable(true);
                        AlertDialog dialog=builder.create();
                        dialog.show();
                        //Toast.makeText(cartActivity.this, "An error occured. Please try again", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(cartActivity.this,"An error occured: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("userId", String.valueOf(userId));
                params.put("total", String.valueOf((int)total));
                params.put("service_cost", ""+calculateServiceCost(total));
                params.put("grand_total", ""+(total+calculateServiceCost(total)));
                params.put("items", finalItems);
                params.put("shop_id", ""+ mySP.getInt("shop_code",0));
                params.put("shop", mySP.getString("shop_db_name",""));
                params.put ("shop_name", mySP.getString("shop_name", ""));
                params.put("phone_number", confirmPhoneNumber.getText().toString().trim());
                return params;
                //return super.getParams();
            }
        };
            submitOrder.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1));

            requesthandler.getInstance(this).addTorequestQueue(submitOrder);

        } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void setTotal(ArrayList<CartItems> arrayList){
        cartIsEmpty();
        total=0.00;
        tvtotal.setText("KSh. "+ total);

        for (int i=0; i<arrayList.size();i++) {
            total += arrayList.get(i).getQuantity() * Double.parseDouble(arrayList.get(i).getPrice());
        }
        DecimalFormat decimalFormat=new DecimalFormat("####.00");
        tvtotal.setText("KSh "+decimalFormat.format(total));
        tvtotal.bringToFront();
        this.arrayList=arrayList;
    }
    @Override
    public void removecartitem(int index) {
        arrayList.remove(index);
        setTotal(arrayList);
        buildRecyclerView();
    }

    private void buildRecyclerView(){
        adapter=new CartRecyclerviewAdapter(arrayList, cartActivity.this, this);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        cartItems.clear();
        JSONObject returnedItems=new JSONObject();
        Intent intent=new Intent(this, shoppingActivity.class);
        for(int i=0; i<arrayList.size(); i++){
            try {
                returnedItems.put("product_id", arrayList.get(i).getProductid());
                returnedItems.put("name", arrayList.get(i).getProductname());
                returnedItems.put("product_picture", arrayList.get(i).getProductpic_url());
                returnedItems.put("unit_price", arrayList.get(i).getPrice());
                returnedItems.put("category", arrayList.get(i).getCategory());
                returnedItems.put("quantity", arrayList.get(i).getQuantity());

                cartItems.add(returnedItems.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        intent.putStringArrayListExtra("cart items", cartItems);
        setResult(2,intent);
        finish();
    }

    @Override
    public void onClickAddItemtoCart(int position, int numberofitems) {

    }

    @Override
    public void changeCartItems(int position, int quantity) {

    }

   /* @Override
    public void setTotal(ArrayList<com.example.supashopa_test.CartItems> arrayList) {

    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btncheckout:
                checkoutDetails.setVisibility(View.VISIBLE);
                checkoutDetails.setClickable(true);
                checkoutDetails.bringToFront();
                btncheckout.setEnabled(false);
                recyclerView.setLayoutFrozen(true);
                confirmPhoneNumber.setText(mySP.getString("phone", ""));
                tvCheckoutTotal.setText("KSh. " + total + "0");
                int serviceCost=calculateServiceCost(total);
                tvServiceCost.setText("KSh. "+serviceCost+".00");
                double grandTotal=total+serviceCost;
                tvGrandTotal.setText("KSh. "+ grandTotal +"0");
                tvCheckoutShop.setText(Constants.shopName);
                break;

            case R.id.btn_paymentDetailsOk:
                confirmCheckoutDetails.setEnabled(false);//disables the button to prevent the user from sending multiple requests
                tvProgressbarMsg.setText("sending order...");
                progressBar.setVisibility(View.VISIBLE);
                tvProgressbarMsg.setVisibility(View.VISIBLE);
                progressBar.bringToFront();
                tvProgressbarMsg.bringToFront();
                recyclerView.setEnabled(false);
                btncheckout.setEnabled(false);
                confirmPhoneNumber.clearFocus();
                checkOut();
        }
    }

    private int calculateServiceCost(double total){
        int serviceCost= (int) (total*0.01);
        return serviceCost;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Constants.shopName==null){
            Constants.shopDbName=mySP.getString("shop_db_name", "");
            Constants.shopName=mySP.getString("shop_name", "");
        }
    }
}
