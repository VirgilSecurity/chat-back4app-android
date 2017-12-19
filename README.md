# End-to-End Encrypted Chat & the road to HIPAA & GDPR compliance!

**Ahoy Back4app community!**

This is a guest post by Virgil Security: we’re the tech behind [Twilio’s End-to-End Encrypted Messaging][_twilio]. We’ve been asked by our friends @ Back4app to show you how to build an End-to-End encrypted chat app on top of Back4app.

In this post, we’ll walk you through the steps to make [Back4app’s Android Simple Messenger app][_back4app] End-to-End Encrypted! Are you ready? Or if you don’t care about the details, you can simply skip to the end of the post and download the final product.

## What is End-to-End Encryption?

First, let’s start with a quick refresher of what E2EE is and how it works. E2EE is simple: when you type in a chat message, it gets encrypted on your mobile device (or in your browser) and gets decrypted only when your chat partner receives it and wants to display it in chat window.

![Virgil Chat](img/chat_example.png)

So essentially, the message remains encrypted while travels over wifi, the internet, gets on the web server, goes into the database and on the way back to your chat partner. In other words, none of the networks or servers have a clue of what the two of you are chatting about.

![Virgil Chat Server](img/chat_example_server.png)

What’s difficult in End-to-End Encryption is the task of managing the encryption keys: managing them in a way that only the users involved in the chat can access them and nobody else. And when I write “nobody else”, I really mean it: even insiders of your cloud provider or even you, the developer are out; [no accidental mistakes][_mistakes] or legally enforced peeking are possible. Writing crypto, especially for multiple platforms is hard: generating true random numbers, picking the right algorithms, choosing the right encryption modes are just a few examples that make most of us developers just end up NOT doing it.

This blog post is about how to ignore all these annoying details and just End-to-End Encrypt using Virgil’s SDK.


**For an intro, this is how we’ll upgrade Back4app’s messenger app to be End-to-End Encrypted:**
1. During sign-up: we’ll generate individual private & public key for new users (remember: public key encrypts messages, the matching private key decrypts them).
2. You’ll encrypt chat messages with the destination user’s public key before they’re sent,
3. When receiving messages, you’ll decrypt them with your user’s private key.


![Virgil E2EE](img/virgil_main.png)

We’ll publish the users’ public keys to Virgil’s Cards Service for chat users to be able to look up each other and be able to encrypt messages for each other; the private keys will be kept on the user devices.

**Keep it simple**

This is the simplest possible implementation of E2EE chat and it works perfectly for simple chat apps between 2 users where conversations are short-lived and the message history is OK to be lost if a device is lost with the private key on it. For a busier, Slack-like chat app where history is important and users are joining and leaving channels all the time, we’ll build a Part II for this post: [sign up here if you’re interested][_next_post] and we’ll ping you once we have it.

**OK, enough talking! Let’s get down to coding.**

- We’ll start by guiding you through the Android app’s setup,
- Then, we’ll make you add the E2EE code and explain what each code block does.

**Prerequisites:**

- Sign up for a [Back4app account][_back4app_account] and create a new app;
- Sign up for a [Virgil Security account][_virgil_account] (we’ll create the app later)
- You’ll need [Android Studio][_android_studio] for the coding work, we used 3.0.1.

## Let’s set up the Back4app messenger app

### 1) Import Project in Android Studio:
  - File -> New -> Project from Version Control -> Git
  - Git Repository URL: https://github.com/VirgilSecurity/chat-back4app-android
  - Check out the “clean-chat” branch
![Chat](img/checkout_clean_chat_arr.jpg)

### 2) Set up the App with the Credentials from your new Back4App App’s Dashboard:
  - Open “Dashboard” of your app -> “App Settings” -> “Security & Keys”:
  ![Back4app credentials](img/back4app_dashboard.png)
  - Return to your  `/app/src/main/res/values/strings.xml` file in the project and paste your “App Id” into “back4app_app_id” and “Client Key” into “back4app_client_key”.
```xml
<string name="back4app_app_id">your_back4app_app_id</string>
<string name="back4app_client_key">your_back4app_client_key</string>
```

