package com.example.supashopa_test.ui.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.supashopa_test.ShopsActivity;
import com.example.supashopa_test.signUpActivity;

public class SplashScreenActivity extends AppCompatActivity {
    SharedPreferences mySP;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash_screen);
        mySP=getSharedPreferences("MySharedPref", MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!(mySP.contains("phone")&&mySP.contains("password"))){
                    intent=new Intent(SplashScreenActivity.this, signUpActivity.class);
                }else {
                    intent = new Intent(SplashScreenActivity.this, ShopsActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
    //#0x7f070078
}
