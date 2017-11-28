package com.android.virgilsecurity.virgilback4app.util;

/**
 * Created by Danylo Oliinyk on 11/23/17 at Virgil Security.
 * -__o
 */

public class Const {

    public static class Http {
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_ACCEPTABLE = 406;
        public static final int UNPROCESSABLE_ENTITY = 422;
        public static final int SERVER_ERROR = 500;
    }

    public static class TableNames {
        public static final String USER_NAME = "username";
        public static final String OBJECT_ID = "objectId";
        public static final String SENDER_USERNAME = "senderUsername";
        public static final String SENDER_ID = "senderId";
        public static final String RECIPIENT_USERNAME = "recipientUsername";
        public static final String RECIPIENT_ID = "recipientId";
        public static final String THREAD_ID = "threadId";
        public static final String LIVE_QUERY_CLASS = "Message";
        public static final String CREATED_AT_CRITERIA = "createdAt";
        public static final String CREATED_AT_DATE = "createdAtDate";
    }

    public static class Request {
        public static final String CRETE_CARD = "csr";
    }
}
