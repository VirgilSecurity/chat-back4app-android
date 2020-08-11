package com.android.virgilsecurity.virgilback4app.base

import android.content.Context
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.virgilsecurity.virgilback4app.R
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Created by Danylo Oliinyk on 16.11.17 at Virgil Security.
 * -__o
 */

abstract class BaseActivity : AppCompatActivity() {

    private var tvToolbarTitle: TextView? = null
    private var ibToolbarBack: View? = null
    private var ibToolbarHamburger: View? = null
    private var toolbar: Toolbar? = null
    private var llBaseLoadingTextNoNetwork: View? = null
    private var llBaseLoading: View? = null

    protected abstract val layout: Int
    private var disposable: Disposable? = null

    protected abstract fun postCreateInit()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = layoutInflater
        val baseView = inflater.inflate(R.layout.activity_base, null)

        val flBaseContainer = baseView.findViewById<FrameLayout>(R.id.flBaseContainer)
        llBaseLoading = baseView.findViewById(R.id.llBaseLoading)
        llBaseLoadingTextNoNetwork = baseView.findViewById(R.id.tvBaseNoNetwork)

        val childView = inflater.inflate(layout, null)
        flBaseContainer.removeAllViews()
        flBaseContainer.addView(childView)

        setContentView(baseView)

        postCreateInit()
    }

    protected fun changeToolbarTitle(titlePage: String) {
        if (toolbar != null) {
            tvToolbarTitle!!.text = titlePage
        } else {
            throw NullPointerException("Init Toolbar first")
        }
    }

    protected fun initToolbar(toolbar: Toolbar, titlePage: String) {
        this.toolbar = toolbar
        this.tvToolbarTitle = toolbar.findViewById(R.id.tvToolbarTitle)
        this.ibToolbarBack = toolbar.findViewById(R.id.ibToolbarBack)
        this.ibToolbarHamburger = toolbar.findViewById(R.id.ibToolbarHamburger)

        setSupportActionBar(toolbar)

        tvToolbarTitle!!.text = titlePage

        if (supportActionBar != null)
            supportActionBar!!.title = ""
    }

    protected fun showBackButton(show: Boolean, listener: View.OnClickListener?) {
        if (show) {
            ibToolbarBack!!.visibility = View.VISIBLE
            ibToolbarBack!!.setOnClickListener(listener)
        } else {
            ibToolbarBack!!.visibility = View.INVISIBLE
        }
    }

    protected fun showHamburger(show: Boolean, listener: View.OnClickListener?) {
        if (show) {
            ibToolbarHamburger!!.visibility = View.VISIBLE
            ibToolbarHamburger!!.setOnClickListener(listener)
        } else {
            ibToolbarHamburger!!.visibility = View.INVISIBLE
        }
    }

    protected fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onResume() {
        super.onResume()

        disposable = ReactiveNetwork.observeNetworkConnectivity(applicationContext)
                .debounce(1000, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { connectivity -> showNoNetwork(connectivity.state != NetworkInfo.State.CONNECTED) }
    }

    override fun onPause() {
        super.onPause()

        disposable?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun showNoNetwork(show: Boolean) {
        showBaseLoading(show)
        llBaseLoadingTextNoNetwork!!.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun showBaseLoading(show: Boolean) {
        llBaseLoading!!.visibility = if (show) View.VISIBLE else View.GONE
        llBaseLoading!!.requestFocus()
        hideKeyboard()
    }
}
