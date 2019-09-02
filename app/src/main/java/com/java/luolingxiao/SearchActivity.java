package com.java.luolingxiao;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.java.luolingxiao.fragment.SearchFragment;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;

public class SearchActivity extends DefaultSwipeBackActivity {
    private MaterialSearchBar searchBar;
    private SearchFragment fragment;

    void initSearchBar() {
        searchBar = findViewById(R.id.search_bar);

        ArrayList<String> channels = getIntent().getStringArrayListExtra("channels");
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (enabled) {
                    searchBar.setHint(searchBar.getPlaceHolderText());
                } else {
                    if (fragment != null) {
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        fragment = null;
                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if (text != null) {
                    if (text.length() > 0) {
                        getDataRepository().insertSearchRecords(text.toString());
                    }
                    searchBar.setPlaceHolder(text);
                    searchBar.disableSearch();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    if (fragment != null) {
                        transaction.remove(fragment);
                    }
                    fragment = SearchFragment.newInstance(channels, text.toString());
                    transaction.add(R.id.container_search, fragment).commit();
                } else {
                    // back to default mode
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        searchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                searchBar.onItemClick(position, v);
            }

            @Override
            public void onItemDelete(int position, View v) {
                searchBar.onItemDelete(position, v);
                if (v.getTag() instanceof String) {
                    getDataRepository().deleteSearchRecords(v.getTag().toString());
                }
            }
        });
        getDataRepository().getAllSearchRecords(records -> searchBar.setLastSuggestions(records));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initSearchBar();
    }
}
