package com.android.virgilsecurity.virgilback4app.auth

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.android.virgilsecurity.virgilback4app.R
import com.android.virgilsecurity.virgilback4app.base.BaseFragment
import com.android.virgilsecurity.virgilback4app.util.UsernameInputFilter
import com.android.virgilsecurity.virgilback4app.util.Utils
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*

/**
 * Created by Danylo Oliinyk on 16.11.17 at Virgil Security.
 * -__o
 */

class LogInFragment : BaseFragment<SignInControlActivity>(), View.OnClickListener {

    private lateinit var authStateListener: AuthStateListener
    private lateinit var presenter: LogInPresenter

    override val layout: Int
        get() = R.layout.fragment_login

    override fun onAttach(context: Context) {
        super.onAttach(context)

        authStateListener = activity
    }

    override fun postCreateInit() {
        etUsername.filters = arrayOf(UsernameInputFilter(), InputFilter.LengthFilter(32))
        etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                tilUserName.error = null
                tilUserName.isErrorEnabled = false
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        presenter = LogInPresenter(activity)

        btnSignIn.setOnClickListener(this)
        btnSignUp.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (!Utils.validateUi(tilUserName))
            return

        val identity = etUsername.text.toString().toLowerCase(Locale.getDefault())

        when (view.id) {
            R.id.btnSignIn -> {
                tilUserName.error = null
                tilUserName.isErrorEnabled = false
                showProgress(true)
                presenter.requestSignIn(identity, ::onLoginSuccess, ::onLoginError)
            }
            R.id.btnSignUp -> {
                tilUserName.error = null
                tilUserName.isErrorEnabled = false
                showProgress(true)
                presenter.requestSignUp(identity, ::onSignUpSuccess, ::onSignUpError)
            }
            else -> {
            }
        }
    }

    private fun onLoginSuccess() {
        showProgress(false)
        authStateListener.onLoggedInSuccesfully()
    }

    private fun onLoginError(throwable: Throwable) {
        showProgress(false)
        Utils.toast(this, Utils.resolveError(throwable))
    }

    private fun onSignUpSuccess() {
        showProgress(false)
        authStateListener.onRegisteredInSuccesfully()
    }

    private fun onSignUpError(throwable: Throwable) {
        showProgress(false)
        Utils.toast(this, Utils.resolveError(throwable))
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            etUsername.isEnabled = false
            btnSignIn.isEnabled = false
            btnSignIn.background = ContextCompat.getDrawable(activity,
                                                             R.drawable.bg_rect_primary_pressed)
            btnSignUp.isEnabled = false
            btnSignUp.background = ContextCompat.getDrawable(activity,
                                                             R.drawable.bg_rect_primary_pressed)

            pbLoading.visibility = View.VISIBLE
        } else {
            etUsername.isEnabled = true
            btnSignIn.isEnabled = true
            btnSignIn.background = ContextCompat.getDrawable(activity, R.drawable.bg_rect_primary)
            btnSignUp.isEnabled = true
            btnSignUp.background = ContextCompat.getDrawable(activity, R.drawable.bg_rect_primary)
            pbLoading.visibility = View.INVISIBLE
        }
    }

    interface AuthStateListener {
        fun onLoggedInSuccesfully()

        fun onRegisteredInSuccesfully()
    }

    companion object {

        fun newInstance() = LogInFragment()
    }
}
