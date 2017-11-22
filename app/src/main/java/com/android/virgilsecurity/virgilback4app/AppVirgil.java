package com.android.virgilsecurity.virgilback4app;

import android.app.Application;

import com.android.virgilsecurity.virgilback4app.api.dagger.AppModule;
import com.android.virgilsecurity.virgilback4app.api.dagger.DaggerVirgilComponent;
import com.android.virgilsecurity.virgilback4app.api.dagger.VirgilComponent;
import com.android.virgilsecurity.virgilback4app.api.dagger.VirgilModule;


/**
 * Created by danylooliinyk on 16.11.17.
 */

public class AppVirgil extends Application {

    private static VirgilComponent virgilComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        virgilComponent = buildVirgilComponent();
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
