package com.android.virgilsecurity.virgilback4app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.virgilsecurity.virgilback4app.auth.SignInControlActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SignInControlActivity.startWithFinish(this);
    }
}
