package com.android.virgilsecurity.virgilback4app.util;

import io.reactivex.Observable;

/**
 * Created by Danylo Oliinyk on 11/27/17 at Virgil Security.
 * -__o
 */

public class RxVirgil {

    public static Observable<String> encodeMessage(String text) {
        return Observable.just(text); // TODO: 11/27/17 just a stub
    }
}
