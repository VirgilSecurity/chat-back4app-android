package com.android.virgilsecurity.virgilback4app.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Preferences
 */
class Preferences(context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun setAuthToken(authToken: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_AUTH_TOKEN, authToken)
            apply()
        }
    }

    fun authToken(): String? {
        with(sharedPreferences) {
            return getString(KEY_AUTH_TOKEN, null)
        }
    }

    fun clearAuthToken() {
        with(sharedPreferences.edit()) {
            remove(KEY_AUTH_TOKEN)
            apply()
        }
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
        private const val PREFERENCES_NAME = "ethree_nexmo_prefs"

        private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
        private const val KEY_VIRGIL_TOKEN = "KEY_VIRGIL_TOKEN"

        @Volatile
        private var INSTANCE: Preferences? = null

        fun instance(context: Context): Preferences = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Preferences(context).also { INSTANCE = it }
        }
    }
}