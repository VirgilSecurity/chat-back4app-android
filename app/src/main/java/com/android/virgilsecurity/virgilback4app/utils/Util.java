package com.android.virgilsecurity.virgilback4app.utils;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.android.virgilsecurity.virgilback4app.R;

/**
 * Created by Danylo Oliinyk on 11/17/17 at Virgil Security.
 * -__o
 */

public class Util {

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

    public static void replaceFragmentNoTag(FragmentManager fm, Fragment fragment) {
        fm.beginTransaction()
          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
          .add(R.id.flContainer, fragment)
          .commit();
    }

    public static void replaceFragmentNoBackStack(FragmentManager fm, Fragment fragment, String tag) {
        fm.beginTransaction()
          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
          .add(R.id.flContainer, fragment, tag)
          .commit();
    }

    public static void replaceFragment(FragmentManager fm, Fragment fragment, String tag) {
        fm.beginTransaction()
          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
          .add(R.id.flContainer, fragment, tag)
          .addToBackStack(tag)
          .commit();
    }
}
