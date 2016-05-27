package com.example.noellin.coupletones;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeremy on 5/4/16.
 *
 * This class defines a FireBaseInteractor, which is used to interface with a Firebase database while
 * "minding our own business."  Methods are defined that write or retrieve data.
 */
public class FireBaseInteractor {

    Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com");

    public FireBaseInteractor(){}

    public void startListenerForAcceptedRequest(final MainActivity callingActivity){
        ref.child("relationships").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("accepted", "relationship request was accepted");
                searchRelationships(callingActivity);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }
    public void searchRelationships(final MainActivity callingActivity){
        try{Thread.sleep(500);}catch(InterruptedException e){}
        ref.child("relationships").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot relationship : dataSnapshot.getChildren()){
                    Log.d("relationship", "relationship: "+relationship);
                    if (relationship.child("emailOne").getValue() != null && relationship.child("emailTwo").getValue() != null &&
                            (relationship.child("emailOne").getValue().toString().equals(callingActivity.relationship.partnerOneEmail) ||
                                    relationship.child("emailTwo").getValue().toString().equals(callingActivity.relationship.partnerOneEmail))){
                        //Someone accepted our request
                        Log.d("accepted", "OUR request was accepted");
                        callingActivity.relationship.partnerTwoEmail = (relationship.child("emailOne").getValue()
                                .toString().equals(callingActivity.relationship.partnerOneEmail)) ? relationship.child("emailTwo").getValue()
                                .toString() : relationship.child("emailOne").getValue().toString();
                        callingActivity.relationship.partnerTwoName = (relationship.child("emailOne").getValue()
                                .toString().equals(callingActivity.relationship.partnerOneEmail)) ? relationship.child("nameTwo").getValue()
                                .toString() : relationship.child("nameOne").getValue().toString();
                        callingActivity.relationship.rel_id = relationship.getKey();
                        callingActivity.updateUI();
                        startListenerForCheatingHoe(callingActivity);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /*
    When we are broken up with, remove all the listeners and remove relationship data from MainActivity
     */
    public void startListenerForCheatingHoe(final MainActivity callingActivity){
        ref.child("relationships").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("removed", "relationship was removed");
                if (dataSnapshot.child("emailOne").getValue() != null && dataSnapshot.child("emailTwo").getValue() != null &&
                        (dataSnapshot.child("emailOne").getValue().toString().equals(callingActivity.relationship.partnerOneEmail) ||
                        dataSnapshot.child("emailTwo").getValue().toString().equals(callingActivity.relationship.partnerOneEmail))){
                    //Fuck that bitch
                    getBrokenUpWith(callingActivity);
                    return;
                }
                Log.d("removeD","we're good, someone else got broken up with");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }

    private void getBrokenUpWith(MainActivity callingActivity) {
        //Dunno if it works like this
        ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com");

        Log.d("cheating hoe", "cheating hoe detected");
        callingActivity.stopService(callingActivity.backgroundIntent);
        callingActivity.relationship.partnerTwoEmail = null;
        callingActivity.relationship.partnerTwoName = null;
        callingActivity.updateUI();
        startListenerForRequests(callingActivity);
    }

    public void searchRequests(final MainActivity callingActivity){
        try{Thread.sleep(500);}catch(InterruptedException e){}
        final String receiverEmail = callingActivity.relationship.partnerOneEmail;
        ref.child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot request : dataSnapshot.getChildren()){
                    Log.d("request", "request: "+request);
                    if (request.child("receiverEmail").getValue() != null &&
                            request.child("receiverEmail").getValue().toString().equals(receiverEmail)){
                        //Found a request! We are loved.
                        String senderName = request.child("senderName").getValue().toString();
                        String senderEmail = request.child("senderEmail").getValue().toString();
                        String senderRegId = request.child("senderRegId").getValue().toString();
                        request.getRef().setValue(null);
                        callingActivity.respondToRequest(senderName, senderEmail, senderRegId);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /*
    This method checks the database for a pending request. A listener is set up to continuously listen
    for new requests, and is additionally prompted to search upon creation
     */
    public void startListenerForRequests(final MainActivity callingActivity){
        Log.d("checkForRequests","checking the database for pending request");
        //Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/requests");
        final String receiverEmail = callingActivity.relationship.partnerOneEmail;
        ref.child("requests").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot req, String s) {

                //Check if the request added is sent to us
                searchRequests(callingActivity);

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

    /*
    This method attempts to send a Partner Request to another user. If the other user is already
    paired, no request is generated
     */
    public void sendPartnerRequest(final String entered_email, final MainActivity callingActivity){
        Log.d("sendPartnerRequest","entered email: "+entered_email);
        final String id = callingActivity.relationship.partnerOneID;
        final String myName = callingActivity.relationship.partnerOneName;
        final String myEmail = callingActivity.relationship.partnerOneEmail;
        final String regId = callingActivity.relationship.partnerOneRegId;
        //Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");

        //attach a listener to read the data
        ref.child("relationships").addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot){
                Log.d("sendPartnerRequest","calling onDataChange");

                //Loop through each of the relationships in the database
                for (DataSnapshot rel : snapshot.getChildren()){
                    //Check if current relationship has the requested partner
                    if (rel.child("emailOne").getValue().toString().equals(entered_email)
                            || rel.child("emailTwo").getValue().toString().equals(entered_email)) {
                        callingActivity.showPartnerAlreadyPairedError(entered_email);
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
                callingActivity.showPartnerRequestConfirmation(entered_email);

            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });
    }

    /*
    This method accepts a request by adding in a new relationship to the database containing the
    requester and the requested partner
     */
    public void acceptRequest(String senderName, String senderEmail, String senderRegId, final MainActivity callingActivity){
        Firebase root = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");

        //this is getting rid of the request listener right??
        ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com");

        //no relationship was found including the user. Create a new request in the database
        Map<String, Object> newEntry = new HashMap<String, Object>();
        String relName = callingActivity.relationship.partnerOneID;
        //String relName = myId;
        newEntry.put(relName, "");
        root.updateChildren(newEntry);
        Log.d("relname", relName);

        //Update the Relationship from MainActivity
        //callingActivity.relationship.partnerTwoName = senderName;
        //callingActivity.relationship.partnerTwoEmail = senderEmail;
        //callingActivity.relationship.partnerTwoRegId = senderRegId;
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

        startListenerForCheatingHoe(callingActivity);
    }

    /*
    This method erases an existing relationship from the database
     */
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
        //Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");
        Log.d("checkForAccount","checkForAccount called");
        //attach a listener to read the data
        ref.child("relationships").addListenerForSingleValueEvent(new ValueEventListener(){
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
                        callingActivity.toMain();
                        //Log.d("rel", "rel_id" +callingActivity.rel_id);
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
                        callingActivity.toMain();
                        //Log.d("rel", "rel_id" +callingActivity.rel_id);
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
