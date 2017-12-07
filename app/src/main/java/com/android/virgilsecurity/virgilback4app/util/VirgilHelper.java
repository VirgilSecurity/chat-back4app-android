package com.android.virgilsecurity.virgilback4app.util;

import android.content.Context;
import android.util.Pair;

import com.android.virgilsecurity.virgilback4app.R;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsAlreadyExistsException;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsNotFoundException;
import com.virgilsecurity.sdk.client.model.CardModel;
import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.crypto.exceptions.KeyEntryNotFoundException;
import com.virgilsecurity.sdk.highlevel.StringEncoding;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilApiContext;
import com.virgilsecurity.sdk.highlevel.VirgilCard;
import com.virgilsecurity.sdk.highlevel.VirgilCards;
import com.virgilsecurity.sdk.highlevel.VirgilKey;
import com.virgilsecurity.sdk.storage.KeyStorage;
import com.virgilsecurity.sdk.utils.ConvertionUtils;
import com.virgilsecurity.sdk.utils.VirgilCardValidator;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Danylo Oliinyk on 11/28/17 at Virgil Security.
 * -__o
 */

public class VirgilHelper {

    private Crypto crypto;
    private KeyStorage keyStorage;
    private VirgilApi virgilApi;
    private Context context;
    private VirgilApiContext virgilApiContext;
    private VirgilCardValidator validator;
    private RxVirgil rxVirgil;

    private VirgilKey privateKey;
    private VirgilCard myVirgilCard;

    @Inject
    public VirgilHelper(Context context, VirgilApi virgilApi,
                        KeyStorage keyStorage, VirgilApiContext virgilApiContext) {
        this.context = context;
        this.virgilApi = virgilApi;
        this.keyStorage = keyStorage;
        this.virgilApiContext = virgilApiContext;

        crypto = new VirgilCrypto();
        validator = new VirgilCardValidator(crypto);
        validator.addVerifier(context.getString(R.string.virgil_app_id),
                              ConvertionUtils.base64ToBytes(context.getString(R.string.virgil_app_public_key)));
        rxVirgil = new RxVirgil(virgilApi);
    }

    /**
     * If there is no private key on device - we can not log in.
     *
     * @param identity aka username
     * @return
     */
    public Observable<VirgilCard> getDeviceOnlyVirgilCard(String identity) {
        if (!keyStorage.exists(identity))
            return Observable.error(KeyEntryNotFoundException::new);

        Observable<VirgilCard> cardObservable;
        try {
            privateKey = loadKey(identity);
        } catch (VirgilKeyIsNotFoundException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        }

        if (PrefsManager.VirgilPreferences.getCardModel() != null) {
            cardObservable =
                    Observable.just(new VirgilCard(virgilApiContext,
                                                   PrefsManager.VirgilPreferences.getCardModel()));
        } else {
            cardObservable = findCard(identity).subscribeOn(Schedulers.io())
                                               .flatMap(card -> {
                                                   myVirgilCard = card;
                                                   return Observable.just(card);
                                               });
        }

        return cardObservable;
    }

    public Observable<VirgilCard> findCard(String identity) {
        return rxVirgil.findCard(identity).toObservable();
    }

    public Observable<Pair<VirgilCard, VirgilKey>> createCard(String identity) {
        if (!keyStorage.exists(identity)) {
            return rxVirgil.createCard(identity)
                           .toObservable()
                           .flatMap(pair -> {
                               myVirgilCard = pair.first;
                               privateKey = pair.second;
                               return Observable.just(pair);
                           });
        } else {
            return Observable.error(VirgilKeyIsAlreadyExistsException::new);
        }
    }

    public PrivateKey loadPrivateKey(String identity) throws VirgilKeyIsNotFoundException, CryptoException {
        return virgilApi.getKeys().load(identity).getPrivateKey();
    }

    public void removePrivateKey(String identity) {
        virgilApi.getKeys().destroy(identity);
    }

    public VirgilKey loadKey(String identity) throws VirgilKeyIsNotFoundException, CryptoException {
        return virgilApi.getKeys().load(identity);
    }

    /**
     * Use after createCard method - last generated private key
     * will be saved in secure storage
     */
    public void saveLastGeneratedPrivateKey() {
        if (privateKey != null) {
            try {
                privateKey.save(myVirgilCard.getIdentity());
            } catch (VirgilKeyIsAlreadyExistsException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Encrypt data
     *
     * @param text  to decrypt
     * @param cards of recipients
     * @return encrypted data
     */
    public String encrypt(String text, VirgilCards cards) {
        String encryptedText = null;

        try {
            VirgilKey key = loadKey(getMyCard().getIdentity());
            encryptedText = key.signThenEncrypt(text, cards).toString(StringEncoding.Base64);
        } catch (VirgilKeyIsNotFoundException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        }

        return encryptedText;
    }

    /**
     * Decrypt data
     *
     * @param text to encrypt
     * @param card senders card to verify data
     * @return decrypted data
     */
    public String decrypt(String text, VirgilCard card) {
        String decryptedText = null;

        try {
            VirgilKey virgilKey = loadKey(getMyCard().getIdentity());
            decryptedText = virgilKey.decryptThenVerify(text, card).toString();
        } catch (VirgilKeyIsNotFoundException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        }

        return decryptedText;
    }

    private VirgilCard getMyCard() {
        CardModel cardModel = PrefsManager.VirgilPreferences.getCardModel();
        return new VirgilCard(virgilApiContext, cardModel);
    }

    public void clearAfterLogout() {
        myVirgilCard = null;
        privateKey = null;
    }
}
