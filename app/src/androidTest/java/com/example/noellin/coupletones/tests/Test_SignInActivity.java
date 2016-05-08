package com.example.noellin.coupletones.tests;

import com.example.noellin.coupletones.R;
import com.example.noellin.coupletones.SignInActivity;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static java.util.regex.Pattern.matches;

/**
 * Created by jeremy on 5/7/16.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class Test_SignInActivity {

    //SignInActivity signInActivity;
    /*public Test_SignInActivity(){
        super(SignInActivity.class);
    }*/

    @Rule
    public ActivityTestRule<SignInActivity> mActivityRule =
            new ActivityTestRule<SignInActivity>(SignInActivity.class);

    @Test
    public void testInitialSignIn(){

        try {
            //In this case, a cached sign in occured. Press Sign Out before signing in
            onView(withText("Sign Out")).check(matches(isDisplayed()));
            onView(withId(R.id.sign_out_button)).perform(click());
        } catch (NoMatchingViewException e) {
            //There was no view with "Sign Out" Continue as normal
        }

        //getActivity();

        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withText("cse110group2@gmail.com")).perform(click());

        //Check that the user was signed in but still in the SignInActivity
        onView(withId(R.id.status)).check(matches(withText("Logged in as: CSE110 Test")));
    }


}
