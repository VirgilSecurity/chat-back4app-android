package com.android.virgilsecurity.virgilback4app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

public class User implements Parcelable {

    @SerializedName("sessionToken")
    private String sessionToken;

    @SerializedName("authData")
    private String authData;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("email")
    private String email;

    public User() {
    }

    private User(Builder builder) {
        setSessionToken(builder.sessionToken);
        setAuthData(builder.authData);
        setUsername(builder.username);
        setPassword(builder.password);
        setEmail(builder.email);
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getAuthData() {
        return authData;
    }

    public void setAuthData(String authData) {
        this.authData = authData;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static final class Builder {
        private String sessionToken;
        private String authData;
        private String username;
        private String password;
        private String email;

        public Builder() {
        }

        public Builder sessionToken(String val) {
            sessionToken = val;
            return this;
        }

        public Builder authData(String val) {
            authData = val;
            return this;
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder email(String val) {
            email = val;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sessionToken);
        dest.writeString(this.authData);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.email);
    }

    protected User(Parcel in) {
        this.sessionToken = in.readString();
        this.authData = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.email = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
