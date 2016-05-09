package com.example.noellin.coupletones.tests;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.example.noellin.coupletones.MainActivity;
import com.example.noellin.coupletones.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Chauncey on 5/8/16.
 */
@RunWith(AndroidJUnit4.class)
public class test_checkMainListButton extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    public test_checkMainListButton() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {

        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mainActivity = getActivity();
    }

    @Test
    public void test_checkMainListButtons() {
        mainActivity = getActivity();

        Button continueBtn = (Button) mainActivity.findViewById(R.id.continue_button);

        if (continueBtn != null) {
            continueBtn.performClick();
        }
        //onView(withId(R.id.mapButton)).check(matches(isDisplayed()));
        //onView(withId(R.id.removePartnerButton)).check(matches(isDisplayed()));
    }
}
