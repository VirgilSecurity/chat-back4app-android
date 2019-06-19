package com.android.virgilsecurity.virgilback4app.chat.thread

import android.content.Context
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.android.virgilsecurity.virgilback4app.R
import com.android.virgilsecurity.virgilback4app.base.BaseFragment
import com.android.virgilsecurity.virgilback4app.model.ChatThread
import com.android.virgilsecurity.virgilback4app.model.Message
import com.android.virgilsecurity.virgilback4app.util.Const
import com.android.virgilsecurity.virgilback4app.util.Utils
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.parse.*
import com.virgilsecurity.sdk.client.exceptions.VirgilCardIsNotFoundException
import com.virgilsecurity.sdk.client.exceptions.VirgilCardServiceException
import com.virgilsecurity.sdk.crypto.PublicKey
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_chat_thread.*
import java.net.URI
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

class ChatThreadFragment : BaseFragment<ChatThreadActivity>() {

    private var page: Int = 0
    private var isLoading: Boolean = false
    private var parseLiveQueryClient: ParseLiveQueryClient? = null
    private var parseQuery: ParseQuery<Message>? = null

    private lateinit var thread: ChatThread
    private lateinit var networkTracker: Disposable
    private lateinit var adapter: ChatThreadRVAdapter
    private lateinit var presenter: ChatThreadPresenter

    override val layout: Int
        get() = R.layout.fragment_chat_thread

