package com.android.virgilsecurity.virgilback4app.util;

import android.content.Context;

import com.android.virgilsecurity.virgilback4app.R;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsNotFoundException;
import com.virgilsecurity.sdk.client.model.CardModel;
import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.highlevel.StringEncoding;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilApiContext;
import com.virgilsecurity.sdk.highlevel.VirgilCard;
import com.virgilsecurity.sdk.highlevel.VirgilCards;
import com.virgilsecurity.sdk.highlevel.VirgilKey;
import com.virgilsecurity.sdk.securechat.SecureChat;
import com.virgilsecurity.sdk.storage.KeyEntry;
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
    private SecureChat secureChat;
    private VirgilCardValidator validator;
    private RxVirgil rxVirgil;

    private VirgilCard card;

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
            return Observable.error(VirgilKeyIsNotFoundException::new);

        Observable<VirgilCard> cardObservable;

        if (PrefsManager.VirgilPreferences.getCardModel() != null) {
            cardObservable =
                    Observable.just(new VirgilCard(virgilApiContext,
                                                   PrefsManager.VirgilPreferences.getCardModel()));
        } else {
            cardObservable = findCard(identity).subscribeOn(Schedulers.io());
        }

        return cardObservable;
    }

    public Observable<VirgilCard> findCard(String identity) {
        return rxVirgil.findCard(identity).toObservable();
    }

    public Observable<VirgilCard> createCard(String identity) {
        return rxVirgil.createCard(identity).toObservable();
    }

    public PrivateKey loadPrivateKey(String identity) throws CryptoException {
            KeyEntry keyEntry = keyStorage.load(identity);
            return crypto.importPrivateKey(keyEntry.getValue());
    }

    public void removePrivateKey(String identity) {
            keyStorage.delete(identity);
    }

    public VirgilKey loadKey(String identity) throws VirgilKeyIsNotFoundException, CryptoException {
            return virgilApi.getKeys().load(identity);
    }

    /**
     * Encrypt data
     *
     * @param text to decrypt
     * @param cards of recipients
     *
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
     *
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
}
