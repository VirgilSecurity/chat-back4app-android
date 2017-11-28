package com.android.virgilsecurity.virgilback4app.api.dagger;

import android.content.Context;

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
    KeyStorage provideKeyStorage(Context context) {
        return new VirgilKeyStorage(context.getFilesDir().getAbsolutePath());
    }

    @Provides
    VirgilApiContext provideVirgilApiContext(KeyStorage keyStorage) {
        VirgilApiContext virgilApiContext =
                new VirgilApiContext("AT.1b6cac21529a7aa6b4c4ee9881c42706f5c4a382326eb7270150f7b8c9e369e1");
        virgilApiContext.setKeyStorage(keyStorage);

        return virgilApiContext;
    }
}
