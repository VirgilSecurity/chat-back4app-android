package com.android.virgilsecurity.virgilback4app.util;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.virgilsecurity.virgilback4app.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.virgilsecurity.sdk.highlevel.VirgilBuffer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.HttpException;

/**
 * Created by Danylo Oliinyk on 11/17/17 at Virgil Security.
 * -__o
 */

public class Utils {

    public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Fragment fragment, String text) {
        Toast.makeText(fragment.getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, int stringResId) {
        Toast.makeText(context,
                       context.getString(stringResId),
                       Toast.LENGTH_SHORT).show();
    }

    public static void toast(Fragment fragment, int stringResId) {
        Toast.makeText(fragment.getActivity(),
                       fragment.getActivity().getString(stringResId),
                       Toast.LENGTH_SHORT).show();
    }

    public static void log(String tag, String text) {
        Log.d(tag, text);
    }

    public static void replaceFragmentNoTag(FragmentManager fm, int containerId, Fragment fragment) {
        fm.beginTransaction()
          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
          .replace(containerId, fragment)
          .commit();
    }

    public static void replaceFragmentNoBackStack(FragmentManager fm, int containerId, Fragment fragment, String tag) {
        fm.beginTransaction()
          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
          .replace(containerId, fragment, tag)
          .commit();
    }

    public static void replaceFragment(FragmentManager fm, int containerId, Fragment fragment, String tag) {
        fm.beginTransaction()
          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
          .replace(containerId, fragment, tag)
          .addToBackStack(tag)
          .commit();
    }

    /*
     * Generates SHA-256 password from base64 string
     *
     */
    public static String generatePassword(VirgilBuffer virgilBuffer) {
        MessageDigest sha;
        byte[] hash = new byte[0];

        try {
            sha = MessageDigest.getInstance("SHA-256");
            hash = sha.digest(virgilBuffer.toString().getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(hash, Base64.DEFAULT);
    }

    public static String resolveError(Throwable t) {
        if (t instanceof HttpException) {
            HttpException exception = (HttpException) t;

            switch (exception.code()) {
                case Const.Http.BAD_REQUEST:
                    return "Bad Request";
                case Const.Http.UNAUTHORIZED:
                    return "Unauthorized";
                case Const.Http.FORBIDDEN:
                    return "Forbidden";
                case Const.Http.NOT_ACCEPTABLE:
                    return "Not acceptable";
                case Const.Http.UNPROCESSABLE_ENTITY:
                    return "Unprocessable entity";
                case Const.Http.SERVER_ERROR:
                    return "Server error";
                default:
                    return "Oops.. Something went wrong ):";
            }
        } else if (t instanceof ParseException) {
            ParseException exception = (ParseException) t;

            switch (exception.getCode()) {
                case ParseException.USERNAME_TAKEN:
                    return "Username is already registered.\nPlease, try another one.";
                case ParseException.OBJECT_NOT_FOUND:
                    return "Username is not registered yet";
                case 60042: // Custom exception in RxParse.class
                    return exception.getMessage();
                default:
                    return "Oops.. Something went wrong ):";
            }
        } else {
            return "Something went wrong";
        }
    }

    public static boolean validateUi(TextInputLayout til) {
        final String text = til.getEditText().getText().toString();
        Context context = til.getContext();

        if (text.isEmpty()) {
            til.setError(context.getString(R.string.username_empty));
            return false;
        } else {
            return true;
        }
    }

    public static boolean validateUi(EditText et) {
        final String text = et.getText().toString();
        Context context = et.getContext();

        if (text.isEmpty()) {
            et.setError(context.getString(R.string.username_empty));
            return false;
        } else {
            return true;
        }
    }

    public static void testParse() {
        ParseObject object = new ParseObject("GoodObject");
        object.put("keeeeyyyy", "valllllue");
        object.saveInBackground(e -> {
            if (e == null) {
                Utils.log("Log", "dog");
            } else {
                Utils.log("Log", "cat");
            }
        });
    }
}
