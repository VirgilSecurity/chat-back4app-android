package com.android.virgilsecurity.virgilback4app.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.android.virgilsecurity.virgilback4app.base.BaseActivity;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

public class ChatControlActivity extends BaseActivity {

    public static void start(AppCompatActivity from) {
        from.startActivity(new Intent(from, ChatControlActivity.class));
    }

    @Override
    protected int getLayout() {
        return 0;
    }

    @Override
    protected void postButterInit() {

    }
}
