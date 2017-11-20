package com.android.virgilsecurity.virgilback4app.model.exception;

/**
 * Created by Danylo Oliinyk on 11/20/17 at Virgil Security.
 * -__o
 */

public class ApiNotInitializedException extends RuntimeException {

    public ApiNotInitializedException() {
        super();
    }

    public ApiNotInitializedException(String message) {
        super(message);
    }
}
