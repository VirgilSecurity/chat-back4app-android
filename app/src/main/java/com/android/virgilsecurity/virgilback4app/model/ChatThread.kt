package com.android.virgilsecurity.virgilback4app.model

import com.android.virgilsecurity.virgilback4app.util.Const
import com.parse.ParseClassName
import com.parse.ParseObject

/**
 * Created by Danylo Oliinyk on 11/24/17 at Virgil Security.
 * -__o
 */

@ParseClassName("ChatThread")
class ChatThread : ParseObject() {

    var senderUsername: String
        get() = getString(Const.TableNames.SENDER_USERNAME) ?: Const.TableNames.SENDER_USERNAME
        set(username) = put(Const.TableNames.SENDER_USERNAME, username)

    var senderId: String
        get() = getString(Const.TableNames.SENDER_ID) ?: Const.TableNames.SENDER_ID
        set(senderId) = put(Const.TableNames.SENDER_ID, senderId)

    var recipientUsername: String
        get() = getString(Const.TableNames.RECIPIENT_USERNAME) ?: Const.TableNames.RECIPIENT_USERNAME
        set(username) = put(Const.TableNames.RECIPIENT_USERNAME, username)

    var recipientId: String
        get() = getString(Const.TableNames.RECIPIENT_ID) ?: Const.TableNames.RECIPIENT_ID
        set(recipientId) = put(Const.TableNames.RECIPIENT_ID, recipientId)

    var createdAtDate: String
        get() = getString(Const.TableNames.CREATED_AT_DATE) ?: Const.TableNames.CREATED_AT_DATE
        set(createdAtDate) = put(Const.TableNames.CREATED_AT_DATE, createdAtDate)
}
