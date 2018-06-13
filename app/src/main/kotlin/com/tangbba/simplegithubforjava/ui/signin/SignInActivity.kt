package com.tangbba.simplegithubforjava.ui.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast

import com.tangbba.simplegithubforjava.BuildConfig
import com.tangbba.simplegithubforjava.R
import com.tangbba.simplegithubforjava.api.AuthApi
import com.tangbba.simplegithubforjava.api.GithubApiProvider
import com.tangbba.simplegithubforjava.api.model.GithubAccessToken
import com.tangbba.simplegithubforjava.data.AuthTokenProvider
import com.tangbba.simplegithubforjava.ui.main.MainActivity

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    internal lateinit var mStartButton: Button
    internal lateinit var mProgressBar: ProgressBar
    internal lateinit var mAuthApi: AuthApi
    internal lateinit var mAuthTokenProvider: AuthTokenProvider

    internal lateinit var mAccessTokenCall: Call<GithubAccessToken>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mStartButton = findViewById(R.id.sign_in_start_button)
        mProgressBar = findViewById(R.id.progress_bar)

        mStartButton.setOnClickListener {
            val authUri = Uri.Builder().scheme("https")
                    .authority("github.com")
                    .appendPath("login")
                    .appendPath("oauth")
                    .appendPath("authorize")
                    .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                    .build()
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(this@SignInActivity, authUri)
        }

        mAuthApi = GithubApiProvider.provideAuthApi()
        mAuthTokenProvider = AuthTokenProvider(this)

        if (null != mAuthTokenProvider.token)
            launchMainActivity()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        showProgress()

        val uri = intent.data ?: throw IllegalArgumentException("No data exists")

        val code = uri.getQueryParameter("code")
                ?: throw IllegalStateException("No code exists.")

        getAccessToken(code)
    }

    private fun getAccessToken(code: String) {
        showProgress()

        mAccessTokenCall = mAuthApi.getAccessToken(BuildConfig.GITHUB_CLIENT_ID,
                BuildConfig.GITHUB_CLIENT_SECRET,
                code)

        mAccessTokenCall.enqueue(object : Callback<GithubAccessToken> {
            override fun onResponse(call: Call<GithubAccessToken>,
                                    response: Response<GithubAccessToken>) {
                hideProgress()

                val token = response.body()
                if (response.isSuccessful && null != token) {
                    mAuthTokenProvider.updateToken(token.accessToken)

                    launchMainActivity()
                } else {
                    showError(IllegalStateException("Not successful: " + response.message()))
                }
            }

            override fun onFailure(call: Call<GithubAccessToken>,
                                   t: Throwable) {
                hideProgress()
                showError(t)
            }
        })

    }

    private fun showProgress() {
        mStartButton.visibility = View.GONE
        mProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        mStartButton.visibility = View.VISIBLE
        mProgressBar.visibility = View.GONE
    }

    private fun showError(throwable: Throwable) {
        Toast.makeText(this, throwable.message, Toast.LENGTH_LONG).show()
    }

    private fun launchMainActivity() {
        startActivity(Intent(
                this@SignInActivity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
