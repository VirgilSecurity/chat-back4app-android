package com.android.virgilsecurity.virgilback4app.api.dagger;

import com.android.virgilsecurity.virgilback4app.AppVirgil;
import com.android.virgilsecurity.virgilback4app.auth.LogInFragment;
import com.android.virgilsecurity.virgilback4app.chat.contactsList.ThreadsListFragment;
import com.android.virgilsecurity.virgilback4app.chat.thread.ChatThreadFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Danylo Oliinyk on 11/20/17 at Virgil Security.
 * -__o
 */

@Component(modules = {VirgilModule.class, AppModule.class})
@Singleton
public interface VirgilComponent {

    void inject(AppVirgil appVirgil);

    void inject(LogInFragment logInFragment);

    void inject(ThreadsListFragment threadsListFragment);

    void inject(ChatThreadFragment chatThreadFragment );
}
