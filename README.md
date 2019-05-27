# E3Kit Back4App Kotlin Demo

## Prerequisites

- [Android Studio][_android_studio] 3.4+.
- Back4App cloud code functions [setted up](#set-up-back4app-cloud-code-functions).

## Set up Back4App cloud code functions

- [Download this archive][_main_js_package_json] that contains two files: `main.js` and `package.json`;
- Extract the archive and open `main.js` with your favorite editor;

### Get Back4App credentials

If you don't have an account yet, [sign up for one](https://back4app.com).

- Open Back4App “Dashboard” of your app -> “App Settings” -> “Security & Keys”;
- Replace `PARSE_APP_ID` with your `Application ID` and `PARSE_REST_API_KEY` with your `REST API key`;

### Get Virgil credentials

If you don't have an account yet, [sign up for one](https://dashboard.virgilsecurity.com/signup).

- Create an E2EE v5 application and replace `APP_ID` with your `Application ID`;
- Create an API Key in `API KEYS` section and replace `API_KEY` with your Private API Key then replace `API_KEY_ID` with your Api Key ID.

### Deploy cloud code function

- Open Back4App “Dashboard” of your app -> Core -> Cloud code functions;
- Click `+ADD` and select your `main.js` and `package.json`, after that move both of them to the `cloud` folder;
- Click `DEPLOY`;

## Start Back4app Kotlin Demo app

Don't forget to setup [Back4App cloud code function](#set-up-back4app-cloud-code-functions) first. It is a mandatory part of this demo. After this do next steps:

- Open Android Studio -> File -> New -> Project from Version Control -> Git
- Enter https://github.com/VirgilSecurity/chat-back4app-android

### Setup Back4App credentials:

- Open Back4App “Dashboard” of your app -> “App Settings” -> “Security & Keys”;
- Go to `/app/src/main/res/values/strings.xml` file in your android project and replace `your_back4app_app_id` with your “Application ID” and `your_back4app_client_key` with your `Client Key`.

### Setup DB:

- Open Back4App “Dashboard” -> “Core” and create classes of `Custom` type named `Message` and `ChatThread`;

### Setup live query:

- Go to Back4App your apps list;
- Press the “Server Settings” button on your Application;
- Find the “Web Hosting and Live Query” block;
- Open the Live Query Settings  and check the “Activate Hosting” option;
- Choose a name for your subdomain to activate Live Query for the 2 classes you created: `Message` and `ChatThread`;
- Copy your new subdomain name and click the SAVE button;
- Go to `/app/src/main/res/values/strings.xml` and replace `yourSubdomainName` with the one you've created in the step above;

After these steps you will be able to hit the Run button in Android Studio and get the sample to work. Use emulator or real device to test it out.

[_android_studio]: https://developer.android.com/studio/index.html
[_main_js_package_json]: https://gist.github.com/xlwknx/2ce304284d9f1e47d7e3c9af4f9dd56c/archive/64b48e7310ad13f3643f607a14416335bc47b26a.zip
