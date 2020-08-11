package com.android.virgilsecurity.virgilback4app.util

import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

import com.android.virgilsecurity.virgilback4app.R
import com.google.android.material.textfield.TextInputLayout
import com.parse.ParseException
import com.virgilsecurity.sdk.client.exceptions.VirgilCardIsNotFoundException
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsAlreadyExistsException
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsNotFoundException
import com.virgilsecurity.sdk.crypto.exceptions.KeyEntryNotFoundException

import retrofit2.HttpException

/**
 * Created by Danylo Oliinyk on 11/17/17 at Virgil Security.
 * -__o
 */

object Utils {

    fun toast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun toast(fragment: Fragment, text: String) {
        Toast.makeText(fragment.activity, text, Toast.LENGTH_SHORT).show()
    }

    fun toast(context: Context, stringResId: Int) {
        Toast.makeText(context,
                       context.getString(stringResId),
                       Toast.LENGTH_SHORT).show()
    }

    fun toast(fragment: Fragment, stringResId: Int) {
        Toast.makeText(fragment.activity,
                       fragment.activity!!.getString(stringResId),
                       Toast.LENGTH_SHORT).show()
    }

    fun log(tag: String, text: String) {
        Log.d(tag, text)
    }

    fun replaceFragmentNoTag(fm: FragmentManager, containerId: Int, fragment: Fragment) {
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(containerId, fragment)
                .commit()
    }

    fun replaceFragmentNoBackStack(fm: FragmentManager,
                                   containerId: Int,
                                   fragment: Fragment,
                                   tag: String) {
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(containerId, fragment, tag)
                .commit()
    }

    fun replaceFragment(fm: FragmentManager, containerId: Int, fragment: Fragment, tag: String) {
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(containerId, fragment, tag)
                .addToBackStack(tag)
                .commit()
    }

    fun resolveError(t: Throwable): String {
        Log.e("Utils", "resolving error", t)
        return when (t) {
            is HttpException -> when (t.code()) {
                Const.Http.BAD_REQUEST -> "Bad Request"
                Const.Http.UNAUTHORIZED -> "Unauthorized"
                Const.Http.FORBIDDEN -> "Forbidden"
                Const.Http.NOT_ACCEPTABLE -> "Not acceptable"
                Const.Http.UNPROCESSABLE_ENTITY -> "Unprocessable entity"
                Const.Http.SERVER_ERROR -> "Server error"
                else -> "Oops.. Something went wrong ):"
            }
            is ParseException -> when (t.code) {
                ParseException.USERNAME_TAKEN -> "Username is already registered.\nPlease, try another one. (Parse)"
                ParseException.OBJECT_NOT_FOUND -> "Username is not registered yet"
                60042 -> t.message ?: "No exception message" // Custom exception in RxParse.class
                else -> "Oops.. Something went wrong ):"
            }
            is VirgilKeyIsNotFoundException -> "Username is not registered yet"
            is VirgilKeyIsAlreadyExistsException -> "Username is already registered. Please, try another one."
            is KeyEntryNotFoundException -> "Username is not found on this device. Maybe you deleted your private key"
            is VirgilCardIsNotFoundException -> "Virgil Card is not found.\nYou can not start chat with user without Virgil Card."
            else -> "Something went wrong"
        }
    }

    fun validateUi(til: TextInputLayout): Boolean {
        val text = til.editText!!.text.toString()
        val context = til.context

        return if (text.isEmpty()) {
            til.error = context.getString(R.string.username_empty)
            false
        } else {
            true
        }
    }

    fun validateUi(et: EditText): Boolean {
        val text = et.text.toString()
        val context = et.context

        return if (text.isEmpty()) {
            et.error = context.getString(R.string.username_empty)
            false
        } else {
            true
        }
    }
}
