package com.android.virgilsecurity.virgilback4app.auth;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import com.virgilsecurity.sdk.storage.KeyEntry;
import com.virgilsecurity.sdk.storage.KeyStorage;

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

    @OnClick({R.id.btnLogin, R.id.btnSignin})
    protected void onInterfaceClick(View v) {
        if (!validateUi(tilUserName))
            return;

        final String username = etUsername.getText().toString();
        boolean keyExists = virgilKeyStorage.exists(username);

        switch (v.getId()) {
            case R.id.btnLogin:
                getPresenter().requestLogIn(etUsername.getText().toString(), "VirgilPasswordsAreTheWorth");
//                if (keyExists) {
//                    logIn(virgilKeyStorage.load(username));
//                } else {
//                    tilUserName.setError(getString(R.string.no_such_user));
//                }
                break;
            case R.id.btnSignin:
                getPresenter().requestRegister(etUsername.getText().toString(), "VirgilPasswordsAreTheWorth");
//                if (keyExists) {
//                    Utils.toast(this, R.string.already_registered);
//                } else {
//                    VirgilKey userKey = virgilApi.getKeys().generate();
//                    userKey.save(username);
//
//                    VirgilCard userCard = virgilApi.getCards().create(username, userKey);
//                    userCard.getIdentity();
//
//                    String exportedCard = userCard.export();
//
//                    String sha256edPass = Utils.generatePassword(userKey.export());
//                    Utils.log("Sha-256", "pass: " + sha256edPass);
//
//                    // TODO: 11/21/17 send to backend
//                    VirgilCard importedCard = virgilApi.getCards().importCard(exportedCard);
//                    virgilApi.getCards().publish(importedCard);
//                }
                break;
            default:
                break;
        }
    }

    private boolean validateUi(TextInputLayout til) {
        final String text = til.getEditText().getText().toString();

        if (text.isEmpty()) {
            til.setError(getString(R.string.username_empty));
            return false;
        } else {
            return true;
        }
    }

    private void logIn(KeyEntry userKey) {
        Utils.toast(this, "Login Stub (" + userKey.getName() + ")");
    }

    public void onLoginSuccess(ParseUser o) {
        authStateListener.onLoggedInSuccesfully();
    }

    public void onLoginError(Throwable throwable) {
        Utils.toast(this, Utils.resolveError(throwable));
    }

    public void onRegisterSuccess(Object o) {
        authStateListener.onRegisteredInSuccesfully();
    }

    public void onRegisterError(Throwable throwable) {
        Utils.toast(this, Utils.resolveError(throwable));
    }

    interface AuthStateListener {
        void onLoggedInSuccesfully();

        void onRegisteredInSuccesfully();
    }
}
