package com.android.virgilsecurity.virgilback4app.api.dagger;

import com.android.virgilsecurity.virgilback4app.auth.LogInFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Danylo Oliinyk on 11/20/17 at Virgil Security.
 * -__o
 */

@Component(modules = VirgilModule.class)
@Singleton
public interface VirgilComponent {

    void inject(LogInFragment logInFragment);
}
