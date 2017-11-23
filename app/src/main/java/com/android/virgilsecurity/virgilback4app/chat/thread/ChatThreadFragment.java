package com.android.virgilsecurity.virgilback4app.chat.thread;

import android.os.Bundle;

import com.android.virgilsecurity.virgilback4app.base.BaseFragmentWithPresenter;
import com.parse.ParseUser;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

public class ChatThreadFragment extends BaseFragmentWithPresenter {

    private static final String KEY_OWNER = "KEY_OWNER";
    private static final String KEY_INTERLOCUTOR = "KEY_INTERLOCUTOR";

    private ParseUser owner;
    private ParseUser interlocutor;

    public static ChatThreadFragment newInstance(ParseUser owner, ParseUser interlocutor) {

        Bundle args = new Bundle();

        args.putParcelable(KEY_OWNER, owner);
        args.putParcelable(KEY_INTERLOCUTOR, interlocutor);

        ChatThreadFragment fragment = new ChatThreadFragment();
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
        owner = getArguments().getParcelable(KEY_OWNER);

    }
}
