package com.android.virgilsecurity.virgilback4app.chat.thread;

import android.os.Bundle;

import com.android.virgilsecurity.virgilback4app.model.ChatThread;
import com.android.virgilsecurity.virgilback4app.util.RxParse;
import com.android.virgilsecurity.virgilback4app.util.VirgilHelper;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilCards;

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
    private static final int GET_CARD = 2;

    private ChatThread thread;
    private int limit;
    private int page;
    private String sortCriteria;
    private String text;
    private VirgilApi virgilApi;
    private VirgilHelper virgilHelper;
    private String identity;
    private VirgilCards cards;

    @Override protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableFirst(GET_MESSAGES, () ->
                                 RxParse.getMessages(thread, limit, page, sortCriteria),
                         ChatThreadFragment::onGetMessagesSuccess,
                         ChatThreadFragment::onGetMessagesError);

        restartableFirst(SEND_MESSAGE, () ->
                                 RxParse.sendMessage(virgilHelper.encrypt(text, cards),
                                                     thread)
                                        .observeOn(AndroidSchedulers.mainThread()),
                         ChatThreadFragment::onSendMessageSuccess,
                         ChatThreadFragment::onSendMessageError);

        restartableFirst(GET_CARD, () ->
        virgilHelper.findCard(identity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()),
                         ChatThreadFragment::onGetCardSuccess,
                         ChatThreadFragment::onGetCardError);
    }

    void requestMessages(ChatThread thread, int limit,
                         int page, String sortCriteria,
                         VirgilApi virgilApi, VirgilHelper virgilHelper) {
        this.thread = thread;
        this.limit = limit;
        this.page = page;
        this.sortCriteria = sortCriteria;
        this.virgilApi = virgilApi;
        this.virgilHelper = virgilHelper;

        start(GET_MESSAGES);
    }

    void requestMessagesPagination(int page) {
        this.page = page;

        start(GET_MESSAGES);
    }

    void requestSendMessage(String text, ChatThread thread, VirgilCards cards) {
        this.text = text;
        this.thread = thread;
        this.cards = cards;

        start(SEND_MESSAGE);
    }

    void requestGetCard(String identity, VirgilHelper virgilHelper) {
        this.identity = identity;
        this.virgilHelper = virgilHelper;

        start(GET_CARD);
    }

    void disposeAll() {
        stop(GET_MESSAGES);
        stop(SEND_MESSAGE);
        stop(GET_CARD);
    }

    boolean isDisposed() {
        return isDisposed(GET_MESSAGES)
                || isDisposed(SEND_MESSAGE)
                || isDisposed(GET_CARD);
    }
}
