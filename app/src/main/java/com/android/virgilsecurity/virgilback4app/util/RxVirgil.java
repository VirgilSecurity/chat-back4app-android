package com.android.virgilsecurity.virgilback4app.util;

import com.android.virgilsecurity.virgilback4app.model.exception.VirgilCardNotCreatedException;
import com.virgilsecurity.sdk.client.exceptions.VirgilCardIsNotFoundException;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsAlreadyExistsException;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilCard;
import com.virgilsecurity.sdk.highlevel.VirgilCards;
import com.virgilsecurity.sdk.highlevel.VirgilKey;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Danylo Oliinyk on 11/27/17 at Virgil Security.
 * -__o
 */

public class RxVirgil {

    private VirgilApi virgilApi;

    public RxVirgil(VirgilApi virgilApi) {
        this.virgilApi = virgilApi;
    }

    public Observable<String> encodeMessage(String text) {
        return Observable.just(text); // TODO: 11/27/17 just a stub
    }

    public Single<VirgilCard> createCard(String identity) {
        return Single.create(e -> {
            VirgilKey userKey = virgilApi.getKeys().generate();

            try {
                userKey.save(identity);
            } catch (VirgilKeyIsAlreadyExistsException exception) {
                e.onError(exception);
            }

            VirgilCard userCard = virgilApi.getCards().create(identity, userKey);
            if (userCard == null)
                e.onError(new VirgilCardNotCreatedException());

            PrefsManager.VirgilPreferences.saveCardModel(userCard.getModel());

            e.onSuccess(userCard);
        });
    }

    public Observable<VirgilCard> findCard(String identity) {
        return Observable.create(e -> {
            VirgilCards cards = virgilApi.getCards().find(identity);
            if (cards.size() > 0) {
                e.onNext(cards.get(0));
                e.onComplete();
            } else {
                e.onError(new VirgilCardIsNotFoundException());
            }
    });
}
}
