package com.android.virgilsecurity.virgilback4app.util

import android.content.Context
import com.android.virgilsecurity.virgilback4app.AppVirgil
import com.virgilsecurity.android.common.model.EThreeParams
import com.virgilsecurity.android.ethree.interaction.EThree
import com.virgilsecurity.common.callback.OnCompleteListener
import com.virgilsecurity.common.callback.OnResultListener
import com.virgilsecurity.sdk.cards.Card
import com.virgilsecurity.sdk.crypto.exceptions.KeyEntryNotFoundException
import io.reactivex.Completable
import io.reactivex.Single

/**
 * RxEthree
 */
class RxEthree(val context: Context) {

    private val preferences = Preferences.instance(context)

    fun initEthree(identity: String, verifyPrivateKey: Boolean = false): Single<EThree> = Single.create { e ->
        val params = EThreeParams(identity, {preferences.virgilToken()!!}, context)
        val ethree = EThree(params)
        if (verifyPrivateKey) {
            if (ethree.hasLocalPrivateKey()) {
                e.onSuccess(ethree)
            } else {
                e.onError(KeyEntryNotFoundException())
            }
        } else {
            e.onSuccess(ethree)
        }
    }

    fun registerEthree(): Completable = Completable.create { e ->
        AppVirgil.eThree.register().addCallback(object : OnCompleteListener {
            override fun onError(throwable: Throwable) {
                e.onError(throwable)
            }

            override fun onSuccess() {
                e.onComplete()
            }

        })
    }

    fun findCard(identity: String): Single<Card> = Single.create { e ->
        AppVirgil.eThree.findUser(identity).addCallback(object : OnResultListener<Card> {
            override fun onError(throwable: Throwable) {
                e.onError(throwable)
            }

            override fun onSuccess(result: Card) {
                e.onSuccess(result)
            }

        })
    }
}