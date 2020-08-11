package com.android.virgilsecurity.virgilback4app.util.customElements

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.transition.TransitionManager
import com.android.virgilsecurity.virgilback4app.R
import com.android.virgilsecurity.virgilback4app.util.UsernameInputFilter
import com.android.virgilsecurity.virgilback4app.util.Utils
import kotlinx.android.synthetic.main.dialog_create_thread.*

/**
 * Created by Danylo Oliinyk on 11/26/17 at Virgil Security.
 * -__o
 */

class CreateThreadDialog(
        context: Context,
        themeResId: Int,
        private val title: String?,
        message: String
) : Dialog(context, themeResId) {

    private var onCreateThreadDialogListener: OnCreateThreadDialogListener? = null
    private var message: String? = message

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_create_thread)
        setCancelable(true)

        etUsername.filters = arrayOf<InputFilter>(UsernameInputFilter())
        etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                etUsername.error = null
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        etUsername.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }

        if (title != null)
            tvTitle.text = title

        tvMessage.text = message

        btnCancel.setOnClickListener {
            cancel()
        }

        btnOk.setOnClickListener {
            if (Utils.validateUi(etUsername)) {
                onCreateThreadDialogListener!!.onCreateThread(etUsername!!.text.toString())
            }
        }
    }

    fun setOnCreateThreadDialogListener(onCreateThreadDialogListener: OnCreateThreadDialogListener) {
        this.onCreateThreadDialogListener = onCreateThreadDialogListener
    }

    fun showProgress(show: Boolean) {
        if (show) {
            setCancelable(false)
            llContentRoot!!.visibility = View.GONE
            TransitionManager.beginDelayedTransition(flRoot!!)
            llLoadingRoot!!.visibility = View.VISIBLE
        } else {
            object : OnFinishTimer(1000, 100) {
                override fun onFinish() {
                    setCancelable(true)
                    llLoadingRoot!!.visibility = View.GONE
                    TransitionManager.beginDelayedTransition(flRoot!!)
                    llContentRoot!!.visibility = View.VISIBLE
                }
            }.start()
        }
    }

    interface OnCreateThreadDialogListener {
        fun onCreateThread(username: String)
    }
}
