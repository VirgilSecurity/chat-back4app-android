package com.android.virgilsecurity.virgilback4app.model

import com.android.virgilsecurity.virgilback4app.util.Const
import com.parse.ParseClassName
import com.parse.ParseObject

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

@ParseClassName("Message")
class Message : ParseObject() {

    var senderId: String
        get() = getString(Const.TableNames.SENDER_ID)
        set(senderId) = put(Const.TableNames.SENDER_ID, senderId)

    var threadId: String
        get() = getString(Const.TableNames.THREAD_ID)
        set(userId) = put(Const.TableNames.THREAD_ID, userId)

    var body: String
        get() = getString(Const.TableNames.MESSAGE_BODY)
        set(body) = put(Const.TableNames.MESSAGE_BODY, body)

    var timestamp: String
        get() = getString(Const.TableNames.TIMESTAMP)
        set(timestamp) = put(Const.TableNames.TIMESTAMP, timestamp)
}