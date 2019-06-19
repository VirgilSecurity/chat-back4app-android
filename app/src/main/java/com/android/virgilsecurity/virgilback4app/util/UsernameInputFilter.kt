package com.android.virgilsecurity.virgilback4app.util

import android.text.InputFilter
import android.text.Spanned

class UsernameInputFilter : InputFilter {

    override fun filter(source: CharSequence, start: Int, end: Int,
                        dest: Spanned, dstart: Int, dend: Int): CharSequence? {

        val constraint = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,-()/='+:?!%&*<>;{}@#_"

        val builder = StringBuilder()
        for (i in start until end) {
            val c = source[i]
            if (constraint.contains(c.toString())) {
                builder.append(c)
            }
        }

        val allCharactersValid = builder.length == end - start
        return if (allCharactersValid) null else builder.toString()
    }
}