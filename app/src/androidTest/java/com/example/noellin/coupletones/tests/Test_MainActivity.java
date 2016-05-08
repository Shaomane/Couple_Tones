package com.example.noellin.coupletones.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.example.noellin.coupletones.MainActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by jeremy on 5/7/16.
 */
public class Test_MainActivity extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity mainActivity;

    public Test_MainActivity(){
        super(MainActivity.class);
    }

    public void test_first(){
        mainActivity = getActivity();
        mainActivity.name = "foo";
        mainActivity.email = "foo@example.com";
        mainActivity.myRegId = "1234567890";
    }

    public void test_sendPartnerRequest_searchExistingRelationships(){
        final String entered_email = "foo@example.com";
        mainActivity.sendPartnerRequest("bar@example.com");

        Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");
        //attach a listener to read the data
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot) {
                //Loop through each of the relationships in the database
                for (DataSnapshot rel : snapshot.getChildren()) {
                    //Check if current relationship has the requested partner
                    if (rel.child("emailOne").getValue().toString().equals(entered_email)
                            || rel.child("emailTwo").getValue().toString().equals(entered_email)) {
                        fail("incorrectly found an existing relationship");
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });
    }
    public void test_sendPartnerRequest_searchForRequest(){
        final String entered_email = "foo@example.com";

        Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/requests");
        //attach a listener to read the data
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot) {
                //Loop through each of the requests in the database
                for (DataSnapshot rel : snapshot.getChildren()) {
                    //Check if current request has the sender
                    if (rel.child("emailOne").getValue().toString().equals(entered_email)
                            || rel.child("emailTwo").getValue().toString().equals(entered_email)) {
                        fail("incorrectly found an existing relationship");
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });

    }


}
