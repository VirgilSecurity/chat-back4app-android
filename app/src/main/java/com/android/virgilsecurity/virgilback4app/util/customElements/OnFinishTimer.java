package com.android.virgilsecurity.virgilback4app.util.customElements;

import android.os.CountDownTimer;

/**
 * Created by Danylo Oliinyk on 11/27/17 at Virgil Security.
 * -__o
 */

public abstract class OnFinishTimer extends CountDownTimer {

    public OnFinishTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override public void onTick(long l) {

    }
}
