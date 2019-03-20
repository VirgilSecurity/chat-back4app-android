package com.android.virgilsecurity.virgilback4app.chat.contactsList

import android.content.Context
import com.android.virgilsecurity.virgilback4app.AppVirgil
import com.android.virgilsecurity.virgilback4app.model.ChatThread
import com.android.virgilsecurity.virgilback4app.util.RxEthree
import com.android.virgilsecurity.virgilback4app.util.RxParse
import com.parse.ParseUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

class ThreadsListFragmentPresenter(context: Context) {

    private var limit: Int = 0
    private lateinit var currentUser: ParseUser
    private lateinit var sortCriteria: String
    private val compositeDisposable = CompositeDisposable()
    private val rxEthree = RxEthree(context)

    fun requestThreads(currentUser: ParseUser,
                                limit: Int,
                                page: Int,
                                sortCriteria: String,
                                onSuccess: (List<ChatThread>) -> Unit,
                                onError: (Throwable) -> Unit) {
        this.currentUser = currentUser
        this.limit = limit
        this.sortCriteria = sortCriteria

        val disposable = RxParse.getMyThreads(this.currentUser,
                                              this.limit,
                                              page,
                                              this.sortCriteria)
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

    fun requestThreadsPagination(page: Int,
                                          onSuccess: (List<ChatThread>) -> Unit,
                                          onError: (Throwable) -> Unit) {
        val disposable = RxParse.getMyThreads(this.currentUser,
                                              this.limit,
                                              page,
                                              this.sortCriteria)
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

    fun requestEthreeInit(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val disposable = rxEthree.initEthree()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        AppVirgil.eThree = it
                        onSuccess()
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
