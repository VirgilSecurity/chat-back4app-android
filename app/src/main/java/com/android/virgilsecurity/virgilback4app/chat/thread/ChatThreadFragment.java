package com.android.virgilsecurity.virgilback4app.chat.thread;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.base.BaseFragmentWithPresenter;
import com.android.virgilsecurity.virgilback4app.model.ChatThread;
import com.android.virgilsecurity.virgilback4app.model.Message;
import com.android.virgilsecurity.virgilback4app.util.Const;
import com.android.virgilsecurity.virgilback4app.util.Utils;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import nucleus5.factory.RequiresPresenter;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

@RequiresPresenter(ChatThreadPresenter.class)
public class ChatThreadFragment extends BaseFragmentWithPresenter<ChatThreadActivity, ChatThreadPresenter> {

    private static final String KEY_THREAD = "KEY_THREAD";
    private static final int VISIBLE_THRESHOLD = 5;

    private ChatThread thread;
    private ChatThreadRVAdapter adapter;
    private List<Message> messages;
    private int page;
    private boolean isLoading;

    @BindView(R.id.rvChat) RecyclerView rvChat;
    @BindView(R.id.etMessage) EditText etMessage;
    @BindView(R.id.btnSend) ImageButton btnSend;
    @BindView(R.id.tvEmpty) View tvEmpty;
    @BindView(R.id.tvError) View tvError;
    @BindView(R.id.pbLoading) View pbLoading;
    @BindView(R.id.srlRefresh) SwipeRefreshLayout srlRefresh;

    public static ChatThreadFragment newInstance(ChatThread thread) {

        Bundle args = new Bundle();

        args.putParcelable(KEY_THREAD, thread);

        ChatThreadFragment fragment = new ChatThreadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_chat_thread;
    }

    @Override
    protected void postButterInit() {
        thread = getArguments().getParcelable(KEY_THREAD);

        btnSend.setEnabled(false);
        btnSend.setBackground(ContextCompat.getDrawable(activity,
                                                        R.drawable.bg_btn_chat_send_pressed));

        if (messages == null) {
            pbLoading.setVisibility(View.VISIBLE);
            isLoading = true;
            getPresenter().requestMessages(thread, 50, page,
                                           Const.TableNames.CREATED_AT_CRITERIA);
        }

        srlRefresh.setOnRefreshListener(() -> {
            tvEmpty.setVisibility(View.INVISIBLE);
            tvError.setVisibility(View.INVISIBLE);
            page = 0;
            pbLoading.setVisibility(View.VISIBLE);
            isLoading = true;
            getPresenter().requestMessages(thread, 50, page,
                                           Const.TableNames.CREATED_AT_CRITERIA);
        });

        initLiveQuery();

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lockSendUi(charSequence.toString().isEmpty(), false);
            }

            @Override public void afterTextChanged(Editable editable) {

            }
        });

        adapter = new ChatThreadRVAdapter(activity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(adapter);
        rvChat.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if ((messages != null && messages.size() > 50) && (!isLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD))) {
                    page++;
                    getPresenter().requestMessagesPagination(page);
                    isLoading = true;
                }
            }
        });
    }

    private void initLiveQuery() {
        ParseLiveQueryClient parseLiveQueryClient = null;
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory
                    .getClient(new URI(getString(R.string.back4app_live_query_url)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
        parseQuery.whereEqualTo(Const.TableNames.THREAD_ID, thread.getObjectId());
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE,
                                         (query, message) -> {
                                             Utils.log("SubscriptionHandling", message.getBody());
                                             activity.runOnUiThread(() -> {
                                                 adapter.addItem(0, message);
                                                 rvChat.smoothScrollToPosition(0);
                                             });
                                         });
    }

    @OnClick({R.id.btnSend}) void onInterfaceClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                lockSendUi(true, true);
                getPresenter().requestSendMessage(etMessage.getText().toString(), thread);
                isLoading = true;
                break;
        }
    }

    void lockSendUi(boolean lock, boolean lockInput) {
        if (lock) {
            btnSend.setEnabled(false);
            btnSend.setBackground(ContextCompat.getDrawable(activity,
                                                            R.drawable.bg_btn_chat_send_pressed));
            if (lockInput)
                etMessage.setEnabled(false);
        } else {
            btnSend.setEnabled(true);
            btnSend.setBackground(ContextCompat.getDrawable(activity,
                                                            R.drawable.bg_btn_chat_send));
            if (lockInput)
                etMessage.setEnabled(true);
        }
    }

    public void onGetMessagesSuccess(List<Message> messages) {
        pbLoading.setVisibility(View.INVISIBLE);
        isLoading = false;
        srlRefresh.setRefreshing(false);

        if (messages.size() != 0) {
            this.messages = new ArrayList<>(messages);
            adapter.setItems(messages);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    public void onGetMessagesError(Throwable t) {
        pbLoading.setVisibility(View.INVISIBLE);
        isLoading = false;
        srlRefresh.setRefreshing(false);

        if (messages == null || messages.size() == 0)
            tvError.setVisibility(View.VISIBLE);

        Utils.toast(this, Utils.resolveError(t));
    }


    public void onSendMessageSuccess(Object o) {
        etMessage.setText("");
        lockSendUi(false, true);
        lockSendUi(true, false);
        isLoading = false;
    }

    public void onSendMessageError(Throwable t) {
        etMessage.setText("");
        lockSendUi(false, true);
        lockSendUi(true, false);
        isLoading = false;
        Utils.toast(this, Utils.resolveError(t));
    }
}
