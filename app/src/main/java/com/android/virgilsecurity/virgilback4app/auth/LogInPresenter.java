package com.android.virgilsecurity.virgilback4app.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.android.virgilsecurity.virgilback4app.model.exception.VirgilCardNotCreatedException;
import com.android.virgilsecurity.virgilback4app.util.PrefsManager;
import com.android.virgilsecurity.virgilback4app.util.RxParse;
import com.android.virgilsecurity.virgilback4app.util.Utils;
import com.parse.ParseUser;
import com.virgilsecurity.sdk.client.exceptions.VirgilCardIsNotFoundException;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsAlreadyExistsException;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsNotFoundException;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.crypto.exceptions.KeyEntryNotFoundException;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilApiContext;
import com.virgilsecurity.sdk.highlevel.VirgilCard;
import com.virgilsecurity.sdk.highlevel.VirgilCards;
import com.virgilsecurity.sdk.highlevel.VirgilKey;
import com.virgilsecurity.sdk.storage.KeyStorage;

import io.reactivex.Observable;
import io.reactivex.Single;
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

    private VirgilApi virgilApi;
    private KeyStorage keyStorage;
    private VirgilApiContext virgilApiContext;
    private VirgilKey privateKey;
    private VirgilCard myVirgilCard;

    private String identity;

    @Override
    protected void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);

        restartableFirst(SIGN_UP, () ->
                                 generatePrivateKey().toObservable()
                                                     .flatMap(privateKey -> {
                                                         return createCard(identity, privateKey).toObservable();
                                                     })
                                                     .flatMap(pair -> {
                                                         privateKey = pair.second;
                                                         if (privateKey == null)
                                                             return Observable.error(VirgilKeyIsNotFoundException::new);

                                                         String password =
                                                                 Utils.generatePassword(privateKey.getPrivateKey()
                                                                                                  .getValue());
                                                         myVirgilCard = pair.first;

                                                         return RxParse.signUp(pair.first.getIdentity(),
                                                                               password,
                                                                               pair.first);
                                                     })
                                                     .flatMap(card -> {
                                                         saveLastGeneratedPrivateKey();
                                                         PrefsManager.UserPreferences.saveCardModel(card.getModel());
                                                         return Observable.just(card);
                                                     })
                                                     .subscribeOn(Schedulers.io())
                                                     .observeOn(AndroidSchedulers.mainThread()),
                         LogInFragment::onSignUpSuccess,
                         LogInFragment::onSignUpError
        );

        restartableFirst(LOG_IN, () ->
                                 getDeviceOnlyVirgilCard(identity)
                                         .flatMap(virgilCard -> {
                                             String password =
                                                     Utils.generatePassword(virgilApi.getKeys()
                                                                                     .load(virgilCard.getIdentity())
                                                                                     .getPrivateKey()
                                                                                     .getValue());

                                             myVirgilCard = virgilCard;
                                             Observable<ParseUser> observableLogIn = RxParse.logIn(virgilCard.getIdentity(),
                                                                                                   password);
                                             Observable<VirgilCard> observableVirgilCard = Observable.just(virgilCard);
//
                                             return Observable.zip(observableLogIn,
                                                                   observableVirgilCard,
                                                                   (user, card) -> {
                                                                       PrefsManager.UserPreferences.saveCardModel(card.getModel());
                                                                       return user;
                                                                   });
                                         })
                                         .subscribeOn(Schedulers.io())
                                         .observeOn(AndroidSchedulers.mainThread()),
                         LogInFragment::onLoginSuccess,
                         LogInFragment::onLoginError
        );
    }

    void requestSignUp(String identity, VirgilApi virgilApi) {
        this.identity = identity;
        this.virgilApi = virgilApi;

        start(SIGN_UP);
    }

    void requestLogIn(String identity,
                      KeyStorage keyStorage,
                      VirgilApi virgilApi,
                      VirgilApiContext virgilApiContext) {
        this.identity = identity;
        this.keyStorage = keyStorage;
        this.virgilApi = virgilApi;
        this.virgilApiContext = virgilApiContext;

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

    private Single<Pair<VirgilCard, VirgilKey>> createCard(String identity, VirgilKey virgilKey) {
        return Single.create(e -> {
            VirgilCard userCard = virgilApi.getCards().create(identity, virgilKey);
            if (userCard != null)
                e.onSuccess(new Pair<>(userCard, virgilKey));
            else
                e.onError(new VirgilCardNotCreatedException());
        });
    }

    private Single<VirgilKey> generatePrivateKey() {
        return Single.create(e -> {
            VirgilKey privateKey = virgilApi.getKeys().generate();

            if (privateKey != null)
                e.onSuccess(privateKey);
            else
                e.onError(new Throwable("Private key is not generated"));
        });
    }

    /**
     * Use after createCard method - last generated private key
     * will be saved in secure storage
     */

    private void saveLastGeneratedPrivateKey() {
        if (privateKey != null) {
            try {
                privateKey.save(myVirgilCard.getIdentity());
            } catch (VirgilKeyIsAlreadyExistsException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * If there is no private key on device - we can not log in.
     *
     * @param identity aka username
     * @return
     */
    private Observable<VirgilCard> getDeviceOnlyVirgilCard(String identity) {
        if (!keyStorage.exists(identity))
            return Observable.error(KeyEntryNotFoundException::new);

        Observable<VirgilCard> cardObservable;
        try {
            privateKey = virgilApi.getKeys().load(identity);
        } catch (VirgilKeyIsNotFoundException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        }

        if (PrefsManager.UserPreferences.getCardModel() != null) {
            cardObservable =
                    Observable.just(new VirgilCard(virgilApiContext,
                                                   PrefsManager.UserPreferences.getCardModel()));
        } else {
            cardObservable = findCard(identity).toObservable()
                                               .subscribeOn(Schedulers.io())
                                               .flatMap(card -> {
                                                   myVirgilCard = card;
                                                   return Observable.just(card);
                                               });
        }

        return cardObservable;
    }

    private Single<VirgilCard> findCard(String identity) {
        return Single.create(e -> {
            VirgilCards cards = virgilApi.getCards().find(identity);
            if (cards.size() > 0) {
                e.onSuccess(cards.get(0));
            } else {
                e.onError(new VirgilCardIsNotFoundException());
            }
        });
    }
}
