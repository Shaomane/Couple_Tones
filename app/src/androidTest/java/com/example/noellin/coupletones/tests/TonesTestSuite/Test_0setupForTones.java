package com.example.noellin.coupletones.tests.TonesTestSuite;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.example.noellin.coupletones.MainActivity;
import com.firebase.client.Firebase;

import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeremy on 6/3/16.
 */
public class Test_0setupForTones extends ActivityInstrumentationTestCase2<MainActivity> {

    public Test_0setupForTones(){
        super(MainActivity.class);
    }

    MainActivity mainActivity;
    String email;
    String partnerOneName;
    String partnerOneEmail;
    String partnerOneRegId;
    String partnerOneID;

    //GIVEN THAT the user is logged in AND is unpaired
    @Before
    public void test_first() {
        partnerOneName = "foo";
        partnerOneEmail = "foo@example.com";
        partnerOneRegId = "1234567890";
        partnerOneID = "0987654321";
        email = "bar@example.com";
    }
/*
    public void test_setUpRelationship(){
        mainActivity = getActivity();
        Firebase root = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");

        Map<String, Object> newEntry = new HashMap<String, Object>();

        String relName = "01010101";

        newEntry.put(relName, "");
        root.updateChildren(newEntry);
        Log.d("relname", relName);

        //Create Maps to put data in for the database
        Map<String, Object> nameOne = new HashMap<String, Object>();
        Map<String, Object> emailOne = new HashMap<String, Object>();
        Map<String, Object> nameTwo = new HashMap<String, Object>();
        Map<String, Object> emailTwo = new HashMap<String, Object>();

        nameOne.put("nameOne","test test");
        emailOne.put("emailOne","test@test");
        nameTwo.put("nameTwo","test2 test2");
        emailTwo.put("emailTwo","test2@test2");

        //update the request in the database with the new information
        root.child(relName).updateChildren(nameOne);
        root.child(relName).updateChildren(emailOne);
        //root.child(relName).updateChildren(regIdOne);
        root.child(relName).updateChildren(nameTwo);
        root.child(relName).updateChildren(emailTwo);
        //root.child(relName).updateChildren(regIdTwo);
    }
*/
}