### 3) Enable Live Query to get live updates for messages and chat threads: 
  - Launch Data Management for your app and create two classes: `Message` and `ChatThread`:

    ![Create Class](img/create_class.jpeg)

  - Go back to your [Back4App account][_back4app_admin]
  - Press the “Server Settings” button on your Application
  - Find the “Web Hosting and Live Query” block
  - Open the Live Query Settings  and check the “Activate Hosting” option.
  - Choose a name for your subdomain to activate Live Query for the 2 classes you created: `Message` and `ChatThread`.
  - Copy your new subdomain name and click the SAVE button:
  ![Enablle Live Query](img/live_query.jpeg)

Return to `/app/src/main/res/values/strings.xml` and paste "Subdomain name" you have entered above into the `back4app_live_query_url` instead of "yourSubdomainName":
```xml
<string name="back4app_live_query_url">wss://yourSubdomainName.back4app.io/</string>
```

### Now you can build and run your app on your device or emulator:

![Enablle Live Query](img/emulator.jpeg)

If it all worked out, you should see the chat messenger app popping up. Register two users and send a few messages to each other: you should see new data showing up in the Message class:

**Note that you can see on the server what your users are chatting about:**

![DB](img/back4app_messages_no_encrypt.png)

**Next**: Close your chat interface and move on to the next step – adding E2EE encryption.

## Now, let’s End-to-End Encrypt those messages!

By the end of this part, this is how your chat messages will look like on the server: can you spot the difference?

![DB 2](img/encrypted_db.png)

And this is how we’ll get there:
  - **Step 1:** we’ll set up a minimal Back4App server app that will approve the creation of new users at registration time: otherwise, you’ll end up with a bunch of spam cards. Later, you can introduce an email/SMS verification by customizing this app!
  - **Step 2:** we’ll modify the messenger app by adding E2EE code; I’ll do my best to explain every step along the way, hopefully simply enough that you can continue playing with it and reuse in your own project!

But before we begin, let’s clear 2 important terms for you: what’s a Virgil Key and a Virgil Card?

  - **Virgil Key** – this is how we call a user's private key. Remember, private keys can decrypt data that was encrypted using the matching public key.
  - **Virgil Card** – Virgil Сards carry the user’s public key. Virgil cards are published to Virgil’s Cards Service (imagine this service like a telephone book) for other users to retrieve them: Alice needs to retrieve Bob’s Public Key in order to encrypt a message for Bob using that key. 

### Step 1: Set up your App Server

You’ll need some minimal server code to make the sample work. This piece of server code will enable you to verify new users before they can start using Virgil’s crypto service. To keep this app server simple, we created one for you: it will automatically approve all new users. Later, you can add your own SMS/email verification code, so that you won’t end up with a ton of false users.

**Let’s get started:**
- [Download this archive][_main_js_package_json] that contains two files: `main.js` and `package.json`;
- Extract the archive and open `main.js` with your favorite editor;
- Go back to your [Virgil developer account][_virgil_dashboard] and create a new application. Make sure that you save the Private Key that is generated for your application. Also, copy the new app’s base64-encoded AppKey string before you complete the app creation:
![Encoded string](img/encoded_string.jpeg)
- Edit main.js, find the function `resolveAppKEy()` and replace: 
  - `YOUR_VIRGIL_APP_PRIVATE_KEY` with the Base64 AppKey on your clipboard
  - `YOUR_VIRGIL_APP_PRIVATE_KEY_PASSWORD` with the password you’ve set for your new Virgil app: 
    ```javascript
    function resolveAppKey() {
      try {
        return virgil.crypto.importPrivateKey('MIGhMF0GCSqGSIb3DQEFDTBQMC8GCSqGSIb3DQEFDDAiBBAmU9m+EJOvLRxRaJP6d......',
          'a0KEOifsd2Ean6fzQ'
        );
      } catch (e) {
        return null;
      }
    }
  ```
