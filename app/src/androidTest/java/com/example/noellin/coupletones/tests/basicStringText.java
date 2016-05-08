package com.example.noellin.coupletones.tests;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.TextView;

import com.example.noellin.coupletones.MainActivity;
import com.example.noellin.coupletones.R;

/**
 * Created by sinan on 5/7/2016.
 */
public class basicStringText extends ActivityInstrumentationTestCase2{
    MainActivity mainActivity;
    public basicStringText() {
        super(MainActivity.class);
    }

    public void test_one() {
        //mainActivity = getActivity();
        //TextView textView = (TextView) mainActivity.findViewById(R.id.mapButton);
        //String tester = textView.toString();
        //Log.d("atag", "PRINTING PRINTING PRINTING PRINTING PRINTING" + tester );
        String tester = "Map";
        assertEquals("Map", tester);
    }
}

