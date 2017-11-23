package com.android.virgilsecurity.virgilback4app.api.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.virgilsecurity.virgilback4app.util.PrefsManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Danylo Oliinyk on 11/21/17 at Virgil Security.
 * -__o
 */

@Module
public class AppModule {

    private Context context;

    public AppModule(@NonNull Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    PrefsManager providePrefsManager(Context context) {
        return new PrefsManager(context);
    }
}
