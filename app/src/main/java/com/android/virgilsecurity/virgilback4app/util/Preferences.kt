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

    fun setVirgilToken(virgilToken: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_VIRGIL_TOKEN, virgilToken)
            apply()
        }
    }

    fun virgilToken(): String? {
        with(sharedPreferences) {
            return getString(KEY_VIRGIL_TOKEN, null)
        }
    }

    fun clearVirgilToken() {
        with(sharedPreferences.edit()) {
            remove(KEY_VIRGIL_TOKEN)
            apply()
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "ethree_back4app_prefs"

        private const val KEY_VIRGIL_TOKEN = "KEY_VIRGIL_TOKEN"

        @Volatile
        private var INSTANCE: Preferences? = null

        fun instance(context: Context): Preferences = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Preferences(context).also { INSTANCE = it }
        }
    }
}