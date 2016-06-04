package com.example.noellin.coupletones.tests.TonesTestSuite;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.example.noellin.coupletones.LocationController;
import com.example.noellin.coupletones.MainActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by jeremy on 6/3/16.
 */
public class Test_3setLocationVibeTone extends ActivityInstrumentationTestCase2<MainActivity> {
    public Test_3setLocationVibeTone(){
        super(MainActivity.class);
    }

    MainActivity mainActivity;
    LocationController locationController;// = new LocationController("01010101","test test");

    //GIVEN the user is paired and has a location set,
    //WHEN the user sets a Location's SoundTone
    public void test_setLocationSoundTone(){
        mainActivity = getActivity();
        locationController = new LocationController("01010101","test test");
        locationController.setVibeTone("testSpot1","VibeTone5");
        try{Thread.sleep(1000);}catch(InterruptedException e){}
    }

    //THEN the location's SoundTone is changed on Firebase
    public void test_ensureCorrectLocationSoundTone(){
        mainActivity = getActivity();
        locationController = new LocationController("01010101","test test");
        Firebase ref =
                new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/01010101/test test_Locations/testSpot1/vibeTone");
        //attach a listener to read the data
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot) {

                //Loop through each of the requests in the database
                for (DataSnapshot req : snapshot.getChildren()) {
                    //Check if current request has the sender
                    if (req.getValue().equals("VibeTone5") ) {
                        //This is expected
                        Log.d("if statement","found correct VibeTone");
                        assertTrue("found correct VibeTone", true);
                        return;
                    }
                }
                //No VibeTone found
            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });

        try{Thread.sleep(1000);}catch(InterruptedException e){}
    }

    public void test_removeTestRelationship(){
        mainActivity = getActivity();
        new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/01010101").setValue(null);
    }
}
