package com.flowrithm.todtracker.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.crashlytics.android.Crashlytics;
import com.flowrithm.todtracker.Adapter.TransportAdapter;
import com.flowrithm.todtracker.Application;
import com.flowrithm.todtracker.DialogActivity.BlockDailog;
import com.flowrithm.todtracker.Fragments.Transports;
import com.flowrithm.todtracker.Model.MTransport;
import com.flowrithm.todtracker.R;
import com.flowrithm.todtracker.Utils.Utils;
import com.flowrithm.todtracker.WebApi.Api;
import com.flowrithm.todtracker.Zoomage;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TransportDetail extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.txtTransportName)
    TextView txtTransportName;

    @Bind(R.id.imgBack)
    ImageView imgBack;

    @Bind(R.id.txtContact)
    TextView txtContact;

    @Bind(R.id.txtPanNumber)
    TextView txtPanNumber;

    @Bind(R.id.txtGSTNumber)
    TextView txtGSTNumber;

    @Bind(R.id.txtOwner1)
    TextView txtOwner1;

    @Bind(R.id.txtOwner2)
    TextView txtOwner2;

    @Bind(R.id.imgVerified)
    ImageView imgVerified;

    @Bind(R.id.txtBlockedCount)
    TextView txtBlockedCount;

    @Bind(R.id.btnBlockRequest)
    Button btnBlockRequest;

    @Bind(R.id.btnUnBlockRequest)
    Button btnUnBlockRequest;

    @Bind(R.id.imgBill)
    ImageView imgBill;

    @Bind(R.id.imgHistory)
    ImageView imgHistory;

    SharedPreferences pref;
    MTransport transport;
    int Status;

    String ImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_detail);
        ButterKnife.bind(this);
        transport = (MTransport) getIntent().getSerializableExtra("Detail");
        imgBack.setOnClickListener(this);
        btnBlockRequest.setOnClickListener(this);
        btnUnBlockRequest.setOnClickListener(this);
        imgBill.setOnClickListener(this);
        imgHistory.setOnClickListener(this);
        pref = Application.getSharedPreferenceInstance();
        GetTransportDetail();
    }

    public void GetTransportDetail() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, Api.GET_TRANSPORT_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject Json = new JSONObject(response);
                    if (Json.getBoolean("Success")) {
                        JSONObject json = Json.getJSONObject("Data");
                        txtTransportName.setText(json.getString("CompanyName"));
                        //txtContact.setText(json.getString("ContactNumber"));
                        txtGSTNumber.setText(json.getString("GST"));
                        txtPanNumber.setText(json.getString("PanNumber"));
                        txtOwner1.setText(json.getString("FirstOwner"));
                        txtOwner2.setText(json.getString("SecondOwner"));
                        if (json.getInt("NumOfBlock") - json.getInt("NumOfUnBlock") > 0) {
                            btnUnBlockRequest.setVisibility(View.VISIBLE);
                            btnBlockRequest.setVisibility(View.GONE);
                            imgVerified.setVisibility(View.GONE);
                            txtBlockedCount.setVisibility(View.VISIBLE);
                            imgBill.setVisibility(View.VISIBLE);
                            txtBlockedCount.setText((json.getInt("NumOfBlock") - json.getInt("NumOfUnBlock")) + "");
                        } else {
                            btnBlockRequest.setVisibility(View.VISIBLE);
                            btnUnBlockRequest.setVisibility(View.GONE);
                            txtBlockedCount.setVisibility(View.GONE);
                            imgBill.setVisibility(View.GONE);
                            imgVerified.setVisibility(View.VISIBLE);
                        }
                        if (!json.getString("Image").equals("")) {
                            ImagePath = Api.HOST + "\\" + json.getString("Image");
                            Picasso.with(TransportDetail.this)
                                    .load(Api.HOST + "\\" + json.getString("Image"))
                                    .placeholder(R.mipmap.ic_launcher).into(imgBill);
                        }
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
                    Toast.makeText(TransportDetail.this,
                            TransportDetail.this.getString(R.string.error_network_timeout),
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
                map.put("TransportId", transport.TransportId + "");
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            this.finish();
        } else if (v.getId() == R.id.btnBlockRequest) {
            Status = 1;
            Intent intent = new Intent(this, BlockDailog.class);
            intent.putExtra("TransportId", transport.TransportId);
            intent.putExtra("PumpId", pref.getInt("PumpId", 0));
            intent.putExtra("BlockStatus", Status);
            startActivityForResult(intent, 101);
        } else if (v.getId() == R.id.btnUnBlockRequest) {
            Status = 2;
            Intent intent = new Intent(this, BlockDailog.class);
            intent.putExtra("TransportId", transport.TransportId);
            intent.putExtra("PumpId", pref.getInt("PumpId", 0));
            intent.putExtra("BlockStatus", Status);
            startActivityForResult(intent, 101);
        } else if (v.getId() == R.id.imgBill) {
            Intent intent = new Intent(this, Zoomage.class);
            intent.putExtra("Path", ImagePath);
            startActivity(intent);
        }else if(v.getId()==R.id.imgHistory){
            Intent intent=new Intent(this,TransportHistory.class);
            intent.putExtra("TransportId", transport.TransportId);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101 && resultCode == RESULT_OK) {
            GetTransportDetail();
            this.setResult(RESULT_OK);
        }
    }

}
