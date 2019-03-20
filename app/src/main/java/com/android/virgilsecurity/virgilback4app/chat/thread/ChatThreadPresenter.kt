package com.android.virgilsecurity.virgilback4app.chat.thread

import android.content.Context
import com.android.virgilsecurity.virgilback4app.AppVirgil
import com.android.virgilsecurity.virgilback4app.model.ChatThread
import com.android.virgilsecurity.virgilback4app.model.Message
import com.android.virgilsecurity.virgilback4app.util.RxEthree
import com.android.virgilsecurity.virgilback4app.util.RxParse
import com.virgilsecurity.sdk.crypto.PublicKey
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Danylo Oliinyk on 11/27/17 at Virgil Security.
 * -__o
 */

class ChatThreadPresenter(context: Context) {

    private var limit: Int = 0
    private lateinit var thread: ChatThread
    private lateinit var sortCriteria: String
    private val compositeDisposable = CompositeDisposable()
    private val eThree = AppVirgil.eThree
    private lateinit var publicKey: PublicKey
    private val rxEthree = RxEthree(context)

    fun requestMessages(thread: ChatThread, limit: Int,
                        page: Int, sortCriteria: String,
                        onSuccess: (List<Message>) -> Unit,
                        onError: (Throwable) -> Unit) {
        this.thread = thread
        this.limit = limit
        this.sortCriteria = sortCriteria

        val disposable = RxParse.getMessages(this.thread, this.limit, page, this.sortCriteria)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        onSuccess(it)
                    },
                    onError = {
                        onError(it)
                    }
                )

        compositeDisposable += disposable
    }

    fun requestMessagesPagination(page: Int,
                                  onSuccess: (List<Message>) -> Unit,
                                  onError: (Throwable) -> Unit) {

        val disposable = RxParse.getMessages(this.thread, this.limit, page, this.sortCriteria)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        onSuccess(it)
                    },
                    onError = {
                        onError(it)
                    }
                )

        compositeDisposable += disposable
    }

    fun requestSendMessage(text: String,
                           thread: ChatThread,
                           onSuccess: () -> Unit,
                           onError: (Throwable) -> Unit) {

        val encryptedText = eThree.encrypt(text, listOf(publicKey))
        val disposable = RxParse.sendMessage(encryptedText, thread)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onComplete = {
                        onSuccess()
                    },
                    onError = {
                        onError(it)
                    }
                )

        compositeDisposable += disposable
    }

    fun requestGetCards(identity: String,
                        onSuccess: (PublicKey) -> Unit,
                        onError: (Throwable) -> Unit) {

        val disposable = rxEthree.findPublicKey(identity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        publicKey = it
                        onSuccess(it)
                    },
                    onError = {
                        onError(it)
                    }
                )

        compositeDisposable += disposable
    }

    fun disposeAll() {
        compositeDisposable.clear()
    }
}
