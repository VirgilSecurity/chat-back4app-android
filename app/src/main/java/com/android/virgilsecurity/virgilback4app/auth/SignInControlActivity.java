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
import com.android.virgilsecurity.virgilback4app.util.Utils;

/**
 * Created by danylooliinyk on 16.11.17.
 */

public class SignInControlActivity extends BaseActivity {

    @StringDef({LoginSection.LOG_IN, LoginSection.SIGN_IN})
    public @interface LoginSection {
        String LOG_IN = "LOG_IN";
        String SIGN_IN = "SIGN_IN";
    }

    public static void start(Activity from) {
        from.startActivity(new Intent(from, SignInControlActivity.class));
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changeSection(LoginSection.LOG_IN);
    }

    public void changeSection(@LoginSection String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = null;

        switch (tag) {
            case LoginSection.LOG_IN:
                fragment = LogInFragment.newInstance();
                break;
            case LoginSection.SIGN_IN:
                fragment = SignInFragment.newInstance();
                break;
        }

        Utils.replaceFragmentNoTag(fm, fragment);
    }
}
