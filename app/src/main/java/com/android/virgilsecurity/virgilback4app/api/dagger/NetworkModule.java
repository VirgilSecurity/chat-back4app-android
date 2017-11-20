package com.android.virgilsecurity.virgilback4app.api.dagger;

import com.android.virgilsecurity.virgilback4app.api.RestApi;
import com.android.virgilsecurity.virgilback4app.api.RetrofitBuilder;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Danylo Oliinyk on 11/17/17 at Virgil Security.
 * -__o
 */

@Module
public class NetworkModule {

    @Provides
    RestApi provideRestApi() {
        RetrofitBuilder retrofitBuilder = new RetrofitBuilder();
        return retrofitBuilder.buildRetrofit().create(RestApi.class);
    }
}
