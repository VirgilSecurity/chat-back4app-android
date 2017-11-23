package com.android.virgilsecurity.virgilback4app.chat.contactsList;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.base.BaseFragmentWithPresenter;
import com.android.virgilsecurity.virgilback4app.chat.ChatControlActivity;
import com.android.virgilsecurity.virgilback4app.chat.thread.ChatThreadFragment;
import com.android.virgilsecurity.virgilback4app.util.Utils;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import nucleus5.factory.RequiresPresenter;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

@RequiresPresenter(FragmentContactsPresenter.class)
public class ContactsFragment extends BaseFragmentWithPresenter<ChatControlActivity, FragmentContactsPresenter> {

    private final int THRESHOLD = 5;

    private ContactsRVAdapter adapter;
    private List<ParseUser> users;
    private int page;

    @BindView(R.id.rvContacts)
    protected RecyclerView rvContacts;

    @Override
    protected int getLayout() {
        return R.layout.fragment_contacts;
    }

    @Override
    protected void postButterInit() {
        setRetainInstance(true);

        adapter = new ContactsRVAdapter(activity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        rvContacts.setLayoutManager(layoutManager);
        rvContacts.setAdapter(adapter);
        rvContacts.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (layoutManager.getItemCount() - layoutManager.findLastVisibleItemPosition() <= THRESHOLD) {
                    page++;
                    getPresenter().requestUsersPagination(page);
                }

            }
        });

        if (users != null && users.size() != 0)
            getPresenter().requestUsers(20, page, "createdAt");
    }

    public void onGetContactsSuccess(List<ParseUser> users) {
        this.users = new ArrayList<>(users);
        adapter.setItems(users);
        adapter.setClickListener((position, user) -> {
            ChatThreadFragment.newInstance(ParseUser.getCurrentUser(), user);
        });
    }

    public void onGetContactsError(Throwable throwable) {
        Utils.toast(activity, Utils.resolveError(throwable));
    }
}
