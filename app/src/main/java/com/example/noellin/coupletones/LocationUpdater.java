package com.example.noellin.coupletones;

import android.location.Location;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeremy on 5/27/16.
 */
public class LocationUpdater {

    Firebase ref; //= new Firebase("https://dazzling-inferno-7112.firebaseio.com");
    String rel_id = null;

    public LocationUpdater(String rel_id){
        ref =  new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/"+rel_id);
        this.rel_id = rel_id;
    }


    public void updateTime(String locName, String myName, int day, int hour, int minute, int second){

        Map<String, Object> lastTimeVisited = new HashMap<String, Object>();
        lastTimeVisited.put("lastTimeVisited","");

        ref.child(myName+"_Locations").child(locName).updateChildren(lastTimeVisited);

        Map<String, Object> dayMap = new HashMap<String, Object>();
        Map<String, Object> hourMap = new HashMap<String, Object>();
        Map<String, Object> minuteMap = new HashMap<String, Object>();
        Map<String, Object> secondMap = new HashMap<String, Object>();

        dayMap.put("day",""+day);
        hourMap.put("hour",""+hour);
        minuteMap.put("minute",""+minute);
        secondMap.put("second",""+minute);

        ref.child(myName+"_Locations").child(locName).child("lastTimeVisited").updateChildren(dayMap);
        ref.child(myName+"_Locations").child(locName).child("lastTimeVisited").updateChildren(hourMap);
        ref.child(myName+"_Locations").child(locName).child("lastTimeVisited").updateChildren(minuteMap);
        ref.child(myName+"_Locations").child(locName).child("lastTimeVisited").updateChildren(secondMap);

    }

    /*
    This method adds a favorite location to the Firebase database
     */
    public void addFavoriteLocation(Location location, String myName){

        if (rel_id == null) return;

        Map<String, Object> newLoc = new HashMap<String, Object>();

        newLoc.put("name", location.getProvider());
        newLoc.put("vibeTone","VibeTone0");
        newLoc.put("soundTone","SoundTone0");
        newLoc.put("latitude",""+location.getLatitude());
        newLoc.put("longitude",""+location.getLongitude());

        ref.child(myName+"_Locations").child(location.getProvider()).updateChildren(newLoc);
        //ref.child(myName+"_Locations").child(location.getProvider()).updateChildren(newLoc);
    }

    /*
    This method removes a favorite location from the Firebase database
     */
    public void removeFavoriteLocation(final Location location, String myName){

        if (rel_id == null) return;

        ref.child(myName+"_Locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot loc : dataSnapshot.getChildren()) {
                    Log.d("remove", "check location to remove: " + loc);
                    //Found the location we want to delete
                    if (loc.getRef().getKey().toString().equals(location.getProvider())) {
                        loc.getRef().setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

}
