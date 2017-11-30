package com.android.virgilsecurity.virgilback4app.util;

import android.content.Context;

import com.android.virgilsecurity.virgilback4app.R;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsNotFoundException;
import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.device.DefaultDeviceManager;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilApiContext;
import com.virgilsecurity.sdk.highlevel.VirgilCard;
import com.virgilsecurity.sdk.securechat.SecureChat;
import com.virgilsecurity.sdk.securechat.SecureChatContext;
import com.virgilsecurity.sdk.securechat.impl.DefaultUserDataStorage;
import com.virgilsecurity.sdk.securechat.keystorage.JsonFileKeyStorage;
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

    private Context context;
    private Crypto crypto;
    private KeyStorage keyStorage;
    private VirgilApi virgilApi;
    private VirgilApiContext virgilApiContext;
    private SecureChat secureChat;
    private PrivateKey privateKey;
    private VirgilCardValidator validator;
    private RxVirgil rxVirgil;

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

    public Observable<Object> initializePfs(VirgilCard card, PrivateKey privateKey) {
        return Observable.create(e -> {
            SecureChatContext chatContext =
                    new SecureChatContext(card.getModel(),
                                          privateKey,
                                          crypto,
                                          context.getString(R.string.virgil_token));
            chatContext.setKeyStorage(new JsonFileKeyStorage(context.getFilesDir().getAbsolutePath(),
                                                             card.getIdentity()));
            chatContext.setDeviceManager(new DefaultDeviceManager());
            chatContext.setUserDataStorage(new DefaultUserDataStorage());
            secureChat = new SecureChat(chatContext);

//            try {
//                secureChat.initialize();
//            } catch (MigrationException exception) {
//                e.onError(exception);
//            }

            secureChat.rotateKeys(5); // TODO: 11/28/17 increase number

            e.onNext(new Object());
            e.onComplete();
        });
    }

    public Observable<VirgilCard> logIn(String identity) {
        if (!keyStorage.exists(identity))
            return Observable.error(VirgilKeyIsNotFoundException::new);

        Observable<VirgilCard> cardObservable;
        KeyEntry keyEntry = keyStorage.load(identity);
        try {
            privateKey = crypto.importPrivateKey(keyEntry.getValue());
        } catch (CryptoException exception) {
            return Observable.error(exception);
        }

        if (PrefsManager.VirgilPreferences.getCardModel() != null) {
            cardObservable =
                    Observable.just(new VirgilCard(virgilApiContext,
                                                   PrefsManager.VirgilPreferences.getCardModel()));
        } else {
            cardObservable = rxVirgil.findCard(identity).subscribeOn(Schedulers.io());
        }

        return cardObservable;
    }

    public Observable<VirgilCard> createCard(String identity) {
        return rxVirgil.createCard(identity).toObservable();
    }

    public PrivateKey loadPrivateKey(String identity) {
        if (privateKey == null) {
            KeyEntry keyEntry = keyStorage.load(identity);
            try {
                return crypto.importPrivateKey(keyEntry.getValue());
            } catch (CryptoException exception) {
                return null;
            }
        } else {
            return privateKey;
        }
    }

    public String encrypt(String text) {
        return text;
    }
}
