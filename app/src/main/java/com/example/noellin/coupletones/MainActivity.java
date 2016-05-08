package com.example.noellin.coupletones;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.ValueEventListener;
import com.firebase.client.snapshot.IndexedNode;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private String name = "";
    private String email = "";
    private String partnerName = "";
    private String partnerEmail = "";
    private String rel_id = "";
    public static String partnersRegId = "";
    public static String myRegId = "";
    protected static GoogleSignInAccount acct;
    static final int PREFERENCE_MODE_PRIVATE = 0;                   // int for shared preferences open mode
    public static final String SAVED_LOCATIONS = "Saved_locations_file";  // file where locations are stored

    GoogleCloudMessaging gcm;
    //String regid;
    String PROJECT_NUMBER = "290538927222";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //determine if the user has logged in
        Bundle extras = getIntent().getExtras();
        boolean logged_in = false;
        if (extras != null){
            logged_in = extras.getBoolean("logged_in");
            acct = SignInActivity.acct;
            name = acct.getDisplayName();
            email = acct.getEmail();
            partnerName = extras.getString("partnerName");
            partnerEmail = extras.getString("partnerEmail");
            rel_id = extras.getString("rel_id");
            myRegId = extras.getString("myRegId");
            partnersRegId = extras.getString("partnersRegId");

            Log.d("found extras", "result of name: " + name);
            Log.d("found extras", "result of email: " + email);
            Log.d("found extras", "result of partnerName: "+partnerName);
            Log.d("found extras", "result of partnerEmail: "+partnerEmail);
            Log.d("found extras", "result of myRegId: "+myRegId);
            Log.d("found extras", "result of partnersRegId: "+partnersRegId);
        }

        Button removePartnerButton = (Button)findViewById(R.id.removePartnerButton);
        Button addPartnerButton = (Button)findViewById(R.id.addPartnerButton);
        if (partnerName == null) {
            checkForRequest();
            addPartnerButton.setClickable(true);
            addPartnerButton.setVisibility(View.VISIBLE);
            removePartnerButton.setClickable(false);
            removePartnerButton.setVisibility(View.GONE);
        }
        else{
            //We are in a relationship. Do not allow another add
            addPartnerButton.setClickable(false);
            addPartnerButton.setVisibility(View.GONE);
            removePartnerButton.setVisibility(View.VISIBLE);
            removePartnerButton.setClickable(true);

        }

        //logged_in = true;//TODO: remove this. It's only so that everyone else can use the app without it keeping them at the login
        //if not logged in make em log in
        if (!logged_in) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        ListView list = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, listItems);
        list.setAdapter(adapter);

        getRegId();
    }

    // loads up the locations from shared preferences and lists them on the main screen using Map<>.
    // CURRENTLY DISPLAYING ALL FAVORITE LOCATIONS, rather than visited locations
    // attempts to refresh favorite locations page upon returning from maps
    protected void onResume() {
        super.onResume();
        adapter.clear();
        adapter.notifyDataSetChanged();

        SharedPreferences savedLocations = getSharedPreferences(SAVED_LOCATIONS, PREFERENCE_MODE_PRIVATE);

        Map<String, ?> previousLocations = savedLocations.getAll();

        for (Map.Entry<String, ?> entry : previousLocations.entrySet()) {

            if (!(listItems.contains(entry.getKey()))) {
                listItems.add(entry.getKey());
            }
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if (partnerName == null)
            checkForRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_signout){
            Intent intent = new Intent(this, SignInActivity.class);
            //intent.putExtra("doNotSignIn", true);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkForRequest(){
        Log.d("checkForRequest","checking the database for pending request");
        Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/requests");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot req, String s) {
                if (req.child("receiverEmail").getValue() != null && req.child("receiverEmail").getValue().toString().equals(email)){
                    //Found a request! We are loved.
                    String senderName = req.child("senderName").getValue().toString();
                    String senderEmail = req.child("senderEmail").getValue().toString();
                    String senderRegId = req.child("senderRegId").getValue().toString();
                    req.getRef().setValue(null);
                    respondToRequest(senderName, senderEmail, senderRegId);
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
    public void sendPartnerRequest(final String entered_email){
        Log.d("sendPartnerRequest","entered email: "+entered_email);
        Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");
        //attach a listener to read the data
        ref.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public synchronized void onDataChange(DataSnapshot snapshot){
                long counter = -1;

                //Loop through each of the relationships in the database
                for (DataSnapshot rel : snapshot.getChildren()){
                    counter++;
                    //Check if current relationship has the requested partner
                    if (rel.child("emailOne").getValue().toString().equals(entered_email)
                            || rel.child("emailTwo").getValue().toString().equals(entered_email)) {
                        //TODO: error, the requested partner already has a partner
                        return;
                    }
                }
                //no relationship was found including the user. Create a new request in the database
                Firebase root = snapshot.getRef().getParent().child("requests");
                Map<String, Object> newEntry = new HashMap<String, Object>();
                String reqName = acct.getId();
                newEntry.put(reqName, "");
                root.updateChildren(newEntry);

                //Create Maps to put data in for the database
                Map<String, Object> senderName = new HashMap<String, Object>();
                Map<String, Object> senderEmail = new HashMap<String, Object>();
                Map<String, Object> senderRegId = new HashMap<String, Object>();
                Map<String, Object> receiverEmail = new HashMap<String, Object>();
                senderName.put("senderName", acct.getDisplayName());
                senderEmail.put("senderEmail", acct.getEmail());
                senderRegId.put("senderRegId", myRegId);
                Log.d("sendPartnerRequest","sending regid: "+myRegId);
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

    public void respondToRequest(String senderName, String senderEmail, String senderRegId){

        final String secondName = senderName;
        final String secondEmail = senderEmail;
        final String secondRegId = senderRegId;
        AlertDialog.Builder respondToRequestDialogue = new AlertDialog.Builder(MainActivity.this);
        respondToRequestDialogue.setTitle("Partner Request:");
        respondToRequestDialogue.setMessage(senderName+" ("+senderEmail+") sent you a partner request!");

        respondToRequestDialogue.setPositiveButton("Accept",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Firebase root = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");

                        //no relationship was found including the user. Create a new request in the database
                        Map<String, Object> newEntry = new HashMap<String, Object>();
                        String relName = acct.getId();
                        newEntry.put(relName, "");
                        root.updateChildren(newEntry);

                        //Create Maps to put data in for the database
                        Map<String, Object> nameOne = new HashMap<String, Object>();
                        Map<String, Object> emailOne = new HashMap<String, Object>();
                        Map<String, Object> regIdOne = new HashMap<String, Object>();
                        Map<String, Object> nameTwo = new HashMap<String, Object>();
                        Map<String, Object> emailTwo = new HashMap<String, Object>();
                        Map<String, Object> regIdTwo = new HashMap<String, Object>();
                        nameOne.put("nameOne", name);
                        emailOne.put("emailOne", email);
                        regIdOne.put("regIdOne", myRegId);
                        nameTwo.put("nameTwo", secondName);
                        emailTwo.put("emailTwo", secondEmail);
                        regIdTwo.put("regIdTwo", secondRegId);

                        partnersRegId = secondRegId;

                        //update the request in the database with the new information
                        root.child(relName).updateChildren(nameOne);
                        root.child(relName).updateChildren(emailOne);
                        root.child(relName).updateChildren(regIdOne);
                        root.child(relName).updateChildren(nameTwo);
                        root.child(relName).updateChildren(emailTwo);
                        root.child(relName).updateChildren(regIdTwo);
                        rel_id = relName;
                    }
                });

        respondToRequestDialogue.setNegativeButton("Decline",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        respondToRequestDialogue.show();
    }

    public void removeRelationship(){
        Firebase root = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");
        root.child(rel_id).setValue(null);

        Button removePartnerButton = (Button)findViewById(R.id.removePartnerButton);
        Button addPartnerButton = (Button)findViewById(R.id.addPartnerButton);

        addPartnerButton.setClickable(true);
        addPartnerButton.setVisibility(View.VISIBLE);
        removePartnerButton.setClickable(false);
        removePartnerButton.setVisibility(View.GONE);
    }

    //Called by clicking the Add Partner button. Creates a dialogue that goes through the partner
    //adding process
    public void addPartner(View view){

        AlertDialog.Builder addPartnerDialogue = new AlertDialog.Builder(MainActivity.this);
        addPartnerDialogue.setTitle("Send an Invite");

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        addPartnerDialogue.setView(input);

        addPartnerDialogue.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String entered_email = input.getText().toString();
                        dialog.cancel();
                        sendPartnerRequest(entered_email);
                    }
                });

        addPartnerDialogue.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        addPartnerDialogue.show();

    }

    public void removePartner(View view){
        AlertDialog.Builder removePartnerDialogue = new AlertDialog.Builder(MainActivity.this);
        removePartnerDialogue.setTitle("Remove Partner");
        removePartnerDialogue.setMessage("Are you sure you want to remove " + partnerName + "?");

        removePartnerDialogue.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        removeRelationship();
                    }
                });

        removePartnerDialogue.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        removePartnerDialogue.show();
    }

    //Called by clicking To Map button. Transitions to the map activity
    public void toMap(View view){

        //Move to activity
        Intent intent = new Intent(this, MapsActivity.class);

        startActivity(intent);
    }

    public void getRegId() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if(gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }

                    myRegId = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + myRegId;
                    Log.i("GCM", "!!!!! " + myRegId);

                } catch(IOException ex) {
                    msg = "Error: " + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                /*etRegId.setText(msg);*/
            }
        }.execute(null, null, null);
    }

}
