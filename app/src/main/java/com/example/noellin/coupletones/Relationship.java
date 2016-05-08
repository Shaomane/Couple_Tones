package com.example.noellin.coupletones;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by jeremy on 5/8/16.
 */
public class Relationship {
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    public String partnerTwoName = "";
    public String partnerTwoEmail = "";
    public String partnerOneID = "";
    //public String partnerTwoID = "";
    public String partnerOneName = "";
    public String partnerOneEmail = "";
    public String rel_id = "";
    public static String partnerOneRegId = "";
    public static String partnerTwoRegId = "";

    public Relationship(){

    }

}
