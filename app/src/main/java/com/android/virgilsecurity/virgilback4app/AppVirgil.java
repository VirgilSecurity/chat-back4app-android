package com.android.virgilsecurity.virgilback4app;

import android.app.Application;

import com.android.virgilsecurity.virgilback4app.api.dagger.AppModule;
import com.android.virgilsecurity.virgilback4app.api.dagger.DaggerVirgilComponent;
import com.android.virgilsecurity.virgilback4app.api.dagger.VirgilComponent;
import com.android.virgilsecurity.virgilback4app.api.dagger.VirgilModule;
import com.android.virgilsecurity.virgilback4app.model.ChatThread;
import com.android.virgilsecurity.virgilback4app.model.Message;
import com.android.virgilsecurity.virgilback4app.util.PrefsManager;
import com.parse.Parse;
import com.parse.ParseObject;

import javax.inject.Inject;

/**
 * Created by Danylo Oliinyk on 16.11.17 at Virgil Security.
 * -__o
 */

public class AppVirgil extends Application {

    private static VirgilComponent virgilComponent;

    @Inject protected PrefsManager prefsManager;

    @Override
    public void onCreate() {
        super.onCreate();

        virgilComponent = buildVirgilComponent();
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(ChatThread.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                                 .applicationId(getString(R.string.back4app_app_id))
                                 .clientKey(getString(R.string.back4app_client_key))
                                 .server(getString(R.string.back4app_server_url))
                                 .build());

        virgilComponent.inject(this);
    }

    public static VirgilComponent getVirgilComponent() {
        return virgilComponent;
    }

    protected VirgilComponent buildVirgilComponent() {
        return DaggerVirgilComponent.builder()
                                    .appModule(new AppModule(this))
                                    .virgilModule(new VirgilModule())
                                    .build();
    }
}
