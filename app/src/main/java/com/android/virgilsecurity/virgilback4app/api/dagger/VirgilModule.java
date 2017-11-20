package com.android.virgilsecurity.virgilback4app.api.dagger;

import com.virgilsecurity.sdk.client.VirgilClient;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilApiImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Danylo Oliinyk on 11/20/17 at Virgil Security.
 * -__o
 */

@Module
public class VirgilModule {

    @Provides
    VirgilApi getVirgilApi() {
        return new VirgilApiImpl("AT.28f63856f96c3cbbda4e453e977acbe6c4c9f67681823781b811f36145a004dc");
    }

    @Provides
    VirgilClient getVirgilClient() {
        return new VirgilClient("AT.28f63856f96c3cbbda4e453e977acbe6c4c9f67681823781b811f36145a004dc");
    }
}
