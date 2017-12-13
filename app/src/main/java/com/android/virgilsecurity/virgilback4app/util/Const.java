package com.android.virgilsecurity.virgilback4app.util;

/**
 * Created by Danylo Oliinyk on 11/23/17 at Virgil Security.
 * -__o
 */

public class Const {

    static class Http {
        static final int BAD_REQUEST = 400;
        static final int UNAUTHORIZED = 401;
        static final int FORBIDDEN = 403;
        static final int NOT_ACCEPTABLE = 406;
        static final int UNPROCESSABLE_ENTITY = 422;
        static final int SERVER_ERROR = 500;
    }

    public static class TableNames {
        public static final String USER_NAME = "username";
        public static final String OBJECT_ID = "objectId";
        public static final String SENDER_USERNAME = "senderUsername";
        public static final String SENDER_ID = "senderId";
        public static final String RECIPIENT_USERNAME = "recipientUsername";
        public static final String RECIPIENT_ID = "recipientId";
        public static final String THREAD_ID = "threadId";
        public static final String MESSAGE_BODY = "body";
        public static final String CREATED_AT_CRITERIA = "createdAt";
        public static final String CREATED_AT_DATE = "createdAtDate";
        public static final String TIMESTAMP = "timestamp";
    }
}
