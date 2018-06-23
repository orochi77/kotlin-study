package com.tangbba.simplegithubforjava.ui.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.tangbba.simplegithubforjava.BuildConfig
import com.tangbba.simplegithubforjava.R
import com.tangbba.simplegithubforjava.api.AuthApi
import com.tangbba.simplegithubforjava.api.model.GithubAccessToken
import com.tangbba.simplegithubforjava.api.provideAuthApi
import com.tangbba.simplegithubforjava.data.AuthTokenProvider
import com.tangbba.simplegithubforjava.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast
import org.jetbrains.anko.newTask
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    internal val mAuthApi: AuthApi by lazy { provideAuthApi() }
    internal val mAuthTokenProvider: AuthTokenProvider by lazy { AuthTokenProvider(this) }

    internal var mAccessTokenCall: Call<GithubAccessToken>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sign_in_start_button.setOnClickListener {
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

        if (null != mAuthTokenProvider.token)
            launchMainActivity()
    }

    override fun onStop() {
        super.onStop()
        mAccessTokenCall?.run { cancel() }
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

        mAccessTokenCall!!.enqueue(object : Callback<GithubAccessToken> {
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
        sign_in_start_button.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        sign_in_start_button.visibility = View.VISIBLE
        progress_bar.visibility = View.GONE
    }

    private fun showError(throwable: Throwable) {
        longToast(throwable.message ?: "No message available")
    }

    private fun launchMainActivity() {
        startActivity(intentFor<MainActivity>().clearTask().newTask())
    }
}
