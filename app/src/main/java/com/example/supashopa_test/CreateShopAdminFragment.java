package com.example.supashopa_test;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateShopAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateShopAdminFragment extends Fragment {
    TextView shopName;
    EditText firstName, otherNames, phone, password, confirmPass;
    Button submitButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String shopId;
    private String name;

    public CreateShopAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateShopAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateShopAdminFragment newInstance(String param1, String param2) {
        CreateShopAdminFragment fragment = new CreateShopAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shopId = getArguments().getString(ARG_PARAM1);
            name = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_create_shop_admin, container, false);
        shopName = view.findViewById(R.id.tv_shop_name);
        shopName.setText(name);
        firstName = view.findViewById(R.id.et_first_name);
        otherNames = view.findViewById(R.id.et_other_names);
        phone = view.findViewById(R.id.et_admin_phone);
        password = view.findViewById(R.id.et_admin_pass);
        confirmPass = view.findViewById(R.id.et_confirm_admin_pass);
        submitButton = view.findViewById(R.id.btn_create_admin);

        // Set a click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the input values
                String firstNameStr = firstName.getText().toString();
                String otherNamesStr = otherNames.getText().toString();
                String phoneStr = phone.getText().toString();
                String passwordStr = password.getText().toString();
                String confirmPassStr = confirmPass.getText().toString();

                // Validate inputs
                if (validateInputs(firstNameStr, otherNamesStr, phoneStr, passwordStr, confirmPassStr)) {
                    createShopAdmin(firstNameStr, otherNamesStr, phoneStr, passwordStr);
                }
            }
        });

        return view;
    }

    private boolean validateInputs(String firstName, String otherNames, String phone, String password, String confirmPass) {
        if (firstName.isEmpty()) {
            showToast("First name is required");
            return false;
        }

        if (otherNames.isEmpty()) {
            showToast("Other names are required");
            return false;
        }

        if (phone.isEmpty()) {
            showToast("Phone number is required");
            return false;
        }

        if (password.isEmpty()) {
            showToast("Password is required");
            return false;
        }

        if (!password.equals(confirmPass)) {
            showToast("Passwords do not match");
            return false;
        }

        // Additional validation rules can be added here

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void createShopAdmin(String firstName, String otherNames, String phone, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.registerShopkeeper, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")){
                        showToast(jsonObject.getString("message"));
                    }else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setMessage(jsonObject.getString("message") + "\n" + jsonObject.getString("details"));
                        alertDialog.setCancelable(true);
                        alertDialog.create().show();
                    }
                } catch (JSONException e) {
                    showToast(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast(error.getMessage());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("shop_id", shopId);
                params.put("first_name", firstName);
                params.put("other_names", otherNames);
                params.put("phone", phone);
                params.put("password", password);
                params.put("role", "2");

                return params;
            }
        };
        MyRequestHandler.getInstance(getContext()).addTorequestQueue(stringRequest);
    }

}