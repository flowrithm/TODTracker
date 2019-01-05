package com.flowrithm.todtracker.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.btnLogin)
    Button btnLogin;

    @Bind(R.id.etContactNo)
    TextInputLayout etContactNo;

    @Bind(R.id.etPassword)
    TextInputLayout etPassword;

    @Bind(R.id.btnRegister)
    Button btnRegister;

    @Bind(R.id.txtForgotPassword)
    TextView txtForgotPassword;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        pref = Application.getSharedPreferenceInstance();
        if (pref.getInt("PumpId", 0) != 0) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            this.finish();
        }
        txtForgotPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    public void Login() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, Api.Login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (!json.getBoolean("Success")) {
                        new AwesomeErrorDialog(MainActivity.this)
                                .setTitle("Invalid Credential")
                                .setMessage(json.getString("Message"))
                                .setColoredCircle(R.color.dialogErrorBackgroundColor)
                                .setDialogIconAndColor(R.drawable.ic_dialog_error, R.color.white)
                                .setCancelable(true).setButtonText(getString(R.string.dialog_ok_button))
                                .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
                                .setButtonText(getString(R.string.dialog_ok_button))
                                .setErrorButtonClick(new Closure() {
                                    @Override
                                    public void exec() {

                                    }
                                })
                                .show();
                    } else {

                        JSONObject jsonData = json.getJSONObject("Data");
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("PumpId", jsonData.getInt("PumpId"));
                        editor.putString("DealerCode",jsonData.getString("DealerCode"));
                        editor.putString("PumpName", jsonData.getString("PumpName"));
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this, Home.class);
                        startActivity(intent);
                        MainActivity.this.finish();
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
                    Toast.makeText(MainActivity.this,
                            MainActivity.this.getString(R.string.error_network_timeout),
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
                map.put("ContactNumber", etContactNo.getEditText().getText().toString());
                map.put("PassWord", etPassword.getEditText().getText().toString());
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    public boolean Validation() {
        boolean Success = true;
        if (!Utils.CheckEmptyValidation(this, etContactNo.getEditText())) {
            Success = false;
        }
        if (!Utils.CheckEmptyValidation(this, etPassword.getEditText())) {
            Success = false;
        }
        return Success;
    }

    public void SendOTP(){
        final KProgressHUD progress=Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, Api.POST_SEND_OTP_FORGOT_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                        JSONObject json = new JSONObject(response);
                    if (json.getBoolean("Success")) {
                        Intent intent=new Intent(MainActivity.this,VerifyMobileNumber.class);
                        JSONObject jdata=json.getJSONObject("Data");
                        intent.putExtra("MobileNo",etContactNo.getEditText().getText().toString());
                        intent.putExtra("OTP",jdata.getString("OTP"));
                        Toast.makeText(MainActivity.this,json.getString("Message"),Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MainActivity.this,
                            MainActivity.this.getString(R.string.error_network_timeout),
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
                map.put("MobileNumber",etContactNo.getEditText().getText().toString());
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogin) {
            if (Validation()) {
                Login();
            }
        }else if(v.getId()==R.id.btnRegister){
            Intent intent=new Intent(this,Register.class);
            startActivity(intent );
        }else if(v.getId()==R.id.txtForgotPassword){
            if(!etContactNo.getEditText().getText().toString().equals("")){
                SendOTP();
            }
        }
    }
}
