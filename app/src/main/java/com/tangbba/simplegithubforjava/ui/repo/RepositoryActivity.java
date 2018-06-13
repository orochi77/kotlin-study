package com.tangbba.simplegithubforjava.ui.repo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tangbba.simplegithubforjava.R;
import com.tangbba.simplegithubforjava.api.GithubApi;
import com.tangbba.simplegithubforjava.api.GithubApiProvider;
import com.tangbba.simplegithubforjava.api.model.GithubRepo;
import com.tangbba.simplegithubforjava.ui.GlideApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositoryActivity extends AppCompatActivity {

    public static final String KEY_USER_LOGIN = "user_login";
    public static final String KEY_REPO_NAME = "repo_name";

    private LinearLayout mRepositoryContent;
    private ImageView mProfileImageView;
    private TextView mRepositoryNameTextView;
    private TextView mStarsTextView;
    private TextView mDescriptionTextView;
    private TextView mLanguageTextView;
    private TextView mLastUpdateTextView;
    private ProgressBar mProgressBar;
    private TextView mMessageTextView;

    private GithubApi mGithubApi;
    private Call<GithubRepo> mGithubRepoCall;

    private SimpleDateFormat mDateFormatInResponse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault());
    private SimpleDateFormat mDateFormatForDisplay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository);

        mRepositoryContent = (LinearLayout) findViewById(R.id.repository_content);
        mProfileImageView = (ImageView) findViewById(R.id.profile_image_view);
        mRepositoryNameTextView = (TextView) findViewById(R.id.repository_name_text_view);
        mStarsTextView = (TextView) findViewById(R.id.stars_text_view);
        mDescriptionTextView = (TextView) findViewById(R.id.description_text_view);
        mLanguageTextView = (TextView) findViewById(R.id.language_text_view);
        mLastUpdateTextView = (TextView) findViewById(R.id.last_update_text_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mMessageTextView = (TextView) findViewById(R.id.message_text_view);

        mGithubApi = GithubApiProvider.provideGithubApi(this);

        String login = getIntent().getStringExtra(KEY_USER_LOGIN);
        if (null == login) {
            throw new IllegalArgumentException("No login info exist in extras.");
        }
        String repo = getIntent().getStringExtra(KEY_REPO_NAME);
        if (null == repo) {
            throw new IllegalArgumentException("No repo info exist in extras.");
        }

        showRepositoryInfo(login, repo);
    }

    private void showRepositoryInfo(String login, final String repoName) {
        showProgress();

        mGithubRepoCall = mGithubApi.getRepository(login, repoName);
        mGithubRepoCall.enqueue(new Callback<GithubRepo>() {
            @Override
            public void onResponse(Call<GithubRepo> call, Response<GithubRepo> response) {
                hideProgress(true);

                GithubRepo repo = response.body();
                if (response.isSuccessful() && null != repo) {
                    GlideApp.with(RepositoryActivity.this)
                            .load(repo.getOwner().getAvatarUrl())
                            .into(mProfileImageView);

                    mRepositoryNameTextView.setText(repo.getFullName());
                    mStarsTextView.setText(getResources().getQuantityString(R.plurals.star, repo.getStars(), repo.getStars()));
                    if (null == repo.getDescription()) {
                        mDescriptionTextView.setText(R.string.no_description_provided);
                    } else {
                        mDescriptionTextView.setText(repo.getDescription());
                    }
                    if (null == repo.getLanguage()) {
                        mLanguageTextView.setText(R.string.no_language_specified);
                    } else {
                        mLanguageTextView.setText(repo.getLanguage());
                    }

                    try {
                        Date lastUpdate = mDateFormatInResponse.parse(repo.getUpdatedAt());
                        mLastUpdateTextView.setText(mDateFormatForDisplay.format(lastUpdate));
                    } catch (ParseException e) {
                        mLastUpdateTextView.setText(getString(R.string.unknown));
                    }
                } else {
                    showError("Not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GithubRepo> call, Throwable t) {
                hideProgress(false);
                showError(t.getMessage());
            }
        });
    }

    private void showProgress() {
        mRepositoryContent.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress(boolean isSuccess) {
        mRepositoryContent.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        mMessageTextView.setText(message);
        mMessageTextView.setVisibility(View.VISIBLE);
    }
}
