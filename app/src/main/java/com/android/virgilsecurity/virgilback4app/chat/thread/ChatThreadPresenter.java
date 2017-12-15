package com.android.virgilsecurity.virgilback4app.chat.thread;

import android.os.Bundle;
import android.util.Pair;

import com.android.virgilsecurity.virgilback4app.model.ChatThread;
import com.android.virgilsecurity.virgilback4app.util.RxParse;
import com.parse.ParseUser;
import com.virgilsecurity.sdk.client.exceptions.VirgilCardIsNotFoundException;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsNotFoundException;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.highlevel.StringEncoding;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilApiContext;
import com.virgilsecurity.sdk.highlevel.VirgilCard;
import com.virgilsecurity.sdk.highlevel.VirgilCards;
import com.virgilsecurity.sdk.highlevel.VirgilKey;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nucleus5.presenter.RxPresenter;

/**
 * Created by Danylo Oliinyk on 11/27/17 at Virgil Security.
 * -__o
 */

public class ChatThreadPresenter extends RxPresenter<ChatThreadFragment> {

    private static final int GET_MESSAGES = 0;
    private static final int SEND_MESSAGE = 1;
    private static final int GET_CARDS = 2;

    private ChatThread thread;
    private int limit;
    private int page;
    private String sortCriteria;
    private String text;
    private VirgilApi virgilApi;
    private VirgilApiContext virgilApiContext;
    private String identitySender;
    private String identityRecipient;
    private VirgilCards cards;

    @Override protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableFirst(GET_MESSAGES, () ->
                                 RxParse.getMessages(thread, limit, page, sortCriteria),
                         ChatThreadFragment::onGetMessagesSuccess,
                         ChatThreadFragment::onGetMessagesError);

        restartableFirst(SEND_MESSAGE, () ->
                                 RxParse.sendMessage(encrypt(text, cards),
                                                     thread)
                                        .observeOn(AndroidSchedulers.mainThread()),
                         ChatThreadFragment::onSendMessageSuccess,
                         ChatThreadFragment::onSendMessageError);

        restartableFirst(GET_CARDS, () ->
                                 Observable.zip(findCard(identitySender).toObservable()
                                                                  .subscribeOn(Schedulers.io()),
                                                findCard(identityRecipient).toObservable()
                                                                  .subscribeOn(Schedulers.io()),
                                                Pair::new)

                                                   .observeOn(AndroidSchedulers.mainThread()),
                         ChatThreadFragment::onGetCardSuccess,
                         ChatThreadFragment::onGetCardError);
    }

    void requestMessages(ChatThread thread, int limit,
                         int page, String sortCriteria) {
        this.thread = thread;
        this.limit = limit;
        this.page = page;
        this.sortCriteria = sortCriteria;

        start(GET_MESSAGES);
    }

    void requestMessagesPagination(int page) {
        this.page = page;

        start(GET_MESSAGES);
    }

    void requestSendMessage(String text,
                            ChatThread thread,
                            VirgilCards cards,
                            VirgilApi virgilApi,
                            VirgilApiContext virgilApiContext) {
        this.text = text;
        this.thread = thread;
        this.cards = cards;
        this.virgilApi = virgilApi;
        this.virgilApiContext = virgilApiContext;

        start(SEND_MESSAGE);
    }

    void requestGetCards(String identitySender, String identityRecipient, VirgilApi virgilApi) {
        this.identitySender = identitySender;
        this.identityRecipient = identityRecipient;
        this.virgilApi = virgilApi;

        start(GET_CARDS);
    }

    void disposeAll() {
        stop(GET_MESSAGES);
        stop(SEND_MESSAGE);
        stop(GET_CARDS);
    }

    boolean isDisposed() {
        return isDisposed(GET_MESSAGES)
                || isDisposed(SEND_MESSAGE)
                || isDisposed(GET_CARDS);
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

    /**
     * Encrypt data
     *
     * @param text  to decrypt
     * @param cards of recipients
     * @return encrypted data
     */

    private String encrypt(String text, VirgilCards cards) {
        String encryptedText = null;

        try {
            VirgilKey key = virgilApi.getKeys().load(ParseUser.getCurrentUser().getUsername());
            encryptedText = key.signThenEncrypt(text, cards).toString(StringEncoding.Base64);
        } catch (VirgilKeyIsNotFoundException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        }

        return encryptedText;
    }

}
