package com.example.noellin.coupletones;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by jeremy on 5/28/16.
 */
public class LocationController {

    //MainActivity callingActivity;
    String rel_id = null;
    String partnerTwo = null;

    ArrayList<String> locationNames;// = new ArrayList<>();
    ArrayList<String> locationVibeTones;// = new ArrayList<>();
    ArrayList<String> locationSoundTones;// = new ArrayList<>();
    ArrayList<String> locationLatitudes;// = new ArrayList<>();
    ArrayList<String> locationLongitudes;// = new ArrayList<>();
    ArrayList<ArrayList<String>> locationTimes;// = new ArrayList<ArrayList<String>>();

    Firebase ref;

    public LocationController(String rel_id, String partnerTwo){
        //this.callingActivity = callingActivity;
        this.rel_id = rel_id;//callingActivity.relationship.rel_id;
        this.partnerTwo = partnerTwo;//callingActivity.relationship.partnerTwoName;

        Log.d("rel_id","rel_id in LocationController.java:"+rel_id);
        Log.d("partnerTwo","partnerTwo in LocationController.jaa: "+partnerTwo);

        this.ref =  new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/"+rel_id+"/"+partnerTwo+"_Locations");

        startListenerForDatabase();
    }

    public void startListenerForDatabase(){
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot location : dataSnapshot.getChildren()){
                    updateLists(location);

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot){
                for (DataSnapshot location : dataSnapshot.getChildren()){
                    updateLists(location);

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s){
                for (DataSnapshot location : dataSnapshot.getChildren()){
                    updateLists(location);

                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s){
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void updateLists(DataSnapshot location) {
        locationNames = new ArrayList<String>();
        locationVibeTones= new ArrayList<String>();
        locationSoundTones = new ArrayList<String>();
        locationLatitudes = new ArrayList<String>();
        locationLongitudes = new ArrayList<String>();
        locationTimes = new ArrayList<ArrayList<String>>();

        locationNames.add(location.child("name").getValue().toString());
        locationVibeTones.add(location.child("vibeTone").getValue().toString());
        locationSoundTones.add(location.child("soundTone").getValue().toString());
        locationLatitudes.add(location.child("latitude").getValue().toString());
        locationLongitudes.add(location.child("longitude").getValue().toString());

        ArrayList<String> times = new ArrayList<String>();
        if (location.child("lastTimeVisited").getValue() == null){
            times.add("NONE");
            times.add("NONE");
            times.add("NONE");
            times.add("NONE");
        }
        else {
            times.add(location.child("lastTimeVisited").child("day").getValue().toString());
            times.add(location.child("lastTimeVisited").child("hour").getValue().toString());
            times.add(location.child("lastTimeVisited").child("minute").getValue().toString());
            times.add(location.child("lastTimeVisited").child("second").getValue().toString());

            locationTimes.add(times);
        }
    }

    public void setVibeTone(String locationName, String vibeTone){
        ref.child(locationName).child("vibeTone").setValue(vibeTone);
        for (int i = 0; i < locationNames.size(); i++){
            if (locationNames.get(i).equals(locationName)){
                locationVibeTones.set(i, vibeTone);
            }
        }
    }

    public void setSoundTone(String locationName, String soundTone){
        ref.child(locationName).child("soundTone").setValue(soundTone);
        for (int i = 0; i < locationNames.size(); i++){
            if (locationNames.get(i).equals(locationName)){
                locationSoundTones.set(i, soundTone);
            }
        }
    }

    /*
    Returns an ArrayList<String> where:
    list.get(0) == DAY_OF_YEAR that location was last visited
    list.get(1) == HOUR_OF_DAY that location was last visited (24 hour time)
    list.get(2) == MINUTE_OF_HOUR that location was last visited
    list.get(3) == SECOND_OF_MINUTE that location was last visited

    IMPORTANT: if the location has not been visited yet, the ArrayList will be populated with "NONE"
     */
    public ArrayList<String> getTime(String locationName){

        for (int i = 0; i < locationNames.size(); i++){
            if (locationNames.get(i).equals(locationName)){
                return locationTimes.get(i);
            }
        }

        return new ArrayList<String>();
    }


    public String getVibeTone(String locationName){
        for (int i = 0; i < locationNames.size(); i++){
            if (locationNames.get(i).equals(locationName)){
                return locationVibeTones.get(i);
            }
        }
        return "";
    }

    public String getSoundTone(String locationName){
        for (int i = 0; i < locationNames.size(); i++){
            if (locationNames.get(i).equals(locationName)){
                return locationSoundTones.get(i);
            }
        }
        return "";
    }

    public String getLatitude(String locationName){
        for (int i = 0; i < locationNames.size(); i++){
            if (locationNames.get(i).equals(locationName)){
                return locationLatitudes.get(i);
            }
        }
        return "";
    }

    public String getLongitude(String locationName){
        for (int i = 0; i < locationNames.size(); i++){
            if (locationNames.get(i).equals(locationName)){
                return locationLongitudes.get(i);
            }
        }
        return "";
    }

}
