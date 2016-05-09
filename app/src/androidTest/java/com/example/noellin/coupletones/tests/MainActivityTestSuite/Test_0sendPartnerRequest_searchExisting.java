package com.example.noellin.coupletones.tests.MainActivityTestSuite;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.example.noellin.coupletones.MainActivity;
import com.example.noellin.coupletones.Relationship;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by jeremy on 5/7/16.
 *
 * This test ensures that sendPartnerRequest does not automatically create a new relationship
 */
public class Test_0sendPartnerRequest_searchExisting extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity mainActivity;
    String email;
    String partnerOneName;
    String partnerOneEmail;
    String partnerOneRegId;
    String partnerOneID;

    public Test_0sendPartnerRequest_searchExisting() {
        super(MainActivity.class);
    }

    //GIVEN THAT the user is logged in AND is unpaired
    @Before
    public void test_first() {
        partnerOneName = "foo";
        partnerOneEmail = "foo@example.com";
        partnerOneRegId = "1234567890";
        partnerOneID = "0987654321";
        email = "bar@example.com";
    }

    //WHEN the user hits Add a Partner AND types in an email address AND hits Send
    //THEN a request is sent but a relationship is not created
    @Test
    public void test_sendPartnerRequest_searchExistingRelationships() {
        mainActivity = getActivity();
        mainActivity.relationship = new Relationship();
        mainActivity.relationship.partnerOneName = "foo";
        mainActivity.relationship.partnerOneEmail = "foo@example.com";
        mainActivity.relationship.partnerOneRegId = "1234567890";
        mainActivity.relationship.partnerOneID = "0987654321";
        final String entered_email = "bar@example.com";

        mainActivity.sendPartnerRequest(entered_email);
        //mainActivity.sendPartnerRequest(entered_email);
        //mainActivity.sendPartnerRequest("entered_email);

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
                Log.d("sendPartnerRequest", "correctly did not find a relationship");
            }

            @Override
            public void onCancelled(FirebaseError fireBaseError) {
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });
    }
}