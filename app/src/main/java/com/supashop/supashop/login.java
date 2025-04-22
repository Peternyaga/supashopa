package com.supashop.supashop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class login extends AppCompatActivity implements View.OnClickListener {
    Button gotosignup, loginbtn;
    EditText editTextphone, editTextpassword;
    ProgressBar progressBar;

    SharedPreferences mySP;
    SharedPreferences.Editor mySPEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        gotosignup=findViewById(R.id.btn_gotologin);
        gotosignup.setOnClickListener(this);
        loginbtn=findViewById(R.id.btn_login);
        loginbtn.setOnClickListener(this);
        editTextphone=findViewById(R.id.et_phone);
        editTextpassword=findViewById(R.id.et_password);
        progressBar=findViewById(R.id.progressbar);

        mySP=getSharedPreferences("MySharedPref", MODE_PRIVATE);
        mySPEditor=mySP.edit();

    }


    private void login(){
       final String phone=editTextphone.getText().toString().trim();
       final String password=editTextpassword.getText().toString().trim();
       progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if (jsonObject.getString("error").equals("false")){
                                mySPEditor.putString("phone", phone);
                                mySPEditor.putString("password", password);
                                mySPEditor.putString("userId", jsonObject.getString("user id"));
                                mySPEditor.commit();
                                finish();
                                Intent intent=new Intent(getApplicationContext(),ShopsActivity.class);
                                startActivity(intent);

                            }else{
                                Toast.makeText(login.this, "incorrect details", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                String Error=error.toString();
                Toast.makeText(getApplicationContext(),"connection failed" ,Toast.LENGTH_LONG).show();


            }
        }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();

                params.put("phone",phone);
                params.put("password",password);
                return params;
                //return super.getParams();
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
        MyRequestHandler.getInstance(this).addTorequestQueue(stringRequest);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_gotologin:
                Intent intent=new Intent(getApplicationContext(), signUpActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_login:
                login();


        }
    }
}
//192.168.137.109