- Now, go back to your Virgil dashboard and copy your new app’s **App ID**:
![Access Token](img/access_token.jpeg)
- Find the function `signCardRequest` and replace `YOUR_VIRGIL_APP_ID` with the ID on your clipboard:
```javascript
function signCardRequest(cardRequest) {
  const signer = virgil.requestSigner(virgil.crypto);
  signer.authoritySign(cardRequest, 'bd7bf7e832f16e2b3f6fd343s1f90778ab0e15515aa775e7b7db3', appKey);
}
```
- Now, let’s get back to the Virgil dashboard and create a new token for your app:
![Access Token](img/access_token.jpeg)
- Choose any name, leave all settings as-is and generate the token:
 <img src="img/access_token2.jpeg" width="250" height="300">
- As a result, you get the Access Token:
  <img src="img/access_token3.jpeg" width="600" >
- Copy the Token to the clipboard, find the function `signCardRequest(cardRequest, appKey)` and replace `YOUR_VIRGIL_APP_ACCESS_TOKEN` with the token on your clipboard:

```javascript
signCardRequest(cardRequest);
const client = virgil.client('AT.8641c450a983a3435aebe79sad32abea997d29b3e8eed7b35beab72be3');
client.publishCard(cardRequest)
...
```

**Now, back to the Back4app dashboard:**
- Go to your App Dashboard at Back4App website:
  <img src="img/back4app_settings.jpeg" width="600" height="300">
- Open “Server Settings” and find “Cloud Code”:

  <img src="img/cloud_settings.jpeg" width="250" height="300">
- Open Cloud “Settings”
- Upload the main.js and package.json files in your Cloud Code settings and press “save” button:
  <img src="img/back4app_cloud_code.jpeg" width="600" height="300">


### Step 2: Update Android app with E2EE code

**Let’s update your InfoHolder Class:**

- Add new fields:
```java
private VirgilApi virgilApi;
private VirgilApiContext virgilApiContext;
private KeyStorage keyStorage;
 ```
- Update constructor to initialize created fileds:
```java
public InfoHolder(Context context) {
    keyStorage = new VirgilKeyStorage(context.getFilesDir().getAbsolutePath());

    virgilApiContext = new VirgilApiContext(context.getString(R.string.virgil_token));
    virgilApiContext.setKeyStorage(keyStorage);

    virgilApi = new VirgilApiImpl(virgilApiContext);
}
```
- Add getters for new fields, so you will be able to access it all-through the application:
```java
public VirgilApi getVirgilApi() {
    return virgilApi;
}

public VirgilApiContext getVirgilApiContext() {
    return virgilApiContext;
}

public KeyStorage getKeyStorage() {
    return keyStorage;
}
```
**Add Virgil’s Android SDK to your project:**

- In the app-level gradle at /app/[build.gradle][_build.gradle_app_level]:
```gradle
implementation "com.virgilsecurity.sdk:crypto-android:$rootProject.ext.virgilSecurity"
implementation "com.virgilsecurity.sdk:sdk-android:$rootProject.ext.virgilSecurity"
```

- Add the following to the end of your project-level /[build.gradle][_build.gradle_project_level]:
```gradle
virgilSecurity = "4.5.0@aar"
```

**Enter your access token:**

- Open /app/src/main/res/values/strings.xml and add copy & paste your Virgil token and App ID from the Virgil app dashboard:
```xml
<string name="virgil_token">your_virgil_token</string>
<string name="virgil_app_id">your_virgil_app_id</string>
```
Note: for simplicity, we re-used the access token you created for the server app. Don’t do this in production: create a separate token for your mobile app with Search-only permissions!


We're ready to use Virgil SDK now!



**For every chat user, the new E2EE app maintains a private & public key:**

1. We generate the private & public key pair as part of signup
2. Store the private Key in the key storage on the device
3. Publish the public key in Virgil’s Card Service as a “Virgil Card” for other users to download & encrypt messages with it.



#### 1. Generate Private Key & Public Key then store Private Key

Let’s make some updates to ../virgilsecurity/virgilback4app/auth/LogInPresenter class.

