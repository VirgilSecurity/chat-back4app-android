package com.android.virgilsecurity.virgilback4app.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class UsernameInputFilter implements InputFilter {

    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {

        final String constraint = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,-()/='+:?!%&*<>;{}@#_";

        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            if (constraint.contains(String.valueOf(c))) {
                builder.append(c);
            }
        }

        boolean allCharactersValid = (builder.length() == end - start);
        return allCharactersValid ? null : builder.toString();
    }
}