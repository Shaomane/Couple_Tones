package com.example.noellin.coupletones;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Andrew on 5/28/2016.
 */
public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private AlertDialog.Builder builder;

    long customVibes[][];
    Uri customTones[];
    ToneContainer t;

    int selectedTone = 0;
    int selectedVibetone = 0;

    String currLoc;

    private LocationController locationController;


    public MyCustomAdapter(ArrayList<String> list, MainActivity context, Vibrator vibrator) {
        this.list = list;
        this.context = context;

        locationController = new LocationController(context.relationship.rel_id, context.relationship.partnerTwoName);
        locationController.readFromDatabase();

        t = new ToneContainer(context);
        customVibes = t.getVibeTones();
        customTones = t.getTones();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        //return list.get(pos).getId();
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_layout, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        //currLoc = list.get(position);
        //System.err.println(currLoc);

        //Handle buttons and add onClickListeners
        Button viewButton = (Button)view.findViewById(R.id.view_btn);
        Button toneBtn = (Button)view.findViewById(R.id.tone_btn);
        Button vibetoneBtn = (Button)view.findViewById(R.id.vibetone_btn);

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        toneBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                notifyDataSetChanged();

                builder = new AlertDialog.Builder(context);
                builder.setTitle("Tone selection").setCancelable(true);
                final CharSequence[] items = {"Tone 0", "Tone 1", "Tone 2", "Tone 3", "Tone 4", "Tone 5",
                        "Tone 6", "Tone 7", "Tone 8", "Tone 9"};


                LinearLayout linearLayout = (LinearLayout)v.getParent().getParent();
                TextView textView = (TextView)linearLayout.findViewById(R.id.list_item_string);
                final String currLoc = textView.getText().toString();
                String currTone = locationController.getSoundTone(currLoc);
                String currToneNumberString = currTone.substring(9);
                int currToneNumber = Integer.parseInt(currToneNumberString);

                builder.setSingleChoiceItems(items, currToneNumber, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        Ringtone r = RingtoneManager.getRingtone(context, customTones[item]);
                        r.play();
                        selectedTone = item;
                    }
                });

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locationController.setSoundTone(currLoc, "SoundTone"+selectedTone);
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
        });

        vibetoneBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                notifyDataSetChanged();

                builder = new AlertDialog.Builder(context);
                builder.setTitle("VibeTone selection").setCancelable(true);

                final CharSequence[] items = {"VibeTone 0", "VibeTone 1", "VibeTone 2", "VibeTone 3", "VibeTone 4", "VibeTone 5",
                        "VibeTone 6", "VibeTone 7", "VibeTone 8", "VibeTone 9"};

                LinearLayout linearLayout = (LinearLayout)v.getParent().getParent();
                TextView textView = (TextView)linearLayout.findViewById(R.id.list_item_string);
                final String currLoc = textView.getText().toString();
                String currVibeTone = locationController.getVibeTone(currLoc);
                String currVibeToneNumberString = currVibeTone.substring(8);
                int currVibeToneNumber = Integer.parseInt(currVibeToneNumberString);

                builder.setSingleChoiceItems(items, currVibeToneNumber, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate (customVibes[item], -1);
                        selectedVibetone = item;
                    }
                });

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locationController.setVibeTone(currLoc, "VibeTone"+selectedVibetone);
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
        });

        return view;
    }
}
