package com.android.virgilsecurity.virgilback4app.chat.thread;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.virgilsecurity.virgilback4app.AppVirgil;
import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.base.BaseFragmentWithPresenter;
import com.android.virgilsecurity.virgilback4app.model.ChatThread;
import com.android.virgilsecurity.virgilback4app.model.Message;
import com.android.virgilsecurity.virgilback4app.util.Const;
import com.android.virgilsecurity.virgilback4app.util.PrefsManager;
import com.android.virgilsecurity.virgilback4app.util.Utils;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.parse.OkHttp3SocketClientFactory;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.SubscriptionHandling;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilApiContext;
import com.virgilsecurity.sdk.highlevel.VirgilCard;
import com.virgilsecurity.sdk.highlevel.VirgilCards;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import nucleus5.factory.RequiresPresenter;
import okhttp3.OkHttpClient;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

@RequiresPresenter(ChatThreadPresenter.class)
public class ChatThreadFragment extends BaseFragmentWithPresenter<ChatThreadActivity, ChatThreadPresenter> {

    private static final String KEY_THREAD = "KEY_THREAD";
    private static final int VISIBLE_THRESHOLD = 5;

    private ChatThread thread;
    private VirgilCard meCard;
    private VirgilCard youCard;
    private ChatThreadRVAdapter adapter;
    private List<Message> messages;
    private int page;
    private boolean isLoading;
    private Disposable networkTracker;
    private ParseLiveQueryClient parseLiveQueryClient;
    private ParseQuery<Message> parseQuery;

    @Inject protected VirgilApi virgilApi;
    @Inject protected VirgilApiContext virgilApiContext;

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
        AppVirgil.getVirgilComponent().inject(this);

        btnSend.setEnabled(false);
        btnSend.setBackground(ContextCompat.getDrawable(activity,
                                                        R.drawable.bg_btn_chat_send_pressed));

