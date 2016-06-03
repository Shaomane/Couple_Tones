package com.example.noellin.coupletones;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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

        TextView textView = (TextView) findViewById(R.id.textView2);
        if (relationship.partnerTwoName != null) {
            textView.setText(relationship.partnerTwoName + " visited: ");
        }
        else{
            textView.setText("When paired, the locations your partner visits throughout the day will be displayed here");
        }

        Firebase ref1 = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/"+relationship.rel_id+
                "/"+relationship.partnerTwoName+"_Locations/");

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listItems.clear();
                HashMap<String, Integer> map = new HashMap<String, Integer>();


                for (DataSnapshot location: dataSnapshot.getChildren()) {
                    if (location.child("lastTimeVisited").getValue() != null) {
                        int timeInSeconds = 0;
                        System.err.println("@@@@@@@@@" + location.getKey());
                        System.err.println("@@@@@@@@@ " + location.child("lastTimeVisited/").child("day").getValue());
                        System.err.println("@@@@@@@@@ " + location.child("lastTimeVisited/").child("hour").getValue());
                        System.err.println("@@@@@@@@@ " + location.child("lastTimeVisited/").child("minute").getValue());
                        System.err.println("@@@@@@@@@ " + location.child("lastTimeVisited/").child("second").getValue());

                        int hour =  Integer.parseInt((String) location.child("lastTimeVisited/")
                                .child("hour").getValue());

                        //Delete the list of visited locations "at" 3AM. In reality this happens when the user next
                        //goes to this activity after 3, but the result is the same
                        if (hour < 3 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 3){
                            location.child("lastTimeVisited/").getRef().setValue(null);
                            return;
                        }

                        //Construct the String to display, depending on when the hour is
                        String hourString;
                        if (hour > 12){
                            hour = hour - 12;
                            hourString = hour + "PM";
                        }
                        else{
                            hourString = hour + "AM";
                        }

                        timeInSeconds += Integer.parseInt((String) location.child("lastTimeVisited/")
                                .child("day").getValue()) * 86400;
                        timeInSeconds += Integer.parseInt((String) location.child("lastTimeVisited/")
                                .child("hour").getValue()) * 3600;
                        timeInSeconds += Integer.parseInt((String) location.child("lastTimeVisited/")
                                .child("minute").getValue()) * 60;
                        timeInSeconds += Integer.parseInt((String) location.child("lastTimeVisited/")
                                .child("second").getValue());
                        System.err.println("@@@@@@@@@ Total ime in seconds: " + timeInSeconds);

                        map.put(location.getKey() + "   " + hourString, timeInSeconds);
                    }
                }
                Object[] a = map.entrySet().toArray();
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Map.Entry<String, Integer>) o2).getValue().compareTo(
                                ((Map.Entry<String, Integer>) o1).getValue());
                    }
                });

                for (Object e : a) {
                    System.err.println (((Map.Entry<String, Integer>) e).getKey());
                    listItems.add (((Map.Entry<String, Integer>) e).getKey());

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
