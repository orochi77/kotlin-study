package com.tangbba.simplegithubforjava.ui.search

import android.content.Intent
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView

import com.tangbba.simplegithubforjava.R
import com.tangbba.simplegithubforjava.api.GithubApi
import com.tangbba.simplegithubforjava.api.GithubApiProvider
import com.tangbba.simplegithubforjava.api.model.GithubRepo
import com.tangbba.simplegithubforjava.api.model.RepoSearchResponse
import com.tangbba.simplegithubforjava.ui.repo.RepositoryActivity

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity(), SearchAdapter.ItemClickListener {

    internal lateinit var mRecyclerView: RecyclerView
    internal lateinit var mProgressBar: ProgressBar
    internal lateinit var mSearchMessageTextView: TextView
    internal lateinit var mSearchMenu: MenuItem
    internal lateinit var mSearchView: SearchView
    internal lateinit var mAdapter: SearchAdapter

    internal lateinit var mGithubApi: GithubApi
    internal lateinit var mRepoSearchResponseCall: Call<RepoSearchResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        mRecyclerView = findViewById(R.id.search_result_recycler_view)
        mProgressBar = findViewById(R.id.progress_bar)
        mSearchMessageTextView = findViewById(R.id.search_message_text_view)

        mAdapter = SearchAdapter()
        mAdapter.setItemClickListener(this)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter

        mGithubApi = GithubApiProvider.provideGithubApi(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_search, menu)
        mSearchMenu = menu.findItem(R.id.menu_activity_search_query)

        mSearchView = mSearchMenu.actionView as SearchView
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                updateTitle(query)
                hideSoftKeyboard()
                collapseSearchView()
                searchRepository(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        mSearchMenu.expandActionView()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.menu_activity_search_query == item.itemId) {
            item.expandActionView()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(repository: GithubRepo) {
        val intent = Intent(this, RepositoryActivity::class.java)
        intent.putExtra(RepositoryActivity.KEY_USER_LOGIN, repository.owner.login)
        intent.putExtra(RepositoryActivity.KEY_REPO_NAME, repository.name)
        startActivity(intent)
    }

    private fun searchRepository(query: String) {
        clearResults()
        hideError()
        showProgress()

        mRepoSearchResponseCall = mGithubApi.searchRepository(query)
        mRepoSearchResponseCall.enqueue(object : Callback<RepoSearchResponse> {
            override fun onResponse(call: Call<RepoSearchResponse>,
                                    response: Response<RepoSearchResponse>?) {
                hideProgress()
                val result = response!!.body()
                if (response.isSuccessful && null != response) {
                    mAdapter.setItems(result!!.items)
                    mAdapter.notifyDataSetChanged()

                    if (0 == result.totalCount) {
                        showError(getString(R.string.no_search_result))
                    }
                } else {
                    showError("Not successful: " + response.message())
                }
            }

            override fun onFailure(call: Call<RepoSearchResponse>, t: Throwable) {
                hideProgress()
                showError(t.message)
            }
        })
    }

    private fun updateTitle(query: String) {
        val actionBar = supportActionBar
        if (null != actionBar) {
            actionBar.subtitle = query
        }
    }

    private fun hideSoftKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mSearchView.windowToken, 0)
    }

    private fun collapseSearchView() {
        mSearchMenu.collapseActionView()
    }

    private fun clearResults() {
        mAdapter.clearItems()
        mAdapter.notifyDataSetChanged()
    }

    private fun showProgress() {
        mProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        mProgressBar.visibility = View.GONE
    }

    private fun showError(message: String?) {
        mSearchMessageTextView.text = message ?: "Unexpected error."
        mSearchMessageTextView.visibility = View.VISIBLE
    }

    private fun hideError() {
        mSearchMessageTextView.text = ""
        mSearchMessageTextView.visibility = View.GONE
    }
}
