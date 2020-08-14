package com.android.virgilsecurity.virgilback4app

import android.app.Application
import com.android.virgilsecurity.virgilback4app.model.ChatThread
import com.android.virgilsecurity.virgilback4app.model.Message
import com.parse.Parse
import com.parse.ParseObject
import com.virgilsecurity.android.ethree.interaction.EThree

/**
 * Created by Danylo Oliinyk on 16.11.17 at Virgil Security.
 * -__o
 */

class AppVirgil : Application() {

    override fun onCreate() {
        super.onCreate()

        ParseObject.registerSubclass(Message::class.java)
        ParseObject.registerSubclass(ChatThread::class.java)

        Parse.initialize(Parse.Configuration.Builder(this)
                                 .applicationId(getString(R.string.back4app_app_id))
                                 .clientKey(getString(R.string.back4app_client_key))
                                 .server(getString(R.string.back4app_server_url))
                                 .build())


    }

    companion object {

        lateinit var eThree: EThree

        fun isEthreeInitialized() = ::eThree.isInitialized
    }
}
