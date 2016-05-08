package com.example.noellin.coupletones.tests;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.example.noellin.coupletones.MainActivity;
import com.example.noellin.coupletones.R;
import com.example.noellin.coupletones.SignInActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by sinan on 5/7/2016.
 */
public class test_signInAct extends ActivityInstrumentationTestCase2{

    private SignInActivity s;

    public test_signInAct() {
        super(SignInActivity.class);
    }

    @Before
    public void setUp() throws Exception {

        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        s = (SignInActivity) getActivity();
    }

    @Test
    public void test_signInButtons() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean shown;

       // if (onView(withId(R.id.sign_out_button)).isShown() == true) {

        //}

        //Check the map button
        onView(withId(R.id.sign_out_button)).check(matches(isDisplayed()));

        //Check the Add Partner Button
        onView(withId(R.id.sign_in_button)).check(matches(isDisplayed()));
    }
}
