package com.example.noellin.coupletones;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setup Firebase for Android for database
        loadFromDatabase();

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
            Log.d("found extras", "result of logged_in: " + logged_in);
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
        listItems.add("");
        listItems.add("");
        listItems.add("");
        listItems.add("");
        listItems.add("");
        listItems.add("");
        listItems.add("");
        listItems.add("");
        listItems.add("");
        listItems.add("");
        adapter.notifyDataSetChanged();
    }

    public void loadFromDatabase(){
        Log.d("loadFromDatabase", "loadFromDataBase called");
        Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships");

        //attach a listener to read the data
        ref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot){
                /*
                long numChildren = snapshot.getChildrenCount();
                Log.d("numChildren", "found "+numChildren+" children");
                String partnerOneName0 = snapshot.child("0").child("partnerOneName").getValue().toString();
                String partnerTwoName0 = snapshot.child("0").child("partnerTwoName").getValue().toString();
                String partnerOneName1 = snapshot.child("1").child("partnerOneName").getValue().toString();
                String partnerTwoName1 = snapshot.child("1").child("partnerTwoName").getValue().toString();
                Log.d("names", partnerOneName0 + "-" + partnerTwoName0 + "-" + partnerOneName1 + "-" + partnerTwoName1);
                */
            }
            @Override
            public void onCancelled(FirebaseError fireBaseError){
                Log.d("Read failed", "Read failed in addValueListener");
            }
        });

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

    public void toMap(View view){

        //Move to activity
        Intent intent = new Intent(this, MapsActivity.class);

        startActivity(intent);
    }

}
