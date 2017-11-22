package com.android.virgilsecurity.virgilback4app.chat.contactsList;

import com.android.virgilsecurity.virgilback4app.base.BaseFragmentWithPresenter;
import com.android.virgilsecurity.virgilback4app.chat.ChatControlActivity;
import com.android.virgilsecurity.virgilback4app.model.User;

import java.util.ArrayList;
import java.util.List;

import nucleus5.factory.RequiresPresenter;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

@RequiresPresenter(FragmentContactsPresenter.class)
public class FragmentContacts extends BaseFragmentWithPresenter<ChatControlActivity, FragmentContactsPresenter> {

    private ContactsRVAdapter adapter;
    private List<User> users;

    @Override
    protected int getLayout() {
        return 0;
    }

    @Override
    protected void postButterInit() {
        adapter = new ContactsRVAdapter(activity);
    }

    public void onGetContactsSuccess(List<User> users) {
        this.users = new ArrayList<>(users);
        adapter.setItems(users);
        adapter.setClickListener((position, user) -> {
            FragmentChatThread.newInstance(user);
        });
    }

    public void onGetContactsError(Throwable throwable) {

    }
}
