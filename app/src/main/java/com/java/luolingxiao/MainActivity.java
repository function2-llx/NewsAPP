package com.java.luolingxiao;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.java.luolingxiao.api.UserApi;
import com.java.luolingxiao.event.NightModeChangeEvent;
import com.java.luolingxiao.fragment.MainFragment;
import com.java.luolingxiao.fragment.MyFragment;
import com.java.luolingxiao.fragment.RecommendNewsListFragment;
import com.java.luolingxiao.fragment.UnauthorizedFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends DeFaultActivity {
    private BottomNavigationView bottomNavigationView;

    // 当前显示 fragment
    private Fragment fragment;
    private MainFragment mainFragment;
    private UnauthorizedFragment unauthorizedFragment;
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

        mainFragment = MainFragment.newInstance();
        recommendNewsListFragment = new RecommendNewsListFragment();
        unauthorizedFragment = new UnauthorizedFragment();
        myFragment = MyFragment.newInstance();

        myFragment.setAuthorizedListener(new MyFragment.AuthorizedListener() {
            @Override
            public void onLogin() { fragments[1] = recommendNewsListFragment; }

            @Override
            public void onLogout() { fragments[1] = unauthorizedFragment; }
        });

        fragments = new Fragment[] {mainFragment, UserApi.getInstance().isAuthorized() ? recommendNewsListFragment : unauthorizedFragment, myFragment};
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment: fragments) {
            transaction.add(R.id.container_main, fragment);
            transaction.hide(fragment);
        }
        transaction.commit();
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
        recreate();
    }
}