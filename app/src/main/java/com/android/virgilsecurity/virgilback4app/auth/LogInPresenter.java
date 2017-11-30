package com.android.virgilsecurity.virgilback4app.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.virgilsecurity.virgilback4app.util.RxParse;
import com.android.virgilsecurity.virgilback4app.util.Utils;
import com.android.virgilsecurity.virgilback4app.util.VirgilHelper;
import com.parse.ParseUser;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsNotFoundException;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.highlevel.VirgilCard;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nucleus5.presenter.RxPresenter;

/**
 * Created by Danylo Oliinyk on 11/17/17 at Virgil Security.
 * -__o
 */

public class LogInPresenter extends RxPresenter<LogInFragment> {

    private static final int SIGN_UP = 0;
    private static final int LOG_IN = 1;

    private VirgilHelper virgilHelper;
    private String identity;


    @Override
    protected void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);

        restartableFirst(SIGN_UP, () ->
                                 virgilHelper.createCard(identity)
                                             .flatMap(virgilCard -> {
                                                 PrivateKey privateKey = virgilHelper.loadPrivateKey(virgilCard.getIdentity());
                                                 if (privateKey == null)
                                                     return Observable.error(VirgilKeyIsNotFoundException::new);

                                                 String password =
                                                         Utils.generatePassword(virgilHelper.loadPrivateKey(virgilCard.getIdentity())
                                                                                            .getValue());

                                                 return RxParse.signUp(virgilCard.getIdentity(),
                                                                       password,
                                                                       virgilCard);
                                             })
                                             .flatMap(virgilCard -> {
                                                 PrivateKey privateKey = virgilHelper.loadPrivateKey(virgilCard.getIdentity());
                                                 if (privateKey == null)
                                                     return Observable.error(VirgilKeyIsNotFoundException::new);

                                                 return virgilHelper.initializePfs(virgilCard, privateKey)
                                                                    .subscribeOn(Schedulers.io());
                                             })
                                             .subscribeOn(Schedulers.io())
                                             .observeOn(AndroidSchedulers.mainThread()),
                         LogInFragment::onSignUpSuccess,
                         LogInFragment::onSignUpError
        );

        restartableFirst(LOG_IN, () ->
                                 virgilHelper.logIn(identity)
                                             .flatMap(virgilCard -> {
                                                 String password =
                                                         Utils.generatePassword(virgilHelper.loadPrivateKey(virgilCard.getIdentity())
                                                                                            .getValue());
                                                 Observable<ParseUser> parseUserObservable =
                                                         RxParse.logIn(virgilCard.getIdentity(),
                                                                       password);

                                                 Observable<VirgilCard> virgilCardObservable =
                                                         Observable.just(virgilCard);

                                                 return Observable.zip(parseUserObservable,
                                                                       virgilCardObservable, (user, card) -> {
                                                             PrivateKey privateKey = virgilHelper.loadPrivateKey(virgilCard.getIdentity());
                                                             if (privateKey == null)
                                                                 return Observable.error(VirgilKeyIsNotFoundException::new);
                                                             return virgilHelper.initializePfs(card, privateKey)
                                                                                .subscribeOn(Schedulers.io());
                                                         });
                                             })
                                             .subscribeOn(Schedulers.io())
                                             .observeOn(AndroidSchedulers.mainThread()),
                         LogInFragment::onLoginSuccess,
                         LogInFragment::onLoginError
        );
    }

    void requestSignUp(String identity, VirgilHelper virgilHelper) {
        this.identity = identity;
        this.virgilHelper = virgilHelper;

        start(SIGN_UP);
    }

    void requestLogIn(String identity, VirgilHelper virgilHelper) {
        this.identity = identity;
        this.virgilHelper = virgilHelper;

        start(LOG_IN);
    }

    void disposeAll() {
        stop(SIGN_UP);
        stop(LOG_IN);
    }
}
