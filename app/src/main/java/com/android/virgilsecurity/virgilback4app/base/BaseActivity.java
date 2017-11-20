package com.android.virgilsecurity.virgilback4app.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by danylooliinyk on 16.11.17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getLayout();

    protected abstract void postButterInit();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayout());
        ButterKnife.bind(this);

        postButterInit();
    }
}
