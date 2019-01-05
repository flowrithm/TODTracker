package com.flowrithm.todtracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.flowrithm.todtracker.Model.MTransport;
import com.flowrithm.todtracker.R;

import java.util.ArrayList;

public class TransportAdapter extends BaseAdapter {

    Context context;
    ArrayList<MTransport> transports;
    LayoutInflater inflater;
    LinearLayout.LayoutParams params;

    public TransportAdapter(Context context, ArrayList<MTransport> transports){
        this.context=context;
        this.transports=transports;
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,3,5,6);
    }

    @Override
    public int getCount() {
        return this.transports.size();
    }

    @Override
    public Object getItem(int position) {
        return this.transports.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_transport,null);
            holder=new ViewHolder();
            holder.txtPumpName=(TextView) convertView.findViewById(R.id.txtPumpName);
            holder.txtContact=(TextView)convertView.findViewById(R.id.txtContact);
            holder.txtPan=(TextView)convertView.findViewById(R.id.txtPan);
            holder.txtGST=(TextView)convertView.findViewById(R.id.txtGST);
            holder.txtOwner1=(TextView)convertView.findViewById(R.id.txtOwner1);
            holder.txtOwner2=(TextView)convertView.findViewById(R.id.txtOwner2);
            holder.layout=(LinearLayout)convertView.findViewById(R.id.layout);
            holder.txtBlockedCount=(TextView)convertView.findViewById(R.id.txtBlockedCount);
            holder.imgCount=(ImageView)convertView.findViewById(R.id.imgVerified);
            holder.layout.setLayoutParams(params);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        MTransport transport=transports.get(position);
        holder.txtPumpName.setText(transport.Name);
        holder.txtContact.setText(transport.ContactNo);
        holder.txtPan.setText(transport.PanNo);
        holder.txtGST.setText(transport.GST);
        holder.txtOwner1.setText(transport.FirstOwner);
        holder.txtOwner2.setText(transport.SecondOwner);
        if(transport.BlockNumber-transport.UnblockNumber>0){
            holder.imgCount.setVisibility(View.GONE);
            holder.txtBlockedCount.setVisibility(View.VISIBLE);
            holder.txtBlockedCount.setText((transport.BlockNumber-transport.UnblockNumber)+"");
        }else{
            holder.txtBlockedCount.setVisibility(View.GONE);
            holder.imgCount.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public class ViewHolder{
        TextView txtPumpName,txtContact,txtPan,txtGST,txtOwner1,txtOwner2,txtBlockedCount;
        public LinearLayout layout;
        public ImageView imgCount;
    }
}
