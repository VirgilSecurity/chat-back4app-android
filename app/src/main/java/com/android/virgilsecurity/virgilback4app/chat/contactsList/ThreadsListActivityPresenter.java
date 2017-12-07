package com.android.virgilsecurity.virgilback4app.chat.contactsList;

import android.os.Bundle;

import com.android.virgilsecurity.virgilback4app.util.RxParse;
import com.parse.ParseUser;

import nucleus5.presenter.RxPresenter;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

public class ThreadsListActivityPresenter extends RxPresenter<ThreadsListActivity> {

    private static final int GET_USER = 0;
    private static final int GET_THREADS = 1;
    private static final int CREATE_THREAD = 2;

    private String username;
    private ParseUser currentUser;
    private ParseUser interlocutorUser;
    private int limit;
    private int page;
    private String sortCriteria;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableFirst(GET_USER, () ->
                                 RxParse.getUserByName(username),
                         ThreadsListActivity::onGetUserSuccess,
                         ThreadsListActivity::onGetUserError
        );

        restartableFirst(GET_THREADS, () ->
                                 RxParse.getMyThreads(currentUser, limit, page, sortCriteria),
                         ThreadsListActivity::onGetThreadsSuccess,
                         ThreadsListActivity::onGetThreadsError
        );

        restartableFirst(CREATE_THREAD, () ->
                                 RxParse.createThread(currentUser, interlocutorUser),
                         ThreadsListActivity::onCreateThreadSuccess,
                         ThreadsListActivity::onCreateThreadError
        );
    }

    void requestUser(String username) {
        this.username = username;

        start(GET_USER);
    }

    void requestThreads(ParseUser currentUser, int limit, int page, String sortCriteria) {
        this.currentUser = currentUser;
        this.limit = limit;
        this.page = page;
        this.sortCriteria = sortCriteria;

        start(GET_THREADS);
    }

    void requestCreateThread(ParseUser currentUser, ParseUser interlocutorUser) {
        this.currentUser = currentUser;
        this.interlocutorUser = interlocutorUser;

        start(CREATE_THREAD);
    }

    void disposeAll() {
        stop(GET_USER);
        stop(GET_THREADS);
        stop(CREATE_THREAD);
    }
}
