package com.android.virgilsecurity.virgilback4app.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.virgilsecurity.virgilback4app.api.rest.RestApi;

import javax.inject.Inject;

import nucleus5.presenter.RxPresenter;

/**
 * Created by Danylo Oliinyk on 11/17/17 at Virgil Security.
 * -__o
 */

public class LogInPresenter extends RxPresenter<LogInFragment> {

    private static final int HANDSHAKE = 0;

    @Inject
    protected RestApi restApi;

    @Override
    protected void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);

//        restartableFirst(HANDSHAKE, () ->
//                );
    }

    public void requestHandshake() {

        start(HANDSHAKE);
    }
}
