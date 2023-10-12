package com.example.supashopa_test;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class requesthandler {
    private static requesthandler mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private requesthandler(Context context){
        mCtx=context;
        mRequestQueue=getRequestqueue();
    }

    public static synchronized requesthandler getInstance(Context context){
        if(mInstance==null){
            mInstance=new requesthandler(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestqueue(){
        if(mRequestQueue==null){
            mRequestQueue= Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public<T> void addTorequestQueue(Request<T> req){
       getRequestqueue().add(req);
    }
}
