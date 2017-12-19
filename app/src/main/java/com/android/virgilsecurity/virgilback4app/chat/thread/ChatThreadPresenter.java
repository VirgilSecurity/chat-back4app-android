package com.android.virgilsecurity.virgilback4app.chat.thread;

import android.os.Bundle;
import android.util.Pair;

import com.android.virgilsecurity.virgilback4app.AppVirgil;
import com.android.virgilsecurity.virgilback4app.model.ChatThread;
import com.android.virgilsecurity.virgilback4app.util.RxParse;
import com.virgilsecurity.sdk.client.exceptions.VirgilCardIsNotFoundException;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.highlevel.StringEncoding;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilCard;
import com.virgilsecurity.sdk.highlevel.VirgilCards;

import java.util.List;

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
    private String identitySender;
    private String identityRecipient;
    private List<VirgilCard> cards;

    @Override protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        virgilApi = AppVirgil.getInfoHolder().getVirgilApi();

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
                            List<VirgilCard> cards) {
        this.text = text;
        this.thread = thread;
        this.cards = cards;

        start(SEND_MESSAGE);
    }

    void requestGetCards(String identitySender, String identityRecipient) {
        this.identitySender = identitySender;
        this.identityRecipient = identityRecipient;

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
    private String encrypt(String text, List<VirgilCard> cards) {
        try {
            VirgilCards virgilCards = new VirgilCards(AppVirgil.getInfoHolder().getVirgilApiContext());
            virgilCards.addAll(cards);
            return virgilCards.encrypt(text).toString(StringEncoding.Base64);
        } catch (CryptoException e) {
            e.printStackTrace();
            return "";
        }
    }

}
