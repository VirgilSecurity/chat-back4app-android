package com.android.virgilsecurity.virgilback4app.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.android.virgilsecurity.virgilback4app.R
import com.android.virgilsecurity.virgilback4app.base.BaseActivity
import com.android.virgilsecurity.virgilback4app.chat.contactsList.ThreadsListActivity
import com.android.virgilsecurity.virgilback4app.util.Utils
import com.android.virgilsecurity.virgilback4app.util.customElements.OnFinishTimer

/**
 * Created by Danylo Oliinyk on 16.11.17 at Virgil Security.
 * -__o
 */

class SignInControlActivity : BaseActivity(), LogInFragment.AuthStateListener {

    private var secondPress: Boolean = false

    override val layout: Int
        get() = R.layout.activity_signin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.replaceFragmentNoTag(supportFragmentManager,
                                   R.id.flContainer,
                                   LogInFragment.newInstance())
    }

    override fun postCreateInit() {}

    override fun onLoggedInSuccesfully() {
        ThreadsListActivity.startWithFinish(this)
    }

    override fun onRegisteredInSuccesfully() {
        ThreadsListActivity.startWithFinish(this)
    }

    override fun onBackPressed() {
        if (secondPress)
            super.onBackPressed()
        else
            Utils.toast(this, getString(R.string.press_exit_once_more))

        secondPress = true

        object : OnFinishTimer(DOUBLE_BACK_TIME, TIMER_INTERVAL) {
            override fun onFinish() {
                secondPress = false
            }
        }.start()
    }

    companion object {

        fun startClearTop(from: Activity) {
            from.startActivity(Intent(from, SignInControlActivity::class.java)
                                       .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        fun startWithFinish(from: Activity) {
            from.startActivity(Intent(from, SignInControlActivity::class.java))
            from.finish()
        }

        private const val DOUBLE_BACK_TIME = 2 * 1000L // 2 seconds
        private const val TIMER_INTERVAL = 100L // 2 seconds
    }
}
