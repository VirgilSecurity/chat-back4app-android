package com.android.virgilsecurity.virgilback4app.util;

import com.virgilsecurity.sdk.client.exceptions.VirgilCardIsNotFoundException;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilCard;
import com.virgilsecurity.sdk.highlevel.VirgilCards;

import io.reactivex.Single;

/**
 * Created by Danylo Oliinyk on 11/27/17 at Virgil Security.
 * -__o
 */

public class RxVirgil {

    private VirgilApi virgilApi;

    RxVirgil(VirgilApi virgilApi) {
        this.virgilApi = virgilApi;
    }




}
