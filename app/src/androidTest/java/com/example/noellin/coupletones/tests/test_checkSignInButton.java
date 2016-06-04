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
public class test_checkSignInButton extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    public test_checkSignInButton() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {

        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mainActivity = getActivity();
    }
/*
    @Test
    public void test_checkSignInButtons() {

        mainActivity = getActivity();

        //Sleep to let the emulator bring up the correct screen
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Button continueBtn = (Button) mainActivity.findViewById(R.id.continue_button);
        //continueBtn.performClick();

        //Check the sign out button
        onView(withId(R.id.sign_out_button)).check(matches(isDisplayed()));

        //Check the sign in button
        onView(withId(R.id.continue_button)).check(matches(isDisplayed()));

    }
*/
}
