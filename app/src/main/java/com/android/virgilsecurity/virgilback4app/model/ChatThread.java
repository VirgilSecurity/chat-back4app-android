package com.android.virgilsecurity.virgilback4app.model;

import com.android.virgilsecurity.virgilback4app.util.Const;
import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Danylo Oliinyk on 11/24/17 at Virgil Security.
 * -__o
 */

@ParseClassName("ChatThread")
public class ChatThread extends ParseObject {

    public String getSenderUsername() {
        return getString(Const.TableNames.SENDER_USERNAME);
    }

    public void setSenderUsername(String username) {
        put(Const.TableNames.SENDER_USERNAME, username);
    }

    public String getSenderId() {
        return getString(Const.TableNames.SENDER_ID);
    }

    public void setSenderId(String senderId) {
        put(Const.TableNames.SENDER_ID, senderId);
    }

    public String getRecipientUsername() {
        return getString(Const.TableNames.RECIPIENT_USERNAME);
    }

    public void setRecipientUsername(String username) {
        put(Const.TableNames.RECIPIENT_USERNAME, username);
    }

    public String getRecipientId() {
        return getString(Const.TableNames.RECIPIENT_ID);
    }

    public void setRecipientId(String recipientId) {
        put(Const.TableNames.RECIPIENT_ID, recipientId);
    }

    public String getCreatedAtDate() {
        return getString(Const.TableNames.CREATED_AT_DATE);
    }

    public void setCreatedAtDate(String createdAtDate) {
        put(Const.TableNames.CREATED_AT_DATE, createdAtDate);
    }
}
