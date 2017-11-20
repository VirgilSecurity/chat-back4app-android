package com.android.virgilsecurity.virgilback4app.auth;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;

import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.base.BaseFragmentWithPresenter;
import com.android.virgilsecurity.virgilback4app.utils.UsernameInputFilter;
import com.android.virgilsecurity.virgilback4app.utils.Util;
import com.virgilsecurity.sdk.client.VirgilClient;
import com.virgilsecurity.sdk.client.requests.PublishCardRequest;
import com.virgilsecurity.sdk.highlevel.StringEncoding;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilKey;
import com.virgilsecurity.sdk.storage.KeyEntry;
import com.virgilsecurity.sdk.storage.VirgilKeyStorage;

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

    @Inject
    protected VirgilApi virgilApi;
    @Inject
    protected VirgilClient virgilClient;

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

        etUsername.setFilters(new InputFilter[]{new UsernameInputFilter()});
    }

    @OnClick({R.id.btnLogin, R.id.btnSignin})
    protected void onInterfaceClick(View v) {
        if (!validateUi(tilUserName))
            return;

        final String username = etUsername.getText().toString();
        VirgilKeyStorage virgilKeyStorage = new VirgilKeyStorage();
        boolean keyExists = virgilKeyStorage.exists(username);

        switch (v.getId()) {
            case R.id.btnLogin:
                if (keyExists) {
                    logIn(virgilKeyStorage.load(username));
                } else {
                    Util.toast(this, R.string.no_such_user);
                }
                break;
            case R.id.btnSignin:
                if (keyExists) {
                    Util.toast(this, R.string.already_registered);
                } else {
                    VirgilKey aliceKey = virgilApi.getKeys().generate();

                    aliceKey.save(username);

                    PublishCardRequest cardRequest =
                            new PublishCardRequest(aliceKey.export()
                                                           .toString(StringEncoding.Base64));
                    virgilClient.publishCard(cardRequest);
                }
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

    }
}
