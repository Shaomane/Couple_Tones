package com.example.noellin.coupletones.tests.MainActivityTestSuite;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.example.noellin.coupletones.MainActivity;
import com.example.noellin.coupletones.Relationship;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by jeremy on 5/8/16.
 *
 * This test ensures that a relationship is correctly created given that the requested partner has
 * accepted
 */
public class Test_6acceptRequest extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity mainActivity;// = getActivity();

    public Test_6acceptRequest(){
        super(MainActivity.class);
    }

    //GIVEN THAT the user has been sent a request AND the user is unpaired
    public void test_first() {
        mainActivity = getActivity();
        mainActivity.relationship.partnerOneName = "foo";
        mainActivity.relationship.partnerOneEmail = "foo@example.com";
        mainActivity.relationship.partnerOneRegId = "1234567890";
        mainActivity.relationship.partnerOneID = "0987654321";
        //mainActivity.sendPartnerRequest("bar@example.com");
    }

    //WHEN the user logs in
    //THEN they receive a notification
    //AND WHEN the user hits accept
    //THEN a new relationship is formed
    public void test_acceptRequest(){

        mainActivity = getActivity();
        mainActivity.relationship.partnerOneName = "bar";
        mainActivity.relationship.partnerOneEmail = "bar@example.com";
        mainActivity.relationship.partnerOneRegId = "00000000";
        mainActivity.relationship.partnerOneID = "11111111";

        mainActivity.acceptRequest("foo","foo@example.com","1234567890");

        //Ensure that the correct variables are being set
        assertEquals(mainActivity.relationship.partnerTwoRegId, "1234567890");
        assertEquals(mainActivity.relationship.partnerTwoEmail, "foo@example.com");
        assertEquals(mainActivity.relationship.partnerTwoName, "foo");

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
