package com.tangbba.simplegithubforjava.data

import android.content.Context
import android.preference.PreferenceManager

class AuthTokenProvider(private val mContext: Context) {

    val token: String?
        get() = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(KEY_AUTH_TOKEN, null)

    fun updateToken(token: String) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                .putString(KEY_AUTH_TOKEN, token)
                .apply()
    }

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}
