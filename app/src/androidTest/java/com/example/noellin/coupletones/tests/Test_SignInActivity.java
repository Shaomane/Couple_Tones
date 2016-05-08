package com.example.noellin.coupletones.tests;

import android.test.ActivityInstrumentationTestCase2;

import com.example.noellin.coupletones.SignInActivity;

public class Test_SignInActivity extends ActivityInstrumentationTestCase2<SignInActivity>{

    SignInActivity signInActivity;

    public Test_SignInActivity(){
        super(SignInActivity.class);
    }

    public void testFirst(){
        signInActivity = getActivity();
    }


}