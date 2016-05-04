package com.example.noellin.coupletones;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* TALK ABOUT THIS ONE. I'm thinking on app open, check if the app is connected to an account.
        If not, bring the user to the login activity automatically. We may need some sort of log out
        feature, and a way to save the user's login information between opens/closes of the app -- Jeremy
         */
        //determine if the user has logged in
        Log.d("tag", "MAIN ACTIVITY WAS RUN");
        Bundle extras = getIntent().getExtras();
        boolean logged_in = false;
        if (extras != null){
            Log.d("othertag", "found extras");
            logged_in = extras.getBoolean("logged_in");
        }

        //logged_in = true;//TODO: remove this. It's only so that everyone else can use the app without it keeping them at the login
        //if not logged in make em log in
        if (!logged_in) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
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
        listItems.add("");
        adapter.notifyDataSetChanged();
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

        return super.onOptionsItemSelected(item);
    }

    public void toMap(View view){

        //Move to activity
        Intent intent = new Intent(this, MapsActivity.class);

        startActivity(intent);
    }

}
