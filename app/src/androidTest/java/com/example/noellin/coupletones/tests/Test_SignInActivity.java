package com.example.noellin.coupletones.tests;

import com.example.noellin.coupletones.SignInActivity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Rule;
import org.junit.runner.RunWith;

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




}
