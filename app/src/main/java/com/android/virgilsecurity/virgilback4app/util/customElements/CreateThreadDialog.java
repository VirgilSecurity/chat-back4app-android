package com.android.virgilsecurity.virgilback4app.util.customElements;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.util.UsernameInputFilter;
import com.android.virgilsecurity.virgilback4app.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Danylo Oliinyk on 11/26/17 at Virgil Security.
 * -__o
 */

public class CreateThreadDialog extends Dialog {

    private OnCreateThreadDialogListener onCreateThreadDialogListener;
    @Nullable private String title;
    private String message;

    @BindView(R.id.flRoot) FrameLayout flRoot;
    @BindView(R.id.llContentRoot) View llContentRoot;
    @BindView(R.id.llLoadingRoot) View llLoadingRoot;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvMessage) TextView tvMessage;
    @BindView(R.id.etUsername) EditText etUsername;

    public CreateThreadDialog(@NonNull Context context, @NonNull String message) {
        super(context);

        this.message = message;
    }

    public CreateThreadDialog(@NonNull Context context, int themeResId,
                              String title, @NonNull String message) {
        super(context, themeResId);

        this.title = title;
        this.message = message;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_create_thread);
        setCancelable(true);
        ButterKnife.bind(this);

        etUsername.setFilters(new InputFilter[]{new UsernameInputFilter()});
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etUsername.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        if (title != null)
            tvTitle.setText(title);

        tvMessage.setText(message);
    }

    @OnClick({R.id.btnCancel, R.id.btnOk})
    void onInterfaceClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancel:
                cancel();
                break;
            case R.id.btnOk:
                if (Utils.validateUi(etUsername)) {
                    showLoading(true);
                    onCreateThreadDialogListener.onCreateThread(etUsername.getText().toString());
                }
                break;
        }
    }

    public void setOnCreateThreadDialogListener(OnCreateThreadDialogListener onCreateThreadDialogListener) {
        this.onCreateThreadDialogListener = onCreateThreadDialogListener;
    }

    public void showLoading(boolean show) {
        if (show) {
            setCancelable(false);
            llContentRoot.setVisibility(View.GONE);
            TransitionManager.beginDelayedTransition(flRoot);
            llLoadingRoot.setVisibility(View.VISIBLE);
        } else {
            new OnFinishTimer(1000, 100) {
                @Override public void onFinish() {
                    setCancelable(true);
                    llLoadingRoot.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(flRoot);
                    llContentRoot.setVisibility(View.VISIBLE);
                }
            }.start();
        }
    }

    public interface OnCreateThreadDialogListener {
        void onCreateThread(String username);
    }
}
