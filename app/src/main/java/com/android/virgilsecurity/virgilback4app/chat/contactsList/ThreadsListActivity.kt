package com.android.virgilsecurity.virgilback4app.chat.contactsList

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import com.android.virgilsecurity.virgilback4app.R
import com.android.virgilsecurity.virgilback4app.auth.SignInControlActivity
import com.android.virgilsecurity.virgilback4app.base.BaseActivity
import com.android.virgilsecurity.virgilback4app.chat.thread.ChatThreadActivity
import com.android.virgilsecurity.virgilback4app.model.ChatThread
import com.android.virgilsecurity.virgilback4app.util.Const
import com.android.virgilsecurity.virgilback4app.util.Preferences
import com.android.virgilsecurity.virgilback4app.util.Utils
import com.android.virgilsecurity.virgilback4app.util.customElements.CreateThreadDialog
import com.android.virgilsecurity.virgilback4app.util.customElements.OnFinishTimer
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.layout_drawer_header.view.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

class ThreadsListActivity : BaseActivity(), ThreadsListFragment.OnStartThreadListener {

    private var createThreadDialog: CreateThreadDialog? = null
    private var secondPress: Boolean = false
    private lateinit var newThreadUser: ParseUser
    private lateinit var presenter: ThreadsListActivityPresenter

    override val layout: Int
        get() = R.layout.activity_contacts

    override fun postCreateInit() {
        initToolbar(toolbar, getString(R.string.contacts))
        initDrawer()
        Utils.replaceFragmentNoBackStack(supportFragmentManager,
                                         R.id.flContainer,
                                         ThreadsListFragment.newInstance(),
                                         THREADS_FRAGMENT)
        hideKeyboard()
        showHamburger(true, View.OnClickListener {
            if (!dlDrawer.isDrawerOpen(Gravity.START))
                dlDrawer.openDrawer(Gravity.START)
            else
                dlDrawer.closeDrawer(Gravity.START)
        })
        presenter = ThreadsListActivityPresenter()
    }

    private fun initDrawer() {
        val tvUsernameDrawer = nvNavigation.getHeaderView(0).tvUsernameDrawer
        tvUsernameDrawer.text = ParseUser.getCurrentUser().username

        val actionBar = this.actionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
        }

        nvNavigation.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemNewChat -> {
                    dlDrawer.closeDrawer(Gravity.START)
                    createThreadDialog = CreateThreadDialog(this, R.style.NotTransBtnsDialogTheme,
                                                            getString(R.string.create_thread),
                                                            getString(R.string.enter_username))

                    createThreadDialog!!.setOnCreateThreadDialogListener(
                        object : CreateThreadDialog.OnCreateThreadDialogListener {
                            override fun onCreateThread(username: String) {
                                if (ParseUser.getCurrentUser().username == username) {
                                    Utils.toast(this@ThreadsListActivity,
                                                R.string.no_chat_with_yourself)
                                } else {
                                    createThreadDialog!!.showProgress(true)
                                    presenter.requestUser(username,
                                                          ::onGetUserSuccess,
                                                          ::onGetUserError)
                                }
                            }
                        })

                    createThreadDialog!!.show()

                    return@setNavigationItemSelectedListener true
                }
                R.id.itemLogOut -> {
                    dlDrawer.closeDrawer(Gravity.START)
                    presenter.disposeAll()
                    showBaseLoading(true)
                    Preferences.instance(this).clearVirgilToken()
                    ParseUser.logOutInBackground { e ->
                        runOnUiThread { showBaseLoading(false) }
                        if (e == null) {
                            SignInControlActivity.startClearTop(this)
                        } else {
                            Utils.toast(this, Utils.resolveError(e))
                        }
                    }
                    return@setNavigationItemSelectedListener true
                }
                else -> return@setNavigationItemSelectedListener false
            }
        }
    }

    override fun onStartThread(thread: ChatThread) {
        ChatThreadActivity.start(this, thread)
    }

    private fun onGetUserSuccess(user: ParseUser?) {
        if (user != null) {
            newThreadUser = user
            presenter.requestThreads(ParseUser.getCurrentUser(),
                                     1000,
                                     0,
                                     Const.TableNames.CREATED_AT_CRITERIA,
                                     ::onGetThreadsSuccess,
                                     ::onGetThreadsError)
        } else {
            createThreadDialog!!.dismiss()
        }
    }

    private fun onGetUserError(t: Throwable) {
        createThreadDialog!!.showProgress(false)
        Utils.toast(this, Utils.resolveError(t))
    }

    private fun onGetThreadsSuccess(threads: List<ChatThread>) {
        var chatThread: ChatThread? = null

        for (thread in threads) {
            if (thread.senderUsername == newThreadUser.username ||
                thread.recipientUsername == newThreadUser.username) {

                chatThread = thread
            }
        }

        if (chatThread == null) {
            presenter.requestCreateThread(ParseUser.getCurrentUser(),
                                          newThreadUser,
                                          ::onCreateThreadSuccess,
                                          ::onCreateThreadError)
        } else {
            createThreadDialog!!.dismiss()
            ChatThreadActivity.start(this, chatThread)
        }
    }

    private fun onGetThreadsError(t: Throwable) {
        createThreadDialog!!.dismiss()
        Utils.toast(this, Utils.resolveError(t))
    }

    private fun onCreateThreadSuccess() {
        presenter.requestThreads(ParseUser.getCurrentUser(),
                                 1000,
                                 0,
                                 Const.TableNames.CREATED_AT_CRITERIA,
                                 ::onGetThreadsSuccess,
                                 ::onGetThreadsError)
    }

    private fun onCreateThreadError(t: Throwable) {
        createThreadDialog!!.dismiss()
        Utils.toast(this, Utils.resolveError(t))
    }

    override fun onBackPressed() {

        if (secondPress)
            super.onBackPressed()
        else
            Utils.toast(this, getString(R.string.press_exit_once_more))

        secondPress = true

        object : OnFinishTimer(2000, 100) {

            override fun onFinish() {
                secondPress = false
            }
        }.start()
    }

    companion object {

        private const val THREADS_FRAGMENT = "THREADS_FRAGMENT"

        fun start(from: AppCompatActivity) {
            from.startActivity(Intent(from, ThreadsListActivity::class.java))
        }

        fun startWithFinish(from: AppCompatActivity) {
            from.startActivity(Intent(from, ThreadsListActivity::class.java))
            from.finish()
        }
    }
}
