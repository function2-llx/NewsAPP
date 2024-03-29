package com.java.luolingxiao;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.java.luolingxiao.api.UserApi;
import com.java.luolingxiao.bean.NewsBean;
import com.java.luolingxiao.fragment.DisplayNewsListFragment;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DisplayActivity extends DefaultSwipeBackActivity {
    String getName() {
        return getIntent().getStringExtra("name");
    }

    public static class LocalFavoritesFragment extends DisplayNewsListFragment {
        @Override
        protected List<NewsBean> getNews() {
            return getDataRepository().getLocalFavoritesSync();
        }
    }

    public static class UserFavoritesFragment extends DisplayNewsListFragment {
        @Override
        protected List<NewsBean> getNews() {
            try {
                return UserApi.getInstance().getFavoriteSync();
            } catch (Exception e) {
                Toast.makeText(getContext(), "获取云收藏列表失败惹", Toast.LENGTH_SHORT).show();
                return Collections.emptyList();
            }
        }
    }

    public static class LocalReadsFragment extends DisplayNewsListFragment {
        @Override
        protected List<NewsBean> getNews() {
            return getDataRepository().getReadsSync();
        }
    }

    private DisplayNewsListFragment getFragment() {
        String name = getName();
        if (name.equals(getString(R.string.local_favorites))) {
            fragment = new LocalFavoritesFragment();
        } else if (name.equals(getString(R.string.user_favorites))) {
            fragment = new UserFavoritesFragment();
        } else if (name.equals(getString(R.string.local_reads))) {
            fragment = new LocalReadsFragment();
        }
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getName().equals(getString(R.string.local_reads))) {
            getMenuInflater().inflate(R.menu.menu_reads, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定要清空所有本地浏览记录吗")
                        .setPositiveButton("确定", (dialog, which) -> getDataRepository().clearReads(() -> fragment.onRefresh(fragment.getRefreshLayout()))).setNegativeButton("取消", null)
                        .show();
            break;
        }
        return true;
    }

    private DisplayNewsListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = findViewById(R.id.toolbar_favorite);
        toolbar.setTitle(getName());
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_favorite, Objects.requireNonNull(getFragment()))
                .commit();
    }
}
