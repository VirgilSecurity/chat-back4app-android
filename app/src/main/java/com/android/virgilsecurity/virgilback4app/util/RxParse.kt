package com.android.virgilsecurity.virgilback4app.util


import com.android.virgilsecurity.virgilback4app.model.ChatThread
import com.android.virgilsecurity.virgilback4app.model.Message
import com.parse.ParseACL
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 * Created by Danylo Oliinyk on 11/23/17 at Virgil Security.
 * -__o
 */

object RxParse {

    fun signUp(username: String, password: String) = Completable.create { e ->
        val user = ParseUser()
        user.username = username
        user.setPassword(password)

        user.signUpInBackground { exception ->
            if (exception == null) {
                e.onComplete()
            } else {
                e.onError(exception)
            }
        }
    }

    fun logIn(username: String, password: String) = Single.create<ParseUser> { e ->
        ParseUser.logInInBackground(username, password) { user, exception ->
            if (user != null) {
                e.onSuccess(user)
            } else {
                e.onError(exception)
            }
        }
    }

    fun getUsers(limit: Int,
                 page: Int,
                 sortCriteria: String) = Single.create<List<ParseUser>> { e ->
        val parseQuery = ParseQuery.getQuery<ParseUser>(ParseUser::class.java)
        parseQuery.limit = limit
        parseQuery.skip = limit * page
        parseQuery.orderByDescending(sortCriteria)

        parseQuery.findInBackground { users, exception ->
            if (users != null) {
                e.onSuccess(users)
            } else {
                e.onError(exception)
            }
        }
    }

    fun getMyThreads(user: ParseUser,
                     limit: Int,
                     page: Int,
                     sortCriteria: String) = Single.create<List<ChatThread>> { e ->
        val queryMeSender = ParseQuery.getQuery<ChatThread>(ChatThread::class.java)
        queryMeSender.whereEqualTo(Const.TableNames.SENDER_ID, user.objectId)

        val queryMeRecipient = ParseQuery.getQuery<ChatThread>(ChatThread::class.java)
        queryMeRecipient.whereEqualTo(Const.TableNames.RECIPIENT_ID, user.objectId)

        val queryResult = ParseQuery.or(Arrays.asList<ParseQuery<ChatThread>>(queryMeSender,
                                                                              queryMeRecipient))

        queryResult.limit = limit
        queryResult.skip = limit * page
        queryResult.orderByDescending(sortCriteria)

        queryResult.findInBackground { users, exception ->
            if (users != null) {
                e.onSuccess(users)
            } else {
                e.onError(exception)
            }
        }
    }

    fun getUsersByIds(userIds: List<String>,
                      limit: Int,
                      page: Int,
                      sortCriteria: String) = Single.create<List<ParseUser>> { e ->
        val parseQuery = ParseQuery.getQuery<ParseUser>(ParseUser::class.java)
        parseQuery.whereContainedIn(Const.TableNames.OBJECT_ID, userIds)
        parseQuery.limit = limit
        parseQuery.skip = limit * page
        parseQuery.orderByDescending(sortCriteria)

        parseQuery.findInBackground { users, exception ->
            if (users != null) {
                e.onSuccess(users)
            } else {
                e.onError(exception)
            }
        }
    }

    fun getUsersByIds(userIds: List<String>) = Single.create<List<ParseUser>> { e ->
        val parseQuery = ParseQuery.getQuery<ParseUser>(ParseUser::class.java)
        parseQuery.whereContainedIn(Const.TableNames.OBJECT_ID, userIds)

        parseQuery.findInBackground { users, exception ->
            if (users != null) {
                e.onSuccess(users)
            } else {
                e.onError(exception)
            }
        }
    }

    fun sendMessage(text: String, thread: ChatThread): Completable {
        return Completable.create { e ->
            val message = Message()
            message.senderId = ParseUser.getCurrentUser().objectId
            message.body = text
            message.threadId = thread.objectId
            message.timestamp = Date().toString()

            val recipientId: String = if (thread.senderId == ParseUser.getCurrentUser().objectId)
                thread.recipientId
            else
                thread.senderId

            val parseACL = ParseACL()
            parseACL.publicReadAccess = false
            parseACL.publicWriteAccess = false
            parseACL.setReadAccess(ParseUser.getCurrentUser(), true)
            parseACL.setReadAccess(recipientId, true)

            message.acl = parseACL

            message.saveInBackground { exception ->
                if (exception == null) {
                    e.onComplete()
                } else {
                    e.onError(exception)
                }
            }
        }
    }

    fun getMessages(thread: ChatThread,
                    limit: Int,
                    page: Int,
                    sortCriteria: String) = Single.create<List<Message>> { e ->
        val query = ParseQuery.getQuery<Message>(Message::class.java)
        query.whereEqualTo(Const.TableNames.THREAD_ID, thread.objectId)
        query.limit = limit
        query.skip = page * limit
        query.orderByDescending(sortCriteria)

        query.findInBackground { messages, exception ->
            if (exception == null) {
                e.onSuccess(messages)
            } else {
                e.onError(exception)
            }
        }
    }

    fun createThread(sender: ParseUser, recipient: ParseUser) = Completable.create { e ->
        val thread = ChatThread()
        thread.senderUsername = sender.username
        thread.senderId = sender.objectId

        thread.recipientUsername = recipient.username
        thread.recipientId = recipient.objectId

        thread.createdAtDate = Date().toString()

        val parseACL = ParseACL()
        parseACL.publicReadAccess = false
        parseACL.publicWriteAccess = false
        parseACL.setReadAccess(sender, true)
        parseACL.setReadAccess(recipient, true)

        thread.acl = parseACL

        thread.saveInBackground { exception ->
            if (exception == null) {
                e.onComplete()
            } else {
                e.onError(exception)
            }

        }
    }

    fun getUserByName(username: String) = Single.create<ParseUser> { e ->
        val parseQuery = ParseQuery.getQuery<ParseUser>(ParseUser::class.java)
        parseQuery.whereEqualTo(Const.TableNames.USER_NAME, username)

        parseQuery.findInBackground { users, exception ->
            if (users != null) {
                if (users.size != 0) {
                    e.onSuccess(users[0])
                } else {
                    e.onError(ParseException(60042, "Username was not found"))
                }
            } else {
                e.onError(exception)
            }
        }
    }
}
