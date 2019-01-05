package com.flowrithm.todtracker.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.flowrithm.todtracker.Activity.MainActivity;
import com.flowrithm.todtracker.Activity.TransportDetail;
import com.flowrithm.todtracker.Adapter.TransportAdapter;
import com.flowrithm.todtracker.Application;
import com.flowrithm.todtracker.Model.MTransport;
import com.flowrithm.todtracker.R;
import com.flowrithm.todtracker.Utils.Utils;
import com.flowrithm.todtracker.WebApi.Api;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Transports extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    @Bind(R.id.btnSearch)
    Button btnSearch;

    @Bind(R.id.txtRole)
    TextView txtRole;

    @Bind(R.id.etSearch)
    EditText etSearch;

    @Bind(R.id.lstUsers)
    ListView lstUsers;

    @Bind(R.id.layoutFilterMain)
    LinearLayout layoutFilterMain;

    @Bind(R.id.layoutFilter)
    LinearLayout layoutFilter;

    @Bind(R.id.imgStatus)
    ImageView imgStatus;

    HashMap<String,String> keys;
    ArrayList<String> data;
    ArrayList<MTransport> transports;

    public String SelectedCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_transports, container, false);
        ButterKnife.bind(this,view);
        layoutFilterMain.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        txtRole.setOnClickListener(this);
        InitializaedKey();
        lstUsers.setOnItemClickListener(this);
        txtRole.setText(data.get(0));
        SelectedCategory=keys.get(data.get(0));
        return view;
    }

    public void InitializaedKey(){
        keys=new HashMap<>();
        keys.put("All","ALL");
        keys.put("Pan Card No","PAN");
        keys.put("GST No","GST");
        keys.put("Name","Name");
        data=new ArrayList<>(keys.keySet());
    }

    public void GetTransport(){
        final KProgressHUD progress=Utils.ShowDialog(this.getContext());
        StringRequest request=new StringRequest(Request.Method.POST, Api.GET_TRANSPORTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    if(json.getBoolean("Success")){
                        transports=new ArrayList<>();
                        JSONArray array=json.getJSONArray("Data");
                        for(int i=0;i<array.length();i++){
                            JSONObject obj=array.getJSONObject(i);
                            MTransport transport=new MTransport();
                            transport.Name=obj.getString("CompanyName");
                            transport.TransportId=obj.getInt("CompanyId");
                            transport.FirstOwner=obj.getString("FirstOwnerName");
                            transport.SecondOwner=obj.getString("SecondOwnerName");
                            transport.PanNo=obj.getString("CompanyPAN");
                            transport.BlockNumber=obj.getInt("BlockNumber");
                            transport.UnblockNumber=obj.getInt("UnblockNumber");
                            transports.add(transport);
                        }
                        layoutFilter.setVisibility(View.GONE);
                        imgStatus.setImageResource(R.mipmap.icon_arrow_down);
                        TransportAdapter adapter=new TransportAdapter(Transports.this.getContext(),transports);
                        lstUsers.setAdapter(adapter);
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
                    Toast.makeText(Transports.this.getContext(),
                            Transports.this.getString(R.string.error_network_timeout),
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
                map.put("Category",SelectedCategory);
                map.put("SearchText",etSearch.getText().toString());
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.layoutFilterMain){
            if(layoutFilter.getVisibility()==View.VISIBLE){
                layoutFilter.setVisibility(View.GONE);
                imgStatus.setImageResource(R.mipmap.icon_arrow_down);
            }else{
                layoutFilter.setVisibility(View.VISIBLE);
                imgStatus.setImageResource(R.mipmap.icon_arrow_up);
            }
        }else if(v.getId()==R.id.btnSearch){
            GetTransport();
        }else if(v.getId()==R.id.txtRole){
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            builder.setTitle("Selet Category");
            builder.setItems( data.toArray(new String[data.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SelectedCategory=keys.get(data.get(which));
                    txtRole.setText(data.get(which));
                    etSearch.setText("");
                }
            });
            builder.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(this.getContext(),TransportDetail.class);
        intent.putExtra("Detail",transports.get(position));
        startActivityForResult(intent,101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==101 && resultCode==RESULT_OK){
            GetTransport();
        }
    }
}
