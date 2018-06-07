package com.tangbba.simplegithubforjava.api.model;

import com.google.gson.annotations.SerializedName;

public class  GithubAccessToken {

    @SerializedName("access_token")
    public final String mAccessToken;

    @SerializedName("scope")
    public final String scope;

    @SerializedName("token_type")
    public final String mTokenType;

    public GithubAccessToken(String accessToken, String scope, String tokenType) {
        mAccessToken = accessToken;
        this.scope = scope;
        mTokenType = tokenType;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getScope() {
        return scope;
    }

    public String getTokenType() {
        return mTokenType;
    }
}
