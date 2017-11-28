package com.android.virgilsecurity.virgilback4app.util;


import com.android.virgilsecurity.virgilback4app.model.ChatThread;
import com.android.virgilsecurity.virgilback4app.model.Message;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Danylo Oliinyk on 11/23/17 at Virgil Security.
 * -__o
 */

public class RxParse {

    public static Observable<Object> register(String username, String password, String csr) {
        return Observable.create(e -> {
            final ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            user.put(Const.Request.CRETE_CARD, csr);

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

    public static Observable<List<ParseUser>> getUsers(int limit, int page, String sortCriteria) {
        return Observable.create(e -> {
            final ParseQuery<ParseUser> parseQuery = ParseQuery.getQuery(ParseUser.class);
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

    public static Observable<List<ChatThread>> getMyThreads(ParseUser user, int limit, int page, String sortCriteria) {
        return Observable.create(e -> {
            final ParseQuery<ChatThread> queryMeSender =
                    ParseQuery.getQuery(ChatThread.class);
            queryMeSender.whereEqualTo(Const.TableNames.SENDER_ID, user.getObjectId());

            final ParseQuery<ChatThread> queryMeRecipient =
                    ParseQuery.getQuery(ChatThread.class);
            queryMeRecipient.whereEqualTo(Const.TableNames.RECIPIENT_ID, user.getObjectId());

            final ParseQuery<ChatThread> queryResult =
                    ParseQuery.or(Arrays.asList(queryMeSender, queryMeRecipient));

            queryResult.setLimit(limit);
            queryResult.setSkip(limit * page);
            queryResult.orderByDescending(sortCriteria);

            queryResult.findInBackground((users, exception) -> {
                if (users != null) {
                    e.onNext(users);
                    e.onComplete();
                } else {
                    e.onError(exception);
                }
            });
        });
    }

    public static Observable<List<ParseUser>> getUsersByIds(List<String> userIds,
                                                            int limit,
                                                            int page,
                                                            String sortCriteria) {
        return Observable.create(e -> {
            final ParseQuery<ParseUser> parseQuery = ParseQuery.getQuery(ParseUser.class);
            parseQuery.whereContainedIn(Const.TableNames.OBJECT_ID, userIds);
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

    public static Observable<List<ParseUser>> getUsersByIds(List<String> userIds) {
        return Observable.create(e -> {
            final ParseQuery<ParseUser> parseQuery = ParseQuery.getQuery(ParseUser.class);
            parseQuery.whereContainedIn(Const.TableNames.OBJECT_ID, userIds);

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

    public static Observable<Object> sendMessage(String text, ChatThread thread) {
        return Observable.create(e -> {
            final Message message = new Message();
            message.setSenderId(ParseUser.getCurrentUser().getObjectId());
            message.setBody(text);
            message.setThreadId(thread.getObjectId());
            message.setTimestamp(new Date().toString());

            String recipientId;
            if (thread.getSenderId().equals(ParseUser.getCurrentUser().getObjectId()))
                recipientId = thread.getRecipientId();
            else
                recipientId = thread.getSenderId();

            ParseACL parseACL = new ParseACL();
            parseACL.setPublicReadAccess(false);
            parseACL.setPublicWriteAccess(false);
            parseACL.setReadAccess(ParseUser.getCurrentUser(), true);
            parseACL.setReadAccess(recipientId, true);

            message.setACL(parseACL);

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

    public static Observable<List<Message>> getMessages(ChatThread thread,
                                                        int limit,
                                                        int page,
                                                        String sortCriteria) {
        return Observable.create(e -> {
            final ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
            query.whereEqualTo(Const.TableNames.THREAD_ID, thread.getObjectId());
            query.setLimit(limit);
            query.setSkip(page * limit);
            query.orderByDescending(sortCriteria);

            query.findInBackground((messages, exception) -> {
                if (exception == null) {
                    e.onNext(messages);
                    e.onComplete();
                } else {
                    e.onError(exception);
                }
            });
        });
    }

    public static Observable<Object> createThread(ParseUser sender, ParseUser recipient) {
        return Observable.create(e -> {
            final ChatThread thread = new ChatThread();
            thread.setSenderUsername(sender.getUsername());
            thread.setSenderId(sender.getObjectId());

            thread.setRecipientUsername(recipient.getUsername());
            thread.setRecipientId(recipient.getObjectId());

            thread.setCreatedAtDate(new Date().toString());

            ParseACL parseACL = new ParseACL();
            parseACL.setPublicReadAccess(false);
            parseACL.setPublicWriteAccess(false);
            parseACL.setReadAccess(sender, true);
            parseACL.setReadAccess(recipient, true);

            thread.setACL(parseACL);

            thread.saveInBackground((exception) -> {
                if (exception == null) {
                    e.onNext(new Object());
                    e.onComplete();
                } else {
                    e.onError(exception);
                }

            });
        });
    }

    public static Observable<ParseUser> getUserByName(String username) {
        return Observable.create(e -> {
            final ParseQuery<ParseUser> parseQuery = ParseQuery.getQuery(ParseUser.class);
            parseQuery.whereEqualTo(Const.TableNames.USER_NAME, username);

            parseQuery.findInBackground((users, exception) -> {
                if (users != null) {
                    if (users.size() != 0) {
                        e.onNext(users.get(0));
                        e.onComplete();
                    } else {
                        e.onError(new ParseException(60042, "Username was not found"));
                    }
                } else {
                    e.onError(exception);
                }
            });
        });
    }
}
