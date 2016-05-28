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

    public LocationUpdater(String rel_id){
        ref =  new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/"+rel_id);
    }


    /*
    This method adds a favorite location to the Firebase database
     */
    public void addFavoriteLocation(Location location, String myName){
        Map<String, Object> newLoc = new HashMap<String, Object>();

        newLoc.put(location.getProvider(), location.getProvider());

        ref.child(myName+"_Locations").updateChildren(newLoc);
    }

    /*
    This method removes a favorite location from the Firebase database
     */
    public void removeFavoriteLocation(final Location location, String myName){

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
