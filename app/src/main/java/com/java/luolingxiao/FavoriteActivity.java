package com.java.luolingxiao;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.java.luolingxiao.fragment.SimpleNewsListFragment;

public class FavoriteActivity extends DefaultSwipeBackActivity {
    boolean isLocalFavorite() {
        return getIntent().getBooleanExtra("local_favorite", false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = findViewById(R.id.toolbar_favorite);

        toolbar.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(toolbar);

        SimpleNewsListFragment fragment;
        if (isLocalFavorite()) {
            fragment = SimpleNewsListFragment.localFavorite();
        } else {
            fragment = SimpleNewsListFragment.userFavorite();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_favorite, fragment)
                .commit();
    }
}
