package com.example.noellin.coupletones.tests.TonesTestSuite;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.example.noellin.coupletones.MainActivity;
import com.example.noellin.coupletones.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Chauncey on 5/29/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class test_partnerVisitsButton extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    //private Instrumentation instrumentation;

    public test_partnerVisitsButton() { super(MainActivity.class); }

    @Before
    public void setUp() throws Exception {

        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mainActivity = getActivity();
    }

    @Test
    public void testPartnerVisitsButton() {
        mainActivity = getActivity();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.continue_button)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.partnerVisits)).check(matches(isDisplayed()));
        onView(withId(R.id.partnerVisits)).perform(click());

        /* Check the partner's list is displayed */

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
