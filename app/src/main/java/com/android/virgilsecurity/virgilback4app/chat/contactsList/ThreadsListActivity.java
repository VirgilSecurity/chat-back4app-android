package com.android.virgilsecurity.virgilback4app.chat.contactsList;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.TextView;

import com.android.virgilsecurity.virgilback4app.AppVirgil;
import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.auth.SignInControlActivity;
import com.android.virgilsecurity.virgilback4app.base.BaseActivityWithPresenter;
import com.android.virgilsecurity.virgilback4app.chat.thread.ChatThreadActivity;
import com.android.virgilsecurity.virgilback4app.model.ChatThread;
import com.android.virgilsecurity.virgilback4app.util.Const;
import com.android.virgilsecurity.virgilback4app.util.PrefsManager;
import com.android.virgilsecurity.virgilback4app.util.Utils;
import com.android.virgilsecurity.virgilback4app.util.VirgilHelper;
import com.android.virgilsecurity.virgilback4app.util.customElements.CreateThreadDialog;
import com.android.virgilsecurity.virgilback4app.util.customElements.OnFinishTimer;
import com.parse.ParseUser;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import nucleus5.factory.RequiresPresenter;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

@RequiresPresenter(ThreadsListActivityPresenter.class)
public class ThreadsListActivity extends BaseActivityWithPresenter<ThreadsListActivityPresenter>
        implements ThreadsListFragment.OnStartThreadListener {

    private static final String THREADS_FRAGMENT = "THREADS_FRAGMENT";

    private CreateThreadDialog createThreadDialog;
    private ParseUser newThreadUser;
    private boolean secondPress;

    @Inject VirgilHelper virgilHelper;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.nvNavigation)
    protected NavigationView nvNavigation;
    @BindView(R.id.dlDrawer)
    protected DrawerLayout dlDrawer;

    public static void start(AppCompatActivity from) {
        from.startActivity(new Intent(from, ThreadsListActivity.class));
    }

    public static void startWithFinish(AppCompatActivity from) {
        from.startActivity(new Intent(from, ThreadsListActivity.class));
        from.finish();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_contacts;
    }

    @Override
    protected void postButterInit() {
        AppVirgil.getVirgilComponent().inject(this);
        initToolbar(toolbar, getString(R.string.contacts));
        initDrawer();
        Utils.replaceFragmentNoBackStack(getSupportFragmentManager(),
                                         R.id.flContainer,
                                         ThreadsListFragment.newInstance(),
                                         THREADS_FRAGMENT);
        hideKeyboard();
        showHamburger(true, view -> {
            if (!dlDrawer.isDrawerOpen(Gravity.START))
                dlDrawer.openDrawer(Gravity.START);
            else
                dlDrawer.closeDrawer(Gravity.START);
        });
    }

    private void initDrawer() {
        TextView tvUsernameDrawer =
                nvNavigation.getHeaderView(0).findViewById(R.id.tvUsernameDrawer);
        tvUsernameDrawer.setText(ParseUser.getCurrentUser().getUsername());

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        nvNavigation.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemNewChat:
                    dlDrawer.closeDrawer(Gravity.START);
                    createThreadDialog =
                            new CreateThreadDialog(this, R.style.NotTransBtnsDialogTheme,
                                                   getString(R.string.create_thread),
                                                   getString(R.string.enter_username));

                    createThreadDialog.setOnCreateThreadDialogListener((username -> {
                        if (ParseUser.getCurrentUser().getUsername().equals(username)) {
                            Utils.toast(this, R.string.no_chat_with_yourself);
                        }
                        else {
                            createThreadDialog.showProgress(true);
                            getPresenter().requestUser(username);
                        }
                    }));

                    createThreadDialog.show();

                    return true;
                case R.id.itemLogOut:
                    dlDrawer.closeDrawer(Gravity.START);
                    getPresenter().disposeAll();
                    showBaseLoading(true);
                    ParseUser.logOutInBackground(e -> {
                        runOnUiThread(() -> showBaseLoading(false));
                        if (e == null) {
                            PrefsManager.VirgilPreferences.clearCardModel();
                            virgilHelper.clearAfterLogout();
                            SignInControlActivity.startClearTop(this);
                        } else {
                            Utils.toast(this, Utils.resolveError(e));
                        }
                    });
                    return true;
                default:
                    return false;
            }
        });
    }

    @Override public void onStartThread(ChatThread thread) {
        ChatThreadActivity.start(this, thread);
    }

    public void onGetUserSuccess(ParseUser user) {
        if (user != null) {
            newThreadUser = user;
            getPresenter().requestThreads(ParseUser.getCurrentUser(),
                                          1000,
                                          0,
                                          Const.TableNames.CREATED_AT_CRITERIA);
        } else {
            createThreadDialog.dismiss();
        }
    }

    public void onGetUserError(Throwable t) {
        createThreadDialog.showProgress(false);
        Utils.toast(this, Utils.resolveError(t));
    }

    public void onGetThreadsSuccess(@NonNull List<ChatThread> threads) {
        boolean threadExists = false;
        ChatThread chatThread = null;

        for (ChatThread thread : threads) {
            if (thread.getSenderUsername().equals(newThreadUser.getUsername())
                    || thread.getRecipientUsername().equals(newThreadUser.getUsername())) {
                threadExists = true;
                chatThread = thread;
            }
        }

        if (!threadExists) {
            getPresenter().requestCreateThread(ParseUser.getCurrentUser(), newThreadUser);
        } else {
            createThreadDialog.dismiss();
            ChatThreadActivity.start(this, chatThread);
        }
    }

    public void onGetThreadsError(Throwable t) {
        createThreadDialog.dismiss();
        Utils.toast(this, Utils.resolveError(t));
    }

    public void onCreateThreadSuccess(Object o) {
        getPresenter().requestThreads(ParseUser.getCurrentUser(),
                                      1000,
                                      0,
                                      Const.TableNames.CREATED_AT_CRITERIA);
    }

    public void onCreateThreadError(Throwable t) {
        createThreadDialog.dismiss();
        Utils.toast(this, Utils.resolveError(t));
    }

    @Override public void onBackPressed() {

        if (secondPress)
            super.onBackPressed();
        else
            Utils.toast(this, getString(R.string.press_exit_once_more));

        secondPress = true;

        new OnFinishTimer(2000, 100) {

            @Override public void onFinish() {
                secondPress = false;
            }
        }.start();
    }
}
