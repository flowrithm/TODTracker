package com.flowrithm.todtracker.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
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

public class Register extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.etName)
    EditText etName;

    @Bind(R.id.etContact)
    EditText etContact;

    @Bind(R.id.etEmail)
    EditText etEmail;

    @Bind(R.id.etAddress)
    EditText etAddress;

    @Bind(R.id.etPanNo)
    EditText etPanNo;

    @Bind(R.id.etGSTNO)
    EditText etGSTNO;

    @Bind(R.id.etPassword)
    EditText etPassword;

    @Bind(R.id.etNewPassword)
    EditText etNewPassword;

    @Bind(R.id.btnRegister)
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        btnRegister.setOnClickListener(this);
    }

    public boolean Validation(){
        boolean Success=true;
        if(!Utils.CheckEmptyValidation(this,etName)){
            Success=false;
        }
        if(!Utils.CheckEmptyValidation(this,etAddress)){
            Success=false;
        }
        if(!Utils.CheckEmptyValidation(this,etContact)){
            Success=false;
        }
        if(!Utils.CheckEmptyValidation(this,etEmail)){
            Success=false;
        }

        if(!etEmail.getText().toString().equals("")){
            if(!Utils.CheckEmailValidation(this,etEmail)){
                Success=false;
            }
        }

        if(!Utils.CheckEmptyValidation(this,etGSTNO)){
            Success=false;
        }
        if(!Utils.CheckEmptyValidation(this,etPanNo)){
            Success=false;
        }

        if(!etPassword.getText().toString().equals(etNewPassword.getText().toString())){
            etNewPassword.setError("Password didn't match");
            Success=false;
        }

        return Success;
    }


    public void SendOTP(){
        final KProgressHUD progress=Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, Api.POST_SEND_OTP_REGISTRATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getBoolean("Success")) {
                        Intent intent=new Intent(Register.this,VerifyMobileNumber.class);
                        intent.putExtra("Name",etName.getText().toString());
                        intent.putExtra("ContactNo",etContact.getText().toString());
                        intent.putExtra("EmailAddress",etEmail.getText().toString());
                        intent.putExtra("Address",etAddress.getText().toString());
                        intent.putExtra("PanNo",etPanNo.getText().toString());
                        intent.putExtra("GstNo",etGSTNO.getText().toString());
                        intent.putExtra("Password",etPassword.getText().toString());
                        JSONObject jdata=json.getJSONObject("Data");
                        intent.putExtra("OTP",jdata.getString("OTP"));
                        Toast.makeText(Register.this,json.getString("Message"),Toast.LENGTH_LONG).show();
                        startActivityForResult(intent,101);
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
                    Toast.makeText(Register.this,
                            Register.this.getString(R.string.error_network_timeout),
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
                map.put("MobileNumber",etContact.getText().toString());
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnRegister){
            if(Validation()) {
//                Register();
                SendOTP();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==101 && resultCode==RESULT_OK){
            this.finish();
        }
    }
}
