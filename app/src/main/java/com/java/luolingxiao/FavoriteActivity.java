package com.java.luolingxiao;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.java.luolingxiao.fragment.FavoriteNewsListFragment;

public class FavoriteActivity extends DefaultSwipeBackActivity {
    boolean isLocal() {
        return getIntent().getBooleanExtra("local", true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = findViewById(R.id.toolbar_favorite);

        toolbar.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(toolbar);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_favorite, FavoriteNewsListFragment.newInstance(isLocal()))
                .commit();
    }
}
