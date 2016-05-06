package com.example.noellin.coupletones;


import com.firebase.client.Firebase;

/**
 * Created by jeremy on 5/4/16.
 */
public class FireBaseInteractor {
    Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/data");
/*
    ref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            System.out.println(snapshot.getValue());
        }
        @Override
        public void onCancelled(FirebaseError firebaseError) {
            System.out.println("The read failed: " + firebaseError.getMessage());
        }
    });
*/

}