    override fun postCreateInit() {
        thread = arguments?.getParcelable(KEY_THREAD)!!

        btnSend.isEnabled = false
        btnSend.background = ContextCompat.getDrawable(activity,
                                                       R.drawable.bg_btn_chat_send_pressed)

        srlRefresh.setOnRefreshListener {
            tvEmpty.visibility = View.INVISIBLE
            tvError.visibility = View.INVISIBLE
            page = 0
            isLoading = true

            showProgress(true)
            adapter.clearItems()
            presenter.requestMessages(thread, 50, page,
                                      Const.TableNames.CREATED_AT_CRITERIA,
                                      ::onGetMessagesSuccess,
                                      ::onGetMessagesError)
        }

        initMessageInput()

        adapter = ChatThreadRVAdapter()
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        rvChat.layoutManager = layoutManager
        rvChat.adapter = adapter
        rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (adapter.itemCount > 40 &&
                    !isLoading &&
                    totalItemCount <= lastVisibleItem + VISIBLE_THRESHOLD) {

                    page++
                    showProgress(true)
                    presenter.requestMessagesPagination(page,
                                                        ::onGetMessagesSuccess,
                                                        ::onGetMessagesError)
                }
            }
        })

        presenter = ChatThreadPresenter(activity)
        showProgress(true)
        val recipientId = if (thread.recipientUsername == ParseUser.getCurrentUser().username)
            thread.senderId
        else
            thread.recipientId

        presenter.requestPublicKey(recipientId,
                                   ::onGetPublicKeySuccess,
                                   ::onGetPublicKeyError)

        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim { it <= ' ' }
            if (message.isNotEmpty()) {
                lockSendUi(lock = true, lockInput = true)
                presenter.requestSendMessage(message,
                                             thread,
                                             ::onSendMessageSuccess,
                                             ::onSendMessageError)
                isLoading = true
            }
        }
    }

    override fun onPause() {
        super.onPause()

        val inputManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                                     InputMethodManager.HIDE_NOT_ALWAYS)

        hideKeyboard()

        presenter.disposeAll()
        unsubscribeLiveQuery()
        if (!networkTracker.isDisposed)
            networkTracker.dispose()
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

    private fun initMessageInput() {
        etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                lockSendUi(charSequence.toString().isEmpty(), false)
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    private fun initLiveQuery() {
        if (parseLiveQueryClient == null) {
            try {
                parseLiveQueryClient = ParseLiveQueryClient.Factory
                        .getClient(URI(getString(R.string.back4app_live_query_url)))
            } catch (e: URISyntaxException) {
                e.printStackTrace()
                throw IllegalStateException("initLiveQuery error")
            }

            parseQuery = ParseQuery.getQuery<Message>(Message::class.java)
            parseQuery!!.whereEqualTo(Const.TableNames.THREAD_ID, thread.objectId)
        }
    }

    private fun subscribeToLiveQuery() {
        val subscriptionHandling = parseLiveQueryClient!!.subscribe(parseQuery)

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE
        ) { _, message ->
            activity.runOnUiThread {
                adapter.addItem(0, message)
                rvChat.smoothScrollToPosition(0)

                if (adapter.itemCount > 0)
                    tvEmpty.visibility = View.INVISIBLE
            }
        }
    }

    private fun unsubscribeLiveQuery() {
        if (parseLiveQueryClient != null) {
            parseLiveQueryClient!!.unsubscribe(parseQuery)
            parseLiveQueryClient = null
        }
    }

    private fun lockSendUi(lock: Boolean, lockInput: Boolean) {
        if (lock) {
            btnSend.isEnabled = false
            btnSend.background = ContextCompat.getDrawable(activity,
                                                           R.drawable.bg_btn_chat_send_pressed)
            if (lockInput) {
                etMessage.isEnabled = false
                val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                             InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
        } else {
            btnSend.isEnabled = true
            btnSend.background = ContextCompat.getDrawable(activity, R.drawable.bg_btn_chat_send)
            if (lockInput)
                etMessage.isEnabled = true
        }
    }

    private fun onGetMessagesSuccess(messages: List<Message>) {
        showProgress(false)
        srlRefresh.isRefreshing = false
        lockSendUi(lock = false, lockInput = false)

        if (messages.isNotEmpty()) {
            tvEmpty.visibility = View.INVISIBLE
            if (adapter.itemCount != 0)
                adapter.addItems(messages)
            else
                adapter.setItems(messages)
        } else if (adapter.itemCount == 0) {
            tvEmpty.visibility = View.VISIBLE
        }
    }

    private fun onGetMessagesError(t: Throwable) {
        showProgress(false)
        srlRefresh.isRefreshing = false
        lockSendUi(lock = false, lockInput = false)

        if (adapter.itemCount == 0)
            tvError.visibility = View.VISIBLE

        Utils.toast(this, Utils.resolveError(t))
    }


    private fun onSendMessageSuccess() {
        etMessage.setText("")
        lockSendUi(false, lockInput = true)
        lockSendUi(lock = true, lockInput = false)
        isLoading = false
    }

    private fun onSendMessageError(t: Throwable) {
        etMessage.setText("")
        lockSendUi(lock = false, lockInput = true)
        lockSendUi(lock = true, lockInput = false)
        isLoading = false
        Utils.toast(this, Utils.resolveError(t))
    }

    private fun onGetPublicKeySuccess(publicKey: PublicKey) {
        showProgress(false)
        adapter.interlocutorPublicKey = publicKey
        presenter.requestMessages(thread,
                                  50,
                                  page,
                                  Const.TableNames.CREATED_AT_CRITERIA,
                                  ::onGetMessagesSuccess,
                                  ::onGetMessagesError)
    }

    private fun onGetPublicKeyError(t: Throwable) {
        if (t is VirgilCardIsNotFoundException || t is VirgilCardServiceException) {
            Utils.toast(this,
                        "Virgil Card is not found.\nYou can not chat with user without Virgil Card")
            activity.onBackPressed()
        }
        showProgress(false)
        srlRefresh.isRefreshing = false
        lockSendUi(lock = false, lockInput = false)

        Utils.toast(this, Utils.resolveError(t))
    }

    private fun showProgress(show: Boolean) {
        pbLoading.visibility = if (show) View.VISIBLE else View.INVISIBLE
        isLoading = show
    }

    companion object {

        private const val KEY_THREAD = "KEY_THREAD"
        private const val VISIBLE_THRESHOLD = 5

        fun newInstance(thread: ChatThread): ChatThreadFragment {

            val args = Bundle()

            args.putParcelable(KEY_THREAD, thread)

            val fragment = ChatThreadFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
