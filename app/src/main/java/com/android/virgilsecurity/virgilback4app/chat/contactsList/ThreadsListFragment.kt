package com.android.virgilsecurity.virgilback4app.chat.contactsList

import android.net.NetworkInfo
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.android.virgilsecurity.virgilback4app.AppVirgil
import com.android.virgilsecurity.virgilback4app.R
import com.android.virgilsecurity.virgilback4app.base.BaseFragment
import com.android.virgilsecurity.virgilback4app.model.ChatThread
import com.android.virgilsecurity.virgilback4app.util.Const
import com.android.virgilsecurity.virgilback4app.util.Utils
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.parse.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_contacts.*
import okhttp3.OkHttpClient
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

class ThreadsListFragment : BaseFragment<ThreadsListActivity>() {

    private lateinit var adapter: ThreadsListRVAdapter
    private var page: Int = 0
    private var onStartThreadListener: OnStartThreadListener? = null
    private var isLoading: Boolean = false
    private var parseLiveQueryClient: ParseLiveQueryClient? = null
    private var parseQueryResult: ParseQuery<ChatThread>? = null
    private var networkTracker: Disposable? = null
    private lateinit var presenter: ThreadsListFragmentPresenter

    override val layout: Int
        get() = R.layout.fragment_contacts

    override fun postCreateInit() {
        onStartThreadListener = activity

        adapter = ThreadsListRVAdapter(activity)
        adapter.setClickListener(object : ThreadsListRVAdapter.ClickListener {
            override fun onItemClicked(position: Int, thread: ChatThread) {
                onStartThreadListener!!.onStartThread(thread)
            }
        })

        val layoutManager = LinearLayoutManager(activity)
        rvContacts.layoutManager = layoutManager
        rvContacts.adapter = adapter
        rvContacts.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (adapter.itemCount > 13 &&
                    !isLoading &&
                    totalItemCount <= lastVisibleItem + VISIBLE_THRESHOLD) {

                    page++
                    presenter.requestThreadsPagination(page,
                                                       ::onGetThreadsSuccess,
                                                       ::onGetThreadsError)
                    showProgress(true)
                }
            }
        })

        srlRefresh.setOnRefreshListener {
            tvEmpty.visibility = View.INVISIBLE
            tvError.visibility = View.INVISIBLE
            page = 0
            isLoading = true

            showProgress(true)
            adapter.clearItems()
            presenter.requestThreads(ParseUser.getCurrentUser(),
                                     20, page, Const.TableNames.CREATED_AT_CRITERIA,
                                     ::onGetThreadsSuccess,
                                     ::onGetThreadsError)
        }

        presenter = ThreadsListFragmentPresenter(activity)

        showProgress(true)
        if (AppVirgil.isEthreeInitialized()) {
            presenter.requestThreads(ParseUser.getCurrentUser(),
                                     20,
                                     page,
                                     Const.TableNames.CREATED_AT_CRITERIA,
                                     ::onGetThreadsSuccess,
                                     ::onGetThreadsError)
        } else {
            presenter.requestEthreeInit(::onInitEthreeSuccess, ::onInitEthreeError)
        }
    }

    override fun onPause() {
        super.onPause()

        presenter.disposeAll()
        unsubscribeLiveQuery()
        if (!networkTracker!!.isDisposed)
            networkTracker!!.dispose()
    }

    override fun onResume() {
        super.onResume()

        networkTracker = ReactiveNetwork.observeNetworkConnectivity(activity.applicationContext)
                .debounce(1000, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { connectivity ->
                    if (connectivity.state == NetworkInfo.State.CONNECTED) {
                        unsubscribeLiveQuery()
                        initLiveQuery()
                        subscribeToLiveQuery()
                    }
                }
    }

    private fun initLiveQuery() {
        if (parseLiveQueryClient == null) {
            try {
                parseLiveQueryClient = ParseLiveQueryClient.Factory
                        .getClient(URI(getString(R.string.back4app_live_query_url)),
                                   OkHttp3SocketClientFactory(OkHttpClient()))
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }

            val parseThreadSender = ParseQuery.getQuery<ChatThread>(ChatThread::class.java)
            parseThreadSender.whereEqualTo(Const.TableNames.SENDER_ID,
                                           ParseUser.getCurrentUser().objectId)
            val parseThreadRecipient = ParseQuery.getQuery<ChatThread>(ChatThread::class.java)
            parseThreadRecipient.whereEqualTo(Const.TableNames.RECIPIENT_ID,
                                              ParseUser.getCurrentUser().objectId)
            parseQueryResult = ParseQuery.or(Arrays.asList(parseThreadSender,
                                                           parseThreadRecipient))
        }
    }

    private fun subscribeToLiveQuery() {
        val subscriptionHandling = parseLiveQueryClient!!.subscribe(parseQueryResult)
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE
        ) { query, thread ->
            Utils.log("Log", query.toString() + thread.toString())
            activity.runOnUiThread {
                if (adapter.contains(thread)) {
                    adapter.addItem(0, thread)
                    rvContacts.smoothScrollToPosition(0)

                    if (adapter.itemCount > 0)
                        tvEmpty.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun unsubscribeLiveQuery() {
        if (parseLiveQueryClient != null) {
            parseLiveQueryClient!!.unsubscribe(parseQueryResult)
            parseLiveQueryClient = null
        }
    }

    private fun onInitEthreeSuccess() {
        presenter.requestThreads(ParseUser.getCurrentUser(),
                                 20,
                                 page,
                                 Const.TableNames.CREATED_AT_CRITERIA,
                                 ::onGetThreadsSuccess,
                                 ::onGetThreadsError)
    }

    private fun onInitEthreeError(throwable: Throwable) {
        showProgress(false)
        if (adapter.itemCount == 0)
            tvError.visibility = View.VISIBLE

        Utils.toast(activity, Utils.resolveError(throwable))
    }

    private fun onGetThreadsSuccess(threads: List<ChatThread>) {
        showProgress(false)
        srlRefresh.isRefreshing = false

        if (threads.isNotEmpty()) {
            tvEmpty.visibility = View.INVISIBLE
            if (adapter.itemCount != 0) {
                adapter.addItems(threads)
            } else {
                adapter.setItems(threads)
            }
        } else if (adapter.itemCount == 0) {
            tvEmpty.visibility = View.VISIBLE
        }
    }

    private fun onGetThreadsError(throwable: Throwable) {
        showProgress(false)
        if (adapter.itemCount == 0)
            tvError.visibility = View.VISIBLE

        Utils.toast(activity, Utils.resolveError(throwable))
    }

    private fun showProgress(show: Boolean) {
        pbLoading.visibility = if (show) View.VISIBLE else View.INVISIBLE
        isLoading = show
    }

    interface OnStartThreadListener {
        fun onStartThread(thread: ChatThread)
    }

    companion object {

        private const val VISIBLE_THRESHOLD = 5

        fun newInstance(): ThreadsListFragment {

            val args = Bundle()

            val fragment = ThreadsListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
