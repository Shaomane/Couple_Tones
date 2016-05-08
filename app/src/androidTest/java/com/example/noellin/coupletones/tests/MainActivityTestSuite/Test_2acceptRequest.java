package com.example.noellin.coupletones.tests.MainActivityTestSuite;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.example.noellin.coupletones.MainActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by jeremy on 5/8/16.
 */
public class Test_2acceptRequest extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity mainActivity;

    public Test_2acceptRequest(){
        super(MainActivity.class);
    }


    public void test_acceptRequest(){
        final String entered_email = "bar@example.com";
        mainActivity = getActivity();
        mainActivity.name = "bar";
        mainActivity.email = "bar@example.com";
        mainActivity.myRegId = "0000000000";
        mainActivity.ID = "1111111111";

        mainActivity.acceptRequest("foo","foo@example.com","1234567890");

        //Ensure that the correct variables are being set
        assertEquals(mainActivity.partnersRegId, "1234567890");
        assertEquals(mainActivity.partnerEmail, "foo@example.com");
        assertEquals(mainActivity.partnerName, "foo");

        //Check if a relationship was added to the database
        Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");
        //attach a listener to read the data
        ref.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot){
                //Loop through each of the relationships in the database
                for (DataSnapshot rel : snapshot.getChildren()){
                    //Check if current relationship has the user as Partner 1 by comparing the acct email
                    if (rel.child("emailOne").getValue().toString().equals("bar@example.com")
                            || rel.child("emailTwo").getValue().toString().equals("bar@example.com")) {
                        Log.d("acceptRequest","correctly found relationship in test_acceptRequest");
                        assertTrue("correctly found relationship", true);
                        return;
                    }
                }
                //No relationship was found including the user
                fail("expecting relationship but did not find one");
            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });
    }
}
