package com.example.noellin.coupletones.tests.TonesTestSuite;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.example.noellin.coupletones.LocationController;
import com.example.noellin.coupletones.LocationUpdater;
import com.example.noellin.coupletones.MainActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by jeremy on 6/3/16.
 */
public class Test_1addFavoriteLocation extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity mainActivity;

    public Test_1addFavoriteLocation(){
        super(MainActivity.class);
    }

    public void test_addFavoriteLocation(){
        mainActivity = getActivity();

        Firebase root = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/01010101/test test_Locations");

        root.child("testSpot1").child("latitude").setValue("0");
        root.child("testSpot1").child("longitude").setValue("0");
        root.child("testSpot1").child("name").setValue("testSpot1");
        root.child("testSpot1").child("soundTone").setValue("SoundTone0");
        root.child("testSpot1").child("vibeTone").setValue("VibeTone0");

        try{Thread.sleep(1000);}catch(InterruptedException e){}
    }

    public void test_checkForFavoriteLocation(){
        mainActivity = getActivity();

        Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/01010101/test test_Locations");
        //attach a listener to read the data
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot) {

                //Loop through each of the requests in the database
                for (DataSnapshot req : snapshot.getChildren()) {
                    //Check if current request has the sender
                    if (req.getKey().equals("testSpot1") ) {
                        //This is expected
                        Log.d("if statement","found a matching location");
                        assertTrue("found a matching location", true);
                        return;
                    }
                }
                //No relationship was found
                fail("No location was found matching expected name");
            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });
    }


}
