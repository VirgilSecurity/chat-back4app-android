package com.android.virgilsecurity.virgilback4app.api.dagger;

import android.content.Context;

import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.util.VirgilHelper;
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

    @Provides VirgilApi provideVirgilApi(VirgilApiContext virgilApiContext) {
        return new VirgilApiImpl(virgilApiContext);
    }

    @Provides KeyStorage provideKeyStorage(Context context) {
        return new VirgilKeyStorage(context.getFilesDir().getAbsolutePath());
    }

    @Provides VirgilApiContext provideVirgilApiContext(KeyStorage keyStorage, Context context) {
        VirgilApiContext virgilApiContext =
                new VirgilApiContext(context.getString(R.string.virgil_token));
        virgilApiContext.setKeyStorage(keyStorage);

        return virgilApiContext;
    }

    @Provides VirgilHelper provideVirgilHelper(Context context, VirgilApi virgilApi,
                                               KeyStorage keyStorage,
                                               VirgilApiContext virgilApiContext) {
        return new VirgilHelper(context, virgilApi, keyStorage, virgilApiContext);
    }
}
