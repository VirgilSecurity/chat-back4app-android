package com.android.virgilsecurity.virgilback4app.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Preferences
 */
class Preferences private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        private const val PREFERENCES_NAME = "ethree_back4app_prefs"

        @Volatile
        private var INSTANCE: Preferences? = null

        fun instance(context: Context): Preferences = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Preferences(context).also { INSTANCE = it }
        }
    }
}
