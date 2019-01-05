package com.flowrithm.todtracker.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.flowrithm.todtracker.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddCustomer extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.imgBack)
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        ButterKnife.bind(this);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imgBack){
            this.finish();
        }
    }
}
