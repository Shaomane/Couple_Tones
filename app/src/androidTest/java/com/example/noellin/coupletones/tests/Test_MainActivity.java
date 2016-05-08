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
        mainActivity.ID = "0987654321";
    }

    public void test_sendPartnerRequest_searchExistingRelationships(){
        mainActivity = getActivity();
        mainActivity.name = "foo";
        mainActivity.email = "foo@example.com";
        mainActivity.myRegId = "1234567890";
        mainActivity.ID = "0987654321";
        final String entered_email = "bar@example.com";

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
                Log.d("sendPartnerRequest","correctly did not find a relationship");
            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });
    }
    public void test_sendPartnerRequest_searchForRequest(){
        final String entered_email = "bar@example.com";
        mainActivity = getActivity();
        mainActivity.name = "foo";
        mainActivity.email = "foo@example.com";
        mainActivity.myRegId = "1234567890";
        mainActivity.ID = "0987654321";

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
/*
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
*/

}
