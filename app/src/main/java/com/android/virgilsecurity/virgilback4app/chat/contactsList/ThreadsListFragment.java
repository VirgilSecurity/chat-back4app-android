package com.android.virgilsecurity.virgilback4app.chat.contactsList;

import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.base.BaseFragmentWithPresenter;
import com.android.virgilsecurity.virgilback4app.model.ChatThread;
import com.android.virgilsecurity.virgilback4app.util.Const;
import com.android.virgilsecurity.virgilback4app.util.Utils;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.parse.OkHttp3SocketClientFactory;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import nucleus5.factory.RequiresPresenter;
import okhttp3.OkHttpClient;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

@RequiresPresenter(ThreadsListFragmentPresenter.class)
public class ThreadsListFragment extends BaseFragmentWithPresenter<ThreadsListActivity, ThreadsListFragmentPresenter> {

    private static final int VISIBLE_THRESHOLD = 5;

    private ThreadsListRVAdapter adapter;
    private List<ChatThread> threads;
    private int page;
    private OnStartThreadListener onStartThreadListener;
    private boolean isLoading;
    private ParseLiveQueryClient parseLiveQueryClient;
    private ParseQuery<ChatThread> parseQueryResult;
    private Disposable networkTracker;

    @BindView(R.id.rvContacts)
    protected RecyclerView rvContacts;
    @BindView(R.id.tvEmpty)
    protected View tvEmpty;
    @BindView(R.id.tvError)
    protected View tvError;
    @BindView(R.id.pbLoading)
    protected View pbLoading;
    @BindView(R.id.srlRefresh)
    protected SwipeRefreshLayout srlRefresh;

    public static ThreadsListFragment newInstance() {

        Bundle args = new Bundle();

        ThreadsListFragment fragment = new ThreadsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_contacts;
    }

    @Override
    protected void postButterInit() {
        onStartThreadListener = activity;

        adapter = new ThreadsListRVAdapter(activity);
        adapter.setClickListener((position, user) -> {
            onStartThreadListener.onStartThread(user);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        rvContacts.setLayoutManager(layoutManager);
        rvContacts.setAdapter(adapter);
        rvContacts.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if ((threads != null && threads.size() > 13) && (!isLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD))) {
                    page++;
                    getPresenter().requestThreadsPagination(page);
                    showProgress(true);
                }
            }
        });

        srlRefresh.setOnRefreshListener(() -> {
            if (!isLoading) {
                tvEmpty.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.INVISIBLE);
                page = 0;
                if (threads != null)
                    threads.clear();

                getPresenter().requestThreads(ParseUser.getCurrentUser(),
                                              20, page, Const.TableNames.CREATED_AT_CRITERIA);
                isLoading = true;
            } else {
                srlRefresh.setRefreshing(false);
            }
        });
    }

    @Override public void onPause() {
        super.onPause();

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
            srlRefresh.setRefreshing(false);
        }

        if (threads == null || threads.isEmpty()) {
            getPresenter().requestThreads(ParseUser.getCurrentUser(),
                                          20, page, Const.TableNames.CREATED_AT_CRITERIA);
            showProgress(true);
        }

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

    private void initLiveQuery() {
        if (parseLiveQueryClient == null) {
            try {
                parseLiveQueryClient = ParseLiveQueryClient.Factory
                        .getClient(new URI(getString(R.string.back4app_live_query_url)),
                                   new OkHttp3SocketClientFactory(new OkHttpClient()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            ParseQuery<ChatThread> parseThreadSender = ParseQuery.getQuery(ChatThread.class);
            parseThreadSender.whereEqualTo(Const.TableNames.SENDER_ID,
                                           ParseUser.getCurrentUser().getObjectId());
            ParseQuery<ChatThread> parseThreadRecipient = ParseQuery.getQuery(ChatThread.class);
            parseThreadRecipient.whereEqualTo(Const.TableNames.RECIPIENT_ID,
                                              ParseUser.getCurrentUser().getObjectId());
            parseQueryResult = ParseQuery.or(Arrays.asList(parseThreadSender,
                                                           parseThreadRecipient));
        }
    }

    private void subscribeToLiveQuery() {
        SubscriptionHandling<ChatThread> subscriptionHandling = parseLiveQueryClient.subscribe(parseQueryResult);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE,
                                         (query, thread) -> {
                                             Utils.log("Log", query.toString() + thread.toString());
                                             activity.runOnUiThread(() -> {
                                                 if (threads == null || !threads.contains(thread)) {
                                                     if (threads == null)
                                                         threads = new ArrayList<>();

                                                     threads.add(0, thread);
                                                     adapter.addItem(0, thread);
                                                     rvContacts.smoothScrollToPosition(0);

                                                     if (adapter.getItemCount() > 0)
                                                         tvEmpty.setVisibility(View.INVISIBLE);
                                                 }
                                             });
                                         });
    }

    private void unsubscribeLiveQuery() {
        if (parseLiveQueryClient != null) {
            parseLiveQueryClient.unsubscribe(parseQueryResult);
            parseLiveQueryClient = null;
        }
    }

    public void onGetThreadsSuccess(@NonNull List<ChatThread> threads) {
        showProgress(false);
        srlRefresh.setRefreshing(false);

        if (threads.size() != 0) {
            tvEmpty.setVisibility(View.INVISIBLE);
            if (this.threads != null && this.threads.size() > 0) {
                this.threads.addAll(threads);
                adapter.addItems(threads);
            } else {
                this.threads = new ArrayList<>(threads);
                adapter.setItems(threads);
            }
        } else if (adapter.getItemCount() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    public void onGetThreadsError(Throwable throwable) {
        showProgress(false);
        if (threads == null || threads.size() == 0)
            tvError.setVisibility(View.VISIBLE);

        Utils.toast(activity, Utils.resolveError(throwable));
    }

    private void showProgress(boolean show) {
        pbLoading.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        isLoading = show;
    }

    interface OnStartThreadListener {
        void onStartThread(ChatThread thread);
    }
}
