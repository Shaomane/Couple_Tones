package com.example.noellin.coupletones.tests.MainActivityTestSuite;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.example.noellin.coupletones.MainActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.junit.Before;

/**
 * Created by jeremy on 5/8/16.
 *
 * This test ensures that a request is not generated when the requested partner is already paired
 */
public class Test_7sendPartnerRequest_alreadyPaired extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity mainActivity;

    public Test_7sendPartnerRequest_alreadyPaired(){
        super(MainActivity.class);
    }

    //GIVEN THAT the user is unpaired AND the requested partner is paired
    @Before
    public void test_before(){
        mainActivity = getActivity();
        mainActivity.relationship.partnerOneName = "whee";
        mainActivity.relationship.partnerOneEmail = "whee@example.com";
        mainActivity.relationship.partnerOneRegId = "1234512345";
        mainActivity.relationship.partnerOneID = "5432154321";
        final String entered_email = "bar@example.com";
    }

    //WHEN the user hits Add a Partner AND types in the requested email AND hits Send
    //THEN a request is not sent to the requested partner that is already paired
    public void test_sendPartnerRequest_alreadyPaired(){
        mainActivity = getActivity();
        //mainActivity.relationship = new Relationship();
        mainActivity.relationship.partnerOneName = "whee";
        mainActivity.relationship.partnerOneEmail = "whee@example.com";
        mainActivity.relationship.partnerOneRegId = "1234512345";
        mainActivity.relationship.partnerOneID = "5432154321";
        final String entered_email = "bar@example.com";

        mainActivity.sendPartnerRequest("bar@example.com");
        mainActivity.sendPartnerRequest("bar@example.com");

        Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/requests");
        //attach a listener to read the data
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot) {
                Log.d("onDataChange","THIS THING HAPPENED");
                //Loop through each of the requests in the database
                for (DataSnapshot req : snapshot.getChildren()) {
                    //Check if current request has the sender
                    if (req.child("senderName").getValue() != null
                            && req.child("senderName").getValue().toString().equals("whee")
                            && req.child("senderEmail").getValue().toString().equals("whee@example.com")
                            && req.child("senderRegId").getValue().toString().equals("1234512345")
                            && req.child("receiverEmail").getValue().toString().equals(entered_email)) {
                        Log.d("if statement","found a matching request");
                        fail("found a matching request");
                        return;
                    }
                }
                //No relationship was found
                assertTrue("No request was found matching values", true);
            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });
    }
}
