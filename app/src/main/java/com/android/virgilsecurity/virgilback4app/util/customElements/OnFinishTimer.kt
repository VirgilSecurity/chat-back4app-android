package com.android.virgilsecurity.virgilback4app.util.customElements

import android.os.CountDownTimer

/**
 * Created by Danylo Oliinyk on 11/27/17 at Virgil Security.
 * -__o
 */

abstract class OnFinishTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(
    millisInFuture,
    countDownInterval) {

    override fun onTick(l: Long) {

    }
}
