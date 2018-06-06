package com.tangbba.simplegithubforjava.ui.repo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tangbba.simplegithubforjava.R;

public class RepositoryActivity extends AppCompatActivity {

    public static final String KEY_USER_LOGIN = "user_login";
    public static final String KEY_REPO_NAME = "repo_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository);
    }
}