- Add `VirgilCard` and `VirgilKey` fields:
```java
private VirgilCard virgilCard;
private VirgilKey virgilKey;
```
- Update `signUp` method passing `virgilCard` as argument, so it will looks like:
```java
restartableFirst(SIGN_UP, () ->
                         RxParse.signUp(identity,
                                        password,
                                        virgilCard)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()),
                 LogInFragment::onSignUpSuccess,
                 LogInFragment::onSignUpError
);
```
- Add method where we will generate private key and public key (for decrypting incoming chat messages) then saving it ../virgilsecurity/virgilback4app/auth/LogInPresenter:
```java
private void generateKeyPair(String identity) {
    virgilKey = infoHolder.getVirgilApi().getKeys().generate();
    try {
        virgilKey.save(identity);
    } catch (VirgilKeyIsAlreadyExistsException e) {
        e.printStackTrace();
    }
    virgilCard = infoHolder.getVirgilApi().getCards().create(identity, virgilKey);
}
```
- Now we have to call this method when requesting sign up and generate password using private key:
```java
void requestSignUp(String identity) {
    this.identity = identity;

    generateKeyPair(identity);
    password = generatePassword(virgilKey.getPrivateKey().getValue());

    start(SIGN_UP);
}
```

When user will request log in - you have to generate password using private key loaded with user’s identity (currently - username):
```java
void requestLogIn(String identity) {
    this.identity = identity;

    try {
        virgilKey = infoHolder.getVirgilApi().getKeys().load(identity);
    } catch (KeyEntryNotFoundException e) {
        getView().onLoginError(e);
        return;
    } catch (VirgilKeyIsNotFoundException e) {
        getView().onLoginError(e);
        return;
    } catch (CryptoException e) {
        getView().onLoginError(e);
        return;
    }
    password = generatePassword(virgilKey.getPrivateKey().getValue());

    start(LOG_IN);
}
```

#### Create and Publish Virgil Card

As you already created VirgilCard few moments ago and passed it to the signUp method - you have to handle new argument and publish Virgil Card.

We pass exported Virgil Card to the Back4App code that will intercept the create user request and publish the Virgil Card on Virgil Cards Service.

Now you need to send this Card request to the App Server where it has to be signed with your application's Private Key (AppKey).

The VirgilCard object has a convenience method called export that returns the base64-encoded string representation of the request suitable for transfer (../virgilsecurity/virgilback4app/util/RxParse.javaRxParse class):
```java
public static Observable<VirgilCard> signUp(String username,
                                            String password,
                                            VirgilCard card) {
    return Observable.create(e -> {
        final ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.put("csr", card.export());

        user.signUpInBackground((exception) -> {
            if (exception == null) {
                e.onNext(card);
                e.onComplete();
            } else {
                e.onError(exception);
            }
        });
    });
}
```

Now your project automatically sends the Virgil Card exported to base64 to the Back4App, after that Cloud Code intercepts and publishes it.

**We encrypt messages with the recipient user's Virgil card.**

Let's add some code to ecnrypt data to the ../virgilsecurity/virgilback4app/chat/thread/ChatThreadPresenter class:

  - Add some new fields:
```java
private static final int GET_CARDS = 2;

private VirgilApi virgilApi;
private String identitySender;
private String identityRecipient;
private List<VirgilCard> cards;
```
  - Init VirgilApi in the `onCreate` method:
```java
@Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);

    virgilApi = AppVirgil.getInfoHolder().getVirgilApi();
...
}
```
  - Add method that encrypts data:
```java
private String encrypt(String text, List<VirgilCard> cards) {
    try {
        VirgilCards virgilCards = new VirgilCards(AppVirgil.getInfoHolder().getVirgilApiContext());
        virgilCards.addAll(cards);
        return virgilCards.encrypt(text).toString(StringEncoding.Base64);
    } catch (CryptoException e) {
        e.printStackTrace();
        return "";
    }
}
```
  - Find method `sendMessage` and update it calling `encrypt` method on message text before sending, so it will looks like:
```java
        restartableFirst(SEND_MESSAGE, () ->
                                 RxParse.sendMessage(encrypt(text, cards),
                                                     thread)
                                        .observeOn(AndroidSchedulers.mainThread()),
                         ChatThreadFragment::onSendMessageSuccess,
                         ChatThreadFragment::onSendMessageError);
```
  - Update `requestSendMessage` method passing to it `List<VirgilCard>` and initializing with it field:
