package com.android.virgilsecurity.virgilback4app.model;

import com.android.virgilsecurity.virgilback4app.util.Const;
import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

@ParseClassName("Message")
public class Message extends ParseObject {

    public String getSenderId() {
        return getString(Const.TableNames.SENDER_ID);
    }

    public void setSenderId(String senderId) {
        put(Const.TableNames.SENDER_ID, senderId);
    }

    public String getThreadId() {
        return getString(Const.TableNames.THREAD_ID);
    }

    public void setThreadId(String userId) {
        put(Const.TableNames.THREAD_ID, userId);
    }

    public String getBody() {
        return getString(Const.TableNames.MESSAGE_BODY);
    }

    public void setBody(String body) {
        put(Const.TableNames.MESSAGE_BODY, body);
    }

    public String getTimestamp() {
        return getString(Const.TableNames.TIMESTAMP);
    }

    public void setTimestamp(String timestamp) {
        put(Const.TableNames.TIMESTAMP, timestamp);
    }
}