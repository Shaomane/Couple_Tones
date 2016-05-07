package com.example.noellin.coupletones.tests;

import android.test.ActivityInstrumentationTestCase2;

import com.example.noellin.coupletones.MapsActivity;

/**
 * Created by Chauncey on 5/6/16.
 **/
public class test_addLocation extends ActivityInstrumentationTestCase2<MapsActivity> {

    //private Button btnAddLocation;

    public test_addLocation() {
        super(MapsActivity.class);
    }

    @Override
    protected void setUp() {
        MapsActivity mapsActivity = getActivity();

        //btnAddLocation = (Button) mapsActivity.findViewById(R.id.addLocationButton);



    }
}