```java
    void requestSendMessage(String text,
                            ChatThread thread,
                            List<VirgilCard> cards) {
        this.text = text;
        this.thread = thread;
        this.cards = cards;

        start(SEND_MESSAGE);
    }
```
That’s almost it! But as you might have noticed - we need sender and receiver’s Virgil Cards.

So let’s go on updating `ChatThreadPresenter` class to be able to get Virgil Cards from Virgil Card Service.
  
  - Add request method:
```java
void requestGetCards(String identitySender, String identityRecipient) {
    this.identitySender = identitySender;
    this.identityRecipient = identityRecipient;

    start(GET_CARDS);
}
```
  - Add new `restartableFirst` to connect presenter with fragment to the `onCreate` method along with others `restartableFirst`'s (`onGetCardSuccess` and `onGetCardError` methods will be implemented later):
```java
restartableFirst(GET_CARDS, () ->
                           Observable.zip(findCard(identitySender).toObservable()
                                                            .subscribeOn(Schedulers.io()),
                                          findCard(identityRecipient).toObservable()
                                                            .subscribeOn(Schedulers.io()),
                                          Pair::new)

                                             .observeOn(AndroidSchedulers.mainThread()),
                   ChatThreadFragment::onGetCardSuccess,
                   ChatThreadFragment::onGetCardError);
```
  - Add method that will find Virgil Card via its identity on Virgil Cards Service:
```java
private Single<VirgilCard> findCard(String identity) {
    return Single.create(e -> {
        VirgilCards cards = virgilApi.getCards().find(identity);
        if (cards.size() > 0) {
            e.onSuccess(cards.get(0));
        } else {
            e.onError(new VirgilCardIsNotFoundException());
        }
    });
}
```
  - Update `isDisposed()` and `disposeAll()` methods:
```java
void disposeAll() {
    stop(GET_MESSAGES);
    stop(SEND_MESSAGE);
    stop(GET_CARDS);
}

boolean isDisposed() {
    return isDisposed(GET_MESSAGES)
            || isDisposed(SEND_MESSAGE)
            || isDisposed(GET_CARDS);
}
```

Now you have to update `ChatThreadFragment` class to handle get cards request.

  - First you have to add fields:
```java
private VirgilCard meCard;
private VirgilCard youCard;
```
  - Implement `initCards()` method:
```java
private void initCards() {
    showProgress(true);

    getPresenter().requestGetCards(thread.getSenderUsername(),
                                   thread.getRecipientUsername());
}
```
  - Update getMessages() method to check whether Virgil Cards are already fetched:
```java
private void getMessages() {
    if (meCard == null || youCard == null) {
        initCards();
    } else if (messages == null || messages.size() == 0) {
        showProgress(true);
        getPresenter().requestMessages(thread, 50, page,
                                       Const.TableNames.CREATED_AT_CRITERIA);
    }
}
```
  - Update `onInterfaceClick` with creating List of `VirgilCard`'s and passing it to the `requestSendMessage` method:
```java
@OnClick({R.id.btnSend}) void onInterfaceClick(View v) {
    switch (v.getId()) {
        case R.id.btnSend:
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                List<VirgilCard> cards = new ArrayList<>();
                cards.add(meCard);
                cards.add(youCard);
                lockSendUi(true, true);
                getPresenter().requestSendMessage(message, thread, cards);
                isLoading = true;
            }
            break;
    }
}
```
  - Add callbacks for successful and unsuccessful Virgil Cards fetching:
```java
public void onGetCardSuccess(Pair<VirgilCard, VirgilCard> cards) {
    if (cards.first.getIdentity().equals(ParseUser.getCurrentUser().getUsername())) {
        meCard = cards.first;
        youCard = cards.second;
    } else {
        meCard = cards.second;
        youCard = cards.first;
    }

    if (meCard != null && youCard != null) {
        if (messages == null) {
            showProgress(false);
            getPresenter().requestMessages(thread, 50, page,
                                           Const.TableNames.CREATED_AT_CRITERIA);
        } else {
            showProgress(false);
            srlRefresh.setRefreshing(false);
            lockSendUi(false, false);
        }
    }
}

public void onGetCardError(Throwable t) {
    showProgress(false);
    srlRefresh.setRefreshing(false);
    lockSendUi(false, false);

    Utils.toast(this, Utils.resolveError(t));
}
```

