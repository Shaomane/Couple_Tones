package com.example.noellin.coupletones;


import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeremy on 5/4/16.
 *
 * This class defines a FireBaseInteractor, which is used to interface with a Firebase database while
 * "minding our own business."  Methods are defined that write or retrieve data.
 */
public class FireBaseAdapter {

    Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com");
    ChildEventListener listenerForAcceptedRequest = null;
    ChildEventListener listenerForBreakup = null;
    ChildEventListener listenerForRequests = null;
    ValueEventListener searchRelationshipListener = null;
    ValueEventListener searchRequestListener = null;
    ValueEventListener sendPartnerRequestListener = null;

    boolean started = false;
    boolean ended = false;
    boolean accepted = false;

    public FireBaseAdapter(){}

    public void startListenerForAcceptedRequest(final MainActivity callingActivity){
        Log.d("startListener", "starting Listener for Accepted Request");
        ref.child("relationships").addChildEventListener(listenerForAcceptedRequest = new ChildEventListener() {
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
        ref.child("relationships").addListenerForSingleValueEvent(searchRelationshipListener = new ValueEventListener() {
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
                        startListenerForBreakup(callingActivity);
                        if (!callingActivity.isMyServiceRunning(BackgroundListenerService.class))
                        {
                            callingActivity.startService(callingActivity.backgroundIntent);
                        }
                        started = false;
                        ref.child("requests").removeEventListener(listenerForRequests);

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
    public void startListenerForBreakup(final MainActivity callingActivity){
        ref.child("relationships").addChildEventListener(listenerForBreakup = new ChildEventListener() {
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
                    //TODO: if this causes a last minute problem put it in its own method with a delay

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

        if (ended)
            return;

        callingActivity.stopService(callingActivity.backgroundIntent);
        callingActivity.relationship.partnerTwoEmail = null;
        callingActivity.relationship.partnerTwoName = null;
        callingActivity.updateUI();
        callingActivity.myCustomAdapter.removeRelationship();
        startListenerForRequests(callingActivity);

        Intent intent = new Intent(callingActivity, SignInActivity.class);
        callingActivity.stopService(callingActivity.backgroundIntent);
        callingActivity.startActivity(intent);

        Toast.makeText(callingActivity, "Unfortunately, you have been broken up with.", Toast.LENGTH_LONG).show();

        callingActivity.finish();
    }

    public void searchRequests(final MainActivity callingActivity){

        try{Thread.sleep(500);}catch(InterruptedException e){}
        final String receiverEmail = callingActivity.relationship.partnerOneEmail;
        ref.child("requests").addListenerForSingleValueEvent(searchRequestListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot request : dataSnapshot.getChildren()){
                    Log.d("request", "request: "+request);
                    if (request.child("receiverEmail").getValue() != null &&
                            request.child("receiverEmail").getValue().toString().equals(receiverEmail)){

                        if (accepted == true)
                            return;

                        //Found a request! We are loved.
                        accepted = true;//temporarily true. will return to false if we decline

                        String senderName = request.child("senderName").getValue().toString();
                        String senderEmail = request.child("senderEmail").getValue().toString();
                        String senderRegId = request.child("senderRegId").getValue().toString();
                        request.getRef().setValue(null);
                        callingActivity.respondToRequest(senderName, senderEmail, senderRegId, request);
                    }
                }
                ref.child("requests").removeEventListener(searchRequestListener);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void declineRequest(){
        accepted = false;
    }

    /*
    This method checks the database for a pending request. A listener is set up to continuously listen
    for new requests, and is additionally prompted to search upon creation
     */
    public void startListenerForRequests(final MainActivity callingActivity){
        Log.d("checkForRequests","checking the database for pending request");

        if (started == true) return;
        started = true;

        ref.child("requests").addChildEventListener(listenerForRequests = new ChildEventListener() {
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

        //attach a listener to read the data
        ref.child("relationships").addListenerForSingleValueEvent(sendPartnerRequestListener = new ValueEventListener(){
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

                startListenerForAcceptedRequest(callingActivity);

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

        started = false;
        ref.child("requests").removeEventListener(listenerForRequests);

        //no relationship was found including the user. Create a new request in the database
        Map<String, Object> newEntry = new HashMap<String, Object>();
        String relName = callingActivity.relationship.partnerOneID;
        //String relName = myId;
        newEntry.put(relName, "");
        root.updateChildren(newEntry);
        Log.d("relname", relName);

        //Update the Relationship from MainActivity
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

        startListenerForBreakup(callingActivity);
    }


    /*
    This method erases an existing relationship from the database
     */
    public void removeRelationship(MainActivity callingActivity){
        Toast.makeText(callingActivity, "You are no longer paired with "+
                callingActivity.relationship.partnerTwoName, Toast.LENGTH_LONG).show();

        ended = true;

        ref.removeEventListener(listenerForBreakup);

        Firebase root = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");
        root.child(callingActivity.relationship.rel_id).setValue(null);
        callingActivity.myCustomAdapter.removeRelationship();
        callingActivity.relationship.partnerTwoName = null;
        callingActivity.relationship.partnerTwoEmail = null;
        callingActivity.relationship.partnerTwoRegId = null;
        Intent intent = new Intent(callingActivity, SignInActivity.class);
        callingActivity.stopService(callingActivity.backgroundIntent);
        callingActivity.startActivity(intent);
        callingActivity.finish();
    }

    //This method checks if the user already has an account with CoupleTones
    //It should be called as the user logs in. The app transitions to MainActivity
    public void checkForAccount(final SignInActivity callingActivity){

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

    public void getPartnerFavoriteLocationsList(MainActivity callingActivity, final ArrayList<String> listItems){

        if (callingActivity.relationship.rel_id == null){
            return;
        }

        ref.child("relationships").child(callingActivity.relationship.rel_id)
                .child(callingActivity.relationship.partnerTwoName+"_Locations")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot loc : dataSnapshot.getChildren()){
                            listItems.add(loc.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

    }

}
