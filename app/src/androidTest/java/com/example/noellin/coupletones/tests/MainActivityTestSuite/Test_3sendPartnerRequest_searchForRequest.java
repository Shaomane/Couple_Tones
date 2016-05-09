package com.example.noellin.coupletones.tests.MainActivityTestSuite;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.example.noellin.coupletones.MainActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by jeremy.
 *
 * This test ensures that a request was created, given that the requested partner and the user
 * both are unpaired
 */
public class Test_3sendPartnerRequest_searchForRequest extends ActivityInstrumentationTestCase2<MainActivity>{

    MainActivity mainActivity;
    String email;
    String partnerOneName;
    String partnerOneEmail;
    String partnerOneRegId;
    String partnerOneID;

    public Test_3sendPartnerRequest_searchForRequest(){
        super(MainActivity.class);
    }

    //GIVEN THAT the user and the requested partner are unpaired
    @Before
    public void test_before(){

        email = "bar@example.com";
        partnerOneName = "foo";
        partnerOneEmail = "foo@example.com";
        partnerOneRegId = "1234567890";
        partnerOneID = "0987654321";
    }

    //WHEN the user sends a request
    //THEN there should be a matching request update in the database
    @Test
    public void test_sendPartnerRequest_searchForRequest(){
        final String entered_email = "bar@example.com";
        mainActivity = getActivity();
        mainActivity.relationship.partnerOneName = "foo";
        mainActivity.relationship.partnerOneEmail = "foo@example.com";
        mainActivity.relationship.partnerOneRegId = "1234567890";
        mainActivity.relationship.partnerOneID = "0987654321";

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
                            && req.child("senderName").getValue().toString().equals("foo")
                            && req.child("senderEmail").getValue().toString().equals("foo@example.com")
                            && req.child("senderRegId").getValue().toString().equals("1234567890")
                            && req.child("receiverEmail").getValue().toString().equals(entered_email)) {
                        //This is expected
                        Log.d("if statement","found a matching request");
                        assertTrue("found a matching request", true);
                        return;
                    }
                }
                //No relationship was found
                fail("No request was found matching expected values");
            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });
    }
}
