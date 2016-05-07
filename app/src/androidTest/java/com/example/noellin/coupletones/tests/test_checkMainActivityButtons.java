package com.example.noellin.coupletones.tests;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.example.noellin.coupletones.MainActivity;
import com.example.noellin.coupletones.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class test_checkMainActivityButtons extends ActivityInstrumentationTestCase2<MainActivity> {

    /*MainActivity mainActivity;
    public test_checkMainActivityButtons() {
        super(MainActivity.class);
    }

    public void test_checkText() {

        String MAP_TEXT = "Map";

        mainActivity = getActivity();

        Button mapButton = (Button) mainActivity.findViewById(R.id.mapButton);

        String mapText = (String) mapButton.getText();

        assertEquals(MAP_TEXT, mapText);
    }*/

    private MainActivity mainActivity;

    public test_checkMainActivityButtons() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {

        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mainActivity = getActivity();
    }

    @Test
    public void test_checkMainButtons() {
        onView(withId(R.id.mapButton)).check(matches(isDisplayed()));
    }
}
