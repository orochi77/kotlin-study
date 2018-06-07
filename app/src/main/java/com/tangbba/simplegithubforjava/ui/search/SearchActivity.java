package com.tangbba.simplegithubforjava.ui.search;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tangbba.simplegithubforjava.R;
import com.tangbba.simplegithubforjava.api.GithubApi;
import com.tangbba.simplegithubforjava.api.GithubApiProvider;
import com.tangbba.simplegithubforjava.api.model.GithubRepo;
import com.tangbba.simplegithubforjava.api.model.RepoSearchResponse;
import com.tangbba.simplegithubforjava.ui.repo.RepositoryActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.ItemClickListener {

    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    TextView mSearchMessageTextView;
    MenuItem mSearchMenu;
    SearchView mSearchView;
    SearchAdapter mAdapter;

    GithubApi mGithubApi;
    Call<RepoSearchResponse> mRepoSearchResponseCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRecyclerView = findViewById(R.id.search_result_recycler_view);
        mProgressBar = findViewById(R.id.progress_bar);
        mSearchMessageTextView = findViewById(R.id.search_message_text_view);

        mAdapter = new SearchAdapter();
        mAdapter.setItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mGithubApi = GithubApiProvider.provideGithubApi(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_search, menu);
        mSearchMenu = menu.findItem(R.id.menu_activity_search_query);

        mSearchView = (SearchView) mSearchMenu.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateTitle(query);
                hideSoftKeyboard();
                collapseSearchView();
                searchRepository(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchMenu.expandActionView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.menu_activity_search_query == item.getItemId()) {
            item.expandActionView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(GithubRepo repository) {
        Intent intent = new Intent(this, RepositoryActivity.class);
        intent.putExtra(RepositoryActivity.KEY_USER_LOGIN, repository.getOwner().getLogin());
        intent.putExtra(RepositoryActivity.KEY_REPO_NAME, repository.getName());
        startActivity(intent);
    }

    private void searchRepository(String query) {
        clearResults();
        hideError();
        showProgress();

        mRepoSearchResponseCall = mGithubApi.searchRepository(query);
        mRepoSearchResponseCall.enqueue(new Callback<RepoSearchResponse>() {
            @Override
            public void onResponse(Call<RepoSearchResponse> call,
                                   Response<RepoSearchResponse> response) {
                hideProgress();
                RepoSearchResponse result = response.body();
                if (response.isSuccessful() && null != response) {
                    mAdapter.setItems(result.getItems());
                    mAdapter.notifyDataSetChanged();

                    if (0 == result.getTotalCount()) {
                        showError(getString(R.string.no_search_result));
                    }
                } else {
                    showError("Not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RepoSearchResponse> call, Throwable t) {
                hideProgress();
                showError(t.getMessage());
            }
        });
    }

    private void updateTitle(String query) {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setSubtitle(query);
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
    }

    private void collapseSearchView() {
        mSearchMenu.collapseActionView();
    }

    private void clearResults() {
        mAdapter.clearItems();
        mAdapter.notifyDataSetChanged();
    }

    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        mSearchMessageTextView.setText(message);
        mSearchMessageTextView.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        mSearchMessageTextView.setText("");
        mSearchMessageTextView.setVisibility(View.GONE);
    }
}
