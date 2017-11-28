package com.android.virgilsecurity.virgilback4app.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.virgilsecurity.virgilback4app.util.RxParse;

import nucleus5.presenter.RxPresenter;

/**
 * Created by Danylo Oliinyk on 11/17/17 at Virgil Security.
 * -__o
 */

public class LogInPresenter extends RxPresenter<LogInFragment> {

    private static final int REGISTER = 0;
    private static final int LOG_IN = 1;

    private String username;
    private String password;
    private String csr;


    @Override
    protected void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);

        restartableFirst(REGISTER, () ->
                                 RxParse.register(username, password, csr),
                         LogInFragment::onRegisterSuccess,
                         LogInFragment::onRegisterError
                );

        restartableFirst(LOG_IN, () ->
                                 RxParse.logIn(username, password),
                         LogInFragment::onLoginSuccess,
                         LogInFragment::onLoginError
                );
    }

    void requestRegister(String username, String password, String csr) {
        this.username = username;
        this.password = password;
        this.csr = csr;

        start(REGISTER);
    }

    void requestLogIn(String username, String password) {
        this.username = username;
        this.password = password;

        start(LOG_IN);
    }

    void disposeAll() {
        stop(REGISTER);
        stop(LOG_IN);
    }
}
