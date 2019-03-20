package com.android.virgilsecurity.virgilback4app.chat.contactsList

import com.android.virgilsecurity.virgilback4app.model.ChatThread
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

class ThreadsListActivityPresenter {

    private var username: String? = null
    private val compositeDisposable = CompositeDisposable()

    internal fun requestUser(username: String,
                             onSuccess: (ParseUser) -> Unit,
                             onError: (Throwable) -> Unit) {
        this.username = username

        val disposable = RxParse.getUserByName(username)
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

    internal fun requestThreads(currentUser: ParseUser,
                                limit: Int,
                                page: Int,
                                sortCriteria: String,
                                onSuccess: (List<ChatThread>) -> Unit,
                                onError: (Throwable) -> Unit) {

        val disposable = RxParse.getMyThreads(currentUser, limit, page, sortCriteria)
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

    internal fun requestCreateThread(currentUser: ParseUser,
                                     interlocutorUser: ParseUser,
                                     onSuccess: () -> Unit,
                                     onError: (Throwable) -> Unit) {
        val disposable = RxParse.createThread(currentUser, interlocutorUser)
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

    internal fun disposeAll() {
        compositeDisposable.clear()
    }
}
