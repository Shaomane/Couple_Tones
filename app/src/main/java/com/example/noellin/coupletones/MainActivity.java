package com.example.noellin.coupletones;

import android.app.ActivityManager;
import android.content.Context;
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
import android.widget.Toast;

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

    public Relationship relationship;
    public FireBaseInteractor FBInteractor = new FireBaseInteractor(this);

    public static String partnersRegId = "";

    static final int PREFERENCE_MODE_PRIVATE = 0;                   // int for shared preferences open mode
    public static final String SAVED_LOCATIONS = "Saved_locations_file";  // file where locations are stored

    GoogleCloudMessaging gcm;
    String PROJECT_NUMBER = "290538927222";
    Intent backgroundIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backgroundIntent = new Intent(MainActivity.this, BackgroundListenerService.class);

        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        relationship = new Relationship();

        //determine if the user has logged in
        Bundle extras = getIntent().getExtras();
        boolean logged_in = false;
        if (extras != null){
            relationship.rel_id = extras.getString("rel_id");

            relationship.partnerOneName = extras.getString("name");
            relationship.partnerOneEmail = extras.getString("email");
            relationship.partnerOneRegId = extras.getString("myRegId");
            relationship.partnerOneID = extras.getString("ID");

            relationship.partnerTwoName = extras.getString("partnerName");
            relationship.partnerTwoEmail = extras.getString("partnerEmail");
            relationship.partnerTwoRegId = extras.getString("partnersRegId");

            logged_in = extras.getBoolean("logged_in");

            Log.d("found extras", "result of name: " + relationship.partnerOneName);
            Log.d("found extras", "result of email: " + relationship.partnerOneEmail);
            Log.d("found extras", "result of partnerName: "+relationship.partnerTwoName);
            Log.d("found extras", "result of partnerEmail: "+relationship.partnerTwoEmail);
            Log.d("found extras", "result of myRegId: "+relationship.partnerOneRegId);
            Log.d("found extras", "result of partnersRegId: "+relationship.partnerTwoRegId);
        }

        //if not logged in make em log in
        if (!logged_in) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (!isMyServiceRunning(BackgroundListenerService.class)) {
            backgroundIntent.putExtra("rel_id", relationship.rel_id);
            backgroundIntent.putExtra("partner_email", relationship.partnerTwoEmail);
            startService(backgroundIntent);
        }
        else{
            Log.d("else","background service was not started ");
        }

        Button removePartnerButton = (Button)findViewById(R.id.removePartnerButton);
        Button addPartnerButton = (Button)findViewById(R.id.addPartnerButton);
        if (relationship.partnerTwoName == null) {
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
        //if (relationship.partnerTwoName == null)
        //    checkForRequest();
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

        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_signout){
            Intent intent = new Intent(this, SignInActivity.class);
            stopService(backgroundIntent);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkForRequest(){
        FBInteractor.checkForRequest(this);
    }

    //helper method to send a partner request from the Add Partner dialogue
    public void sendPartnerRequest(final String entered_email){
        FBInteractor.sendPartnerRequest(entered_email, this);
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
                        acceptRequest(secondName, secondEmail, secondRegId);
                        dialog.cancel();
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

    public void acceptRequest(String senderName, String senderEmail, String senderRegId){
        FBInteractor.acceptRequest(senderName, senderEmail, senderRegId, this);
    }

    public void removeRelationship(){
        FBInteractor.removeRelationship(this);

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

    public void showPartnerAlreadyPairedError(String entered_email){
        Toast.makeText(MainActivity.this, "Error: "+entered_email+" is already paired", Toast.LENGTH_SHORT).show();
    }

    public void showPartnerRequestConfirmation(String entered_email){
        Toast.makeText(MainActivity.this, "Error: "+entered_email+" is already paired", Toast.LENGTH_SHORT).show();
    }

    public void removePartner(View view){
        AlertDialog.Builder removePartnerDialogue = new AlertDialog.Builder(MainActivity.this);
        removePartnerDialogue.setTitle("Remove Partner");
        removePartnerDialogue.setMessage("Are you sure you want to remove " + relationship.partnerTwoName + "?");

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
        intent.putExtra("rel_id", relationship.rel_id);
        intent.putExtra("senderEmail", relationship.partnerOneEmail);
        intent.putExtra("senderName", relationship.partnerOneName);

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

                    relationship.partnerOneRegId = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + relationship.partnerOneRegId;
                    Log.i("GCM", "!!!!! " + relationship.partnerOneRegId);

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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
