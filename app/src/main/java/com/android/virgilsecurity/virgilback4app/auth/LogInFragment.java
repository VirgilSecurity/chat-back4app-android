package com.android.virgilsecurity.virgilback4app.auth;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.android.virgilsecurity.virgilback4app.AppVirgil;
import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.base.BaseFragmentWithPresenter;
import com.android.virgilsecurity.virgilback4app.util.UsernameInputFilter;
import com.android.virgilsecurity.virgilback4app.util.Utils;
import com.android.virgilsecurity.virgilback4app.util.VirgilHelper;
import com.parse.ParseUser;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.storage.KeyEntry;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import nucleus5.factory.RequiresPresenter;

/**
 * Created by Danylo Oliinyk on 16.11.17 at Virgil Security.
 * -__o
 */

@RequiresPresenter(LogInPresenter.class)
public class LogInFragment extends BaseFragmentWithPresenter<SignInControlActivity, LogInPresenter> {

    @BindView(R.id.tilUserName)
    protected TextInputLayout tilUserName;
    @BindView(R.id.etUsername)
    protected EditText etUsername;
    @BindView(R.id.btnLogin)
    protected View btnLogin;
    @BindView(R.id.btnSignup)
    protected View btnSignup;
    @BindView(R.id.pbLoading)
    protected View pbLoading;

    @Inject protected VirgilHelper virgilHelper;

    private AuthStateListener authStateListener;
    private String identity;

    public static LogInFragment newInstance() {

        Bundle args = new Bundle();

        LogInFragment fragment = new LogInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_login;
    }

    @Override
    protected void postButterInit() {

        AppVirgil.getVirgilComponent().inject(this);
        authStateListener = activity;

        etUsername.setFilters(new InputFilter[]{new UsernameInputFilter()});
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilUserName.setError(null);
                tilUserName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        getPresenter().disposeAll();
    }

    @Override public void onResume() {
        super.onResume();

        showProgress(!getPresenter().isDisposed());
    }

    @OnClick({R.id.btnLogin, R.id.btnSignup})
    protected void onInterfaceClick(View v) {
        if (!Utils.validateUi(tilUserName))
            return;

        identity = etUsername.getText().toString().toLowerCase(Locale.getDefault());
//        VirgilHelper virgilHelper = new VirgilHelper(activity, virgilApi,
//                                                     virgilKeyStorage, virgilApiContext);

        switch (v.getId()) {
            case R.id.btnLogin:
                tilUserName.setError(null);
                tilUserName.setErrorEnabled(false);
                showProgress(true);
                getPresenter().requestLogIn(identity, virgilHelper);
                break;
            case R.id.btnSignup:
                tilUserName.setError(null);
                tilUserName.setErrorEnabled(false);
                showProgress(true);
                getPresenter().requestSignUp(identity, virgilHelper);
                break;
            default:
                break;
        }
    }

    private void logIn(KeyEntry userKey) {
        Utils.toast(this, "Login Stub (" + userKey.getName() + ")");
    }

    public void onLoginSuccess(ParseUser user) {
        showProgress(false);
        authStateListener.onLoggedInSuccesfully();
    }

    public void onLoginError(Throwable throwable) {
        showProgress(false);
        Utils.toast(this, Utils.resolveError(throwable));
    }

    public void onSignUpSuccess(Object o) {
        showProgress(false);
        authStateListener.onRegisteredInSuccesfully();
    }

    public void onSignUpError(Throwable throwable) {
        showProgress(false);
        Utils.toast(this, Utils.resolveError(throwable));

        try {
            if (virgilHelper.loadPrivateKey(identity) != null)
                virgilHelper.removePrivateKey(identity);
        } catch (CryptoException e) {
            e.printStackTrace();
        }
    }

    private void showProgress(boolean show) {
        if (show) {
            etUsername.setEnabled(false);
            btnLogin.setEnabled(false);
            btnLogin.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_rect_primary_pressed));
            btnSignup.setEnabled(false);
            btnSignup.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_rect_primary_pressed));

            pbLoading.setVisibility(View.VISIBLE);
        } else {
            etUsername.setEnabled(true);
            btnLogin.setEnabled(true);
            btnLogin.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_rect_primary));
            btnSignup.setEnabled(true);
            btnSignup.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_rect_primary));
            pbLoading.setVisibility(View.INVISIBLE);
        }
    }

    interface AuthStateListener {
        void onLoggedInSuccesfully();

        void onRegisteredInSuccesfully();
    }
}
