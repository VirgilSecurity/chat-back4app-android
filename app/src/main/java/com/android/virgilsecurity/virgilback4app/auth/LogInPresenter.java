package com.android.virgilsecurity.virgilback4app.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.android.virgilsecurity.virgilback4app.AppVirgil;
import com.android.virgilsecurity.virgilback4app.util.InfoHolder;
import com.android.virgilsecurity.virgilback4app.util.RxParse;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsAlreadyExistsException;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsNotFoundException;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.crypto.exceptions.KeyEntryNotFoundException;
import com.virgilsecurity.sdk.highlevel.VirgilCard;
import com.virgilsecurity.sdk.highlevel.VirgilKey;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    private InfoHolder infoHolder;

    private VirgilCard virgilCard;
    private VirgilKey virgilKey;

    private String identity;
    private String password;

    @Override
    protected void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);

        infoHolder = AppVirgil.getInfoHolder();

        restartableFirst(SIGN_UP, () ->
                                 RxParse.signUp(identity,
                                                password,
                                                virgilCard)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread()),
                         LogInFragment::onSignUpSuccess,
                         LogInFragment::onSignUpError
        );

        restartableFirst(LOG_IN, () ->
                                 RxParse.logIn(identity,
                                               password)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread()),
                         LogInFragment::onLoginSuccess,
                         LogInFragment::onLoginError
        );
    }

    void requestSignUp(String identity) {
        this.identity = identity;

        generateKeyPair(identity);
        password = generatePassword(virgilKey.getPrivateKey().getValue());

        start(SIGN_UP);
    }

    void requestLogIn(String identity) {
        this.identity = identity;

        try {
            virgilKey = infoHolder.getVirgilApi().getKeys().load(identity);
        } catch (KeyEntryNotFoundException e) {
            getView().onLoginError(e);
            return;
        } catch (VirgilKeyIsNotFoundException e) {
            getView().onLoginError(e);
            return;
        } catch (CryptoException e) {
            getView().onLoginError(e);
            return;
        }
        password = generatePassword(virgilKey.getPrivateKey().getValue());

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

    /**
     * Generates SHA-256 password from base[]
     *
     * @param privateKey byte[] representation of private key
     * @return
     */
    private static String generatePassword(byte[] privateKey) {
        MessageDigest sha;
        byte[] hash = new byte[0];

        try {
            sha = MessageDigest.getInstance("SHA-256");
            hash = sha.digest(privateKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(hash, Base64.DEFAULT);
    }

    private void generateKeyPair(String identity) {
        virgilKey = infoHolder.getVirgilApi().getKeys().generate();
        try {
            virgilKey.save(identity);
        } catch (VirgilKeyIsAlreadyExistsException e) {
            e.printStackTrace();
        }
        virgilCard = infoHolder.getVirgilApi().getCards().create(identity, virgilKey);
    }
}
