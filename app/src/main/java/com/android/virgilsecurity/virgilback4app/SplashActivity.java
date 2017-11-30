package com.android.virgilsecurity.virgilback4app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.virgilsecurity.virgilback4app.auth.SignInControlActivity;
import com.android.virgilsecurity.virgilback4app.chat.contactsList.ThreadsListActivity;
import com.parse.ParseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            ThreadsListActivity.startWithFinish(this);
        } else {
            SignInControlActivity.startWithFinish(this);
        }
    }

    @Override public void onBackPressed() {

    }
}
