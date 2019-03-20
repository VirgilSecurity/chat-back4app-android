package com.android.virgilsecurity.virgilback4app.util

import android.content.Context
import com.android.virgilsecurity.virgilback4app.AppVirgil
import com.virgilsecurity.android.ethree.kotlin.interaction.EThree
import com.virgilsecurity.sdk.crypto.PublicKey
import io.reactivex.Completable
import io.reactivex.Single

/**
 * RxEthree
 */
class RxEthree(val context: Context) {

    private val preferences = Preferences.instance(context)

    fun initEthree(): Single<EThree> = Single.create<EThree> { e ->
        EThree.initialize(context,
                          object : EThree.OnGetTokenCallback {
                              override fun onGetToken(): String {
                                  return preferences.virgilToken()!!
                              }
                          },
                          object : EThree.OnResultListener<EThree> {
                              override fun onSuccess(result: EThree) {
                                  e.onSuccess(result)
                              }

                              override fun onError(throwable: Throwable) {
                                  e.onError(throwable)
                              }
                          })
    }

    fun registerEthree(): Completable = Completable.create { e ->
        AppVirgil.eThree.register(object : EThree.OnCompleteListener {
            override fun onSuccess() {
                e.onComplete()
            }

            override fun onError(throwable: Throwable) {
                e.onError(throwable)
            }
        })
    }

    fun findPublicKey(identity: String): Single<PublicKey> = Single.create<PublicKey> { e ->
        AppVirgil.eThree.lookupPublicKeys(listOf(identity),
                                          object : EThree.OnResultListener<Map<String, PublicKey>> {
                                              override fun onSuccess(result: Map<String, PublicKey>) {
                                                  val publicKey = result[identity]

                                                  if (publicKey != null)
                                                      e.onSuccess(publicKey)
                                                  else
                                                      e.onError(Throwable("Public key for identity " +
                                                                          "\"$identity\" was not found"))
                                              }

                                              override fun onError(throwable: Throwable) {
                                                  e.onError(throwable)
                                              }
                                          })
    }
}