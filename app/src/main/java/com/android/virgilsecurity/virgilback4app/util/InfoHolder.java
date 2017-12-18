package com.android.virgilsecurity.virgilback4app.util;

import android.content.Context;

import com.android.virgilsecurity.virgilback4app.R;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilApiContext;
import com.virgilsecurity.sdk.highlevel.VirgilApiImpl;
import com.virgilsecurity.sdk.storage.KeyStorage;
import com.virgilsecurity.sdk.storage.VirgilKeyStorage;

/**
 * Created by Danylo Oliinyk on 12/15/17 at Virgil Security.
 * -__o
 */

public class InfoHolder {
    private String identity;

    private VirgilApi virgilApi;
    private VirgilApiContext virgilApiContext;
    private KeyStorage keyStorage;

    public InfoHolder(Context context) {
        keyStorage = new VirgilKeyStorage(context.getFilesDir().getAbsolutePath());

        virgilApiContext = new VirgilApiContext(context.getString(R.string.virgil_token));
        virgilApiContext.setKeyStorage(keyStorage);

        virgilApi = new VirgilApiImpl(virgilApiContext);
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    public VirgilApi getVirgilApi() {
        return virgilApi;
    }

    public VirgilApiContext getVirgilApiContext() {
        return virgilApiContext;
    }

    public KeyStorage getKeyStorage() {
        return keyStorage;
    }
}