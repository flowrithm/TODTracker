package com.flowrithm.todtracker.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flowrithm.todtracker.ClickListner;
import com.flowrithm.todtracker.Model.MHistory;
import com.flowrithm.todtracker.Model.MTransport;
import com.flowrithm.todtracker.R;
import com.flowrithm.todtracker.WebApi.Api;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter implements View.OnClickListener {

    Context context;
    ArrayList<MHistory> histories;
    LayoutInflater inflater;
    LinearLayout.LayoutParams params;
    ClickListner listner;

    public HistoryAdapter(Context context, ArrayList<MHistory> histories, ClickListner listner){
        this.context=context;
        this.histories=histories;
        this.listner=listner;
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,3,5,6);
    }

    @Override
    public int getCount() {
        return this.histories.size();
    }

    @Override
    public Object getItem(int position) {
        return this.histories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_history,null);
            holder=new ViewHolder();
            holder.layout=(LinearLayout)convertView.findViewById(R.id.layout);
            holder.txtPumpName=(TextView)convertView.findViewById(R.id.txtPumpName);
            holder.txtStatus=(TextView)convertView.findViewById(R.id.txtStatus);
            holder.txtComment=(TextView)convertView.findViewById(R.id.txtComment);
            holder.txtDateTime=(TextView)convertView.findViewById(R.id.txtDateTime);
            holder.imgBill=(ImageView)convertView.findViewById(R.id.imgBill);
            holder.imgBill.setOnClickListener(this);
            holder.layout.setLayoutParams(params);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        MHistory transport=histories.get(position);
        holder.txtPumpName.setText(transport.PumpName);
        holder.txtStatus.setText(transport.Status);
        if(transport.Status.equals("Blocked")){
            holder.txtStatus.setTextColor(context.getColor(R.color.colorAccent));
        }else{
            holder.txtStatus.setTextColor(context.getColor(R.color.green));
        }
        holder.imgBill.setTag(position);
        holder.txtComment.setText(transport.Comment);
        holder.txtDateTime.setText(transport.DateTime);
        if(!transport.Image.equals("")){
            Picasso.with(context).load(Api.HOST + "\\" +transport.Image).placeholder(R.mipmap.ic_launcher).into(holder.imgBill);
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        listner.Click((int)v.getTag());
    }

    public class ViewHolder{
        TextView txtDateTime,txtStatus,txtComment,txtPumpName;
        ImageView imgBill;
        LinearLayout layout;
    }
}
