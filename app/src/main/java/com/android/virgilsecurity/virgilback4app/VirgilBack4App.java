package com.android.virgilsecurity.virgilback4app;

import android.app.Application;

/**
 * Created by danylooliinyk on 16.11.17.
 */

public class VirgilBack4App extends Application {

//    private static VirgilApi virgilApi;

    @Override
    public void onCreate() {
        super.onCreate();

//        virgilApi = new VirgilApiImpl("AT.28f63856f96c3cbbda4e453e977acbe6c4c9f67681823781b811f36145a004dc");
    }

//    public static VirgilApi getVirgilApi() {
//        if (virgilApi == null) {
//            return virgilApi;
//        } else {
//            throw new ApiNotInitializedException("Please, initialize VirgilApi before you use it");
//        }
//    }
}
