package com.android.virgilsecurity.virgilback4app.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import butterknife.ButterKnife;
import nucleus5.presenter.RxPresenter;
import nucleus5.view.NucleusSupportFragment;

/**
 * Created by Danylo Oliinyk on 16.11.17 at Virgil Security.
 * -__o
 */

public abstract class BaseFragmentWithPresenter<A extends Activity, P extends RxPresenter> extends NucleusSupportFragment<P> {

    protected A activity;

    protected abstract int getLayout();

    protected abstract void postButterInit();

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (A) activity;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);

        activity = (A) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        postButterInit();
    }

    protected final void hideKeyboard() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
