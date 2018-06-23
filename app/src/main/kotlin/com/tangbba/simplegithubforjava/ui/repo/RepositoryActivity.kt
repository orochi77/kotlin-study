package com.tangbba.simplegithubforjava.ui.repo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.tangbba.simplegithubforjava.R
import com.tangbba.simplegithubforjava.api.GithubApi
import com.tangbba.simplegithubforjava.api.model.GithubRepo
import com.tangbba.simplegithubforjava.api.provideGithubApi
import com.tangbba.simplegithubforjava.ui.GlideApp

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kotlinx.android.synthetic.main.activity_repository.*

class RepositoryActivity : AppCompatActivity() {

    companion object {
        const val KEY_USER_LOGIN = "user_login"
        const val KEY_REPO_NAME = "repo_name"
    }

    internal val mGithubApi by lazy { provideGithubApi(this) }
    internal var mGithubRepoCall: Call<GithubRepo>? = null

    internal var mDateFormatInResponse = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    internal var mDateFormatForDisplay = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)


        val login = intent.getStringExtra(KEY_USER_LOGIN)
                ?: throw IllegalArgumentException("No login info exist in extras.")
        val repo = intent.getStringExtra(KEY_REPO_NAME)
                ?: throw IllegalArgumentException("No repo info exist in extras.")

        showRepositoryInfo(login, repo)
    }

    override fun onStop() {
        super.onStop()
        mGithubRepoCall?.run { cancel() }
    }

    private fun showRepositoryInfo(login: String, repoName: String) {
        showProgress()

        mGithubRepoCall = mGithubApi.getRepository(login, repoName)
        mGithubRepoCall!!.enqueue(object : Callback<GithubRepo> {
            override fun onResponse(call: Call<GithubRepo>, response: Response<GithubRepo>) {
                hideProgress(true)

                val repo = response.body()
                if (response.isSuccessful && null != repo) {
                    GlideApp.with(this@RepositoryActivity)
                            .load(repo.owner.avatarUrl)
                            .into(profile_image_view)

                    repository_name_text_view.text = repo.fullName
                    stars_text_view.text = resources.getQuantityString(R.plurals.star, repo.stars, repo.stars)
                    if (null == repo.description) {
                        description_text_view.setText(R.string.no_description_provided)
                    } else {
                        description_text_view.text = repo.description
                    }
                    if (null == repo.language) {
                        language_text_view.setText(R.string.no_language_specified)
                    } else {
                        language_text_view.text = repo.language
                    }

                    try {
                        val lastUpdate = mDateFormatInResponse.parse(repo.updatedAt)
                        last_update_text_view.text = mDateFormatForDisplay.format(lastUpdate)
                    } catch (e: ParseException) {
                        last_update_text_view.text = getString(R.string.unknown)
                    }

                } else {
                    showError("Not successful: " + response.message())
                }
            }

            override fun onFailure(call: Call<GithubRepo>, t: Throwable) {
                hideProgress(false)
                showError(t.message)
            }
        })
    }

    private fun showProgress() {
        repository_content.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE
    }

    private fun hideProgress(isSuccess: Boolean) {
        repository_content.visibility = if (isSuccess) View.VISIBLE else View.GONE
        progress_bar.visibility = View.GONE
    }

    private fun showError(message: String?) {
        with(message_text_view) {
            text = message ?: "Unexpected Error."
            visibility = View.VISIBLE
        }
    }


}
