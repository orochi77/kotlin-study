package com.tangbba.simplegithubforjava.ui.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tangbba.simplegithubforjava.R
import com.tangbba.simplegithubforjava.ui.search.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search_fab.setOnClickListener {
            startActivity<SearchActivity>()
        }
    }
}
