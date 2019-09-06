package com.java.luolingxiao;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private ArrayList<String> channels;

    private void performSearch(String text) {
        if (text != null) {
            if (text.length() > 0) {
                getDataRepository().insertSearchRecords(text);
            }
            searchBar.setPlaceHolder(text);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (fragment != null) {
                fragment.removeFragments();
                transaction.remove(fragment);
            }
            fragment = SearchFragment.newInstance(channels, text);
            transaction.add(R.id.container_search, fragment).commit();
            searchBar.disableSearch();
        } else {
            // back to default mode
        }
    }

    void initSearchBar() {
        searchBar = findViewById(R.id.search_bar);

        channels = getIntent().getStringArrayListExtra("channels");
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (enabled) {
                    searchBar.setHint(searchBar.getPlaceHolderText());
                } else {
//                    if (fragment != null) {
//                        fragment.removeFragments();
//                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//                        fragment = null;
//                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                performSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        searchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                searchBar.onItemClick(position, v);
                performSearch(v.getTag().toString());
            }

            @Override
            public void onItemDelete(int position, View v) {
                searchBar.onItemDelete(position, v);
                if (v.getTag() instanceof String) {
                    getDataRepository().deleteSearchRecords(v.getTag().toString());
                }
            }
        });

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getDataRepository().getAllSearchRecords(records -> searchBar.setLastSuggestions(records));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initSearchBar();
    }
}
