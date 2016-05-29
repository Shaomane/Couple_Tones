package com.example.noellin.coupletones;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by jeremy on 5/28/16.
 */
public class LocationController {

    MainActivity callingActivity;
    String rel_id = null;
    String partnerTwo = null;

    ArrayList<String> locationNames = new ArrayList<>();
    ArrayList<String> locationVibeTones = new ArrayList<>();
    ArrayList<String> locationSoundTones = new ArrayList<>();
    ArrayList<String> locationLatitudes = new ArrayList<>();
    ArrayList<String> locationLongitudes = new ArrayList<>();

    Firebase ref;

    public LocationController(MainActivity callingActivity){
        this.callingActivity = callingActivity;
        this.rel_id = callingActivity.relationship.rel_id;
        this.partnerTwo = callingActivity.relationship.partnerTwoName;

        Log.d("rel_id","rel_id in LocationController.java:"+rel_id);
        Log.d("partnerTwo","partnerTwo in LocationController.jaa: "+partnerTwo);

        this.ref =  new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/"+rel_id+"/"+partnerTwo+"_Locations");
    }

    public void readFromDatabase(){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot location : dataSnapshot.getChildren()){

                    locationNames.add(location.child("name").getValue().toString());
                    locationVibeTones.add(location.child("vibeTone").getValue().toString());
                    locationSoundTones.add(location.child("soundTone").getValue().toString());
                    locationLatitudes.add(location.child("latitude").getValue().toString());
                    locationLongitudes.add(location.child("longitude").getValue().toString());

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void setVibeTone(String locationName, String vibeTone){
        ref.child(locationName).setValue(vibeTone);
        for (int i = 0; i < locationNames.size(); i++){
            if (locationNames.get(i).equals(locationName)){
                locationVibeTones.set(i, vibeTone);
            }
        }
    }

    public void setSoundTone(String locationName, String soundTone){
        ref.child(locationName).setValue(soundTone);
        for (int i = 0; i < locationNames.size(); i++){
            if (locationNames.get(i).equals(locationName)){
                locationSoundTones.set(i, soundTone);
            }
        }
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
