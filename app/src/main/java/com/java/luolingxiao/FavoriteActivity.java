package com.java.luolingxiao;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.java.luolingxiao.fragment.SimpleNewsListFragment;

public class FavoriteActivity extends DefaultSwipeBackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = findViewById(R.id.toolbar_favorite);

        toolbar.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(toolbar);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_favorite, new SimpleNewsListFragment())
                .commit();
    }
}