**Decrypt the Encrypted Message in the HolderMessage class (nested in the ChatThreadRVAdapter class)**

**General logics:**

1. We first load the user’s private key from Android’s secure storage
2. Then use it to decrypt the message received



  - Add VirgilKey field:
```java
private Virgilkey virgilKey;
```
  - Init VirgilKey field in HolderMessage constructor:
```java
HolderMessage(View v) {
    super(v);
    ButterKnife.bind(this, v);

    try {
        virgilKey = AppVirgil.getInfoHolder()
                             .getVirgilApi()
                             .getKeys()
                             .load(ParseUser.getCurrentUser().getUsername());
    } catch (VirgilKeyIsNotFoundException e) {
        e.printStackTrace();
    } catch (CryptoException e) {
        e.printStackTrace();
    }
}
```
  - Implement decrypt method:
```java
String decrypt(String text) {
    try {
        return virgilKey.decrypt(text).toString();
    } catch (CryptoException e) {
        e.printStackTrace();
        return "";
    }
}
```
  - Decrypt message before displaying:
```java
void bind(Message message) {
    tvMessage.setText(decrypt(message.getBody()));
}
```

That's all for encryption and decryption.
At last - you have to update `resolveError` method in `Utils` class to handle some exceptions that can be thrown during the work with Virgil Security SDK. So add handling for `VirgilKeyIsNotFoundException`, `VirgilKeyIsAlreadyExistsException`, `VirgilCardIsNotFoundException` and `KeyEntryNotFoundException` so corresponding part of `resolveError` method will looks like:
```java
...
} else if (t instanceof ParseException) {
    ParseException exception = (ParseException) t;

    switch (exception.getCode()) {
        case ParseException.USERNAME_TAKEN:
            return "Username is already registered.\nPlease, try another one. (Parse)";
        case ParseException.OBJECT_NOT_FOUND:
            return "Username is not registered yet";
        case 60042: // Custom exception in RxParse.class
            return exception.getMessage();
        default:
            return "Oops.. Something went wrong ):";
    }
} else if (t instanceof VirgilKeyIsNotFoundException) {
    return "Username is not registered yet";
} else if (t instanceof VirgilKeyIsAlreadyExistsException) {
    return "Username is already registered. Please, try another one.";
} else if (t instanceof KeyEntryNotFoundException) {
    return "Username is not found on this device. Maybe you deleted your private key";
} else if (t instanceof VirgilCardIsNotFoundException) {
    return "Virgil Card is not found.\nYou can not start chat with user without Virgil Card.";
} else {
    return "Something went wrong";
}
...
```

### Important! 
You have to **Log Out** current user and register two new users, after that you can start e2ee chat with those two new users. The reason is that your first two users have got no `Virgil Card`'s, so you can not use encrypt\decrypt for them.

## HIPAA & GDPR compliance:

End-to-End Encryption is a way to meet the technical requirements for HIPAA & GDPR. If you need more details, sign up for a free [Virgil account][_virgil_account], join our Slack community and ping us there: we’re happy to discuss your own privacy circumstances and help you understand what’s required to meet the technical HIPAA & GDPR requirements.


## Where to go from here?

[Final project][_final_project]. If you missed pieces from the puzzle, open the E2EE project branch. You can insert your application credentials in this code (as you did during the article) and build the project.

Don’t forget to subscribe to our [Youtube channel][_youtube].  Where you find video series on how to do End-to-End Encryption.

Also, use Virgil Security to verify the integrity of data at any point. Data Integrity is essential to anyone who wants to guarantee that their data has not been tampered with. [Our tutorial][_data_integrity] provides mode details.

More information about what you can build with Virgil Security you will find [here][_virgil_www]. 

