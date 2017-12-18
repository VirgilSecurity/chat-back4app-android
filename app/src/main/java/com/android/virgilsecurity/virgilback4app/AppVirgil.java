package com.android.virgilsecurity.virgilback4app;

import android.app.Application;

import com.android.virgilsecurity.virgilback4app.model.ChatThread;
import com.android.virgilsecurity.virgilback4app.model.Message;
import com.android.virgilsecurity.virgilback4app.util.InfoHolder;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Danylo Oliinyk on 16.11.17 at Virgil Security.
 * -__o
 */

public class AppVirgil extends Application {

    private static InfoHolder infoHolder;

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(ChatThread.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                                 .applicationId(getString(R.string.back4app_app_id))
                                 .clientKey(getString(R.string.back4app_client_key))
                                 .server(getString(R.string.back4app_server_url))
                                 .build());

        infoHolder = new InfoHolder(this);

    }

    public static InfoHolder getInfoHolder() {
        if (infoHolder != null)
            return infoHolder;
        else
            throw new RuntimeException("Init InfoHolder in Application class first");
    }
}
