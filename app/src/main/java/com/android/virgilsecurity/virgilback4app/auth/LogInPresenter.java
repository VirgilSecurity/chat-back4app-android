package com.android.virgilsecurity.virgilback4app.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.virgilsecurity.virgilback4app.util.PrefsManager;
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
                                             .flatMap(pair -> {
                                                 PrivateKey privateKey = pair.second.getPrivateKey();
                                                 if (privateKey == null)
                                                     return Observable.error(VirgilKeyIsNotFoundException::new);

                                                 String password =
                                                         Utils.generatePassword(privateKey.getValue());

                                                 return RxParse.signUp(pair.first.getIdentity(),
                                                                       password,
                                                                       pair.first);
                                             })
                                             .flatMap(card -> {
                                                 virgilHelper.saveLastGeneratedPrivateKey();
                                                 PrefsManager.VirgilPreferences.saveCardModel(card.getModel());
                                                 return Observable.just(card);
                                             })
                                             .subscribeOn(Schedulers.io())
                                             .observeOn(AndroidSchedulers.mainThread()),
                         LogInFragment::onSignUpSuccess,
                         LogInFragment::onSignUpError
        );

        restartableFirst(LOG_IN, () ->
                                 virgilHelper.getDeviceOnlyVirgilCard(identity)
                                             .flatMap(virgilCard -> {
                                                 String password =
                                                         Utils.generatePassword(virgilHelper.loadPrivateKey(virgilCard.getIdentity())
                                                                                            .getValue());

                                                 Observable<ParseUser> observableLogIn = RxParse.logIn(virgilCard.getIdentity(),
                                                                                                       password);
                                                 Observable<VirgilCard> observableVirgilCard = Observable.just(virgilCard);
//
                                                 return Observable.zip(observableLogIn,
                                                                       observableVirgilCard,
                                                                       (user, card) -> {
                                                                           PrefsManager.VirgilPreferences.saveCardModel(card.getModel());
                                                                           return user;
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

    boolean isDisposed() {
        return isDisposed(SIGN_UP)
                || isDisposed(LOG_IN);
    }
}