**Follow our posts on Back4App.** In the next tutorial, we will be helping two people or IoT devices to communicate with end-to-end encryption with [PFS][_pfs] enabled. You’ll find out how to protect previously intercepted traffic from being decrypted even if the main private key is compromised. 



[_mistakes]: https://techcrunch.com/2017/11/29/meet-the-man-who-deactivated-trumps-twitter-account/
[_twilio]: https://www.twilio.com/blog/2016/05/introducing-end-to-end-encryption-for-twilio-ip-messaging-with-virgil-security.html
[_back4app]: https://docs.back4app.com/docs/new-parse-app/simple-messenger/
[_next_post]: https://virgilsecurity.us13.list-manage.com/subscribe?u=b2d755932a192a668f143411a&id=d2891963f1
[_back4app_account]: https://www.back4app.com/
[_back4app_admin]: https://dashboard.back4app.com/apps/#!/admin
[_android_studio]: https://developer.android.com/studio/index.html
[_virgil_account]: https://developer.virgilsecurity.com/account/signup?utm_source=back4app&utm_medium=blog&utm_campaign=e2eechat
[_build.gradle_app_level]: https://github.com/VirgilSecurity/chat-back4app-android/blob/e2ee/app/build.gradle
[_build.gradle_project_level]: https://github.com/VirgilSecurity/chat-back4app-android/blob/e2ee/build.gradle
[_string.xml]: https://github.com/VirgilSecurity/chat-back4app-android/blob/e2ee/app/src/main/res/values/strings.xml
[_login_presenter]: https://github.com/VirgilSecurity/chat-back4app-android/blob/e2ee/app/src/main/java/com/android/virgilsecurity/virgilback4app/auth/LogInPresenter.java
[_chat_thread_presenter]: https://github.com/VirgilSecurity/chat-back4app-android/blob/e2ee/app/src/main/java/com/android/virgilsecurity/virgilback4app/chat/thread/ChatThreadPresenter.java
[_helper]: https://github.com/VirgilSecurity/chat-back4app-android/blob/e2ee/app/src/main/java/com/android/virgilsecurity/virgilback4app/util/VirgilHelper.java
[_rxparse]: https://github.com/VirgilSecurity/chat-back4app-android/blob/e2ee/app/src/main/java/com/android/virgilsecurity/virgilback4app/util/RxParse.java
[_virgil_dashboard]: https://developer.virgilsecurity.com/account/dashboard?utm_source=back4app&utm_medium=blog&utm_campaign=e2eechat
[_main_js_package_json]: https://gist.github.com/vadimavdeev/0df867740156ca787908a4f2b62997b8/archive/80a7f913fdb81fa8e05f23cec30ac992aff70ee3.zip
[_ChatThreadRVAdapter.java]: https://github.com/VirgilSecurity/chat-back4app-android/blob/e2ee/app/src/main/java/com/android/virgilsecurity/virgilback4app/chat/thread/ChatThreadRVAdapter.java
[_ChatThreadFragment.java]: https://github.com/VirgilSecurity/chat-back4app-android/blob/e2ee/app/src/main/java/com/android/virgilsecurity/virgilback4app/chat/thread/ChatThreadFragment.java
[_LogInFragment.java]: https://github.com/VirgilSecurity/chat-back4app-android/blob/e2ee/app/src/main/java/com/android/virgilsecurity/virgilback4app/auth/LogInFragment.java
[_youtube]: https://www.youtube.com/channel/UCU8BhA1nVzKKRiU5P4N3D6A/featured
[_final_project]: https://github.com/VirgilSecurity/chat-back4app-android/tree/e2ee
[_data_integrity]: https://developer.virgilsecurity.com/docs/java/get-started/data-integrity?utm_source=back4app&utm_medium=blog&utm_campaign=e2eechat
[_virgil_www]: https://virgilsecurity.com?utm_source=back4app&utm_medium=blog&utm_campaign=e2eechat
[_pfs]: https://developer.virgilsecurity.com/docs/java/get-started/perfect-forward-secrecy?utm_source=back4app&utm_medium=blog&utm_campaign=e2eechat
