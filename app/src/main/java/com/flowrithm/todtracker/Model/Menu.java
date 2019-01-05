package com.flowrithm.todtracker.Model;

import com.flowrithm.todtracker.Application;
import com.flowrithm.todtracker.R;

import java.util.ArrayList;

public class Menu {

    public Menu(int Image, String Name, String Tag){
        this.MenuImage=Image;
        this.MenuName=Name;
        this.Tag=Tag;
    }

    public int MenuImage;
    public String MenuName;
    public String Tag;

    public static ArrayList<Menu> parseMenu(){

        ArrayList<Menu> menus=new ArrayList<>();
        menus.add(new Menu(R.mipmap.icon_transport,"Transports","Transports"));
        menus.add(new Menu(R.mipmap.icon_owner,"Add Customer","AddCustomer"));
        menus.add(new Menu(R.mipmap.icon_lock,"Change Password","ChangePassword"));
        menus.add(new Menu(R.mipmap.icon_logout,"Logout","Logout"));

        return menus;
    }

}
