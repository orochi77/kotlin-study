package com.tangbba.simplegithubforjava.ui.main

import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.tangbba.simplegithubforjava.R
import com.tangbba.simplegithubforjava.ui.search.SearchActivity

class MainActivity : AppCompatActivity() {

    internal lateinit var mSearchFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSearchFab = findViewById(R.id.search_fab)
        mSearchFab.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }
    }
}
