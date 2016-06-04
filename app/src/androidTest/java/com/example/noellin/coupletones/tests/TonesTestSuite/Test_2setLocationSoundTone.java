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
public class Test_2setLocationSoundTone extends ActivityInstrumentationTestCase2<MainActivity> {
    public Test_2setLocationSoundTone(){
        super(MainActivity.class);
    }

    MainActivity mainActivity;
    LocationController locationController;

    //GIVEN the user is paired and has a location set,
    //WHEN the user sets a Location's SoundTone
    public void test_setLocationSoundTone(){
        mainActivity = getActivity();
        locationController = new LocationController("01010101","test test");
        locationController.setVibeTone("testSpot1","SoundTone5");
        try{Thread.sleep(1000);}catch(InterruptedException e){}
    }

    //THEN the location's SoundTone is changed on Firebase
    public void test_ensureCorrectLocationSoundTone(){

        mainActivity = getActivity();
        locationController = new LocationController("01010101","test test");
        Firebase ref =
                new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/01010101/test test_Locations/testSpot1/soundTone");
        //attach a listener to read the data
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot) {

                //Loop through each of the requests in the database
                for (DataSnapshot req : snapshot.getChildren()) {
                    //Check if current request has the sender
                    if (req.getValue()!=null&&req.getValue().equals("SoundTone5") ) {
                        //This is expected
                        Log.d("if statement","found correct SoundTone");
                        assertTrue("found correct SoundTone", true);
                        return;
                    }
                }
                //No SoundTone found
            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });
    }
}
