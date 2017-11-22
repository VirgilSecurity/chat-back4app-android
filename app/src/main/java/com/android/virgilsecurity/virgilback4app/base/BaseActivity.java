package com.android.virgilsecurity.virgilback4app.base;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.android.virgilsecurity.virgilback4app.R;

import butterknife.ButterKnife;

/**
 * Created by danylooliinyk on 16.11.17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private TextView tvToolbarTitle;
    @Nullable private Toolbar toolbar;

    protected abstract int getLayout();

    protected abstract void postButterInit();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayout());
        ButterKnife.bind(this);

        postButterInit();
    }


    protected final void changeTitle(String titlePage) {
        if (toolbar != null) {
            tvToolbarTitle.setText(titlePage);
        } else {
            throw new NullPointerException("Init Toolbar first");
        }
    }

    protected final void initToolbar(Toolbar toolbar, String titlePage) {
        this.toolbar = toolbar;
        this.tvToolbarTitle = toolbar.findViewById(R.id.tvToolbarTitle);

        setSupportActionBar(toolbar);

        tvToolbarTitle.setText(titlePage);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");
    }

    protected void setupToolbarWithUpNav(Toolbar toolbar, String titlePage, @DrawableRes int res) {
        setSupportActionBar(toolbar);

        tvToolbarTitle = toolbar.findViewById(R.id.tvToolbarTitle);
        tvToolbarTitle.setText(titlePage);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(res);
        getSupportActionBar().setTitle("");
    }
}
