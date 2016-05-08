package com.example.noellin.coupletones;


import android.app.Activity;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeremy on 5/4/16.
 */
public class FireBaseInteractor {

    //public Relationship relationship;

    public FireBaseInteractor(MainActivity mainActivity){
        //this.relationship = mainActivity.relationship;
    }
    public FireBaseInteractor(SignInActivity signInActivity){
        //this.relationship = new Relationship();
    }

    public void checkForRequest(final MainActivity callingActivity){
        Log.d("checkForRequest","checking the database for pending request");
        Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/requests");
        final String receiverEmail = callingActivity.relationship.partnerOneEmail;
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot req, String s) {
                if (req.child("receiverEmail").getValue() != null
                        && req.child("receiverEmail").getValue().toString().equals(receiverEmail)){
                    //Found a request! We are loved.
                    String senderName = req.child("senderName").getValue().toString();
                    String senderEmail = req.child("senderEmail").getValue().toString();
                    String senderRegId = req.child("senderRegId").getValue().toString();
                    req.getRef().setValue(null);
                    callingActivity.respondToRequest(senderName, senderEmail, senderRegId);
                }
            }
            //Unused
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("Read failed", "Read failed in addChildEventListener");
            }
        });
    }

    //helper method to send a partner request from the Add Partner dialogue
    public void sendPartnerRequest(final String entered_email,
                                   final MainActivity callingActivity){
        Log.d("sendPartnerRequest","entered email: "+entered_email);
        final String id = callingActivity.relationship.partnerOneID;
        final String myName = callingActivity.relationship.partnerOneName;
        final String myEmail = callingActivity.relationship.partnerOneEmail;
        final String regId = callingActivity.relationship.partnerOneRegId;
        Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");

        //attach a listener to read the data
        ref.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot){
                long counter = -1;
                Log.d("sendPartnerRequest","calling onDataChange");

                //Loop through each of the relationships in the database
                for (DataSnapshot rel : snapshot.getChildren()){
                    counter++;
                    //Check if current relationship has the requested partner
                    if (rel.child("emailOne").getValue().toString().equals(entered_email)
                            || rel.child("emailTwo").getValue().toString().equals(entered_email)) {
                        //TODO: error, the requested partner already has a partner
                        Log.d("sendPartnerRequest","SOMETHING BAD HAPPENED");
                        return;
                    }
                }
                //no relationship was found including the user. Create a new request in the database
                Firebase root = snapshot.getRef().getParent().child("requests");
                Map<String, Object> newEntry = new HashMap<String, Object>();
                String reqName = id;
                newEntry.put(reqName, "");
                root.updateChildren(newEntry);

                //Create Maps to put data in for the database
                Map<String, Object> senderName = new HashMap<String, Object>();
                Map<String, Object> senderEmail = new HashMap<String, Object>();
                Map<String, Object> senderRegId = new HashMap<String, Object>();
                Map<String, Object> receiverEmail = new HashMap<String, Object>();
                senderName.put("senderName", myName);
                senderEmail.put("senderEmail", myEmail);
                senderRegId.put("senderRegId", regId);
                Log.d("sendPartnerRequest","sending regid: "+regId);
                receiverEmail.put("receiverEmail", entered_email);

                //update the request in the database with the new information
                root.child(reqName).updateChildren(senderName);
                root.child(reqName).updateChildren(senderEmail);
                root.child(reqName).updateChildren(senderRegId);
                root.child(reqName).updateChildren(receiverEmail);

            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });
    }

    public void acceptRequest(String senderName, String senderEmail, String senderRegId, final MainActivity callingActivity){
        Firebase root = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");

        //no relationship was found including the user. Create a new request in the database
        Map<String, Object> newEntry = new HashMap<String, Object>();
        String relName = callingActivity.relationship.partnerOneID;
        //String relName = myId;
        newEntry.put(relName, "");
        root.updateChildren(newEntry);

        callingActivity.relationship.partnerTwoName = senderName;
        callingActivity.relationship.partnerTwoEmail = senderEmail;
        callingActivity.relationship.partnerTwoRegId = senderRegId;
        callingActivity.relationship.rel_id = relName;

        //Create Maps to put data in for the database
        Map<String, Object> nameOne = new HashMap<String, Object>();
        Map<String, Object> emailOne = new HashMap<String, Object>();
        Map<String, Object> regIdOne = new HashMap<String, Object>();
        Map<String, Object> nameTwo = new HashMap<String, Object>();
        Map<String, Object> emailTwo = new HashMap<String, Object>();
        Map<String, Object> regIdTwo = new HashMap<String, Object>();
        nameOne.put("nameOne", callingActivity.relationship.partnerOneName);
        emailOne.put("emailOne", callingActivity.relationship.partnerOneEmail);
        regIdOne.put("regIdOne", callingActivity.relationship.partnerOneRegId);
        nameTwo.put("nameTwo", senderName);
        emailTwo.put("emailTwo", senderEmail);
        regIdTwo.put("regIdTwo", senderRegId);

        //update the request in the database with the new information
        root.child(relName).updateChildren(nameOne);
        root.child(relName).updateChildren(emailOne);
        root.child(relName).updateChildren(regIdOne);
        root.child(relName).updateChildren(nameTwo);
        root.child(relName).updateChildren(emailTwo);
        root.child(relName).updateChildren(regIdTwo);
    }

    public void removeRelationship(MainActivity callingActivity){
        Firebase root = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");
        root.child(callingActivity.relationship.rel_id).setValue(null);
        callingActivity.relationship.partnerTwoName = null;
        callingActivity.relationship.partnerTwoEmail = null;
        callingActivity.relationship.partnerTwoRegId = null;
    }

    //This method checks if the user already has an account with CoupleTones
    //It should be called as the user logs in. The app transitions to MainActivity
    public void checkForAccount(final SignInActivity callingActivity){
        Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");
        //attach a listener to read the data
        ref.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot){

                //Loop through each of the relationships in the database
                for (DataSnapshot rel : snapshot.getChildren()){
                    //Check if current relationship has the user as Partner 1 by comparing the acct email
                    if (rel.child("emailOne").getValue().toString().equals(callingActivity.acct.getEmail())) {
                        //rel_number = counter;
                        //The user has an account. If there is a partner, update partnerName and partnerEmail
                        if (rel.child("nameTwo").getValue() != null){
                            callingActivity.partnerName = rel.child("nameTwo").getValue().toString();
                            callingActivity.partnerEmail = rel.child("emailTwo").getValue().toString();
                            callingActivity.rel_id = rel.getKey().toString();
                        }
                        callingActivity.myRegId = rel.child("regIdOne").getValue().toString();
                        callingActivity.partnersRegId = rel.child("regIdTwo").getValue().toString();
                        //callingActivity.accountFound = true;
                        callingActivity.toMain();
                        return;
                    }
                    //Check if current relationship has the user as Partner 2
                    else if(rel.child("emailTwo").getValue().toString().equals(callingActivity.acct.getEmail())){
                        if (rel.child("nameOne").getValue() != null){
                            callingActivity.partnerName = rel.child("nameOne").getValue().toString();
                            callingActivity.partnerEmail = rel.child("emailOne").getValue().toString();
                            callingActivity.rel_id = rel.getKey().toString();
                        }
                        callingActivity.myRegId = rel.child("regIdTwo").getValue().toString();
                        callingActivity.partnersRegId = rel.child("regIdOne").getValue().toString();
                        //callingActivity.accountFound = true;
                        callingActivity.toMain();
                        return;
                    }
                }
                //No relationship was found including the user
                callingActivity.toMain();
            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });
    }

}
