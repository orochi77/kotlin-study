package com.tangbba.simplegithubforjava.ui.signin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tangbba.simplegithubforjava.BuildConfig;
import com.tangbba.simplegithubforjava.R;
import com.tangbba.simplegithubforjava.api.AuthApi;
import com.tangbba.simplegithubforjava.api.GithubApiProvider;
import com.tangbba.simplegithubforjava.api.model.GithubAccessToken;
import com.tangbba.simplegithubforjava.data.AuthTokenProvider;
import com.tangbba.simplegithubforjava.ui.main.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    Button mStartButton;
    ProgressBar mProgressBar;
    AuthApi mAuthApi;
    AuthTokenProvider mAuthTokenProvider;

    Call<GithubAccessToken> mAccessTokenCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mStartButton = findViewById(R.id.sign_in_start_button);
        mProgressBar = findViewById(R.id.progress_bar);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri authUri = new Uri.Builder().scheme("https")
                        .authority("github.com")
                        .appendPath("login")
                        .appendPath("oauth")
                        .appendPath("authorize")
                        .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                        .build();
                CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                intent.launchUrl(SignInActivity.this, authUri);
            }
        });

        mAuthApi = GithubApiProvider.provideAuthApi();
        mAuthTokenProvider = new AuthTokenProvider(this);

        if (null != mAuthTokenProvider.getToken())
            launchMainActivity();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        showProgress();

        Uri uri = intent.getData();
        if (null == uri) {
            throw new IllegalArgumentException("No data exists");
        }

        String code = uri.getQueryParameter("code");
        if (null == code) {
            throw new IllegalStateException("No code exists.");
        }

        getAccessToken(code);
    }

    private void getAccessToken(@NonNull String code) {
        showProgress();

        mAccessTokenCall = mAuthApi.getAccessToken(BuildConfig.GITHUB_CLIENT_ID,
                BuildConfig.GITHUB_CLIENT_SECRET,
                code);

        mAccessTokenCall.enqueue(new Callback<GithubAccessToken>() {
            @Override
            public void onResponse(Call<GithubAccessToken> call,
                                   Response<GithubAccessToken> response) {
                hideProgress();

                GithubAccessToken token = response.body();
                if (response.isSuccessful() && null != token) {
                    mAuthTokenProvider.updateToken(token.getAccessToken());

                    launchMainActivity();
                } else {
                    showError(new IllegalStateException("Not successful: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<GithubAccessToken> call,
                                  Throwable t) {
                hideProgress();
                showError(t);
            }
        });

    }

    private void showProgress() {
        mStartButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        mStartButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void showError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void launchMainActivity() {
        startActivity(new Intent(
                SignInActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }
}
