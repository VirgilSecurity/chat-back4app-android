package com.android.virgilsecurity.virgilback4app.util

/**
 * Created by Danylo Oliinyk on 11/23/17 at Virgil Security.
 * -__o
 */

class Const {

    internal object Http {
        val BAD_REQUEST = 400
        val UNAUTHORIZED = 401
        val FORBIDDEN = 403
        val NOT_ACCEPTABLE = 406
        val UNPROCESSABLE_ENTITY = 422
        val SERVER_ERROR = 500
    }

    object TableNames {
        val USER_NAME = "username"
        val OBJECT_ID = "objectId"
        val SENDER_USERNAME = "senderUsername"
        val SENDER_ID = "senderId"
        val RECIPIENT_USERNAME = "recipientUsername"
        val RECIPIENT_ID = "recipientId"
        val THREAD_ID = "threadId"
        val MESSAGE_BODY = "body"
        val CREATED_AT_CRITERIA = "createdAt"
        val CREATED_AT_DATE = "createdAtDate"
        val TIMESTAMP = "timestamp"
    }
}
