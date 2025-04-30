package com.supashop.supashop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class signUpActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edittextfirstname, edittextlastname, edittextphone, edittextemail, edittextpassword,edittextconfirmpassword;
    private Button buttonsignup, buttongotologin;
    private ProgressDialog progressDialog;
    String first_name, last_name, phone, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edittextfirstname= findViewById(R.id.et_firstname);
        edittextlastname= findViewById(R.id.et_lastname);
        edittextphone= findViewById(R.id.et_phone);
        edittextemail= findViewById(R.id.et_email);
        edittextpassword= findViewById(R.id.et_password);
        edittextconfirmpassword= findViewById(R.id.et_confirmpassword);

        buttonsignup= findViewById(R.id.btn_signup);
        buttongotologin=findViewById(R.id.btn_gotologin);
        progressDialog=new ProgressDialog(this);
        buttonsignup.setOnClickListener(this);
        buttongotologin.setOnClickListener(this);

    }

    private void registerUser() {
       //String confirm_password=edittextconfirmpassword.getText().toString().trim();
       final String user_id="";
        progressDialog.setMessage("registering user...");
        progressDialog.show();
        StringRequest StringRequest=new StringRequest(Request.Method.POST, Constants.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();

                            if (jsonObject.getString("message").equals("user registered successfully")){
                                progressDialog.show();

                                /*the block below delays opening the log in activity for 3 seconds for a smooth transition*/

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(getApplicationContext(),login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                },3000);
                                                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                AlertDialog.Builder builder=new AlertDialog.Builder(signUpActivity.this);
                builder.setTitle("CONNECTION PROBLEM!");
               // builder.setMessage(error.getMessage());
                builder.setMessage("OOOPS! \nCannot get to the server at the moment");
                builder.setCancelable(true);

                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("user_id",user_id);
                params.put("first_name",first_name);
                params.put("last_name",last_name);
                params.put("phone",phone);
                params.put("email",email);
                params.put("password",password);
                params.put("address","");
                params.put("city_town","");
                params.put("residence","");

                return params;

            }
        };
       StringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 3,2));
        MyRequestHandler.getInstance(this).addTorequestQueue(StringRequest);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_signup) {
            String first = edittextfirstname.getText().toString().trim();
            String last  = edittextlastname.getText().toString().trim();
            String phoneNo = edittextphone.getText().toString().trim();
            String mail  = edittextemail.getText().toString().trim();
            String pwd   = edittextpassword.getText().toString();
            String conf  = edittextconfirmpassword.getText().toString();

            // 1. Non-empty
            if (first.isEmpty() || last.isEmpty() || phoneNo.isEmpty() || mail.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Clean input by removing common formatting characters (optional)
            String cleanedPhone = phoneNo.replaceAll("[\\s\\-()]", "");

            if (cleanedPhone.startsWith("+")) {
                // International format: + followed by 7–15 digits, no leading zeros after country code
                if (!cleanedPhone.matches("^\\+[1-9]\\d{6,14}$")) {
                    edittextphone.setError("Invalid international phone number format");
                    edittextphone.requestFocus();
                    return;
                }
            } else {
                // Local format: e.g., starts with 07, 01 (Kenya mobile numbers), 10 digits
                if (!cleanedPhone.matches("^(07|01)\\d{8}$")) {
                    edittextphone.setError("Invalid local phone number format");
                    edittextphone.requestFocus();
                    return;
                }
            }

            // 3. Email format
            if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                edittextemail.setError("Invalid email");
                edittextemail.requestFocus();
                return;
            }


            // 4. Password strength
//            if (!pwd.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z]).{8,}$")) {
//                edittextpassword.setError("Weak password");
//                edittextpassword.requestFocus();
//                return;
//            }
            // 5. Confirm password
            if (!pwd.equals(conf)) {
                edittextconfirmpassword.setError("Passwords do not match");
                edittextconfirmpassword.requestFocus();
                return;
            }
            // Everything’s valid!
            first_name = first;
            last_name  = last;
            phone      = phoneNo;
            email      = mail;
            password   = pwd;
            registerUser();
        }
        else if (v.getId() == R.id.btn_gotologin) {
            startActivity(new Intent(this, login.class));
            finish();
        }
    }

}
