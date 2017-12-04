package com.android.virgilsecurity.virgilback4app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.virgilsecurity.sdk.client.model.CardModel;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

public class PrefsManager {

    private static SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

//    public static class UserPreferences {
//
//        private static final String USER_FACILITY = "USER_FACILITY";
//
//        public static void saveUser(User user) {
//            prefs.edit()
//                 .putString(USER_FACILITY, new Gson().toJson(user))
//                 .apply();
//        }
//
//        public static User getUser() {
//            return new Gson().fromJson(prefs.getString(USER_FACILITY, ""), User.class);
//        }
//
//        public static String getSessionToken() {
//            return getUser().getSessionToken();
//        }
//
//        public static String getAuthData() {
//            return getUser().getAuthData();
//        }
//
//        public static String getUsername() {
//            return getUser().getUsername();
//        }
//
//        public static String getPassword() {
//            return getUser().getPassword();
//        }
//
//        public static String getEmail() {
//            return getUser().getEmail();
//        }
//    }

    public static class VirgilPreferences {
        private static final String VIRGIL_CARD = "VIRGIL_CARD";

        public static void saveCardModel(CardModel cardModel) {
            prefs.edit()
                 .putString(VIRGIL_CARD, new Gson().toJson(cardModel))
                 .apply();
        }

        public static CardModel getCardModel() {
            if (prefs.getString(VIRGIL_CARD, null) == null)
                return null;
            else
                return new Gson().fromJson(prefs.getString(VIRGIL_CARD, null), CardModel.class);
        }

        public static void clearCardModel() {
            prefs.edit()
                 .remove(VIRGIL_CARD)
                 .apply();
        }
    }

}
