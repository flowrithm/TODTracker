package com.flowrithm.todtracker.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.flowrithm.todtracker.Adapter.HistoryAdapter;
import com.flowrithm.todtracker.Application;
import com.flowrithm.todtracker.ClickListner;
import com.flowrithm.todtracker.Model.MHistory;
import com.flowrithm.todtracker.R;
import com.flowrithm.todtracker.Utils.Utils;
import com.flowrithm.todtracker.WebApi.Api;
import com.flowrithm.todtracker.Zoomage;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TransportHistory extends AppCompatActivity implements View.OnClickListener, ClickListner {

    @Bind(R.id.imgBack)
    ImageView imgBack;

    @Bind(R.id.lstHistory)
    ListView lstHistory;

    int TransportId;
    ArrayList<MHistory> histories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_history);
        ButterKnife.bind(this);
        TransportId=getIntent().getIntExtra("TransportId",0);
        imgBack.setOnClickListener(this);
        GetHistory();
    }

    public void GetHistory(){
        final KProgressHUD progress=Utils.ShowDialog(this);
        StringRequest request=new StringRequest(Request.Method.POST, Api.GET_TRANSPORT_HISTORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    if(json.getBoolean("Success")){
                        JSONArray array=json.getJSONArray("Data");
                        histories=new ArrayList<>();
                        for(int i=0;i<array.length();i++){
                            JSONObject obj=array.getJSONObject(i);
                            MHistory history=new MHistory();
                            history.PumpId=obj.getInt("PumpId");
                            history.TransportHistoryId=obj.getInt("TransportHisoryId");
                            history.PumpName=obj.getString("PumpName");
                            history.Comment=obj.getString("Comment");
                            history.Image=obj.getString("Image");
                            history.DateTime=obj.getString("DateTime");
                            history.Status=obj.getString("Status");
                            histories.add(history);
                        }
                        HistoryAdapter adapter=new HistoryAdapter(TransportHistory.this,histories,TransportHistory.this);
                        lstHistory.setAdapter(adapter);
                    }
                }catch (Exception ex){
                    Crashlytics.logException(ex);
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(TransportHistory.this,
                            TransportHistory.this.getString(R.string.error_network_timeout),
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<>();
                map.put("TransportId", TransportId + "");
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imgBack){
            this.finish();
        }
    }

    @Override
    public void Click(int position) {
        Intent intent=new Intent(this,Zoomage.class);
        intent.putExtra("Path",Api.HOST+"\\"+histories.get(position).Image);
        startActivity(intent);
    }
}
