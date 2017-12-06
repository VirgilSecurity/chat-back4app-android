package com.android.virgilsecurity.virgilback4app.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.base.BaseActivity;
import com.android.virgilsecurity.virgilback4app.chat.contactsList.ThreadsListActivity;
import com.android.virgilsecurity.virgilback4app.util.Utils;
import com.android.virgilsecurity.virgilback4app.util.customElements.OnFinishTimer;
import com.parse.ParseUser;

/**
 * Created by Danylo Oliinyk on 16.11.17 at Virgil Security.
 * -__o
 */

public class SignInControlActivity extends BaseActivity
        implements LogInFragment.AuthStateListener {

    @StringDef({Section.LOG_IN, Section.SIGN_IN})
    public @interface Section {
        String LOG_IN = "LOG_IN";
        String SIGN_IN = "SIGN_IN";
    }

    private boolean secondPress;

    public static void start(Activity from) {
        from.startActivity(new Intent(from, SignInControlActivity.class));
    }

    public static void startClearTop(Activity from) {
        from.startActivity(new Intent(from, SignInControlActivity.class)
                                   .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                     | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void startWithFinish(Activity from) {
        from.startActivity(new Intent(from, SignInControlActivity.class));
        from.finish();
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_signin;
    }

    @Override
    protected void postButterInit() {

    }

    @Override protected void onResume() {
        super.onResume();

        if (ParseUser.getCurrentUser() != null) {
            ThreadsListActivity.startWithFinish(this);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changeSection(Section.LOG_IN);
    }

    public void changeSection(@Section String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = null;

        switch (tag) {
            case Section.LOG_IN:
                fragment = LogInFragment.newInstance();
                break;
            case Section.SIGN_IN:
                fragment = SignInFragment.newInstance();
                break;
        }

        Utils.replaceFragmentNoTag(fm, R.id.flContainer, fragment);
    }

    @Override
    public void onLoggedInSuccesfully() {
        ThreadsListActivity.start(this);
    }

    @Override
    public void onRegisteredInSuccesfully() {
        ThreadsListActivity.start(this);
    }

    @Override public void onBackPressed() {

        if (secondPress)
            super.onBackPressed();
        else
            Utils.toast(this, getString(R.string.press_exit_once_more));

        secondPress = true;

        new OnFinishTimer(2000, 100) {

            @Override public void onFinish() {
                secondPress = false;
            }
        }.start();
    }
}
