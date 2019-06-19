package com.android.virgilsecurity.virgilback4app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.android.virgilsecurity.virgilback4app.auth.SignInControlActivity
import com.android.virgilsecurity.virgilback4app.chat.contactsList.ThreadsListActivity
import com.parse.ParseUser

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ParseUser.getCurrentUser() != null) {
            ThreadsListActivity.startWithFinish(this)
        } else {
            SignInControlActivity.startWithFinish(this)
        }
    }

    override fun onBackPressed() {
        // Must be empty, so we can't press back from the splash screen
    }
}
