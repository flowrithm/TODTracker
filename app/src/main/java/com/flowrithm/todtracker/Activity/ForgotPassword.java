package com.flowrithm.todtracker.Activity;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.flowrithm.todtracker.Application;
import com.flowrithm.todtracker.R;
import com.flowrithm.todtracker.Utils.Utils;
import com.flowrithm.todtracker.WebApi.Api;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.etOTP)
    TextInputLayout etOTP;

    @Bind(R.id.etNewPassword)
    TextInputLayout etNewPassword;

    @Bind(R.id.etConfirmPassword)
    TextInputLayout etConfirmPassword;

    @Bind(R.id.btnSave)
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        btnSave.setOnClickListener(this);
    }

    public void ForgotPassword(){
        final KProgressHUD progress=Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, Api.POST_FORGOT_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Toast.makeText(ForgotPassword.this,json.getString("Message"),Toast.LENGTH_LONG).show();
                    if (json.getBoolean("Success")) {
                        ForgotPassword.this.setResult(RESULT_OK);
                        ForgotPassword.this.finish();
                    }
                } catch (Exception ex) {
                    Crashlytics.logException(ex);
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(ForgotPassword.this,
                            ForgotPassword.this.getString(R.string.error_network_timeout),
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                    Crashlytics.log(error.getMessage());
                } else if (error instanceof ServerError) {
                    //TODO
                    Crashlytics.log(error.getMessage());
                } else if (error instanceof NetworkError) {
                    //TODO
                    Crashlytics.log(error.getMessage());
                } else if (error instanceof ParseError) {
                    //TODO
                    Crashlytics.log(error.getMessage());
                }
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("MobileNumber",getIntent().getStringExtra("MobileNo"));
                map.put("NewPassword",etConfirmPassword.getEditText().getText().toString());
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    public boolean Validation(){
        boolean Success=true;
        if(!Utils.CheckEmptyValidation(this,etOTP.getEditText())){
            Success=false;
        }
        if(!Utils.CheckEmptyValidation(this,etNewPassword.getEditText())){
            Success=false;
        }
        if(!Utils.CheckEmptyValidation(this,etConfirmPassword.getEditText())){
            Success=false;
        }
        return Success;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnSave){
            if(Validation()) {
                ForgotPassword();
            }
        }
    }
}
