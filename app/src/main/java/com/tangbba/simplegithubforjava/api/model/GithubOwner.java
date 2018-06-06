package com.tangbba.simplegithubforjava.api.model;

import com.google.gson.annotations.SerializedName;

public class GithubOwner {

    @SerializedName("login")
    private String mLogin;

    @SerializedName("avatar_url")
    private String mAvatarUrl;

    public GithubOwner(String login, String avatarUrl) {
        mLogin = login;
        mAvatarUrl = avatarUrl;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String login) {
        mLogin = login;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }
}
