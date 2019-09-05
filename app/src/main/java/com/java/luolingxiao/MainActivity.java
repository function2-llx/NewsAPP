package com.java.luolingxiao;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.java.luolingxiao.event.NightModeChangeEvent;
import com.java.luolingxiao.fragment.MainFragment;
import com.java.luolingxiao.fragment.MyFragment;
import com.java.luolingxiao.fragment.RecommendNewsListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends DefaultActivity {
    private BottomNavigationView bottomNavigationView;

    // 当前显示 fragment
    private Fragment fragment;
    private MainFragment mainFragment;
//    private UnauthorizedFragment unauthorizedFragment;
    private RecommendNewsListFragment recommendNewsListFragment;
    private MyFragment myFragment;
    private Fragment[] fragments;

    void setFragment(Fragment fragment) {
        if (this.fragment != fragment) {
            FragmentTransaction transaction =  getSupportFragmentManager().beginTransaction();
            if (this.fragment != null) transaction.hide(this.fragment);
            transaction.show(fragment).commit();
            this.fragment = fragment;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            mainFragment = MainFragment.newInstance();
            recommendNewsListFragment = new RecommendNewsListFragment();
            myFragment = MyFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_main, mainFragment, "main")
                    .hide(mainFragment)
                    .add(R.id.container_main, recommendNewsListFragment, "recommend")
                    .hide(recommendNewsListFragment)
                    .add(R.id.container_main, myFragment, "my")
                    .hide(myFragment)
                    .commit();
        } else {
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("main");
            recommendNewsListFragment = (RecommendNewsListFragment) getSupportFragmentManager().findFragmentByTag("recommend");
            myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag("my");
        }

        fragments = new Fragment[] {mainFragment, recommendNewsListFragment, myFragment};
        setFragment(fragments[0]);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            Menu menu = bottomNavigationView.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i) == menuItem) {
                    setFragment(fragments[i]);
                }
            }
            return true;
        });

        EventBus.getDefault().register(this);
    }

    public void startSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NightModeChangeEvent event) {
//        if (isNightMode()) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
       recreate();
    }
}