        srlRefresh.setOnRefreshListener(() -> {
            if (messages == null || messages.size() == 0) {
                tvEmpty.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.INVISIBLE);
                page = 0;
                isLoading = true;
                getMessages();
            }
            srlRefresh.setRefreshing(false);
        });

        initMessageInput();

        adapter = new ChatThreadRVAdapter(virgilApi, virgilApiContext);
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

                if ((messages != null && messages.size() > 40) && (!isLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD))) {
                    page++;
                    showProgress(true);
                    getPresenter().requestMessagesPagination(page);
                }
            }
        });
    }

    private void getMessages() {
        if (meCard == null || youCard == null) {
            initCards();
        } else if (messages == null || messages.size() == 0) {
            showProgress(true);
            getPresenter().requestMessages(thread, 50, page,
                                           Const.TableNames.CREATED_AT_CRITERIA,
                                           virgilApi, virgilApiContext);
        }
    }

    private void initCards() {
        showProgress(true);

        meCard = new VirgilCard(virgilApiContext, PrefsManager.UserPreferences.getCardModel());

        if (thread.getSenderUsername().equals(meCard.getIdentity()))
            getPresenter().requestGetCard(thread.getRecipientUsername(), virgilApi);
        else
            getPresenter().requestGetCard(thread.getSenderUsername(), virgilApi);
    }

    private void initMessageInput() {
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
    }

    private void initLiveQuery() {
        if (parseLiveQueryClient == null) {
            try {
                parseLiveQueryClient = ParseLiveQueryClient.Factory
                        .getClient(new URI(getString(R.string.back4app_live_query_url)),
                                   new OkHttp3SocketClientFactory(new OkHttpClient()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            parseQuery = ParseQuery.getQuery(Message.class);
            parseQuery.whereEqualTo(Const.TableNames.THREAD_ID, thread.getObjectId());
        }
    }

    private void subscribeToLiveQuery() {
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE,
                                         (query, message) -> {
                                             Utils.log("SubscriptionHandling", message.getBody());
                                             activity.runOnUiThread(() -> {
                                                 adapter.addItem(0, message);
                                                 rvChat.smoothScrollToPosition(0);

                                                 if (adapter.getItemCount() > 0)
                                                     tvEmpty.setVisibility(View.INVISIBLE);
                                             });
                                         });
    }

    private void unsubscribeLiveQuery() {
        if (parseLiveQueryClient != null) {
            parseLiveQueryClient.unsubscribe(parseQuery);
            parseLiveQueryClient = null;
        }
    }

    @OnClick({R.id.btnSend}) void onInterfaceClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                String message = etMessage.getText().toString().trim();
                if (meCard != null && youCard != null) {
                    if (!message.isEmpty()) {
                        VirgilCards cards = new VirgilCards(virgilApiContext);
                        cards.add(meCard);
                        cards.add(youCard);
                        lockSendUi(true, true);
                        getPresenter().requestSendMessage(message,
                                                          thread,
                                                          cards,
                                                          virgilApi,
                                                          virgilApiContext);
                        isLoading = true;
                    }
                } else {
                    if (!isLoading) {
                        isLoading = true;
                        showProgress(true);
                        getMessages();
                        lockSendUi(true, false);
                    }
                }
                break;
        }
    }

    void lockSendUi(boolean lock, boolean lockInput) {
        if (lock) {
            btnSend.setEnabled(false);
            btnSend.setBackground(ContextCompat.getDrawable(activity,
                                                            R.drawable.bg_btn_chat_send_pressed));
            if (lockInput) {
                etMessage.setEnabled(false);
                InputMethodManager inputManager =
                        (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

                if (inputManager != null)
                    inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                                 InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        } else {
            btnSend.setEnabled(true);
            btnSend.setBackground(ContextCompat.getDrawable(activity,
                                                            R.drawable.bg_btn_chat_send));
            if (lockInput)
                etMessage.setEnabled(true);
        }
    }

    public void onGetMessagesSuccess(List<Message> messages) {
        showProgress(false);
        srlRefresh.setRefreshing(false);
        lockSendUi(false, false);

        if (messages.size() != 0) {
            tvEmpty.setVisibility(View.INVISIBLE);
            if (this.messages != null && this.messages.size() > 0) {
                this.messages.addAll(messages);
                adapter.addItems(messages);
            } else {
                this.messages = new ArrayList<>(messages);
                adapter.setItems(messages);
            }
        } else if (adapter.getItemCount() == -1) {
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    public void onGetMessagesError(Throwable t) {
        showProgress(false);
        srlRefresh.setRefreshing(false);
        lockSendUi(false, false);

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

    @Override public void onPause() {
        super.onPause();

        InputMethodManager inputManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null)
            inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                                         InputMethodManager.HIDE_NOT_ALWAYS);

        hideKeyboard();

        getPresenter().disposeAll();
        unsubscribeLiveQuery();
        if (!networkTracker.isDisposed())
            networkTracker.dispose();
    }

    @Override public void onResume() {
        super.onResume();

        if (getPresenter().isDisposed()) {
            showProgress(false);
            isLoading = false;
            lockSendUi(true, false);
            lockSendUi(false, false);
            srlRefresh.setRefreshing(false);
        }

        getMessages();

        networkTracker = ReactiveNetwork.observeNetworkConnectivity(activity.getApplicationContext())
                                        .debounce(1000, TimeUnit.MILLISECONDS)
                                        .distinctUntilChanged()
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe((connectivity) -> {
                                            if (connectivity.getState() == NetworkInfo.State.CONNECTED) {
                                                unsubscribeLiveQuery();
                                                initLiveQuery();
                                                subscribeToLiveQuery();
                                            }
                                        });
    }

    public void onGetCardSuccess(VirgilCard virgilCard) {
        youCard = virgilCard;

        adapter.setCards(meCard, youCard);

        if (messages == null) {
            showProgress(false);
            getPresenter().requestMessages(thread, 50, page,
                                           Const.TableNames.CREATED_AT_CRITERIA,
                                           virgilApi, virgilApiContext);
        } else {
            showProgress(false);
            srlRefresh.setRefreshing(false);
            lockSendUi(false, false);
        }
    }

    public void onGetCardError(Throwable t) {
        showProgress(false);
        srlRefresh.setRefreshing(false);
        lockSendUi(false, false);

        Utils.toast(this, Utils.resolveError(t));
    }

    private void showProgress(boolean show) {
        pbLoading.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        isLoading = show;
    }
}
