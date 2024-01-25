package com.example.supashopa_test;

import android.graphics.Bitmap;
import androidx.annotation.Nullable;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<NetworkResponse> {

    private final Response.Listener<NetworkResponse> mListener;
    private final Map<String, String> mHeaders;
    private final Map<String, String> mParams;
    private final String mMimeType;
    private final Bitmap mImage;

    public MultipartRequest(String url, Map<String, String> headers, Map<String, String> params,
                            String mimeType, Bitmap image,
                            Response.Listener<NetworkResponse> listener,
                            @Nullable Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mHeaders = headers;
        mParams = params;
        mMimeType = mimeType;
        mImage = image;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + "*****";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            buildMultipartEntity(byteArrayOutputStream);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void buildMultipartEntity(ByteArrayOutputStream outputStream) throws IOException {
        // Start writing the multipart/form-data
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        // Add image to the request
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        mImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        dataOutputStream.writeBytes("--*****\r\n");
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"image.png\"\r\n");
        dataOutputStream.writeBytes("Content-Type: " + mMimeType + "\r\n\r\n");
        dataOutputStream.write(imageBytes, 0, imageBytes.length);
        dataOutputStream.writeBytes("\r\n");

        // Add other form fields to the request
        for (Map.Entry<String, String> entry : mParams.entrySet()) {
            dataOutputStream.writeBytes("--*****\r\n");
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
            dataOutputStream.writeBytes(entry.getValue() + "\r\n");
        }

        // Mark the end of the multipart/form-data
        dataOutputStream.writeBytes("--*****--\r\n");


        //CreateShop.shopDescription.setText(dataOutputStream.toString());
        //CreateShop.shopDescription.setHeight(500);

        dataOutputStream.flush();
        dataOutputStream.close();
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }
}
