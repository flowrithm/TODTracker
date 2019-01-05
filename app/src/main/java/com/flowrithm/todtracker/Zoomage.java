package com.flowrithm.todtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Zoomage extends AppCompatActivity {

    @Bind(R.id.myZoomageView)
    ImageView myZoomageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomage);
        ButterKnife.bind(this);
        if(!getIntent().getExtras().getString("Path").equals("")){
            Picasso.with(this).load(getIntent().getExtras().getString("Path")).into(myZoomageView);
        }
    }
}
