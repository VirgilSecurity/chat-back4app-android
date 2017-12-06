package com.android.virgilsecurity.virgilback4app.chat.contactsList;

import android.os.Bundle;

import com.android.virgilsecurity.virgilback4app.util.RxParse;
import com.parse.ParseUser;

import nucleus5.presenter.RxPresenter;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

public class ThreadsListFragmentPresenter extends RxPresenter<ThreadsListFragment> {

    private static final int GET_THREADS = 0;
    private static final int GET_USERS_BY_IDS = 1;

    private ParseUser currentUser;
    private int limit;
    private int page;
    private String sortCriteria;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableFirst(GET_THREADS, () ->
                                 RxParse.getMyThreads(currentUser, limit, page, sortCriteria),
                         ThreadsListFragment::onGetThreadsSuccess,
                         ThreadsListFragment::onGetThreadsError
        );
    }

    void requestThreads(ParseUser currentUser, int limit, int page, String sortCriteria) {
        this.currentUser = currentUser;
        this.limit = limit;
        this.page = page;
        this.sortCriteria = sortCriteria;

        start(GET_THREADS);
    }

    void requestThreadsPagination(int page) {
        this.page = page;

        start(GET_THREADS);
    }

    void disposeAll() {
        stop(GET_THREADS);
        stop(GET_USERS_BY_IDS);
    }

    boolean isDisposed() {
        return isDisposed(GET_THREADS)
                || isDisposed(GET_USERS_BY_IDS);
    }
}
