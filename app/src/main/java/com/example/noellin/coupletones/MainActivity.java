package com.example.noellin.coupletones;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    MyCustomAdapter myCustomAdapter;

    public Relationship relationship;
    public FireBaseInteractor FBInteractor;// = new FireBaseInteractor();

    static final int PREFERENCE_MODE_PRIVATE = 0;                   // int for shared preferences open mode
    public static String SAVED_LOCATIONS = "Saved_locations_file";  // file where locations are stored

    GoogleCloudMessaging gcm;
    String PROJECT_NUMBER = "290538927222";
    Intent backgroundIntent;

    private AlertDialog.Builder builder;
    ArrayList mSelectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setup for the background intent listening for new notifications, as well as the Firebase database
        backgroundIntent = new Intent(MainActivity.this, BackgroundListenerService.class);
        Firebase.setAndroidContext(this);

        FBInteractor = new FireBaseInteractor();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        relationship = new Relationship();

        //determine if the user has logged in
        Bundle extras = getIntent().getExtras();
        //boolean logged_in = false;
        if (extras != null){
            relationship.rel_id = extras.getString("rel_id");
            relationship.partnerOneName = extras.getString("name");
            relationship.partnerOneEmail = extras.getString("email");
            relationship.partnerOneRegId = extras.getString("myRegId");
            relationship.partnerOneID = extras.getString("ID");

            relationship.partnerTwoName = extras.getString("partnerName");
            relationship.partnerTwoEmail = extras.getString("partnerEmail");
            relationship.partnerTwoRegId = extras.getString("partnersRegId");

            SAVED_LOCATIONS+=relationship.partnerOneName;

            //Check if the BackgroundListenerService is running. Only start if not AND we are in a relationship
            if (relationship.partnerTwoName != null && !isMyServiceRunning(BackgroundListenerService.class)) {
                backgroundIntent.putExtra("rel_id", relationship.rel_id);
                backgroundIntent.putExtra("partner_email", relationship.partnerTwoEmail);
                startService(backgroundIntent);
                startListenerForCheatingHoe();
            }
            else{
                startListenerForRequests();
                startListenerForAcceptedRequest();
            }

        }
        //if not logged in make the user log in by transitioning to SignInActivity
        else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        //Update the UI depending on whether we are in a relationship or not
        updateUI();

        ListView list = (ListView) findViewById(R.id.list);
        //adapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, listItems);

        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        FBInteractor.getPartnerFavoriteLocationsList(this, listItems);

        myCustomAdapter = new MyCustomAdapter(listItems, this, v);
        list.setAdapter(myCustomAdapter);

        getRegId();
    }

    private void startListenerForAcceptedRequest(){
        FBInteractor.startListenerForAcceptedRequest(this);
    }

    private void startListenerForCheatingHoe(){
        FBInteractor.startListenerForCheatingHoe(this);
    }

    public void updateUI() {
        //Set up the Add/Remove Partner button accordingly depending on whether the user is paired
        Button removePartnerButton = (Button)findViewById(R.id.removePartnerButton);
        Button addPartnerButton = (Button)findViewById(R.id.addPartnerButton);
        if (relationship.partnerTwoName == null) {
            //startListenerForRequests();
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
    }

    // loads up the locations from shared preferences and lists them on the main screen using Map<>.
    // CURRENTLY DISPLAYING ALL FAVORITE LOCATIONS, rather than visited locations
    // attempts to refresh favorite locations page upon returning from maps
    protected void onResume() {
        super.onResume();
        //adapter.clear();
        //adapter.notifyDataSetChanged();



        /*SharedPreferences savedLocations = getSharedPreferences(SAVED_LOCATIONS, PREFERENCE_MODE_PRIVATE);

        Map<String, ?> previousLocations = savedLocations.getAll();

        for (Map.Entry<String, ?> entry : previousLocations.entrySet()) {

            //if (!(listItems.contains(entry.getKey()))) {
            //    listItems.add(entry.getKey());
            //}
            //adapter.notifyDataSetChanged();
            myCustomAdapter.notifyDataSetChanged();

        }


        }*/

    }


    @Override
    public void onStart(){
        super.onStart();
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
            settingDialog();
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

    /*
    This method asks the Interactor to check if anyone has sent a partner request
     */
    public void startListenerForRequests(){
        FBInteractor.startListenerForRequests(this);
    }

    /*
    This method asks the Interactor to send a partner request to the entered email address
     */
    public void sendPartnerRequest(final String entered_email){
        FBInteractor.sendPartnerRequest(entered_email, this);
    }

    /*
    This method creates a dialogue that prompts the user to either accept or decline an
    incoming partner request
     */
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

    /*
    This helper method is called from respondToRequest.  It asks the Interactor to accept
    the request by creating a new relationship in the database involving the user and the
    request sender
     */
    public void acceptRequest(String senderName, String senderEmail, String senderRegId){
        FBInteractor.acceptRequest(senderName, senderEmail, senderRegId, this);

        relationship.partnerTwoName = senderName;
        relationship.partnerTwoEmail = senderEmail;
        startService(backgroundIntent);
        updateUI();
    }

    /*
    This method asks the Interactor to remove a relationship from the database, erasing the connection
    between the partners
     */
    public void removeRelationship(){
        FBInteractor.removeRelationship(this);

        relationship.partnerTwoEmail = null;
        relationship.partnerTwoName = null;
        stopService(backgroundIntent);

        updateUI();
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

    /*
    This method displays an error Toast upon sending a partner request to a user that is already paired
     */
    public void showPartnerAlreadyPairedError(String entered_email){
        Toast.makeText(MainActivity.this, "Error: "+entered_email+" is already paired", Toast.LENGTH_SHORT).show();
    }

    /*
    This method displays a confirmation Toast upon successfully sending a partner request
     */
    public void showPartnerRequestConfirmation(String entered_email){
        Toast.makeText(MainActivity.this, "Sent a request to "+ entered_email, Toast.LENGTH_SHORT).show();
    }

    /*
    This method opens a dialogue upon pressing the Remove a Partner button. It prompts for confirmation
    before calling removeRelationship or cancelling
     */
    public void removePartner(View view){
        AlertDialog.Builder removePartnerDialogue = new AlertDialog.Builder(MainActivity.this);
        removePartnerDialogue.setTitle("Remove Partner");
        removePartnerDialogue.setMessage("Are you sure you want to remove " + relationship.partnerTwoName + "?");

        removePartnerDialogue.setPositiveButton("Yes",
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

        //Move to activity, sending relevant information
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("rel_id", relationship.rel_id);

        intent.putExtra("senderEmail", relationship.partnerOneEmail);
        intent.putExtra("senderName", relationship.partnerOneName);

        startActivity(intent);
    }

    /*
    This method gets the user's reg_id, which is used for Google cloud messaging
     */
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

    /*
     * Tests whether a background service is running. Is useful for determining whether to start
     * the messaging listener
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /*
     * This is the method to run the dialog box when the settings button is pressed
     */
    private void settingDialog() {

        mSelectedItems = new ArrayList();

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Settings").setCancelable(true);
        builder.setMultiChoiceItems(R.array.settings_choices, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked) {
                            mSelectedItems.add(which);

                        } else if(mSelectedItems.contains(which)) {
                            mSelectedItems.remove(Integer.valueOf(which));
                        }
                    }
                });
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
