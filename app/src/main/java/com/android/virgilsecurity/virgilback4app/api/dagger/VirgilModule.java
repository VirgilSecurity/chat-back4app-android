package com.android.virgilsecurity.virgilback4app.api.dagger;

import android.content.Context;

import com.virgilsecurity.sdk.client.VirgilClient;
import com.virgilsecurity.sdk.highlevel.Credentials;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilApiContext;
import com.virgilsecurity.sdk.highlevel.VirgilApiImpl;
import com.virgilsecurity.sdk.storage.KeyStorage;
import com.virgilsecurity.sdk.storage.VirgilKeyStorage;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Danylo Oliinyk on 11/20/17 at Virgil Security.
 * -__o
 */

@Module
public class VirgilModule {

    @Provides
    VirgilApi provideVirgilApi(VirgilApiContext virgilApiContext) {
        return new VirgilApiImpl(virgilApiContext);
    }

    @Provides
    VirgilClient provideVirgilClient() {
        return new VirgilClient("AT.28f63856f96c3cbbda4e453e977acbe6c4c9f67681823781b811f36145a004dc");
    }

    @Provides
    KeyStorage provideKeyStorage(Context context) {
        return new VirgilKeyStorage(context.getFilesDir().getAbsolutePath());
    }

    @Provides
    VirgilApiContext provideVirgilApiContext(KeyStorage keyStorage, Credentials credentials) {
        VirgilApiContext virgilApiContext =
                new VirgilApiContext("AT.a5c9a1ed088a3a1f22d9ad3bd75214570ed20ffc97ad1a4614f1cfafbb49fe86");
        virgilApiContext.setCredentials(credentials);
        virgilApiContext.setKeyStorage(keyStorage);

        return virgilApiContext;
    }
}
