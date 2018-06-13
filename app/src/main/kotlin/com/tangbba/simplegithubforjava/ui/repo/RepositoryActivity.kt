package com.tangbba.simplegithubforjava.ui.repo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.tangbba.simplegithubforjava.R
import com.tangbba.simplegithubforjava.api.GithubApi
import com.tangbba.simplegithubforjava.api.GithubApiProvider
import com.tangbba.simplegithubforjava.api.model.GithubRepo
import com.tangbba.simplegithubforjava.ui.GlideApp

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepositoryActivity : AppCompatActivity() {

    private lateinit var mRepositoryContent: LinearLayout
    internal lateinit var mProfileImageView: ImageView
    internal lateinit var mRepositoryNameTextView: TextView
    internal lateinit var mStarsTextView: TextView
    internal lateinit var mDescriptionTextView: TextView
    internal lateinit var mLanguageTextView: TextView
    internal lateinit var mLastUpdateTextView: TextView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mMessageTextView: TextView

    private lateinit var mGithubApi: GithubApi
    private lateinit var mGithubRepoCall: Call<GithubRepo>

    internal var mDateFormatInResponse = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    internal var mDateFormatForDisplay = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)

        mRepositoryContent = findViewById(R.id.repository_content)
        mProfileImageView = findViewById(R.id.profile_image_view)
        mRepositoryNameTextView = findViewById(R.id.repository_name_text_view)
        mStarsTextView = findViewById(R.id.stars_text_view)
        mDescriptionTextView = findViewById(R.id.description_text_view)
        mLanguageTextView = findViewById(R.id.language_text_view)
        mLastUpdateTextView = findViewById(R.id.last_update_text_view)
        mProgressBar = findViewById(R.id.progress_bar)
        mMessageTextView = findViewById(R.id.message_text_view)

        mGithubApi = GithubApiProvider.provideGithubApi(this)

        val login = intent.getStringExtra(KEY_USER_LOGIN)
                ?: throw IllegalArgumentException("No login info exist in extras.")
        val repo = intent.getStringExtra(KEY_REPO_NAME)
                ?: throw IllegalArgumentException("No repo info exist in extras.")

        showRepositoryInfo(login, repo)
    }

    private fun showRepositoryInfo(login: String, repoName: String) {
        showProgress()

        mGithubRepoCall = mGithubApi.getRepository(login, repoName)
        mGithubRepoCall.enqueue(object : Callback<GithubRepo> {
            override fun onResponse(call: Call<GithubRepo>, response: Response<GithubRepo>) {
                hideProgress(true)

                val repo = response.body()
                if (response.isSuccessful && null != repo) {
                    GlideApp.with(this@RepositoryActivity)
                            .load(repo.owner.avatarUrl)
                            .into(mProfileImageView)

                    mRepositoryNameTextView.text = repo.fullName
                    mStarsTextView.text = resources.getQuantityString(R.plurals.star, repo.stars, repo.stars)
                    if (null == repo.description) {
                        mDescriptionTextView.setText(R.string.no_description_provided)
                    } else {
                        mDescriptionTextView.text = repo.description
                    }
                    if (null == repo.language) {
                        mLanguageTextView.setText(R.string.no_language_specified)
                    } else {
                        mLanguageTextView.text = repo.language
                    }

                    try {
                        val lastUpdate = mDateFormatInResponse.parse(repo.updatedAt)
                        mLastUpdateTextView.text = mDateFormatForDisplay.format(lastUpdate)
                    } catch (e: ParseException) {
                        mLastUpdateTextView.text = getString(R.string.unknown)
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
        mRepositoryContent.visibility = View.GONE
        mProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgress(isSuccess: Boolean) {
        mRepositoryContent.visibility = if (isSuccess) View.VISIBLE else View.GONE
        mProgressBar.visibility = View.GONE
    }

    private fun showError(message: String?) {
        mMessageTextView.text = message
        mMessageTextView.visibility = View.VISIBLE
    }

    companion object {
        val KEY_USER_LOGIN = "user_login"
        val KEY_REPO_NAME = "repo_name"
    }
}
