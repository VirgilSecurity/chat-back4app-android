package com.android.virgilsecurity.virgilback4app.chat.thread;

import android.os.Bundle;

import com.android.virgilsecurity.virgilback4app.model.ChatThread;
import com.android.virgilsecurity.virgilback4app.util.RxParse;
import com.android.virgilsecurity.virgilback4app.util.RxVirgil;

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

    private ChatThread thread;
    private int limit;
    private int page;
    private String sortCriteria;
    private String text;

    @Override protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableFirst(GET_MESSAGES, () ->
                                 RxParse.getMessages(thread, limit, page, sortCriteria),
                         ChatThreadFragment::onGetMessagesSuccess,
                         ChatThreadFragment::onGetMessagesError);

        restartableFirst(SEND_MESSAGE, () ->
                                 RxVirgil.encodeMessage(text)
                                         .subscribeOn(Schedulers.io())
                                         .flatMap(s -> {
                                             return RxParse.sendMessage(s, thread);})
                                         .observeOn(AndroidSchedulers.mainThread()),
                         ChatThreadFragment::onSendMessageSuccess,
                         ChatThreadFragment::onSendMessageError);
    }

    void requestMessages(ChatThread thread, int limit, int page, String sortCriteria) {
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

    void requestSendMessage(String text, ChatThread thread) {
        this.text = text;
        this.thread = thread;

        start(SEND_MESSAGE);
    }
}
