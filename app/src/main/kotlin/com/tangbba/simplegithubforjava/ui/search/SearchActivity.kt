package com.tangbba.simplegithubforjava.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.tangbba.simplegithubforjava.R
import com.tangbba.simplegithubforjava.api.GithubApi
import com.tangbba.simplegithubforjava.api.model.GithubRepo
import com.tangbba.simplegithubforjava.api.model.RepoSearchResponse
import com.tangbba.simplegithubforjava.api.provideGithubApi
import com.tangbba.simplegithubforjava.ui.repo.RepositoryActivity
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity(), SearchAdapter.ItemClickListener {

    internal lateinit var mSearchMenu: MenuItem
    internal lateinit var mSearchView: SearchView
    internal val mAdapter: SearchAdapter by lazy {
        SearchAdapter().apply { setItemClickListener(this@SearchActivity) }
    }

    internal val mGithubApi: GithubApi by lazy {
        provideGithubApi(this)
    }

    internal var mRepoSearchResponseCall: Call<RepoSearchResponse>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        with(search_result_recycler_view) {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter =this@SearchActivity.mAdapter
        }
    }

    override fun onStop() {
        super.onStop()
        mRepoSearchResponseCall?.run { cancel() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_search, menu)
        mSearchMenu = menu.findItem(R.id.menu_activity_search_query)

        mSearchView = (mSearchMenu.actionView as SearchView).apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    updateTitle(query)
                    hideSoftKeyboard()
                    collapseSearchView()
                    searchRepository(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
        }

        with(mSearchMenu) {
            setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    if ("" == mSearchView.query) {
                        finish()
                    }
                    return true
                }

            })
            expandActionView()
        }
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
        startActivity<RepositoryActivity>(
                RepositoryActivity.KEY_USER_LOGIN to repository.owner.login,
                RepositoryActivity.KEY_REPO_NAME to repository.name
        )
    }

    private fun searchRepository(query: String) {
        clearResults()
        hideError()
        showProgress()

        mRepoSearchResponseCall = mGithubApi.searchRepository(query)
        mRepoSearchResponseCall!!.enqueue(object : Callback<RepoSearchResponse> {
            override fun onResponse(call: Call<RepoSearchResponse>,
                                    response: Response<RepoSearchResponse>?) {
                hideProgress()

                val result = response!!.body()
                if (response!!.isSuccessful && null != response) {
                    with(mAdapter) {
                        setItems(result!!.items)
                        notifyDataSetChanged()
                    }

                    if (0 == result!!.totalCount) {
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
        supportActionBar?.run {
            subtitle = query
        }
    }

    private fun hideSoftKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).run {
            hideSoftInputFromWindow(mSearchView.windowToken, 0)
        }
    }

    private fun collapseSearchView() {
        mSearchMenu.collapseActionView()
    }

    private fun clearResults() {
        with(mAdapter) {
            clearItems()
            notifyDataSetChanged()
        }
    }

    private fun showProgress() {
        progress_bar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progress_bar.visibility = View.GONE
    }

    private fun showError(message: String?) {
        with(search_message_text_view) {
            text = message ?: "Unexpected error."
            visibility = View.VISIBLE
        }
    }

    private fun hideError() {
        with(search_message_text_view) {
            text = ""
            visibility = View.GONE
        }
    }
}
