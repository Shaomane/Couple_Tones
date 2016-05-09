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
 */
public class Test_9removeRelationship extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity mainActivity;

    public Test_9removeRelationship(){
        super(MainActivity.class);
    }

    public void test_removeRelationship(){

        Firebase cleanup = new Firebase("https://dazzling-inferno-7112.firebaseio.com/requests/0987654321");
        cleanup.setValue(null);

        mainActivity = getActivity();
        mainActivity.relationship = new Relationship();
        String rel_id = "11111111";
        mainActivity.relationship.rel_id = rel_id;
        mainActivity.FBInteractor.removeRelationship(mainActivity);

        //Check if a relationship was removed from the database
        Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");
        //attach a listener to read the data
        ref.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot){
                //Loop through each of the relationships in the database
                for (DataSnapshot rel : snapshot.getChildren()){
                    //Check if current relationship has the user as Partner 1 by comparing the acct email
                    if (rel.child("emailOne").getValue().toString().equals("foo@example.com")
                            || rel.child("emailTwo").getValue().toString().equals("foo@example.com")) {
                        fail("relationship was not removed");
                        return;
                    }
                }
                //No relationship was found including the user
                assertTrue("correctly found no relationship",true);
            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });

    }

}
