package com.android.virgilsecurity.virgilback4app.util

import com.android.virgilsecurity.virgilback4app.model.AuthenticateResponse
import com.android.virgilsecurity.virgilback4app.model.VirgilJwtResponse
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.reactivex.Single
import org.json.JSONObject

/**
 * AuthRx to work with https://github.com/VirgilSecurity/sample-backend-java
 */
object AuthRx {

    fun authenticate(identity: String) = Single.create<String> { emitter ->
        val jsonBody = JSONObject()
        jsonBody.put(KEY_IDENTITY, identity)

        (BASE_URL + AUTH + AUTHENTICATE).httpPost()
            .body(jsonBody.toString())
            .header("Content-Type", "application/json")
            .responseString { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        with(result.get().let { Gson().fromJson(it, AuthenticateResponse::class.java) }) {
                            emitter.onSuccess(authToken)
                        }
                    }
                    is Result.Failure -> {
                        emitter.onError(result.getException())
                    }
                }
            }
    }

    /**
     * You can call it only after successful [authenticate]
     */
    fun virgilJwt(authToken: String) = Single.create<String> { emitter ->
        (BASE_URL + AUTH + VIRGIL_JWT).httpGet()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $authToken")
            .responseString { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        with(result.get().let { Gson().fromJson(it, VirgilJwtResponse::class.java) }) {
                            emitter.onSuccess(virgilToken)
                        }
                    }
                    is Result.Failure -> {
                        emitter.onError(result.getException())
                    }
                }
            }
    }

    private const val BASE_URL = "http://10.0.2.2:3000"
    private const val AUTH = "/auth"
    private const val AUTHENTICATE = "/authenticate"
    private const val VIRGIL_JWT = "/virgil-jwt"

    private const val KEY_IDENTITY = "identity"
}