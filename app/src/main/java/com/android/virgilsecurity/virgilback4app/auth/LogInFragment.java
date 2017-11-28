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
import com.parse.ParseUser;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilCard;
import com.virgilsecurity.sdk.highlevel.VirgilKey;
import com.virgilsecurity.sdk.storage.KeyEntry;
import com.virgilsecurity.sdk.storage.KeyStorage;

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
    @BindView(R.id.btnSignin)
    protected View btnSignin;
    @BindView(R.id.pbLoading)
    protected View pbLoading;

    @Inject protected VirgilApi virgilApi;
    @Inject protected KeyStorage virgilKeyStorage;

    private AuthStateListener authStateListener;

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

    @OnClick({R.id.btnLogin, R.id.btnSignin})
    protected void onInterfaceClick(View v) {
        if (!Utils.validateUi(tilUserName))
            return;

        final String username = etUsername.getText().toString().toLowerCase(Locale.getDefault());
        boolean keyExists = virgilKeyStorage.exists(username);

        switch (v.getId()) {
            case R.id.btnLogin:
                tilUserName.setError(null);
                showProgress(true);
                if (keyExists) {
                    logIn(virgilKeyStorage.load(username));
                    VirgilKey userKey = virgilApi.getKeys().load(username);

                    getPresenter().requestLogIn(username, Utils.generatePassword(userKey.export()));
                } else {
                    tilUserName.setError(getString(R.string.no_such_user));
                    showProgress(false);
                }
                break;
            case R.id.btnSignin:
                tilUserName.setError(null);
                showProgress(true);
                if (keyExists) {
                    Utils.toast(this, R.string.already_registered);
                    showProgress(false);
                } else {
                    VirgilKey userKey = virgilApi.getKeys().generate();
                    userKey.save(username);

                    VirgilCard userCard = virgilApi.getCards().create(username, userKey);

                    getPresenter().requestRegister(username,
                                                   Utils.generatePassword(userKey.export()),
                                                   userCard.export());
                }
                break;
            default:
                break;
        }
    }



    private void logIn(KeyEntry userKey) {
        Utils.toast(this, "Login Stub (" + userKey.getName() + ")");
    }

    public void onLoginSuccess(ParseUser o) {
        showProgress(false);
        authStateListener.onLoggedInSuccesfully();
    }

    public void onLoginError(Throwable throwable) {
        showProgress(false);
        Utils.toast(this, Utils.resolveError(throwable));
    }

    public void onRegisterSuccess(Object o) {
        showProgress(false);
        authStateListener.onRegisteredInSuccesfully();
    }

    public void onRegisterError(Throwable throwable) {
        showProgress(false);
        Utils.toast(this, Utils.resolveError(throwable));
    }

    private void showProgress(boolean show) {
        if (show) {
            btnLogin.setEnabled(false);
            btnLogin.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_rect_primary_pressed));
            btnSignin.setEnabled(false);
            btnSignin.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_rect_primary_pressed));

            pbLoading.setVisibility(View.VISIBLE);
        } else {
            btnLogin.setEnabled(true);
            btnLogin.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_rect_primary));
            btnSignin.setEnabled(true);
            btnSignin.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_rect_primary));
            pbLoading.setVisibility(View.INVISIBLE);
        }
    }

    interface AuthStateListener {
        void onLoggedInSuccesfully();

        void onRegisteredInSuccesfully();
    }
}
