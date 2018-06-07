package com.tangbba.simplegithubforjava.data;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class AuthTokenProvider {

    private static final String KEY_AUTH_TOKEN = "auth_token";

    @NonNull
    private Context mContext;

    public AuthTokenProvider(@NonNull Context context) {
        mContext = context;
    }

    public void updateToken(@NonNull String token) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                .putString(KEY_AUTH_TOKEN, token)
                .apply();
    }

    @Nullable
    public String getToken() {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(KEY_AUTH_TOKEN, null);
    }
}
