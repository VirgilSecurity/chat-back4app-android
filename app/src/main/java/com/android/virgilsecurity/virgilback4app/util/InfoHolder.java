package com.android.virgilsecurity.virgilback4app.util;

import android.content.Context;

/**
 * Created by Danylo Oliinyk on 12/15/17 at Virgil Security.
 * -__o
 */

public class InfoHolder {
    private String identity;

    public InfoHolder(Context context) {

    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }
}