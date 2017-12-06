package com.android.virgilsecurity.virgilback4app.util;

import com.android.virgilsecurity.virgilback4app.model.exception.VirgilCardNotCreatedException;
import com.virgilsecurity.sdk.client.exceptions.VirgilCardIsNotFoundException;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsAlreadyExistsException;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilCard;
import com.virgilsecurity.sdk.highlevel.VirgilCards;
import com.virgilsecurity.sdk.highlevel.VirgilKey;

import io.reactivex.Single;

/**
 * Created by Danylo Oliinyk on 11/27/17 at Virgil Security.
 * -__o
 */

public class RxVirgil {

    private VirgilApi virgilApi;

    RxVirgil(VirgilApi virgilApi) {
        this.virgilApi = virgilApi;
    }

    Single<VirgilCard> createCard(String identity) {
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

    Single<VirgilCard> findCard(String identity) {
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
