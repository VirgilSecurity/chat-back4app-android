package com.android.virgilsecurity.virgilback4app.api.dagger;

import com.android.virgilsecurity.virgilback4app.auth.LogInPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Danylo Oliinyk on 11/17/17 at Virgil Security.
 * -__o
 */

@Singleton
@Component(modules = NetworkModule.class)
public interface NetworkComponent {

    void inject(LogInPresenter logInPresenter);
}
