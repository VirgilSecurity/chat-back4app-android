package com.android.virgilsecurity.virgilback4app.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

/**
 * Created by Danylo Oliinyk on 16.11.17 at Virgil Security.
 * -__o
 */

abstract class BaseFragment<A : Activity> : Fragment() {

    protected lateinit var activity: A

    protected abstract val layout: Int

    protected abstract fun postCreateInit()

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        this.activity = (activity as A?)!!
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        activity = (context as A?)!!
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postCreateInit()
    }

    protected fun hideKeyboard() {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
