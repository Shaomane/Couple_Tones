package com.example.noellin.coupletones;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by noellin on 5/29/16.
 */
public class VisitsActivity extends FragmentActivity {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    Relationship relationship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d ("abc", "@@@@@@@@@@@@@@@@@@@@@WE ARE IN VISITSACTIVITY");
        relationship = new Relationship();
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_visits);

        if (extras != null){

            relationship.rel_id = extras.getString("rel_id");
            relationship.partnerTwoName = extras.getString("partnerName");
        }

        ListView list = (ListView) findViewById(R.id.visitsList);
        adapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, listItems);

        list.setAdapter(adapter);

        adapter.clear();
        adapter.notifyDataSetChanged();

        Firebase ref1 = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/"+relationship.rel_id+
                "/"+relationship.partnerTwoName+"_Locations/");

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listItems.clear();
                for (DataSnapshot location: dataSnapshot.getChildren())
                {
                    System.err.println("Adding "+location.getKey()+" @@@@@@@@@@@@@@@@@@@");
                    listItems.add (location.getKey());
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
    }

}
