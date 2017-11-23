package com.android.virgilsecurity.virgilback4app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.virgilsecurity.virgilback4app.auth.SignInControlActivity;
import com.android.virgilsecurity.virgilback4app.chat.ChatControlActivity;
import com.parse.ParseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            ChatControlActivity.start(this);
        } else {
            SignInControlActivity.startWithFinish(this);
        }
    }
}
