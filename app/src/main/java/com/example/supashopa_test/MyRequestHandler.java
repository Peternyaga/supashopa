package com.example.supashopa_test;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyRequestHandler {
    private static MyRequestHandler mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private MyRequestHandler(Context context){
        mCtx=context;
        mRequestQueue=getRequestqueue();
    }

    public static synchronized MyRequestHandler getInstance(Context context){
        if(mInstance==null){
            mInstance=new MyRequestHandler(context);
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
