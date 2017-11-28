package com.android.virgilsecurity.virgilback4app.chat.thread;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.base.BaseActivity;
import com.android.virgilsecurity.virgilback4app.model.ChatThread;
import com.android.virgilsecurity.virgilback4app.util.Utils;

import butterknife.BindView;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

public class ChatThreadActivity extends BaseActivity {

    private static final String CHAT_THREAD = "CHAT_THREAD";

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    public static void start(AppCompatActivity from, ChatThread chatThread) {
        from.startActivity(new Intent(from, ChatThreadActivity.class)
                                   .putExtra(CHAT_THREAD, chatThread));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_chat_thread;
    }

    @Override
    protected void postButterInit() {
        initToolbar(toolbar, getString(R.string.contacts));
        Utils.replaceFragmentNoTag(getSupportFragmentManager(),
                                   R.id.flContainer,
                                   ChatThreadFragment.newInstance(getIntent().getParcelableExtra(CHAT_THREAD)));

    }
}
