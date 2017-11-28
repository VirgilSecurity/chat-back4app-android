package com.android.virgilsecurity.virgilback4app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

@ParseClassName("Message")
public class Message extends ParseObject {

    private static final String SENDER_ID = "senderId";
    private static final String THREAD_ID_KEY = "threadId";
    private static final String BODY_KEY = "body";
    private static final String TIMESTAMP_KEY = "timestamp";

    public String getSenderId() {
        return getString(SENDER_ID);
    }

    public void setSenderId(String senderId) {
        put(SENDER_ID, senderId);
    }

    public String getThreadId() {
        return getString(THREAD_ID_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setThreadId(String userId) {
        put(THREAD_ID_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public String getTimestamp() {
        return getString(TIMESTAMP_KEY);
    }

    public void setTimestamp(String timestamp) {
        put(TIMESTAMP_KEY, timestamp);
    }
}