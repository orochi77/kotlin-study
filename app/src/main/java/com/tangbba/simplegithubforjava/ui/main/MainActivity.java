package com.tangbba.simplegithubforjava.ui.main;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tangbba.simplegithubforjava.R;
import com.tangbba.simplegithubforjava.ui.search.SearchActivity;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton mSearchFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchFab = findViewById(R.id.search_fab);
        mSearchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
    }
}
