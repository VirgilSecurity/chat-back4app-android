package com.android.virgilsecurity.virgilback4app.chat.contactsList;

import android.os.Bundle;

import com.android.virgilsecurity.virgilback4app.base.BaseFragmentWithPresenter;
import com.android.virgilsecurity.virgilback4app.model.User;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

public class FragmentChatThread extends BaseFragmentWithPresenter {

    private static final String KEY_INTERLOCUTOR = "KEY_INTERLOCUTOR";

    private User interlocutor;

    public static FragmentChatThread newInstance(User interlocutor) {

        Bundle args = new Bundle();

        args.putParcelable(KEY_INTERLOCUTOR, interlocutor);

        FragmentChatThread fragment = new FragmentChatThread();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return 0;
    }

    @Override
    protected void postButterInit() {
        interlocutor = getArguments().getParcelable(KEY_INTERLOCUTOR);


    }
}
