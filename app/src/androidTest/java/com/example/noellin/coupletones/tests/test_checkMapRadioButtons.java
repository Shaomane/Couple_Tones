package com.example.noellin.coupletones.tests;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.example.noellin.coupletones.MapsActivity;
import com.example.noellin.coupletones.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class test_checkMapRadioButtons extends ActivityInstrumentationTestCase2<MapsActivity>{
    private MapsActivity mapsActivity;

    public test_checkMapRadioButtons() {
        super(MapsActivity.class);
    }

    @Before
    public void setUp() throws Exception {

        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mapsActivity = getActivity();
    }

    @Test
    public void test_checkMapsButtons() {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check the No Action Button is displayed
        onView(withId(R.id.noAction)).check(matches(isDisplayed()));

        //Check the Add Location Button is displayed
        onView(withId(R.id.addLocation)).check(matches(isDisplayed()));

        //Check the Remove Location Button is displayed
        onView(withId(R.id.removeLocation)).check(matches(isDisplayed()));

    }
}
