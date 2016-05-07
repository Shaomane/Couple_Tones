package com.example.noellin.coupletones;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
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
    protected static GoogleSignInAccount acct;
    static final int PREFERENCE_MODE_PRIVATE = 0;                   // int for shared preferences open mode
    public static final String SAVED_LOCATIONS = "Saved_locations_file";  // file where locations are stored


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //determine if the user has logged in
        Bundle extras = getIntent().getExtras();
        boolean logged_in = true;    // change back to false
        if (extras != null){
            logged_in = extras.getBoolean("logged_in");
            acct = SignInActivity.acct;
            name = acct.getDisplayName();
            email = acct.getEmail();
            partnerName = extras.getString("partnerName");
            partnerEmail = extras.getString("partnerEmail");
            Log.d("found extras", "result of name: " + name);
            Log.d("found extras", "result of email: " + email);
            Log.d("found extras", "result of partnerName: "+partnerName);
            Log.d("found extras", "result of partnerEmail: "+partnerEmail);
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

        //Have mercy on me guys, I'll get rid of this later --Andrew

        // loads up the locations from shared preferences and lists them on the main screen
        // using Map<>.
        // CURRENTLY DISPLAYING ALL FAVORITE LOCATIONS, rather than visited locations
        SharedPreferences savedLocations = getSharedPreferences(SAVED_LOCATIONS, PREFERENCE_MODE_PRIVATE);
        Map<String, ?> previousLocations = savedLocations.getAll();

        for (Map.Entry<String, ?> entry : previousLocations.entrySet()) {

            listItems.add(entry.getKey());
            adapter.notifyDataSetChanged();

        }
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

    //helper method to send a partner request from the Add Partner dialogue
    public void sendPartnerRequest(String entered_email){

        Log.d("sendPartnerRequest","entered email: "+entered_email);
    }

    //Called by clicking To Map button. Transitions to the map activity
    public void toMap(View view){

        //Move to activity
        Intent intent = new Intent(this, MapsActivity.class);

        startActivity(intent);
    }

}
