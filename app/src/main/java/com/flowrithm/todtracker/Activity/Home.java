package com.flowrithm.todtracker.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flowrithm.todtracker.Adapter.MenuAdapter;
import com.flowrithm.todtracker.Application;
import com.flowrithm.todtracker.Fragments.Transports;
import com.flowrithm.todtracker.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Home extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.lstMenu)
    ListView lstMenu;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    String CurrentTag = "";
    ArrayList<com.flowrithm.todtracker.Model.Menu> menuItems;
    MenuAdapter adapter;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        pref = Application.getSharedPreferenceInstance();
        menuItems = com.flowrithm.todtracker.Model.Menu.parseMenu();

        adapter = new MenuAdapter(this, menuItems);
        lstMenu.setAdapter(adapter);
        lstMenu.setOnItemClickListener(this);
        lstMenu.performItemClick(null, 0, 0);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        com.flowrithm.todtracker.Model.Menu menu = menuItems.get(position);
        Fragment fragment = null;
        switch (menu.Tag) {
            case "Transports":
                fragment = new Transports();
                break;
            case "AddCustomer":
                Intent intent = new Intent(this, AddCustomer.class);
                startActivity(intent);
                break;
            case "Logout":
                pref.edit().clear().commit();
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case "ChangePassword":
                intent=new Intent(this,ChangePassword.class);
                startActivity(intent);
                break;
        }
        if (!menu.Tag.equals("ChangePassword") ) {
            toolbar.setTitle(menu.MenuName);
            CurrentTag = menu.Tag;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        drawer.closeDrawer(GravityCompat.START);
    }

}
