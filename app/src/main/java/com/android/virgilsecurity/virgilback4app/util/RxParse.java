package com.android.virgilsecurity.virgilback4app.util;


import com.android.virgilsecurity.virgilback4app.model.Message;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Danylo Oliinyk on 11/23/17 at Virgil Security.
 * -__o
 */

public class RxParse {

    public static Observable<Object> register(String username, String password) {
        return Observable.create(e -> {
            final ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);

            user.signUpInBackground((exception) -> {
                if (exception == null) {
                    e.onNext(new Object());
                    e.onComplete();
                } else {
                    e.onError(exception);
                }

            });
        });
    }

    public static Observable<ParseUser> logIn(String username, String password) {
        return Observable.create(e -> {
            ParseUser.logInInBackground(username, password, (user, exception) -> {
                if (user != null) {
                    e.onNext(user);
                    e.onComplete();
                } else {
                    e.onError(exception);
                }
            });
        });
    }

    public static Observable<Object> sendMessage(Message message) {
        return Observable.create(e -> {
            message.saveInBackground((exception) -> {
                if (exception == null) {
                    e.onNext(new Object());
                    e.onComplete();
                } else {
                    e.onError(exception);
                }
            });
        });
    }

//    public static Observable<Object> queryMessages(int limit, long skipStep, int page, String sortCriteria) {
//        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
//        parseQuery.wh
//        return Observable.create(e -> {
//            message.saveInBackground((exception) -> {
//                if (exception == null) {
//                    e.onNext(new Object());
//                    e.onComplete();
//                } else {
//                    e.onError(exception);
//                }
//            });
//        });
//    }

    public static Observable<List<ParseUser>> getUsers(int limit, int page, String sortCriteria) {
        return Observable.create(e -> {
            ParseQuery<ParseUser> parseQuery = ParseQuery.getQuery(ParseUser.class);
            parseQuery.setLimit(limit);
            parseQuery.setSkip(limit * page);
            parseQuery.orderByDescending(sortCriteria);

            parseQuery.findInBackground((users, exception) -> {
                if (users != null) {
                    e.onNext(users);
                    e.onComplete();
                } else {
                    e.onError(exception);
                }
            });
        });
    }
}
