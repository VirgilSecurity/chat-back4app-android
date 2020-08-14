package com.android.virgilsecurity.virgilback4app.auth

import android.content.Context
import android.util.Base64
import com.android.virgilsecurity.virgilback4app.AppVirgil
import com.android.virgilsecurity.virgilback4app.util.AuthRx
import com.android.virgilsecurity.virgilback4app.util.Preferences
import com.android.virgilsecurity.virgilback4app.util.RxEthree
import com.android.virgilsecurity.virgilback4app.util.RxParse
import com.parse.ParseUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by Danylo Oliinyk on 11/17/17 at Virgil Security.
 * -__o
 */

class LogInPresenter(context: Context) {

    private val compositeDisposable = CompositeDisposable()
    private val rxEthree = RxEthree(context)
    private val preferences = Preferences.instance(context)

    fun requestSignUp(identity: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val password = generatePassword(identity.toByteArray())

        val disposable = RxParse.signUp(identity, password)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .toSingle { ParseUser.getCurrentUser() }
                .flatMap { AuthRx.virgilJwt(it.sessionToken) }
                .map { preferences.setVirgilToken(it) }
                .flatMap { rxEthree.initEthree(identity) }
                .map { AppVirgil.eThree = it }
                .flatMap { rxEthree.registerEthree().toSingle { Unit } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        onSuccess()
                    },
                    onError = {
                        onError(it)
                    }
                )

        compositeDisposable += disposable
    }

    /**
     * Sign In user in Back4App, exchange Back4App User's sessionToken for Virgil Jwt,
     * then initialize and register [EThree].
     */
    fun requestSignIn(identity: String,
                      onSuccess: () -> Unit,
                      onError: (Throwable) -> Unit) {

        val password = generatePassword(identity.toByteArray())

        val disposable = RxParse.logIn(identity, password)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { AuthRx.virgilJwt(it.sessionToken) }
                .map { preferences.setVirgilToken(it) }
                .flatMap { rxEthree.initEthree(identity, true) }
                .map { AppVirgil.eThree = it }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
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

    /**
     * !!! NOT SECURE !!!
     *
     * It's just for example purpose. Ask your user for the password.
     *
     * This function generates SHA-256 password as Base64 String from provided data.
     */
    private fun generatePassword(data: ByteArray): String {
        val sha: MessageDigest
        val hash: ByteArray

        try {
            sha = MessageDigest.getInstance("SHA-256")
            hash = sha.digest(data)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            throw IllegalStateException("No Such Algorithm")
        }

        return Base64.encodeToString(hash, Base64.DEFAULT)
    }
}
