package com.android.virgilsecurity.virgilback4app.model.exception;

import com.virgilsecurity.sdk.crypto.exceptions.VirgilException;

/**
 * Created by Danylo Oliinyk on 11/20/17 at Virgil Security.
 * -__o
 */

public class VirgilCardNotCreatedException extends VirgilException {

    public VirgilCardNotCreatedException() {
        super();
    }

    public VirgilCardNotCreatedException(String message) {
        super(message);
    }
}
