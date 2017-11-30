package com.android.virgilsecurity.virgilback4app.base;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.virgilsecurity.virgilback4app.R;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import nucleus5.presenter.RxPresenter;
import nucleus5.view.NucleusAppCompatActivity;

/**
 * Created by Danylo Oliinyk on 16.11.17 at Virgil Security.
 * -__o
 */

public abstract class BaseActivityWithPresenter<P extends RxPresenter> extends NucleusAppCompatActivity<P> {

    private TextView tvToolbarTitle;
    private View ibToolbarBack;
    private View ibToolbarHamburger;
    @Nullable private Toolbar toolbar;
    private View llBaseLoading;

    protected abstract int getLayout();

    protected abstract void postButterInit();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getLayoutInflater();
        View baseView = inflater.inflate(R.layout.activity_base, null);

        FrameLayout flBaseContainer = baseView.findViewById(R.id.flBaseContainer);
        llBaseLoading = baseView.findViewById(R.id.llBaseLoading);

        View childView = inflater.inflate(getLayout(), null);
        flBaseContainer.removeAllViews();
        flBaseContainer.addView(childView);

        setContentView(baseView);

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
        this.ibToolbarBack = toolbar.findViewById(R.id.ibToolbarBack);
        this.ibToolbarHamburger = toolbar.findViewById(R.id.ibToolbarHamburger);

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

    @Override
    protected void onResume() {
        super.onResume();

        ReactiveNetwork.observeNetworkConnectivity(getApplicationContext())
                       .debounce(1000, TimeUnit.MILLISECONDS)
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe((connectivity) -> {
                           showNoNetwork(!(connectivity.getState() == NetworkInfo.State.CONNECTED));
                       });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showNoNetwork(boolean show) {
        llBaseLoading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    protected final void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected final void showBackButton(boolean show, @Nullable View.OnClickListener listener) {
        if (show) {
            ibToolbarBack.setVisibility(View.VISIBLE);
            ibToolbarBack.setOnClickListener(listener);
        } else {
            ibToolbarBack.setVisibility(View.INVISIBLE);
        }
    }

    protected final void showHamburger(boolean show, @Nullable View.OnClickListener listener) {
        if (show) {
            ibToolbarHamburger.setVisibility(View.VISIBLE);
            ibToolbarHamburger.setOnClickListener(listener);
        } else {
            ibToolbarHamburger.setVisibility(View.INVISIBLE);
        }
    }
}
