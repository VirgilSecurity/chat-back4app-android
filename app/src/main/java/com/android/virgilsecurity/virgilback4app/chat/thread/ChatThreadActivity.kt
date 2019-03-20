package com.android.virgilsecurity.virgilback4app.chat.thread

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.virgilsecurity.virgilback4app.R
import com.android.virgilsecurity.virgilback4app.base.BaseActivity
import com.android.virgilsecurity.virgilback4app.model.ChatThread
import com.android.virgilsecurity.virgilback4app.util.Utils
import com.parse.ParseUser
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

class ChatThreadActivity : BaseActivity() {

    override val layout: Int
        get() = R.layout.activity_chat_thread

    override fun postCreateInit() {
        val chatThread = intent.getParcelableExtra<ChatThread>(CHAT_THREAD)
        if (ParseUser.getCurrentUser().username == chatThread.senderUsername)
            initToolbar(toolbar, chatThread.recipientUsername)
        else
            initToolbar(toolbar, chatThread.senderUsername)

        Utils.replaceFragmentNoTag(supportFragmentManager,
                                   R.id.flContainer,
                                   ChatThreadFragment.newInstance(chatThread))

        showBackButton(true, View.OnClickListener {
            onBackPressed()
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()

        hideKeyboard()
    }

    companion object {

        private const val CHAT_THREAD = "CHAT_THREAD"

        fun start(from: AppCompatActivity, chatThread: ChatThread) {
            from.startActivity(Intent(from, ChatThreadActivity::class.java)
                                       .putExtra(CHAT_THREAD, chatThread))
        }
    }
}
