package com.android.virgilsecurity.virgilback4app.chat.contactsList;

import android.os.Bundle;

import com.android.virgilsecurity.virgilback4app.util.RxParse;

import nucleus5.presenter.RxPresenter;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

public class FragmentContactsPresenter extends RxPresenter<ContactsFragment> {

    private static final int GET_USERS = 0;

    private int limit;
    private long skipStep;
    private int page;
    private String sortCriteria;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableFirst(GET_USERS, () ->
                                 RxParse.getUsers(limit, page, sortCriteria),
                         ContactsFragment::onGetContactsSuccess,
                         ContactsFragment::onGetContactsError
        );
    }

    void requestUsers(int limit, int page, String sortCriteria) {
        this.limit = limit;
        this.skipStep = skipStep;
        this.page = page;
        this.sortCriteria = sortCriteria;

        start(GET_USERS);
    }

    void requestUsersPagination(int page) {
        this.page = page;

        start(GET_USERS);
    }
}
