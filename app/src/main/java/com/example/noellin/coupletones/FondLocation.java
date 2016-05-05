package com.example.noellin.coupletones;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by noellin on 5/4/16.
 */
public class FondLocation  {

    public String locationName;
    public LatLng latLng;

    // creates a locatoin and initializes the latitude and longitude.
    public FondLocation(double latitude, double longitude) {
        latLng = new LatLng(latitude, longitude);

    }
    // setter for location name
    public void setName (String locationName) {

        this.locationName = locationName;
    }
    /* getter for location name */
    public String getName () {

        return this.locationName;
    }
}